package com.iuh.stream.datalocal;

import android.app.Application;

import com.iuh.stream.utils.Util;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataLocalManager.init(getApplicationContext());

    }
}
