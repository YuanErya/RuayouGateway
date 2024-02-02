package com.ruayou.core;

import com.ruayou.common.api_interface.config_center.ConfigCenter;
import com.ruayou.common.api_interface.config_center.ConfigChangeListener;
import com.ruayou.common.api_interface.register_center.RegisterCenter;
import com.ruayou.common.api_interface.register_center.RegisterCenterListener;
import com.ruayou.common.config.*;
import com.ruayou.common.constant.ServiceConst;
import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.common.utils.NetUtils;
import com.ruayou.common.utils.YamlUtils;
import com.ruayou.config_center.nacosimpl.NacosConfigCenter;
import com.ruayou.core.httpclient.AsyncHttpCoreClient;
import com.ruayou.core.netty.NettyHttpServer;
import com.ruayou.core.netty.processor.HttpProcessor;
import com.ruayou.core.netty.processor.HttpServerCoreProcessor;
import com.ruayou.register_center.nacosimpl.NacosRegisterCenter;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author：ruayou
 * @Date：2024/1/26 19:04
 * @Filename：ServerContainer
 */
@Log4j2
@Data
public class ServerContainer implements LifeCycle{
    private static GlobalConfig globalConfig=GlobalConfig.getConfig();
    private static HttpClientConfig httpClientConfig=globalConfig.getHttpClientConfig();
    private static NettyServerConfig nettyServerConfig=globalConfig.getNettyServerConfig();
    private static NacosConfig nacosConfig=globalConfig.getNacosConfig();
    private HttpProcessor processor;
    boolean initFlag=false;

    private static final  ArrayList<LifeCycle> run=new ArrayList<>();

    @Override
    public void init() {
        if (initFlag) {
            return;
        }
        //后期调整核心处理器
        HttpServerCoreProcessor coreProcessor=new HttpServerCoreProcessor();
        this.processor=coreProcessor;
        run.add(processor);
        NettyHttpServer server = new NettyHttpServer(nettyServerConfig, processor);
        run.add(server);
        run.add(new AsyncHttpCoreClient(httpClientConfig,server.getWorkerEventLoopGroup()));
        run.forEach(LifeCycle::init);
        this.initFlag=true;
    }

    @Override
    public void start() {
        if (!initFlag) {
            init();
        }
        run.forEach(LifeCycle::start);
        registerAndSubscribe(nacosConfig,nettyServerConfig);
        ConfigCenter configCenter=new NacosConfigCenter();
        configCenter.init(nacosConfig.getRegistryAddress(), nacosConfig.getEnv());
        configCenter.subscribeConfigChange(GlobalConfig.dataId, new ConfigChangeListener() {
            @Override
            public void onConfigChange(String configInfo) {
                GlobalConfig origin=GlobalConfig.getConfig();
                GlobalConfig gf=YamlUtils.parseYaml(configInfo, GlobalConfig.class);
                if (gf!=null&&!origin.equals(gf)) {
                    log.info("检测到核心组件配置更新：{}",gf);
                    GlobalConfig.saveConfig(gf);
                    updateConfig(gf);
                    //重启配置变更的组件
                    if (!gf.getNettyServerConfig().equals(origin.getNettyServerConfig())) {
                        run.forEach((component)->{
                            if(component instanceof NettyHttpServer )component.restart();
                        });
                    }
                    //重启配置变更的组件
                    if (!gf.getHttpClientConfig().equals(origin.getHttpClientConfig())) {
                        run.forEach((component)->{
                            if(component instanceof AsyncHttpCoreClient )component.restart();
                        });
                    }
                }
            }
        });
        //订阅路由规则
        configCenter.subscribeConfigChange(PatternPathConfig.dataId, new ConfigChangeListener() {
            @Override
            public void onConfigChange(String configInfo) {
                PatternPathConfig patternPathConfig = YamlUtils.parseYaml(configInfo, PatternPathConfig.class);
                PatternPathConfig.saveConfig(patternPathConfig);
                log.info("检测到路由规则配置更新：{}",PatternPathConfig.getConfig());
            }
        });
        log.debug("RuayouGateway网关启动成功，正在监听端口：{}", nettyServerConfig.getPort());
    }

    @Override
    public void close() {
        run.forEach(LifeCycle::close);
        log.info("========RuayouGateway已停止运行！========");
    }

    @Override
    public void restart() {
    }

    public static void addComponent(LifeCycle component){
        run.add(component);
    }

    public static List<LifeCycle> getComponents(){
        return run;
    }

    private static RegisterCenter registerAndSubscribe(NacosConfig nacosConfig,NettyServerConfig nettyServerConfig) {
        final RegisterCenter registerCenter = new NacosRegisterCenter();
        registerCenter.init(nacosConfig.getRegistryAddress(), nacosConfig.getEnv());
        //构造网关服务定义和服务实例
        ServiceDefinition serviceDefinition = buildGatewayServiceDefinition(nacosConfig);
        ServiceInstance serviceInstance = buildGatewayServiceInstance(nettyServerConfig,nacosConfig);
        //注册
        registerCenter.register(serviceDefinition, serviceInstance);
        //订阅
        registerCenter.subscribeAllServices(new RegisterCenterListener() {
            @Override
            public void onChange(ServiceDefinition serviceDefinition, Set<ServiceInstance> serviceInstanceSet) {
                log.info("refresh service and instance: {} {}", serviceDefinition.getUniqueId(), serviceInstanceSet);
                //要做的是把变更的新的获取到的新的实例的列表重新保存
                ServiceAndInstanceManager manager = ServiceAndInstanceManager.getManager();
                //将这次变更事件影响之后的服务实例再次添加到对应的服务实例集合
                manager.addServiceInstance(serviceDefinition.getUniqueId(), serviceInstanceSet);
                //修改发生对应的服务定义
                manager.putServiceDefinition(serviceDefinition.getUniqueId(),serviceDefinition);
            }
        });
        return registerCenter;
    }

    private static ServiceInstance buildGatewayServiceInstance(NettyServerConfig nettyServerConfig,NacosConfig config) {
        String localIp = NetUtils.getLocalIp();
        int port = nettyServerConfig.getPort();
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceInstanceId(localIp + ":" + port);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setWeight(ServiceConst.DEFAULT_WEIGHT);
        serviceInstance.setRegisterTime(System.currentTimeMillis());
        serviceInstance.setUniqueId(config.getApplicationName());
        return serviceInstance;
    }

    private static ServiceDefinition buildGatewayServiceDefinition(NacosConfig config) {
        String applicationName = config.getApplicationName();
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setUniqueId(applicationName);
        serviceDefinition.setServiceId(applicationName);
        serviceDefinition.setGroup(config.getEnv());
        return serviceDefinition;
    }

    public static void updateConfig(GlobalConfig config){
        globalConfig = config;
        httpClientConfig=config.getHttpClientConfig();
        nettyServerConfig=config.getNettyServerConfig();
        nacosConfig=config.getNacosConfig();
    }
}
