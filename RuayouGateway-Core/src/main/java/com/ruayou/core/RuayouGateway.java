package com.ruayou.core;


/**
 * @Author：ruayou
 * @Date：2024/1/2 18:34
 * @Filename：Application
 * 程序入口
 */
public class RuayouGateway {
    public static void main(String[] args) {
        ServerContainer container = new ServerContainer();
        container.start();
    }
}
