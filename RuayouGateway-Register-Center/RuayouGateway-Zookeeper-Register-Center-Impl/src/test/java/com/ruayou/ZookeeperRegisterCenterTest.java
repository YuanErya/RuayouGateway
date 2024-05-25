package com.ruayou;

import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.register_center.zookeeperimpl.ZookeeperRegisterCenter;
import org.junit.Test;
import java.util.Arrays;

/**
 * @Author：ruayou
 * @Date：2024/5/25 20:01
 * @Filename：ZookeeperRegisterCenterTest
 */
public class ZookeeperRegisterCenterTest {
    @Test
    public void testRegisterZookeeperRegisterCenter() throws InterruptedException {
        ZookeeperRegisterCenter zookeeperRegisterCenter = new ZookeeperRegisterCenter();
        zookeeperRegisterCenter.init("127.0.0.1:2181", "test-center");
        ServiceDefinition definition = new ServiceDefinition();
        definition.setServiceId("gateway");
        definition.setEnv("test");
        definition.setProtocol("http");
        definition.setPatternPath(Arrays.asList("12412:22", "wqeqw:333"));

        ServiceInstance instance = new ServiceInstance();
        instance.setIp("127.0.0.21");
        instance.setServiceInstanceId("127.0.0.21");
        instance.setVersion("1.0.0");
        instance.setRegisterTime(System.currentTimeMillis());
        zookeeperRegisterCenter.register(definition, instance);
        zookeeperRegisterCenter.subscribeAllServices(null);
        while (true);
//        Thread.sleep(10000);
//        zookeeperRegisterCenter.deregister(definition, instance);
//
//        Thread.sleep(10000);
    }
}
