package com.ruayou.common.config;

import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author：ruayou
 * @Date：2024/2/1 0:18
 * @Filename：ServiceAndInstanceManager
 */
public class ServiceAndInstanceManager {
    private static final ServiceAndInstanceManager INSTANCE=new ServiceAndInstanceManager();
    //	服务的定义集合：uniqueId代表服务的唯一标识
    private ConcurrentHashMap<String /* uniqueId */ , ServiceDefinition> serviceDefinitionMap = new ConcurrentHashMap<>();

    //	服务的实例集合：uniqueId与一对服务实例对应
    private ConcurrentHashMap<String /* uniqueId */ , Set<ServiceInstance>>  serviceInstanceMap = new ConcurrentHashMap<>();

    public static ServiceAndInstanceManager getManager() {
        return INSTANCE;
    }


    /***************** 	对服务定义缓存进行操作的系列方法 	***************/

    public void putServiceDefinition(String uniqueId,
                                     ServiceDefinition serviceDefinition) {
        serviceDefinitionMap.put(uniqueId, serviceDefinition);;
    }

    public ServiceDefinition getServiceDefinition(String uniqueId) {
        return serviceDefinitionMap.get(uniqueId);
    }

    public void removeServiceDefinition(String uniqueId) {
        serviceDefinitionMap.remove(uniqueId);
    }

    public ConcurrentHashMap<String, ServiceDefinition> getServiceDefinitionMap() {
        return serviceDefinitionMap;
    }

    /***************** 	对服务实例缓存进行操作的系列方法 	***************/

    public Set<ServiceInstance> getServiceInstanceByUniqueId(String uniqueId, boolean gray){
        Set<ServiceInstance> serviceInstances = serviceInstanceMap.get(uniqueId);
        if (serviceInstances==null||serviceInstances.isEmpty()) {
            return Collections.emptySet();
        }
        //不为空且为灰度流量
        if (gray) {
            return  serviceInstances.stream()
                    .filter(ServiceInstance::isGray)
                    .collect(Collectors.toSet());
        }
        return serviceInstances;
    }
    public void addServiceInstance(String uniqueId, ServiceInstance serviceInstance) {
        Set<ServiceInstance> set = serviceInstanceMap.get(uniqueId);
        set.add(serviceInstance);
    }

    public void addServiceInstance(String uniqueId, Set<ServiceInstance> serviceInstanceSet) {
        serviceInstanceMap.put(uniqueId, serviceInstanceSet);
    }

    public void updateServiceInstance(String uniqueId, ServiceInstance serviceInstance) {
        Set<ServiceInstance> set = serviceInstanceMap.get(uniqueId);
        Iterator<ServiceInstance> it = set.iterator();
        while(it.hasNext()) {
            ServiceInstance is = it.next();
            if(is.getServiceInstanceId().equals(serviceInstance.getServiceInstanceId())) {
                it.remove();
                break;
            }
        }
        set.add(serviceInstance);
    }

    public void removeServiceInstance(String uniqueId, String serviceInstanceId) {
        Set<ServiceInstance> set = serviceInstanceMap.get(uniqueId);
        Iterator<ServiceInstance> it = set.iterator();
        while(it.hasNext()) {
            ServiceInstance is = it.next();
            if(is.getServiceInstanceId().equals(serviceInstanceId)) {
                it.remove();
                break;
            }
        }
    }

    public void removeServiceInstancesByUniqueId(String uniqueId) {
        serviceInstanceMap.remove(uniqueId);
    }

}
