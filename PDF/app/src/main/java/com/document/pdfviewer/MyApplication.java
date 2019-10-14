package com.document.pdfviewer;

import android.app.Application;

import com.znitenda.ZAndroidSDK;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ZAndroidSDK.initApplication(this, getPackageName());
    }
}
