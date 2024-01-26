package com.ruayou.core;

import com.ruayou.core.netty.NettyServerConfig;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;

/**
 * @Author：ruayou
 * @Date：2024/1/26 19:04
 * @Filename：ServerContainer
 */
@Log4j2
public class ServerContainer implements LifeCycle{
    private final NettyServerConfig config;

    private static final  ArrayList<LifeCycle> run=new ArrayList<>();
    public ServerContainer(NettyServerConfig config) {
        this.config = config;
        init();
    }
    @Override
    public void init() {

    }

    @Override
    public void start() {
        run.forEach((run)->{run.start();});
        log.info("RuayouGateway网关启动成功，正在监听端口：{}", this.config.getPort());
    }

    @Override
    public void close() {
        run.forEach((run)->{run.close();});
        log.info("========RuayouGateway已停止运行！========");
    }
}
