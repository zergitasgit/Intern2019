package com.os13lockscreen.ilockscreenos13;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.znitenda.ZAndroidSDK;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ZAndroidSDK.initApplication(this, getPackageName());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(getApplicationContext());
    }
}
