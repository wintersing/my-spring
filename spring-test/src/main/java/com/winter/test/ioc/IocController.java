package com.winter.test.ioc;

import com.winter.ioc.annotation.Autowired;
import com.winter.ioc.annotation.Controller;

@Controller
public class IocController {

    @Autowired
    private IocService iocService;

    public void iocController() {
        System.out.println("controller....");
        iocService.iocService();
    }
}
