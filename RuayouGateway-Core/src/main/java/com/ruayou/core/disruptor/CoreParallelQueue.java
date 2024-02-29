package com.ruayou.core.disruptor;

import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author：ruayou
 * @Date：2024/2/28 13:37
 * @Filename：CoreParallelQueue
 */
public class CoreParallelQueue<E> implements IParallelQueue<E>{
    /**
     * 环形缓冲区 内部缓冲区存放我们的事件Holder类
     */
    private RingBuffer<Event> ringBuffer;

    /**
     * 事件监听器
     */
    private final EventListener<E> eventListener;

    /**
     * 工作线程池
     */
    private final WorkerPool<Event> worker;

    /**
     * 线程池
     */
    private final ExecutorService executorService;

    /**
     * Disruptor 框架中的一个接口，用于在事件发布（publish）时将数据填充到事件对象中
     */
    private final EventTranslatorOneArg<Event, E> eventTranslator;


    public CoreParallelQueue(Builder<E> builder){
        this.executorService = Executors.newFixedThreadPool(builder.threads,
                new ThreadFactoryBuilder().setNameFormat("ParallelQueueHandler" + builder.namePrefix + "-pool-%d").build());

        this.eventListener = builder.listener;
        this.eventTranslator = new EventTranslator();
        // 创建 RingBuffer
        this.ringBuffer = RingBuffer.create(builder.producerType, new CoreEventFactory(),
                builder.bufferSize, builder.waitStrategy);
        // 通过 RingBuffer 创建屏障 (固定流程）
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
        // 创建多个消费者组
        WorkHandler<Event>[] workHandlers = new WorkHandler[builder.threads];
        for (int i = 0; i < workHandlers.length; i++) {
            workHandlers[i] = new EventWorkHandler();
        }
        // 创建多消费者线程池
        WorkerPool<Event> workerPool = new WorkerPool<>(ringBuffer, sequenceBarrier, new EventExceptionHandler(),
                workHandlers);
        // 设置多消费者的 Sequence 序号，主要用于统计消费进度，
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        this.worker = workerPool;
    }
    @Override
    public void add(E event) {
        final RingBuffer<Event> holderRing = ringBuffer;
        if (holderRing == null) {
            process(this.eventListener, new IllegalStateException("ParallelQueueHandler is close"), event);
        }
        try {
            ringBuffer.publishEvent(this.eventTranslator, event);
        } catch (NullPointerException e) {
            process(this.eventListener, new IllegalStateException("ParallelQueueHandler is close"), event);
        }
    }

    @Override
    public void add(E... events) {
        final RingBuffer<Event> holderRing = ringBuffer;
        if (holderRing == null) {
            process(this.eventListener, new IllegalStateException("ParallelQueueHandler is close"), events);
        }
        try {
            ringBuffer.publishEvents(this.eventTranslator, events);
        } catch (NullPointerException e) {
            process(this.eventListener, new IllegalStateException("ParallelQueueHandler is close"), events);
        }
    }

