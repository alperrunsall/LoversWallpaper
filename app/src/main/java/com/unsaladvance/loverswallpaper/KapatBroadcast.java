package com.unsaladvance.loverswallpaper;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KapatBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentt = new Intent(context, ArkaService.class);
        //SuanBulunanlar.taramaAktifMi = false;
        MainActivity.servisKapandiMi = true;
        context.stopService(intentt);

    }
}