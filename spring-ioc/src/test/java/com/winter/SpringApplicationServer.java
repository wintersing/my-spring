package com.winter;

import com.winter.ioc.ApplicationContext;
import com.winter.test.ioc.Student;

public class SpringApplicationServer {
    public static void main(String[] args) {
        ApplicationContext springIocApplication = new ApplicationContext("com.winter.test");
        Student student = springIocApplication.getBean("student");
        System.out.println(student);
    }
}