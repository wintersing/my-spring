package com.winter.test.aop;

import com.winter.ioc.annotation.Bean;
import com.winter.ioc.annotation.Configuration;

@Configuration
//@Import(AnnotationAwareAspectJAutoProxyCreator.class)
public class UserConfig {

    public UserConfig() {
        System.out.println("userConfig....");
    }

    @Bean
    public User user() {
        return new User();
    }


    public User user1() {
        return new User();
    }
}
