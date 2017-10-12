package com.homin.basic.data.net;

import android.widget.Toast;

import com.homin.basic.BaseApplication;
import com.homin.basic.utils.LogUtil;
import com.homin.basic.utils.NetworkUtil;

import rx.Subscriber;


/**
 * Created by Anthony on 2016/7/8.
 * Class Note:
 * {@link Subscriber} for {@link retrofit2.Retrofit},
 * add implementation to {@link #onCompleted()}and {@link #onError(Throwable)}
 * <p>
 * 继承自{@link Subscriber},添加对{@link #onCompleted()}和 {@link #onError(Throwable)}的实现。
 */
public abstract class HttpSubscriber<T> extends Subscriber<T> {
    protected boolean hasNetWork() {
        if (!NetworkUtil.isNetworkConnected(BaseApplication.getContext())) {
            Toast.makeText(BaseApplication.getContext(), "请连接网络或稍后重试...", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!hasNetWork()) {
            //无网络
        }
    }

    @Override
    public void onCompleted() {
        LogUtil.i("HttpHelper Subscriber On Completed");
    }

    @Override
    public void onError(Throwable e) {
        LogUtil.e("HttpHelper Subscriber On Error: " + e.toString());
    }
}