    @Override
    public boolean tryAdd(E event) {
        final RingBuffer<Event> holderRing = ringBuffer;
        if (holderRing == null) {
            return false;
        }
        try {
            return ringBuffer.tryPublishEvent(this.eventTranslator, event);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public boolean tryAdd(E... events) {
        final RingBuffer<Event> holderRing = ringBuffer;
        if (holderRing == null) {
            return false;
        }
        try {
            return ringBuffer.tryPublishEvents(this.eventTranslator, events);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public void start() {
        this.ringBuffer = worker.start(executorService);
    }

    @Override
    public void shutDown() {
        RingBuffer<Event> holder = ringBuffer;
        ringBuffer = null;
        if (holder == null) {
            return;
        }
        if (worker != null) {
            worker.drainAndHalt();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public boolean isShutDown() {
        return ringBuffer == null;
    }

    private static <E> void process(EventListener<E> listener, Throwable e, E event) {
        listener.onException(e, -1, event);
    }

    private static <E> void process(EventListener<E> listener, Throwable e, E... events) {
        for (E event : events) {
            process(listener, e, event);
        }
    }


    /**
     * 事件对象
     */
    public class Event {
        /**
         * 事件
         */
        private E event;

        // 设置事件的值
        public void setValue(E event) {
            this.event = event;
        }

        // 重写 toString 方法，用于调试时打印事件信息
        @Override
        public String toString() {
            return "Event{" + "event=" + event + '}';
        }
    }



    public static class Builder<E> {
        /**
         * 生产者类型 默认使用多生产者类型
         */
        private ProducerType producerType = ProducerType.MULTI;
        /**
         * 线程队列大小
         */
        private int bufferSize = 1024 * 16;
        /**
         * 工作线程默认为1
         */
        private int threads = 1;
        /**
         * 前缀 定位模块
         */
        private String namePrefix = "";
        /**
         * 等待策略
         */
        private WaitStrategy waitStrategy = new BlockingWaitStrategy();
        /**
         * 监听器
         */
        private EventListener<E> listener;

        // 设置生产者类型，默认为多生产者类型
        public Builder<E> setProducerType(ProducerType producerType) {
            Preconditions.checkNotNull(producerType);
            this.producerType = producerType;
            return this;
        }

        // 设置线程队列大小，要求是2的幂次方
        public Builder<E> setBufferSize(int bufferSize) {
            Preconditions.checkArgument(Integer.bitCount(bufferSize) == 1);
            this.bufferSize = bufferSize;
            return this;
        }

        // 设置工作线程数
        public Builder<E> setThreads(int threads) {
            Preconditions.checkArgument(threads > 0);
            this.threads = threads;
            return this;
        }

        // 设置线程名前缀
        public Builder<E> setNamePrefix(String namePrefix) {
            Preconditions.checkNotNull(namePrefix);
            this.namePrefix = namePrefix;
            return this;
        }

        // 设置等待策略，默认为 BlockingWaitStrategy
        public Builder<E> setWaitStrategy(WaitStrategy waitStrategy) {
            Preconditions.checkNotNull(waitStrategy);
            this.waitStrategy = waitStrategy;
            return this;
        }

        // 设置事件监听器
        public Builder<E> setListener(EventListener<E> listener) {
            Preconditions.checkNotNull(listener);
            this.listener = listener;
            return this;
        }

        // 构建 ParallelQueueHandler 对象
        public CoreParallelQueue<E> build() {
            return new CoreParallelQueue<>(this);
        }
    }

    private class CoreEventFactory implements EventFactory<Event> {
        @Override
        public Event newInstance() {
            return new Event();
        }
    }

    private class EventTranslator implements EventTranslatorOneArg<Event, E> {
        @Override
        public void translateTo(Event event, long l, E e) {
            // 将事件数据填充到 Holder 对象中
            event.setValue(e);
        }
    }


    // 消费者工作处理器
    private class EventWorkHandler implements WorkHandler<Event> {
        @Override
        public void onEvent(Event event) throws Exception {
            // 调用事件监听器的处理事件方法
            eventListener.onEvent(event.event);
            // 处理完事件后，将事件置为空，帮助 GC 回收资源
            event.setValue(null);
        }
    }

    // 异常处理器
    private class EventExceptionHandler implements ExceptionHandler<Event> {

        @Override
        public void handleEventException(Throwable throwable, long l, Event event) {
            Event ev = (Event) event;
            try {
                eventListener.onException(throwable, l, ev.event);
            } catch (Exception e) {
                // 异常处理时出现异常的话，可以在这里进行额外的处理
            } finally {
                ev.setValue(null);
            }
        }

        @Override
        public void handleOnStartException(Throwable throwable) {
            throw new UnsupportedOperationException(throwable);
        }

        @Override
        public void handleOnShutdownException(Throwable throwable) {
            throw new UnsupportedOperationException(throwable);
        }
    }

}
