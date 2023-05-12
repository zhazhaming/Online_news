package com.example.admin;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication//(exclude = {MongoAutoConfiguration.class})
@MapperScan("com.example.admin.mapper")
@ComponentScan({"com.example"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run (Application.class,args);
    }
}
