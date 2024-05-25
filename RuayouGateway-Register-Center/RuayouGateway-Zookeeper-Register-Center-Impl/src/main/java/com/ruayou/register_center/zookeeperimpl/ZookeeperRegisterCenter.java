package com.ruayou.register_center.zookeeperimpl;

import com.alibaba.fastjson.JSON;
import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.common.exception.GatewayException;
import com.ruayou.registercenter.api.RegisterCenter;
import com.ruayou.registercenter.api.RegisterCenterListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author：ruayou
 * @Date：2024/5/25 19:06
 * @Filename：ZookeeperRegisterCenter Zookeeper作为注册中心
 */
@Slf4j
public class ZookeeperRegisterCenter implements RegisterCenter {

    private CuratorFramework client;
    private String env;
    private String registerAddress;
    private static String GANG = "/";
    private static String SERVICE_BASE="/service";


    @Override
    public void init(String registerAddress, String env) {
        this.registerAddress = registerAddress;
        this.env = env;
        this.client = CuratorFrameworkFactory.builder()
                .connectString(registerAddress)
                .retryPolicy(new ExponentialBackoffRetry(1000, 5))
                //重试策略
                .sessionTimeoutMs(5 * 1000)
                .namespace(env)
                //相当于本次连接的所有操作路径都有一个前缀
                .build();
        client.start();
        try {
            //添加Service的根节点
            if (client.checkExists().forPath(SERVICE_BASE)==null) {
               client.create().withMode(CreateMode.CONTAINER).forPath(SERVICE_BASE);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) throws GatewayException {
        try {
            String servicePath=SERVICE_BASE+GANG+serviceDefinition.getServiceId();
            if (client.checkExists().forPath(servicePath)==null) {
                //注册服务，CONTAINER模式当它包含的最后一个子节点被删除后，该container父节点会被删除
                client.create().withMode(CreateMode.CONTAINER).forPath(servicePath, JSON.toJSONBytes(serviceDefinition));
            }
            String instancePath=servicePath+GANG+serviceInstance.getServiceInstanceId();
            if (client.checkExists().forPath(instancePath)==null) {
                //注册实例
                client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, JSON.toJSONBytes(serviceInstance));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        try {
            String instancePath=SERVICE_BASE+GANG+serviceDefinition.getServiceId()+GANG+serviceInstance.getServiceInstanceId();
            if (client.checkExists().forPath(instancePath)!=null) {
                //删除实例
                client.delete().forPath(instancePath);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void subscribeAllServices(RegisterCenterListener registerCenterListener) {
        TreeCache treeCache = new TreeCache(client, SERVICE_BASE);
        treeCache.getListenable().addListener(new ZookeeperRegisterListener(registerCenterListener));
        try {
            treeCache.start();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static class ZookeeperRegisterListener implements TreeCacheListener {
        RegisterCenterListener registerCenterListener;

        public ZookeeperRegisterListener(RegisterCenterListener rcl) {
            this.registerCenterListener = rcl;
        }

        @Override
        public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
            ChildData data = treeCacheEvent.getData();
            if (data == null|| data.getPath().equals(SERVICE_BASE)) {
                return;
            }
            String path = data.getPath().substring(SERVICE_BASE.length()+1);
            String[] split = path.split(GANG);
            String servicePath = SERVICE_BASE+GANG+split[0];
            ServiceDefinition definition = JSON.parseObject(curatorFramework.getData().forPath(servicePath), ServiceDefinition.class);
            List<String> childPaths = curatorFramework.getChildren().forPath(servicePath);
            Set<ServiceInstance> instanceList=new HashSet<>();
            for (String childPath : childPaths) {
                String cPath = servicePath+ GANG + childPath;
                ServiceInstance inst = JSON.parseObject(curatorFramework.getData().forPath(cPath), ServiceInstance.class);
                if (inst != null) {instanceList.add(inst);}
            }
            registerCenterListener.onChange(definition,instanceList);
        }
    }
}
