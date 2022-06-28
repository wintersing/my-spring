package com.winter.test.ioc;

import com.winter.ioc.annotation.Autowired;
import com.winter.ioc.annotation.Component;

@Component
public class IocService {
    @Autowired
    private IocDao iocDao;
    public void iocService() {
        System.out.println("service...");
        iocDao.iocDao();
    }
}
