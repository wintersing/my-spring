package com.winter;

import com.winter.ioc.ApplicationContext;
import com.winter.test.ioc.Student;
import com.winter.test.ioc.StudentService;
import com.winter.test.ioc.UserService;

public class SpringApplicationServer {
    public static void main(String[] args) {
        ApplicationContext springIocApplication = new ApplicationContext("com.winter.test");
        StudentService student = springIocApplication.getBean("studentService");
        student.studentInfo();

        UserService userService = springIocApplication.getBean("userService");
        userService.userInfo();
    }
}