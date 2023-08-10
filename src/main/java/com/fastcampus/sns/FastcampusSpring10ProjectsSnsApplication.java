package com.fastcampus.sns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class FastcampusSpring10ProjectsSnsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastcampusSpring10ProjectsSnsApplication.class, args);
    }

}
