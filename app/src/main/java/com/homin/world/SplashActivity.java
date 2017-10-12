package com.homin.world;

import android.Manifest;
import android.os.Bundle;

import com.homin.basic.base.AbsBaseActivity;
import com.homin.basic.utils.RxUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * Created by Administrator on 2017/10/12.
 */
public class SplashActivity extends AbsBaseActivity{
    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .compose(RxPermissions.getInstance(this).ensureEach(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE))
                .compose(RxUtils.defaultSchedulers())
                .subscribe(permission -> {
                    if (permission.granted) {
                        startActivity(MainActivity.class);
                        finish();
                    }
                });
    }

    @Override
    protected int getContentViewID() {
        return R.layout.prj_activity_flash;
    }
}
