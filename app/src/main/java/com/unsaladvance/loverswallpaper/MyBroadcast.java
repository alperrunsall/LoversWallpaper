package com.unsaladvance.loverswallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent service = new Intent(context, ArkaService.class);
            context.startForegroundService(service);
        }
        /*else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            Intent service = new Intent(context, ArkaService.class);
            context.startForegroundService(service);
        }*/
        else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Intent service = new Intent(context, ArkaService.class);
            context.stopService(service);
        }
    }
}
