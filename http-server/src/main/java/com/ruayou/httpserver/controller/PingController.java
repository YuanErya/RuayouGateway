package com.ruayou.httpserver.controller;

import com.ruayou.client.annotation.RGService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：ruayou
 * @Date：2024/2/29 12:53
 * @Filename：PingController
 */
@RestController
@RGService(serviceId = "ping-http-server",patternPath = {"/ping/*"})
public class PingController {
    @GetMapping("/ping/nb")
    public static String pingNB(){
        return "pong-nb-HttpServer";
    }

    @PostMapping("/ping/mb")
    public static String pingMB(){
        return "pong-mb-HttpServer";
    }

    @PostMapping("/ping/mb/nb")
    public static String pingMBNB(){
        return "pong-mbnb-HttpServer";
    }

}
