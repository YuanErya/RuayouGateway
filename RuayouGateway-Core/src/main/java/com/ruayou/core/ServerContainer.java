package com.ruayou.core;

import com.ruayou.core.httpclient.AsyncHttpCoreClient;
import com.ruayou.core.httpclient.HttpClientConfig;
import com.ruayou.core.netty.NettyHttpServer;
import com.ruayou.core.netty.NettyServerConfig;
import com.ruayou.core.netty.processor.HttpProcessor;
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
    private HttpProcessor processor;
    boolean initFlag=false;

    private static final  ArrayList<LifeCycle> run=new ArrayList<>();
    public ServerContainer(NettyServerConfig nettyServerconfig, HttpClientConfig httpClientConfig) {
        this.nettyServerconfig = nettyServerconfig;
        this.httpClientConfig=httpClientConfig;
    }
    @Override
    public void init() {
        if (initFlag) {
            return;
        }
        HttpServerCoreProcessor coreProcessor=new HttpServerCoreProcessor();
        this.processor=coreProcessor;
        run.add(processor);
        NettyHttpServer server = new NettyHttpServer(nettyServerconfig, processor);
        run.add(server);
        run.add(new AsyncHttpCoreClient(httpClientConfig,server.getWorkerEventLoopGroup()));
        run.forEach(LifeCycle::init);
    }

    @Override
    public void start() {
        if (!initFlag) {
            init();
        }
        run.forEach(LifeCycle::start);
        log.info("RuayouGateway网关启动成功，正在监听端口：{}", this.nettyServerconfig.getPort());
    }

    @Override
    public void close() {
        run.forEach(LifeCycle::close);
        log.info("========RuayouGateway已停止运行！========");
    }
    public static void addComponent(LifeCycle component){
        run.add(component);
    }

    public static List<LifeCycle> getComponents(){
        return run;
    }
}
