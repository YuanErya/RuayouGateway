package com.ruayou.core.context;

import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

/**
 * @Author：ruayou
 * @Date：2024/1/26 20:24
 * @Filename：IContext
 */
public interface IContext {
    /**
     * 一个请求正在执行中的状态
     */
    int RUNNING = 0;
    /**
     * 标志请求结束，写回Response
     */
    int WRITTEN = 1;
    /**
     * 写回成功后，设置该标识，如果是Netty ，ctx.WriteAndFlush(response)
     */
    int COMPLETED = 2;
    /**
     * 整个网关请求完毕，彻底结束
     */
    int TERMINATED = -1;

    /**
     * 设置上下文状态为正常运行状态
     */
    void running();

    /**
     * 设置上下文状态为标记写回
     */
    void written();
    /**
     * 设置上下文状态为标记写回成功
     */
    void completed();
    /**
     * 设置上下文状态为标记写回成功
     */
    void terminated();

    /**
     *获取当前状态
     */
    int getStatus();

    /**
     * 判断网关状态运行状态
     * @return
     */
    boolean isRunning();
    boolean isWritten();
    boolean isCompleted();
    boolean isTerminated();


    /**
     * 获取请求结果
     * @return
     */
    Object getResponse();

    /**
     * 获取Netty上下文
     *
     * @return
     */
    ChannelHandlerContext getNettyCtx();


    /**
     * 是否保持连接
     * @return
     */
    boolean isKeepAlive();
    /**
     * 释放资源
     */
    void releaseRequest();

    /**
     * 设置回调函数
     * @param consumer
     */
    void setCompletedCallBack(Consumer<IContext> consumer);

    /**
     * 设置回调函数
     */
    void invokeCompletedCallBack();
}
