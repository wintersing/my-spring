package com.winter.test;

import com.winter.ioc.bean.ApplicationContext;
import com.winter.ioc.bean.DefaultListableBeanFactory;

public class SpringApplicationServer {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new DefaultListableBeanFactory("com.winter.test");

    }


}
