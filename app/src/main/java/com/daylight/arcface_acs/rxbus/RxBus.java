package com.daylight.arcface_acs.rxbus;

import java.util.HashMap;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.SerializedSubscriber;

class RxBus {
    private final FlowableProcessor<Object> mBus;
    private static volatile RxBus sRxBus = null;

    private RxBus() {
        mBus = PublishProcessor.create().toSerialized();
    }

    static synchronized RxBus getDefault() {
        if (sRxBus == null) {
            synchronized (RxBus.class) {
                if (sRxBus == null) {
                    sRxBus = new RxBus();
                }
            }
        }
        return sRxBus;
    }

    /**
     * 发送消息
     */
    void post(Object o) {
        new SerializedSubscriber<>(mBus).onNext(o);
    }

    /**
     * 确定接收消息的类型
     */
    <T> Flowable<T> toFlowable(Class<T> aClass) {
        return mBus.ofType(aClass);
    }

    /**
     * 判断是否有订阅者
     */
    boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }

    void release()
    {
        sRxBus = null;
    }
}