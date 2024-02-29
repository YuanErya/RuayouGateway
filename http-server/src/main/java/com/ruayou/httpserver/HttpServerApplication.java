package com.ruayou.httpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ruayou")
public class HttpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpServerApplication.class, args);
    }

}
