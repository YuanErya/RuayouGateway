package com.ruayou.core;

import com.ruayou.common.config.*;
import com.ruayou.common.constant.CommonConst;
import com.ruayou.common.constant.ServiceConst;
import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.common.exception.GatewayException;
import com.ruayou.common.utils.NetUtils;
import com.ruayou.common.utils.YamlUtils;
import com.ruayou.configcenter.api.ConfigCenter;
import com.ruayou.configcenter.api.ConfigChangeListener;
import com.ruayou.core.filter.filter_rule.FilterRules;
import com.ruayou.core.filter.flowcontrol.limiter.LocalCountLimiter;
import com.ruayou.core.httpclient.AsyncHttpCoreClient;
import com.ruayou.core.manager.CacheManager;
import com.ruayou.core.manager.ServiceAndInstanceManager;
import com.ruayou.core.netty.NettyHttpServer;
import com.ruayou.core.netty.processor.DisruptorHttpServerProcessor;
import com.ruayou.core.netty.processor.HttpProcessor;
import com.ruayou.core.netty.processor.HttpServerCoreProcessor;
import com.ruayou.registercenter.api.RegisterCenter;
import com.ruayou.registercenter.api.RegisterCenterListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.util.*;


/**
 * @Author：ruayou
 * @Date：2024/1/26 19:04
 * @Filename：ServerContainer
 */
@Slf4j
@Data
public class ServerContainer implements LifeCycle{
    private static GlobalConfig globalConfig=GlobalConfig.getConfig();
    private static HttpClientConfig httpClientConfig=globalConfig.getHttpClientConfig();
    private static NettyServerConfig nettyServerConfig=globalConfig.getNettyServerConfig();
    private static RegisterAndConfigCenterConfig registerAndConfigCenterConfig =globalConfig.getRegisterAndConfigCenterConfig();
    private HttpProcessor processor;
    boolean initFlag=false;
    boolean startFlag=false;

    private static final  ArrayList<LifeCycle> run=new ArrayList<>();

    @Override
    public void init() {
        if (initFlag) {
            return;
        }
        HttpServerCoreProcessor coreProcessor=new HttpServerCoreProcessor();
        DisruptorHttpServerProcessor processor=new DisruptorHttpServerProcessor(globalConfig.getDisruptorConfig(),coreProcessor);
        this.processor=processor;
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
        ConfigCenter configCenter = null;
        ServiceLoader<ConfigCenter> serviceLoader = ServiceLoader.load(ConfigCenter.class);
        Iterator<ConfigCenter> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            ConfigCenter next = iterator.next();
            if (next.getClass().getName().equals(registerAndConfigCenterConfig.getConfigServer())) {
                configCenter=next;
                break;
            }
        }
        if(configCenter==null){
            throw new GatewayException("load configCenter failed !");
        }
        configCenter.init(registerAndConfigCenterConfig.getConfigAddress(), registerAndConfigCenterConfig.getEnv());
        configCenter.subscribeConfigChange(GlobalConfig.dataId, new ConfigChangeListener() {
            @Override
            public void onConfigChange(String configInfo) {
                GlobalConfig origin=GlobalConfig.getConfig();
                GlobalConfig gf=YamlUtils.parseYaml(configInfo, GlobalConfig.class);
                if (gf!=null&&!origin.equals(gf)) {
                    log.info("检测到核心组件配置更新：{}",gf);
                    GlobalConfig.saveConfig(gf);
                    updateConfig(gf);
                    //第一次启动之前不用重启组件。
                    if(!startFlag) {
                        return;
                    }
                    //重启配置变更的组件
                    if (!gf.getNettyServerConfig().equals(origin.getNettyServerConfig())) {
                        run.forEach((component)->{
                            if(component instanceof NettyHttpServer ) {
                                component.restart();
                            }
                        });
                    }
                    //重启配置变更的组件
                    if (!gf.getHttpClientConfig().equals(origin.getHttpClientConfig())) {
                        run.forEach((component)->{
                            if(component instanceof AsyncHttpCoreClient ) {
                                component.restart();
                            }
                        });
                    }
                }
            }
        });
        configCenter.subscribeConfigChange(FilterRules.dataId, new ConfigChangeListener() {
            @Override
            public void onConfigChange(String configInfo) {
                FilterRules filterRules=YamlUtils.parseYaml(configInfo, FilterRules.class);
                if (filterRules!=null) {
                    FilterRules.updateRules(filterRules);
                }
                //清空规则相关缓存
                CacheManager.cleanAllCache(CacheManager.FILTER_RULE_CACHE);
                LocalCountLimiter.cleanCache();
                log.info("检测到过滤规则配置更新：{}",FilterRules.getGlobalRules());
            }
        });

