package com.ruayou.client.manager;
import com.ruayou.client.AutoRegisterProperties;
import com.ruayou.client.annotation.AnnotationScanner;
import com.ruayou.client.annotation.RGService;
import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SpringClientRegisterManager extends AutoRegisterManager implements ApplicationListener<ApplicationEvent>, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Autowired
    private ServerProperties serverProperties;
    public SpringClientRegisterManager(AutoRegisterProperties properties) {
        super(properties);
    }

    @Override
    public void doRegister() {

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
        ServiceDefinition serviceDefinition = new ServiceDefinition(service.serviceId()+":"+service.version(),
                service.serviceId(),
                service.version(),
                service.protocol(),
                service.patternPath(),
                super.properties.getEnv(),true);

        ServiceInstance serviceInstance = new ServiceInstance();
        //具体实例信息
        if (properties.isGray()){
            serviceInstance.setGray(true);
        }
        //注册
        register(serviceDefinition, serviceInstance);
    }
}
