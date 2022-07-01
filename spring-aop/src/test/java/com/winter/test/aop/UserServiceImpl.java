package com.winter.test.aop;

import com.winter.ioc.annotation.Autowired;
import com.winter.ioc.annotation.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IMessageService messageService;

    public UserServiceImpl() {
    }

    //    @Override
    public String sendMessage() {
        System.out.println("sendMessage");
        return "";
    }

}
