package com.ruayou.core.netty;

import com.ruayou.common.config.GlobalConfig;
import com.ruayou.core.LifeCycle;
import com.ruayou.common.config.NettyServerConfig;
import com.ruayou.core.netty.handler.HttpServerHandler;
import com.ruayou.core.netty.handler.NettyServerConnectManagerHandler;
import com.ruayou.core.netty.processor.HttpProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * @Author：ruayou
 * @Date：2024/1/26 20:26
 * @Filename：HttpServer netty服务端, 用于接收处理Http请求
 */
@Log4j2
public class NettyHttpServer implements LifeCycle {
    public static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private NettyServerConfig config;
    private ServerBootstrap bootstrap;
    private EventLoopGroup bossEventLoopGroup;

    @Getter
    private EventLoopGroup workerEventLoopGroup;

    private Channel channel;
    private final HttpProcessor processor;

    public NettyHttpServer(NettyServerConfig config, HttpProcessor processor) {
        this.config = config;
        this.processor = processor;
    }

    @Override
    public void init() {
        //是否使用Epoll模型
        if (useEpoll()) {
            this.bossEventLoopGroup = new EpollEventLoopGroup(1,
                    new DefaultThreadFactory("epoll-netty-boss-nio"));
            this.workerEventLoopGroup = new EpollEventLoopGroup(config.getEventLoopGroupWorkerNum(),
                    new DefaultThreadFactory("epoll-netty-worker-nio"));
        } else {
            this.bossEventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("netty-boss-nio"));
            this.workerEventLoopGroup = new NioEventLoopGroup(config.getEventLoopGroupWorkerNum(), new DefaultThreadFactory("netty-worker-nio"));
        }
        this.bootstrap = new ServerBootstrap()
                .group(bossEventLoopGroup, workerEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)            // TCP连接的最大队列长度
                .option(ChannelOption.SO_REUSEADDR, true)          // 允许端口重用
                .childOption(ChannelOption.SO_KEEPALIVE, true)          // 保持连接检测
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
                                new HttpServerHandler(processor),
                                new NettyServerConnectManagerHandler()
                        );
                    }
                });
    }
    public boolean useEpoll() {
        return OS_NAME.contains("linux") && Epoll.isAvailable();
    }
    @Override
    public void start() {
        try {
            this.channel = this.bootstrap.bind().sync().channel();
            log.info("RuayouGateway-HttpServer启动成功，正在监听端口：{}", config.getPort());
        } catch (InterruptedException e) {
            throw new RuntimeException("启动服务器时发生异常", e);
        }
    }

    @Override
    public void close() {
        if (bossEventLoopGroup != null) bossEventLoopGroup.shutdownGracefully();
        if (workerEventLoopGroup != null) workerEventLoopGroup.shutdownGracefully();
    }

    @Override
    public void restart() {
        channel.close();
        close();
        //重新载入配置文件重新启动
        this.config = GlobalConfig.getConfig().getNettyServerConfig();
        init();
        start();
    }
}
