package com.winter.test.ioc;

import com.winter.ioc.annotation.Autowired;
import com.winter.ioc.annotation.Component;

@Component
public class StudentService {

    @Autowired
    private UserService userService;

    public void studentInfo() {
        System.out.println(userService.getUserInfo());
    }

    public String getStudentInfo() {
        return "studentInfo...";
    }

}
