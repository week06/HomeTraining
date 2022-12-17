package com.example.hometraing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class HometraingApplication {


    public static void main(String[] args) {

        SpringApplication.run(HometraingApplication.class, args);
        System.out.println("실행 완료~~~~~~");
    }

}
