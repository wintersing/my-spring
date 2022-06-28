package com.winter.test.ioc;

import com.winter.ioc.annotation.Autowired;
import com.winter.ioc.annotation.Component;

@Component
public class UserService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    public void userInfo() {
        System.out.println(studentService.getStudentInfo());
        System.out.println(userService.getUserInfo());
    }

    public String getUserInfo() {
        return "userInfo...";
    }

}
