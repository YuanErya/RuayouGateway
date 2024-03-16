package com.ruayou.httpserver;

import com.ruayou.client.annotation.RGService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()

public class HttpServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(HttpServerApplication.class, args);
    }

}
