package com.ruayou.registercenter.api;


import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;

import java.util.Set;

public interface RegisterCenterListener {

    void onChange(ServiceDefinition serviceDefinition,
                  Set<ServiceInstance> serviceInstanceSet);
}
