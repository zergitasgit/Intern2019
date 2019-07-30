package com.example.checknetwork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CheckNetWork extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (checkNetWork(context)) {
            Intent intent1 = new Intent(context, HomeActivity.class);
            context.startActivity(intent1);
        }
    }

    boolean checkNetWork(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            return true;
        }
        return false;
    }
}
