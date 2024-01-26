package com.unsaladvance.loverswallpaper;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ArkaService extends Service {

    Thread thread;
    WallpaperManager wallpaperManager = null;
    public String link;
    DataSnapshot dsnapshot;
    FirebaseUser fuser;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wallpaperManager = WallpaperManager.getInstance(this);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(fuser.getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dsnapshot = snapshot;
                if (dsnapshot != null)
                    if(dsnapshot.hasChild("wallpaper")){
                        String yeni = dsnapshot.child("wallpaper").getValue(String.class);
                        String deger = dsnapshot.child("nereye").getValue(String.class);
                        dsnapshot.child("nereye").getRef().removeValue();
                        dsnapshot.child("foto_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                        dsnapshot.child("wallpaper").getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if(link != yeni) {
                                    new ResimIndir(getApplicationContext(), yeni, "LoversWallpaper", "LoversWallpaper_SentetToMe.jpg",deger);
                                    link = yeni;
                                }
                            }
                        });
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dsnapshot = snapshot;
                if (dsnapshot != null)
                    if(dsnapshot.hasChild("wallpaper")){
                        String yeni = dsnapshot.child("wallpaper").getValue(String.class);
                        String deger = dsnapshot.child("nereye").getValue(String.class);
                        dsnapshot.child("nereye").getRef().removeValue();
                        dsnapshot.child("foto_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                        dsnapshot.child("wallpaper").getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if(link != yeni) {
                                    new ResimIndir(getApplicationContext(), yeni, "LoversWallpaper", "LoversWallpaper_SentetToMe.jpg",deger);
                                    link = yeni;
                                }
                            }
                        });
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true){

                }
            }
        };
        thread = new Thread(runnable);
        thread.start();*/

        Intent broadcastIntent = new Intent(getApplicationContext(), KapatBroadcast.class);
        final PendingIntent pintent = PendingIntent.getBroadcast(getApplicationContext(), 0, broadcastIntent, PendingIntent.FLAG_MUTABLE);
        final String CHANNEL_ID = "Foreground";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this,CHANNEL_ID)
                //.setContentText("Yükleniyor..")
                .setContentTitle("Service Active")
                .addAction(R.drawable.icon, "Stop", pintent)
                .setSmallIcon(R.drawable.icon);

        startForeground(1001,notification.build());




        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}

