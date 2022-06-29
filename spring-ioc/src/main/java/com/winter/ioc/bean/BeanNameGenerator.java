package com.winter.ioc.bean;

import com.winter.ioc.annotation.Component;
import com.winter.ioc.annotation.Controller;
import com.winter.ioc.annotation.Service;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;

public class BeanNameGenerator {
    public static String generateBeanName(Class<?> aClass) {
        String beanName = null;
        Annotation[] annotations = aClass.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Component) {
                beanName = ((Component) annotation).value();
            } else if (annotation instanceof Controller) {
                beanName = ((Controller) annotation).value();
            } else if (annotation instanceof Service) {
                beanName = ((Service) annotation).value();
            }
        }
        if (StringUtils.isBlank(beanName)) {
            beanName = toLowerCaseFirstWord(aClass.getSimpleName());
        }
        return beanName;
    }

    public static String toLowerCaseFirstWord(String s) {
        return Character.isLowerCase(s.charAt(0)) ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