        registerAndSubscribe(registerAndConfigCenterConfig,nettyServerConfig);
        run.forEach(LifeCycle::start);
        this.startFlag=true;
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

    private static RegisterCenter registerAndSubscribe(RegisterAndConfigCenterConfig registerAndConfigCenterConfig, NettyServerConfig nettyServerConfig) throws GatewayException {
        ServiceLoader<RegisterCenter> serviceLoader = ServiceLoader.load(RegisterCenter.class);
        RegisterCenter registerCenter=null;
        Iterator<RegisterCenter> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            RegisterCenter next = iterator.next();
            if (next.getClass().getName().equals(registerAndConfigCenterConfig.getRegisterServer())) {
                registerCenter=next;
                break;
            }
        }
        if (registerCenter==null) {
            throw new GatewayException("registerCenter impl load fail");
        }
        registerCenter.init(registerAndConfigCenterConfig.getRegistryAddress(), registerAndConfigCenterConfig.getEnv());
        //构造网关服务定义和服务实例
        ServiceDefinition serviceDefinition = buildGatewayServiceDefinition(registerAndConfigCenterConfig);
        ServiceInstance serviceInstance = buildGatewayServiceInstance(serviceDefinition,nettyServerConfig, registerAndConfigCenterConfig);
        //注册
        registerCenter.register(serviceDefinition, serviceInstance);
        //订阅
        registerCenter.subscribeAllServices(new RegisterCenterListener() {
            @Override
            public void onChange(ServiceDefinition serviceDefinition, Set<ServiceInstance> serviceInstanceSet) {
                log.info("refresh service and instance: {} {}", serviceDefinition.getServiceId(), serviceInstanceSet);
                if (serviceDefinition.getServiceId().equals(registerAndConfigCenterConfig.getApplicationName())) {
                    //网关本体并不需要加入实例
                    return;
                }
                //要做的是把变更的新的获取到的新的实例的列表重新保存
                ServiceAndInstanceManager manager = ServiceAndInstanceManager.getManager();
                //将这次变更事件影响之后的服务实例再次添加到对应的服务实例集合
                manager.addServiceInstance(serviceDefinition.getServiceId(), serviceInstanceSet);
                //修改发生对应的服务定义
                manager.putServiceDefinition(serviceDefinition.getServiceId(),serviceDefinition);
                //清空实例相关缓存
                CacheManager.cleanAllCache(CacheManager.SERVICE_CACHE);
            }
        });
        return registerCenter;
    }

    private static ServiceInstance buildGatewayServiceInstance(ServiceDefinition definition, NettyServerConfig nettyServerConfig, RegisterAndConfigCenterConfig config) {
        String localIp = NetUtils.getLocalIp();
        int port = nettyServerConfig.getPort();
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceInstanceId(localIp + CommonConst.COLON_SEPARATOR + port);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setWeight(ServiceConst.DEFAULT_WEIGHT);
        serviceInstance.setRegisterTime(System.currentTimeMillis());
        serviceInstance.setUniqueId(definition.getServiceId()+ CommonConst.COLON_SEPARATOR +GlobalConfig.version);
        return serviceInstance;
    }

    private static ServiceDefinition buildGatewayServiceDefinition(RegisterAndConfigCenterConfig config) {
        String applicationName = config.getApplicationName();
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setServiceId(applicationName);
        serviceDefinition.setEnv(config.getEnv());
        return serviceDefinition;
    }

    public static void updateConfig(GlobalConfig config){
        globalConfig = config;
        httpClientConfig=config.getHttpClientConfig();
        nettyServerConfig=config.getNettyServerConfig();
        registerAndConfigCenterConfig =config.getRegisterAndConfigCenterConfig();
    }
}
