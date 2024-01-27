package com.ruayou.core.netty;

import com.ruayou.core.LifeCycle;
import com.ruayou.core.netty.handler.HttpServerHandler;
import com.ruayou.core.netty.processor.HttpProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.log4j.Log4j2;

/**
 * @Author：ruayou
 * @Date：2024/1/26 20:26
 * @Filename：HttpServer
 * netty服务端,用于接收处理Http请求
 */
@Log4j2
public class NettyHttpServer implements LifeCycle {
    private final NettyServerConfig config;
    private ServerBootstrap bootstrap;
    private EventLoopGroup bossEventLoopGroup;

    public EventLoopGroup getWorkerEventLoopGroup() {
        return workerEventLoopGroup;
    }

    private EventLoopGroup workerEventLoopGroup;
    private final HttpProcessor processor;

    public NettyHttpServer(NettyServerConfig config,HttpProcessor processor) {
        this.config = config;
        this.processor=processor;
    }

    @Override
    public void init() {
        this.bossEventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("default-netty-boss-nio"));
        this.workerEventLoopGroup = new NioEventLoopGroup(config.getEventLoopGroupWorkerNum(), new DefaultThreadFactory("default-netty-worker-nio"));
        this.bootstrap = new ServerBootstrap()
                .group(bossEventLoopGroup, workerEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)            // TCP连接的最大队列长度
                .option(ChannelOption.SO_REUSEADDR, true)          // 允许端口重用
                .option(ChannelOption.SO_KEEPALIVE, true)          // 保持连接检测
                .childOption(ChannelOption.TCP_NODELAY, true)      // 禁用Nagle算法，适用于小数据即时传输
                .childOption(ChannelOption.SO_SNDBUF, 65535)       // 设置发送缓冲区大小
                .childOption(ChannelOption.SO_RCVBUF, 65535)       // 设置接收缓冲区大小
                .localAddress(config.getPort())
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new HttpServerCodec(),//解码出来的请求头和请求体是分离的，下面的处理器能合并完整的请求
                                new HttpObjectAggregator(config.getMaxContentLength()), // 聚合HTTP请求
                                new HttpServerExpectContinueHandler(), // 处理HTTP 100 Continue请求
                                new HttpServerHandler(processor)
                        );
                    }
                });
    }
    @Override
    public void start() {
        try {
            this.bootstrap.bind().sync();
            log.info("RuayouGateway-HttpServer启动成功，正在监听端口：{}", this.config.getPort());
        } catch (InterruptedException e) {
            throw new RuntimeException("启动服务器时发生异常", e);
        }
    }
    @Override
    public void close() {
        if(bossEventLoopGroup!=null)bossEventLoopGroup.shutdownGracefully();
        if(workerEventLoopGroup!=null)workerEventLoopGroup.shutdownGracefully();
    }
}
