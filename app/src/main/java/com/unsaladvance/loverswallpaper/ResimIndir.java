package com.unsaladvance.loverswallpaper;


import static android.content.Context.MODE_PRIVATE;

import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;


public class ResimIndir {
    private Context mContext;
    private ImageView normalView;
    private String url, dosyaParent, fotoAdi;
    private String lockdeger;

    public ResimIndir(Context mContext, String url, String dosyaParent, String fotoAdi, String lockdeger) {
        this.mContext = mContext;
        this.url = url;
        this.fotoAdi = fotoAdi;
        this.normalView = normalView;
        this.dosyaParent = dosyaParent;
        this.lockdeger = lockdeger;

        new downloadImage().execute(url);
    }

    public class downloadImage extends AsyncTask<String, Void, Bitmap> {

        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(mContext, result, fotoAdi);
        }
        public void saveImage(Context context, Bitmap b, String imageName) {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "LoversWallpaper_ISent");
            //values.put(MediaStore.Images.Media.DESCRIPTION, description);
            Uri imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            try {
                // OutputStream kullanarak bitmap'i dosyaya yazma
                OutputStream outputStream = context.getContentResolver().openOutputStream(imageUri);
                b.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            MainActivity.Degistir(lockdeger, imageUri);
        }
    }

}
