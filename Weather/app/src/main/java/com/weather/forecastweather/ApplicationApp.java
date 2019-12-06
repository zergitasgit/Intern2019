package com.weather.forecastweather;

import android.app.Application;

import com.znitenda.ZAndroidSDK;

public class ApplicationApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ZAndroidSDK.initApplication(this,getPackageName());
    }
}
