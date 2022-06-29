package com.winter;

import com.winter.ioc.ApplicationContext;
import com.winter.ioc.annotation.Component;
import com.winter.ioc.annotation.Controller;
import com.winter.ioc.annotation.Service;
import com.winter.test.ioc.Student;
import com.winter.test.ioc.StudentService;
import com.winter.test.ioc.UserService;

import java.lang.annotation.Annotation;

public class SpringApplicationServer {
    public static void main(String[] args) {
        ApplicationContext springIocApplication = new ApplicationContext("com.winter.test");
        StudentService student = springIocApplication.getBean("studentService");
        student.studentInfo();

        UserService userService = springIocApplication.getBean("userService");
        userService.userInfo();


        StudentService component = new StudentService();
        Annotation[] annotations = component.getClass().getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println(annotation instanceof Service);
        }
    }
}