package com.os13musicapp.os13musicplayer;

import android.app.Application;

import com.znitenda.ZAndroidSDK;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ZAndroidSDK.initApplication(this, getPackageName());
    }
}
