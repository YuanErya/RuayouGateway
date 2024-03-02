package com.ruayou.common.config;

import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.common.utils.PathUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author：ruayou
 * @Date：2024/2/1 0:18
 * @Filename：ServiceAndInstanceManager
 */
public class ServiceAndInstanceManager {
    private static final ServiceAndInstanceManager INSTANCE = new ServiceAndInstanceManager();
    //	服务的定义集合：uniqueId代表服务的唯一标识
    private ConcurrentHashMap<String /* uniqueId */ , ServiceDefinition> serviceDefinitionMap = new ConcurrentHashMap<>();

    //	服务的实例集合：uniqueId与一对服务实例对应
    private ConcurrentHashMap<String /* uniqueId */ , Set<ServiceInstance>> serviceInstanceMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String /* ruleId */ , FilterRule> ruleMap = new ConcurrentHashMap<>();

    //路径以及规则集合
    private ConcurrentHashMap<String /* 路径 */ , FilterRule> pathRuleMap = new ConcurrentHashMap<>();

    public static ServiceAndInstanceManager getManager() {
        return INSTANCE;
    }


    /***************** 	对服务定义缓存进行操作的系列方法 	***************/

    public void putServiceDefinition(String serviceId,
                                     ServiceDefinition serviceDefinition) {
        serviceDefinitionMap.put(serviceId, serviceDefinition);
        List<String> patterns = serviceDefinition.getPatternPath();
        HashMap<String,String> noRulePatterns = new HashMap<>();
        for (String pattern : patterns) {
            if (pathRuleMap.containsKey(pattern)) {
                continue;
            }
            noRulePatterns.put(pattern, serviceId);
            pathRuleMap.put(pattern, FilterRules.getDefaultFilterRule());
        }
        FilterRules.updateDefaultFilterRule(noRulePatterns);
    }

    public ServiceDefinition getServiceDefinition(String serviceId) {
        return serviceDefinitionMap.get(serviceId);
    }

    public void removeServiceDefinition(String serviceId) {
        serviceDefinitionMap.remove(serviceId);
    }

    public ConcurrentHashMap<String, ServiceDefinition> getServiceDefinitionMap() {
        return serviceDefinitionMap;
    }

    /***************** 	对服务实例缓存进行操作的系列方法 	***************/

    public Set<ServiceInstance> getServiceInstanceByServiceId(String serviceId,boolean gray,String version) {

        //待添加缓存
        Set<ServiceInstance> serviceInstances = serviceInstanceMap.get(serviceId);
        if (serviceInstances == null || serviceInstances.isEmpty()) {
            return Collections.emptySet();
        }
        serviceInstances=serviceInstances.stream()
                .filter((instance)-> instance.getVersion().equals(version))
                .collect(Collectors.toSet());
        //不为空且为灰度流量
        if (gray) {
            return serviceInstances.stream()
                    .filter(ServiceInstance::isGray)
                    .collect(Collectors.toSet());
        }
        return serviceInstances;
    }

    public void addServiceInstance(String serviceId, ServiceInstance serviceInstance) {
        Set<ServiceInstance> set = serviceInstanceMap.get(serviceId);
        set.add(serviceInstance);
    }

    public void addServiceInstance(String serviceId, Set<ServiceInstance> serviceInstanceSet) {
        serviceInstanceMap.put(serviceId, serviceInstanceSet);
    }

    public void updateServiceInstance(String serviceId, ServiceInstance serviceInstance) {
        Set<ServiceInstance> set = serviceInstanceMap.get(serviceId);
        Iterator<ServiceInstance> it = set.iterator();
        while (it.hasNext()) {
            ServiceInstance is = it.next();
            if (is.getServiceInstanceId().equals(serviceInstance.getServiceInstanceId())) {
                it.remove();
                break;
            }
        }
        set.add(serviceInstance);
    }

    public void removeServiceInstance(String serviceId, String serviceInstanceId) {
        Set<ServiceInstance> set = serviceInstanceMap.get(serviceId);
        Iterator<ServiceInstance> it = set.iterator();
        while (it.hasNext()) {
            ServiceInstance is = it.next();
            if (is.getServiceInstanceId().equals(serviceInstanceId)) {
                it.remove();
                break;
            }
        }
    }

    public void removeServiceInstancesByServiceId(String serviceId) {
        serviceInstanceMap.remove(serviceId);
    }


    /***************** 	对规则缓存进行操作的系列方法 	***************/
    public void putRule(String ruleId, FilterRule rule) {
        ruleMap.put(ruleId, rule);
    }

    public void putAllFilterRules(FilterRules filterRules) {
        ConcurrentHashMap<String, FilterRule> newRuleMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, FilterRule> newPathMap = new ConcurrentHashMap<>();
        Map<String, FilterRule> rules = filterRules.getRules();
        rules.forEach((k, v) -> {
                    newRuleMap.put(k, v);
                    Set<String> keySet = v.getPatterns().keySet();
                    for (String pattern : keySet) {
                        newPathMap.put(pattern, v);
                    }
                }
        );
        this.ruleMap = newRuleMap;
        this.pathRuleMap = newPathMap;
    }

    public FilterRule getRule(String ruleId) {
        return ruleMap.get(ruleId);
    }

    public void removeRule(String ruleId) {
        ruleMap.remove(ruleId);
    }

    /**
     * 传入路径，模式匹配
     * @param path
     * @return
     */
    public FilterRule getRuleByPath(String path) {
        ConcurrentHashMap.KeySetView<String, FilterRule> keySet = pathRuleMap.keySet();
        for (String pattern : keySet) {
            if (PathUtils.isMatch(path, pattern)) {
                return pathRuleMap.get(pattern);
            }
        }
        return pathRuleMap.get(path);
    }
}
