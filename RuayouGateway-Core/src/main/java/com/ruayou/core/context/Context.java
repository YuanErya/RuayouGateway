package com.ruayou.core.context;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @Author：ruayou
 * @Date：2024/1/26 23:36
 * @Filename：Context
 */
public class Context implements IContext {
    /**
     * 转发协议
     */
    protected final String protocol;

    /**
     * 上下文状态
     */
    protected volatile int status = IContext.RUNNING;

    /**
     * Netty上下文
     */
    protected final ChannelHandlerContext nettyCtx;


    /**
     * 上下文参数集合
     */
    protected final Map<String, Object> attributes = new HashMap<String, Object>();

    /**
     * 请求过程中发生的异常
     */
    protected Throwable throwable;
    /**
     * 是否保持长连接
     */
    protected final boolean keepAlive;

    /**
     * 是否已经释放资源
     */
    protected final AtomicBoolean requestReleased = new AtomicBoolean(false);
    /**
     * 存放回调函数的集合
     */
    protected List<Consumer<IContext>> completedCallbacks;


    public Context(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive) {
        this.protocol = protocol;
        this.nettyCtx = nettyCtx;
        this.keepAlive = keepAlive;
    }

    @Override
    public void running() {
        status = IContext.RUNNING;
    }

    @Override
    public void written() {
        status = IContext.WRITTEN;
    }

    @Override
    public void completed() {
        status = IContext.COMPLETED;
    }

    @Override
    public void terminated() {
        status = IContext.TERMINATED;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public boolean isRunning() {
        return status == IContext.RUNNING;
    }

    @Override
    public boolean isWritten() {
        return  status == IContext.WRITTEN;
    }

    @Override
    public boolean isCompleted() {
        return status == IContext.COMPLETED;
    }

    @Override
    public boolean isTerminated() {
        return status == IContext.TERMINATED;
    }



    public Throwable getThrowable() {
        return this.throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public void setAttribute(String key,Object obj) {
        attributes.put(key,obj);
    }
    public Object getAttribute(String key) {
        return attributes.get(key);
    }


    public ChannelHandlerContext getNettyCtx() {
        return this.nettyCtx;
    }


    public boolean isKeepAlive() {
        return this.keepAlive;
    }

    public void releaseRequest() {
        this.requestReleased.compareAndSet(false,true);
    }

    public void setCompletedCallBack(Consumer<IContext> consumer) {
        if(completedCallbacks == null){
            completedCallbacks = new ArrayList<>();
        }
        completedCallbacks.add(consumer);
    }

    public void invokeCompletedCallBack() {
        if(completedCallbacks == null){
            completedCallbacks.forEach(call->call.accept(this));
        }
    }
}
