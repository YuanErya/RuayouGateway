package com.ruayou.core.disruptor;

public interface EventListener<E> {
    void onEvent(E event);

    void onException(Throwable e,long sequence,E event);
}
