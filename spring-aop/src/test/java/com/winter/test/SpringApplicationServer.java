package com.winter.test;

import com.winter.ioc.bean.ApplicationContext;
import com.winter.ioc.bean.DefaultListableBeanFactory;
import com.winter.test.aop.IUserService;

public class SpringApplicationServer {


    public static void main(String[] args) {
        ApplicationContext applicationContext = new DefaultListableBeanFactory("com.winter.test");

        IUserService userService = applicationContext.getBean("userServiceImpl");
        userService.saveMessage();

    }


}
