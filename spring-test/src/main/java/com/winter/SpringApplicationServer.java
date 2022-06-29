package com.winter;

import com.winter.ioc.bean.DefaultListableBeanFactory;
import com.winter.test.ioc.IocController;

public class SpringApplicationServer {
    public static void main(String[] args) {
        DefaultListableBeanFactory springIocApplication = new DefaultListableBeanFactory("com.winter.test");
        IocController iocController = springIocApplication.getBean("iocController");
        iocController.iocController();
    }
}