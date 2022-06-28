package com.winter;

import com.winter.ioc.ApplicationContext;
import com.winter.test.ioc.IocController;

public class SpringApplicationServer {
    public static void main(String[] args) {
        ApplicationContext springIocApplication = new ApplicationContext("com.winter.test");
        IocController iocController = springIocApplication.getBean("iocController");
        iocController.iocController();
    }
}