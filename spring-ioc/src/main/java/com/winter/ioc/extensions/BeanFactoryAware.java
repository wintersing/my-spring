package com.winter.ioc.extensions;

import com.winter.ioc.bean.BeanFactory;

public interface BeanFactoryAware extends Aware {

    void setBeanFactory(BeanFactory beanFactory);

}
