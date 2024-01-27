package com.ruayou.core.httpclient;

import com.ruayou.core.ContainerComponent;
import com.ruayou.core.LifeCycle;
import com.ruayou.core.helper.AsyncHttpHelper;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import lombok.extern.log4j.Log4j2;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;

import java.io.IOException;


/**
 * @Author：ruayou
 * @Date：2024/1/27 20:37
 * @Filename：AsyncHttpCoreClient
 */
@Log4j2
public class AsyncHttpCoreClient extends ContainerComponent implements LifeCycle {
    private AsyncHttpClient httpClient;
    private final HttpClientConfig config;
    private final EventLoopGroup worker;

    public AsyncHttpCoreClient(HttpClientConfig config, EventLoopGroup worker) {
        this.worker = worker;
        this.config = config;
        super.registerComponent(this);
    }

    @Override
    public void init() {
        // 创建异步HTTP客户端配置的构建器
        DefaultAsyncHttpClientConfig.Builder builder = new DefaultAsyncHttpClientConfig.Builder()
                .setEventLoopGroup(worker) // 使用传入的Netty事件循环组
                .setConnectTimeout(config.getHttpConnectTimeout()) // 连接超时设置
                .setRequestTimeout(config.getHttpRequestTimeout()) // 请求超时设置
                .setMaxRedirects(config.getHttpMaxRequestRetry()) // 最大重定向次数
                .setAllocator(PooledByteBufAllocator.DEFAULT) // 使用池化的ByteBuf分配器以提升性能
                .setCompressionEnforced(true) // 强制压缩
                .setMaxConnections(config.getHttpMaxConnections()) // 最大连接数
                .setMaxConnectionsPerHost(config.getHttpConnectionsPerHost()) // 每个主机的最大连接数
                .setPooledConnectionIdleTimeout(config.getHttpPooledConnectionIdleTimeout()); // 连接池中空闲连接的超时时间
        // 根据配置创建异步HTTP客户端
        this.httpClient = new DefaultAsyncHttpClient(builder.build());
    }

    @Override
    public void start() {
        AsyncHttpHelper.getInstance().initialized(httpClient);
    }

    @Override
    public void close() {
        if (httpClient != null) {
            try {
                this.httpClient.close();
            } catch (IOException e) {
                // 记录关闭时发生的错误
                log.error("AsyncHttpCoreClient close error", e);
            }
        }
    }
}
