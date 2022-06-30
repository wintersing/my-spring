package com.winter;

import com.winter.ioc.bean.ApplicationContext;
import com.winter.ioc.bean.DefaultListableBeanFactory;
import com.winter.test.ioc.StudentService;
import com.winter.test.ioc.UserService;


public class SpringApplicationServer {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new DefaultListableBeanFactory("com.winter.test");
        StudentService student = applicationContext.getBean("studentService");
        student.studentInfo();

        UserService userService = applicationContext.getBean("userService");
        userService.userInfo();
    }
}