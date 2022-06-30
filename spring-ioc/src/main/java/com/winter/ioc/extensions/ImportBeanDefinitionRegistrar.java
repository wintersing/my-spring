package com.winter.ioc.extensions;

import com.winter.ioc.bean.BeanDefinitionRegistry;

public interface ImportBeanDefinitionRegistrar {

    void registerBeanDefinitions(BeanDefinitionRegistry registry);

}
