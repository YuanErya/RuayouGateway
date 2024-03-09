package com.ruayou.core.manager;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ruayou.core.filter.filter_rule.FilterRule;
import com.ruayou.core.filter.filter_rule.FilterRules;
import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.common.exception.ServiceNotFoundException;
import com.ruayou.common.utils.PathUtils;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ruayou.common.constant.ServiceConst.DEFAULT_ID;
import static com.ruayou.common.enums.ResponseCode.SERVICE_DEFINITION_NOT_FOUND;

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
    private ConcurrentHashMap<String /* 路径 */ , FilterRule> patternRuleMap = new ConcurrentHashMap<>();

    private static final Cache<String,String> ruleIdCache= CacheManager.createCache(CacheManager.FILTER_RULE_CACHE,"ruleIdCache");
//            Caffeine.newBuilder().recordStats().expireAfterWrite(10,
//            TimeUnit.MINUTES).build();
    private static final Cache<String,Set<ServiceInstance>> instanceSetCache= CacheManager.createCache(CacheManager.SERVICE_CACHE,"instanceSetCache");
//        Caffeine.newBuilder().recordStats().expireAfterWrite(10,
//            TimeUnit.MINUTES).build();

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
            if (patternRuleMap.containsKey(pattern)) {
                continue;
            }
            noRulePatterns.put(pattern, serviceId);
            patternRuleMap.put(pattern, FilterRules.getDefaultFilterRule());
        }
        FilterRules.updateDefaultFilterRule(noRulePatterns);
        if(!ruleMap.containsKey(DEFAULT_ID))ruleMap.put(DEFAULT_ID,FilterRules.getDefaultFilterRule());
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
        return instanceSetCache.get(serviceId+gray+version,k->searchServiceInstance(serviceId,gray,version));
    }

    private Set<ServiceInstance> searchServiceInstance(String serviceId,boolean gray,String version){
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
        this.patternRuleMap = newPathMap;
    }

    public FilterRule getRule(String ruleId) {
        return ruleMap.get(ruleId);
    }

    public void removeRule(String ruleId) {
        ruleMap.remove(ruleId);
    }

    /**
     * 路径查规则设计缓存机制
     * @param path
     * @return
     */
    public FilterRule getRuleByPath(String path) {
        String ruleId = ruleIdCache.get(path, this::getRuleIDByPath);
        return ruleMap.get(ruleId);
    }
    private String getRuleIDByPath(String path){
        ConcurrentHashMap.KeySetView<String, FilterRule> keySet = patternRuleMap.keySet();
        for (String pattern : keySet) {
            if (PathUtils.isMatch(path, pattern)) {
                return patternRuleMap.get(pattern).getRuleId();
            }
        }
        //未找到相关的规则，抛异常找不到服务。
        throw new ServiceNotFoundException(SERVICE_DEFINITION_NOT_FOUND);
    }

}
