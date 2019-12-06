package com.swipe.navigationbar;

import android.app.Application;
import android.util.Log;

import com.znitenda.ZAndroidSDK;

public class ApplicationApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("DEBUG","oncreate");
        ZAndroidSDK.initApplication(this, getPackageName());
    }
}
