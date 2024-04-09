package com.ruayou.client.annotation;

import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author：ruayou
 * @Date：2024/2/21 22:24
 * @Filename：AnnotationScanner
 */
public class AnnotationScanner {
    private static final AnnotationScanner scanner =new AnnotationScanner();
    private AnnotationScanner(){}

    public static AnnotationScanner getScanner()
    {
        return scanner;
    }

    public List<RGService> getBeansWithRGServiceAnnotation(ApplicationContext applicationContext) {
        List<RGService> annotatedBeans = new ArrayList<>();
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RGService.class);
        System.out.println(beans);
        System.out.println(beans.get("serviceConfig").getClass().getName());
        beans.forEach((key, value) -> annotatedBeans.add(value.getClass().getAnnotation(RGService.class)));
        return annotatedBeans;
    }



}
