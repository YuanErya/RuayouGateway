package com.ruayou.client.manager;
import com.ruayou.client.AutoRegisterProperties;
import com.ruayou.client.annotation.AnnotationScanner;
import com.ruayou.client.annotation.RGService;
import com.ruayou.common.constant.CommonConst;
import com.ruayou.common.constant.ServiceConst;
import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.common.utils.NetUtils;
import com.ruayou.common.utils.SystemTime;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 * @Author：ruayou
 * @Date：2024/2/21 16:42
 * @Filename：SpringClientRegisterManager
 */
@Log4j2
public class SpringClientRegisterManager extends AbstractRegisterManager implements ApplicationListener<ApplicationEvent>, ApplicationContextAware {
    private ApplicationContext applicationContext;

    private ServerProperties serverProperties;
    public SpringClientRegisterManager(AutoRegisterProperties properties,ServerProperties serverProperties) {
        super(properties);
        this.serverProperties=serverProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ApplicationStartedEvent) {
            try {
                doRegisterSpringService();
            } catch (Exception e) {
                log.error("doRegisterSpring error", e);
                throw new RuntimeException(e);
            }
            log.info("spring register success");
        }
    }

    private void doRegisterSpringService() {
        AnnotationScanner scanner=AnnotationScanner.getScanner();
        List<RGService> beans = scanner.getBeansWithRGServiceAnnotation(applicationContext);
        if (beans.size()>1) {
            throw new RuntimeException("The annotation @RGService can only be used to mark a single location.");
        }
        RGService service = beans.get(0);
        ServiceDefinition serviceDefinition = new ServiceDefinition(
                service.serviceId(),
                service.protocol(),
                service.patternPath(),
                super.properties.getEnv(),true);
        ServiceInstance serviceInstance = new ServiceInstance();
        String localIp = NetUtils.getLocalIp();
        int port = serverProperties.getPort();
        serviceInstance.setServiceInstanceId(localIp + CommonConst.COLON_SEPARATOR + port);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setWeight(ServiceConst.DEFAULT_WEIGHT);
        serviceInstance.setRegisterTime(SystemTime.currentTimeMillis());
        serviceInstance.setVersion(service.version());
        serviceInstance.setUniqueId(serviceDefinition.getServiceId()+CommonConst.COLON_SEPARATOR+service.version());
        //具体实例信息待完善
        if (properties.isGray()){
            serviceInstance.setGray(true);
        }
        //注册
        register(serviceDefinition, serviceInstance);
    }
}
