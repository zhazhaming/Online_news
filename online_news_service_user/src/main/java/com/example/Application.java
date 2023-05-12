package com.example;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@MapperScan("com.example.user.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run (Application.class,args);
    }
}
