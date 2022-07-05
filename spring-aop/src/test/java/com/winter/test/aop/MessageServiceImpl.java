package com.winter.test.aop;

import com.winter.ioc.annotation.Autowired;
import com.winter.ioc.annotation.Bean;
import com.winter.ioc.annotation.Service;

import javax.annotation.PostConstruct;

@Service("messageServiceImpl")
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private IUserService userService;

    @PostConstruct
    public void init() {
        System.out.println("执行init方法");
    }

    public MessageServiceImpl() {
    }

    @Override
    public String getMessage() {
        System.out.println("getMessage");
        return "";
    }

    @Bean
    public MessageInfo registerMessageInfo() {
        return new MessageInfo(1, "messageInfo.....");
    }
}
