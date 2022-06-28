package com.winter.test.ioc;

import com.winter.ioc.annotation.Component;

@Component
public class IocDao {
    public void iocDao() {
        System.out.println("dao...");
    }
}
