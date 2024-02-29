package com.ruayou.core.disruptor;

/**
 * @Author：ruayou
 * @Date：2024/2/28 13:34
 * @Filename：ParallelQueue
 */
public interface IParallelQueue<E> {

    /**
     * 添加元素
     * @param event
     */
    void add(E event);
    void add(E... events);

    /**
     * 添加多个元素 返回是否添加成功的标志
     * @param event
     * @return
     */
    boolean tryAdd(E event);
    boolean tryAdd(E... events);

    /**
     * 启动
     */
    void start();

    /**
     * 销毁
     */
    void shutDown();
    /**
     * 判断是否已经销毁
     */
    boolean isShutDown();
}
