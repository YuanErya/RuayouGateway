package com.ruayou.core;

import com.ruayou.core.httpclient.HttpClientConfig;
import com.ruayou.core.netty.NettyServerConfig;
import com.ruayou.core.netty.processor.HttpServerCoreProcessor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：ruayou
 * @Date：2024/1/26 19:04
 * @Filename：ServerContainer
 */
@Log4j2
public class ServerContainer implements LifeCycle{
    private final NettyServerConfig nettyServerconfig;
    private final HttpClientConfig httpClientConfig;

    private static final  ArrayList<LifeCycle> run=new ArrayList<>();
    public ServerContainer(NettyServerConfig nettyServerconfig, HttpClientConfig httpClientConfig) {
        this.nettyServerconfig = nettyServerconfig;
        this.httpClientConfig=httpClientConfig;
        init();
    }
    @Override
    public void init() {
        HttpServerCoreProcessor coreProcessor=new HttpServerCoreProcessor();

    }

    @Override
    public void start() {
        run.forEach((run)->{run.start();});
        log.info("RuayouGateway网关启动成功，正在监听端口：{}", this.nettyServerconfig.getPort());
    }

    @Override
    public void close() {
        run.forEach((run)->{run.close();});
        log.info("========RuayouGateway已停止运行！========");
    }
    public static void addComponent(LifeCycle component){
        run.add(component);
    }
    public static List<LifeCycle> getComponents(){
        return run;
    }
}
