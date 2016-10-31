package com.albumselector.album.rxbus;

import rx.Subscriber;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/7/22 下午2:40
 */
public abstract class RxBusSubscriber<T> extends Subscriber<T> {

    @Override
    public void onNext(T t) {
        try {
            onEvent(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
    }

    protected abstract void onEvent(T t) throws Exception;
}