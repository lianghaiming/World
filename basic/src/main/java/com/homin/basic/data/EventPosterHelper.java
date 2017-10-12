package com.homin.basic.data;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by Anthony on 2016/6/12.
 * Class Note:
 * Provides helper methods to post event to an Otto event bus
 * used only by{@link BaseDataRepository} is recommended
 * 提供Otto事件总线的帮助类,推荐只在{@link BaseDataRepository}中使用
 */
public class EventPosterHelper {
    private final Bus mBus;



    public EventPosterHelper(Bus bus) {
        mBus = bus;
    }

    public Bus getBus() {
        return mBus;
    }

    /**
     * Helper method to post an event from a different thread to the main one.
     */
    public void postEventSafely(final Object event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mBus.post(event);
            }
        });
    }
}
