package com.guyuanguo.dribobo;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class DriboboApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
