package com.winter.test.ioc;

import com.winter.ioc.annotation.Bean;
import com.winter.ioc.annotation.Configuration;
import com.winter.ioc.annotation.Import;

@Configuration
@Import(Student.class)
public class StudentConfig {

    /*@Bean
    public Student student() {
        return new Student();
    }*/

}
