package com.winter;

import com.winter.ioc.bean.DefaultListableBeanFactory;
import com.winter.ioc.annotation.Service;
import com.winter.test.ioc.StudentService;
import com.winter.test.ioc.UserService;

import java.lang.annotation.Annotation;

public class SpringApplicationServer {
    public static void main(String[] args) {
        DefaultListableBeanFactory springIocApplication = new DefaultListableBeanFactory("com.winter.test");
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