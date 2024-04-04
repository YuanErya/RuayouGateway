package com.ruayou.core.netty.processor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import com.ruayou.common.config.DisruptorConfig;
import com.ruayou.common.enums.ResponseCode;
import com.ruayou.core.context.HttpRequestWrapper;
import com.ruayou.core.disruptor.CoreParallelQueue;
import com.ruayou.core.disruptor.EventListener;
import com.ruayou.core.helper.ResponseHelper;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DisruptorHttpServerProcessor implements HttpProcessor{
    private static final String THREAD_NAME_PREFIX = "gateway-queue-";

    private HttpServerCoreProcessor processor;

    private CoreParallelQueue<HttpRequestWrapper>  queue;

    public DisruptorHttpServerProcessor(DisruptorConfig config, HttpServerCoreProcessor processor){
        this.processor=processor;

        CoreParallelQueue.Builder<HttpRequestWrapper> builder = new CoreParallelQueue.Builder<HttpRequestWrapper>()
                .setBufferSize(config.getBufferSize())
                .setThreads(config.getProcessThread())
                .setProducerType(ProducerType.MULTI)
                .setNamePrefix(THREAD_NAME_PREFIX)
                .setWaitStrategy(new BlockingWaitStrategy());
        BatchEventListenerProcessor batchEventListenerProcessor = new BatchEventListenerProcessor();
        builder.setListener(batchEventListenerProcessor);
        this.queue = builder.build();

    }
    @Override
    public void init() {

    }

    @Override
    public void start() {
        queue.start();
    }

    @Override
    public void close() {
queue.shutDown();
    }

    @Override
    public void restart() {

    }

    @Override
    public void process(HttpRequestWrapper httpRequestWrapper) {
this.queue.add(httpRequestWrapper);
    }


    /**
     * 监听处理类，处理从 Disruptor 处理队列中取出的事件。
     */
    public class BatchEventListenerProcessor implements EventListener<HttpRequestWrapper> {

        @Override
        public void onEvent(HttpRequestWrapper event) {
            // 使用 Netty 核心处理器处理事件。
            processor.process(event);
        }

        @Override
        public void onException(Throwable ex, long sequence, HttpRequestWrapper event) {
            HttpRequest request = event.getRequest();
            ChannelHandlerContext ctx = event.getCtx();

            try {
                log.error("BatchEventListenerProcessor onException 请求写回失败，request:{}, errMsg:{} ", request, ex.getMessage(), ex);
                // 构建响应对象
                FullHttpResponse fullHttpResponse = ResponseHelper.getHttpResponse(ResponseCode.INTERNAL_ERROR);

                if (!HttpUtil.isKeepAlive(request)) {
                    ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
                } else {
                    fullHttpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    ctx.writeAndFlush(fullHttpResponse);
                }
            } catch (Exception e) {
                log.error("BatchEventListenerProcessor onException 请求写回失败，request:{}, errMsg:{} ", request, e.getMessage(), e);
            }
        }
    }
}
