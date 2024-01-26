package com.unsaladvance.loverswallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    static Context mContext;
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    FirebaseUser fuser;
    RelativeLayout kamera, galeri, hayir, evet,takeSS,takeSSAsil,yenile;
    LinearLayout foto_edit, anasayfa, istekLayout, username_belirle, istekgonder,istek_kabul_edildi;
    EditText karsiId, benimId, yorum;
    TextView istek, yazi, yaziAsil, kimoo;
    ImageView resim,resimAsil;
    File dosya;
    Uri dosya2;
    Spinner renk_spinner,size_spinner,font_spinner;
    static String benimid, baglantiid, baglantifuserid;
    CheckBox lockscreenCB, wallpaperCB;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    boolean wall, lock, galeriden;
    Button publish;
    ProgressBar pbar;
    byte[] bitmapdata;
    private Bitmap resimbitmap;
    int screenWidth, screenHeight, onun_screenWidth, onun_screenHeight;
    public static boolean servisKapandiMi;
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        servisKapandiMi = false;
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        pref = this.getPreferences(MODE_PRIVATE);
        edit = pref.edit();

        kamera = findViewById(R.id.kamera);
        galeri = findViewById(R.id.galeri);
        anasayfa = findViewById(R.id.anasayfa);
        foto_edit = findViewById(R.id.foto_edit);
        istekLayout = findViewById(R.id.istekvarlayout);
        istek = findViewById(R.id.istek);
        hayir = findViewById(R.id.hayir);
        evet = findViewById(R.id.evet);
        pbar = findViewById(R.id.pbar);
        kimoo = findViewById(R.id.kimoo);
        username_belirle = findViewById(R.id.usernameOnayLay);
        istekgonder = findViewById(R.id.istekGonderLay);
        istek_kabul_edildi = findViewById(R.id.istek_kabul_edildi);
        takeSS = findViewById(R.id.takeSS);
        takeSS.setDrawingCacheEnabled(true);
        takeSSAsil = findViewById(R.id.takeSSAsil);
        takeSSAsil.setDrawingCacheEnabled(true);
        yazi = findViewById(R.id.yazi);
        yaziAsil = findViewById(R.id.yaziAsil);
        karsiId = findViewById(R.id.baglan_id);
        benimId = findViewById(R.id.benim_id);
        resim = findViewById(R.id.resim);
        resimAsil = findViewById(R.id.resimAsil);
        lockscreenCB = findViewById(R.id.lockscreenCB);
        wallpaperCB = findViewById(R.id.wallpaperCB);
        yorum = findViewById(R.id.yorum);
        font_spinner = findViewById(R.id.font_spinner);
        renk_spinner = findViewById(R.id.renk_spinner);
        size_spinner = findViewById(R.id.size_spinner);
        publish = findViewById(R.id.publish);
        yenile = findViewById(R.id.yenile);

        kimoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.instagram.com/kimooapp/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        wallpaperCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                wall = b;
            }
        });
        lockscreenCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                lock = b;
            }
        });

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,R.array.font,  R.layout.spinner_view);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        font_spinner.setAdapter(adapter3);
        font_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Typeface type = ResourcesCompat.getFont(getApplicationContext(), R.font.burbin);
                if(i == 0)
                    type = ResourcesCompat.getFont(getApplicationContext(), R.font.burbin);
                else if(i == 1)
                    type = ResourcesCompat.getFont(getApplicationContext(), R.font.alphabit);
                else if(i == 2)
                    type = ResourcesCompat.getFont(getApplicationContext(), R.font.kazy);
                else if(i == 3)
                    type = ResourcesCompat.getFont(getApplicationContext(), R.font.kitten);
                else if(i == 4)
                    type = ResourcesCompat.getFont(getApplicationContext(), R.font.krishart);
                else if(i == 5)
                    type = ResourcesCompat.getFont(getApplicationContext(), R.font.mulled);
                else if(i == 6)
                    type = ResourcesCompat.getFont(getApplicationContext(), R.font.pulang);
                else if(i == 7)
                    type = ResourcesCompat.getFont(getApplicationContext(), R.font.rans);
                yazi.setTypeface(type);
                yaziAsil.setTypeface(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.size, R.layout.spinner_view);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        size_spinner.setAdapter(adapter2);
        size_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0)
                    yazi.setTextSize(20);
                else if(i == 1)
                    yazi.setTextSize(25);
                else if(i == 2)
                    yazi.setTextSize(30);
                else if(i == 3)
                    yazi.setTextSize(45);
                else if(i == 4)
                    yazi.setTextSize(45);
                else if(i == 5)
                    yazi.setTextSize(50);
                else if(i == 6)
                    yazi.setTextSize(55);
                else if(i == 7)
                    yazi.setTextSize(60);
                else if(i == 8)
                    yazi.setTextSize(65);
                yaziAsil.setTextSize(yazi.getTextSize());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.renkler,  R.layout.spinner_view);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        renk_spinner.setAdapter(adapter);
        renk_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0)
                    yazi.setTextColor(Color.BLACK);
                else if(i == 1)
                    yazi.setTextColor(Color.WHITE);
                else if(i == 2)
                    yazi.setTextColor(Color.BLUE);
                else if(i == 3)
                    yazi.setTextColor(Color.RED);
                else if(i == 4)
                    yazi.setTextColor(Color.GREEN);
                else if(i == 5)
                    yazi.setTextColor(Color.YELLOW);
                else if(i == 6)
                    yazi.setTextColor(Color.MAGENTA);
                yaziAsil.setTextColor(yazi.getCurrentTextColor());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resimbitmap = screenShot(takeSS);
                saveBitMap(resimbitmap);
                if(lock || wall){
                    pbar.setVisibility(View.VISIBLE);
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(fuser.getUid()).child("wallpaper.jpeg");
                    InputStream stream = null;
                    try {
                        stream = new FileInputStream(dosya);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(!galeriden) {
                        if (stream != null) {
                            InputStream finalStream = stream;
                            FirebaseDatabase.getInstance().getReference("Kullanicilar").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int sayac = 0, yoksayac = 0;
                                    for(DataSnapshot ds: snapshot.getChildren()){
                                        if(ds.getChildrenCount() > 0) {
                                            sayac++;
                                            if (ds.child("benim_id").getValue(String.class).equals(baglantiid)) {
                                                mStorageRef.putStream(finalStream).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    int deger = 0;
                                                                    if (lock && wall)
                                                                        deger = 1;
                                                                    else if (wall)
                                                                        deger = 2;
                                                                    else
                                                                        deger = 3;
                                                                    FirebaseDatabase.getInstance().getReference("Kullanicilar").child(baglantifuserid)
                                                                            .child("nereye").setValue("" + deger);
                                                                    FirebaseDatabase.getInstance().getReference("Kullanicilar").child(baglantifuserid)
                                                                            .child("foto_zaman").setValue(ServerValue.TIMESTAMP);
                                                                    FirebaseDatabase.getInstance().getReference("Kullanicilar").child(baglantifuserid)
                                                                            .child("wallpaper").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            Toast toast = Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT);
                                                                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                                            toast.show();
                                                                            pbar.setVisibility(View.GONE);
                                                                            anasayfa.setVisibility(View.VISIBLE);
                                                                            resim.setImageDrawable(null);
                                                                            yorum.setText("");
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        } else
                                                            Toast.makeText(MainActivity.this, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            } else
                                                yoksayac++;

                                            if (sayac == snapshot.getChildrenCount() - 1)
                                                if (sayac == yoksayac) {
                                                    //Toast.makeText(MainActivity.this, "Your partner was not found", Toast.LENGTH_SHORT).show();
                                                    pbar.setVisibility(View.GONE);
                                                }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
                else {
                    Toast toast= Toast.makeText(getApplicationContext(), "Please select wallpaper or lockscreen", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
        });
        pref = getPreferences(MODE_PRIVATE);
        //Iziniste();
        SharedPreferencesGetir();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Kullanicilar");
        yenile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yazi.setX(0);
                yazi.setY(0);
                yaziAsil.setX(0);
                yaziAsil.setY(0);
            }
        });
        yazi.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    v.setX(event.getRawX() - v.getWidth() / 2.0f);
                    v.setY(event.getRawY() - v.getHeight() / 2.0f);
                    yaziAsil.setX(event.getRawX() - v.getWidth() / 2.0f);
                    yaziAsil.setY(event.getRawY() - v.getHeight() / 2.0f);
                }

                return true;
            }

        });
        yorum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                yorum.setHint(editable.toString());
                yazi.setText(editable.toString());
                yaziAsil.setText(editable.toString());
            }
        });

        benimId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (benimid != null)
                        benimId.setError("if you change your username your partner will lose you!");
                    if (!benimId.getText().toString().equals("")) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("isim", benimId.getText().toString());
                        clipboard.setPrimaryClip(clip);
                        Toast toast= Toast.makeText(getApplicationContext(), "You copied username: " + benimId.getText().toString(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                }
            }
        });
        karsiId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().length() < 5){
                    karsiId.setError("Too short!");
                }
                else{
                    FirebaseDatabase.getInstance().getReference("Kullanicilar").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds : snapshot.getChildren()){
                                if(ds.hasChild("benim_id")){
                                    if(!karsiId.getText().toString().equals(benimid) && !karsiId.getText().toString().equals(baglantiid)){
                                        if(ds.child("benim_id").getValue(String.class).equals(karsiId.getText().toString())){
                                            istekgonder.setVisibility(View.VISIBLE);
                                            istekgonder.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    ds.getRef().child("istekler").child(fuser.getUid()).setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast toast= Toast.makeText(getApplicationContext(), "You sent request to: " + karsiId.getText().toString(), Toast.LENGTH_SHORT);
                                                            toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
                                                            toast.show();
                                                            istekgonder.setVisibility(View.INVISIBLE);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
        benimId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //benimId.setText(benimId.getText().toString().trim().toLowerCase());
                if(editable.toString().length() < 5){
                    benimId.setError("Too short!");
                }
                else{
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long toplamChildCount = snapshot.getChildrenCount();
                            long kontrolChildCount = 0;
                            for(DataSnapshot ds : snapshot.getChildren()){
                                if (ds.hasChild("benim_id")) {
                                    if(!ds.getKey().equals(fuser.getUid())) {
                                        if (benimId.getText().toString().equals(ds.child("benim_id").getValue(String.class))) {
                                            benimId.setError("Change different username!");
                                            username_belirle.setVisibility(View.INVISIBLE);
                                        }
                                        else {
                                            kontrolChildCount++;
                                        }
                                    }
                                    else
                                        kontrolChildCount++;
                                }
                                else {
                                    kontrolChildCount++;
                                }
                                if (kontrolChildCount == toplamChildCount) {
                                    if(!benimId.getText().toString().equals(benimid)) {
                                        benimId.setError(null);
                                        username_belirle.setVisibility(View.VISIBLE);
                                        username_belirle.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                                int height = displayMetrics.heightPixels;
                                                int width = displayMetrics.widthPixels;
                                                ref.child(fuser.getUid()).child("benim_ekranx").getRef().setValue("" + width);
                                                ref.child(fuser.getUid()).child("benim_ekrany").getRef().setValue("" + height);
                                                ref.child(fuser.getUid()).child("baglantim").setValue("");
                                                ref.child(fuser.getUid()).child("benim_id").setValue(benimId.getText().toString().toLowerCase(Locale.ROOT).trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                        karsiId.setEnabled(true);
                                                        Toast toast = Toast.makeText(getApplicationContext(), "You changed your username " + benimId.getText().toString().toLowerCase(Locale.ROOT).trim(), Toast.LENGTH_SHORT);
                                                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                        toast.show();
                                                        edit.putString("isim", benimId.getText().toString().toLowerCase(Locale.ROOT).trim());
                                                        edit.commit();
                                                        SharedPreferencesGetir();
                                                        username_belirle.setVisibility(View.INVISIBLE);
                                                        benimId.setError(null);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });


        resim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentGalley = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentGalley.setType("image/*");
                intentGalley.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentGalley, "Select Picture"), 2);
            }
        });
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
        }

        galeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //galeriden = true;

                if(benimid != null) {
                    if (baglantiid != null) {

                            Intent intentGalley = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intentGalley.setType("image/*");
                            intentGalley.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intentGalley, "Select Picture"), 2);


                    }
                    else{
                        Toast toast= Toast.makeText(getApplicationContext(), "Write the username of the person you want to connect to", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                }
                else{
                    Toast toast= Toast.makeText(getApplicationContext(), "Your username is null", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }

            }
        });
        kamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galeriden = false;
                if(benimid != null) {
                    if (baglantiid != null) {

                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                        } else {

                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    }
                    else{
                        Toast toast= Toast.makeText(getApplicationContext(), "Write the username of the person you want to connect to", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                }
                else{
                    Toast toast= Toast.makeText(getApplicationContext(), "Your username is null", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(fuser == null){
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        fuser = FirebaseAuth.getInstance().getCurrentUser();
                        IstekKontrol();
                    }
                    else
                        Toast.makeText(MainActivity.this, "Sorry, there is a problem.", Toast.LENGTH_SHORT).show();
                }
            });

        }
        else
        {
            BaglantimiGetir();
        }
    }



    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if(anasayfa.getVisibility() != View.VISIBLE){
            anasayfa.setVisibility(View.VISIBLE);
        }
        else{
            finishAffinity();
        }
    }

    private void BaglantimiGetir() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Kullanicilar");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(fuser.getUid()))
                    if(!snapshot.child(fuser.getUid()).child("baglantim").getValue(String.class).equals("")) {
                        FirebaseDatabase.getInstance().getReference("Kullanicilar").child(snapshot.child(fuser.getUid()).child("baglantim").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot2) {

                                edit.putString("baglantifuserid", snapshot2.getKey());
                                edit.putString("baglantiid", snapshot2.child("benim_id").getValue(String.class));
                                edit.putInt("onun_ekranx",Integer.parseInt(snapshot2.child("benim_ekranx").getValue(String.class)));
                                edit.putInt("onun_ekrany",Integer.parseInt(snapshot2.child("benim_ekrany").getValue(String.class)));
                                edit.commit();
                                SharedPreferencesGetir();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                IstekKontrol();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Iziniste() {

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        else {

        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private void SharedPreferencesGetir(){

        if(!pref.getString("isim","").equals("")){
            benimId.setText(pref.getString("isim",""));
            benimid = pref.getString("isim","");
        }
        else {
            karsiId.setEnabled(false);
            Toast toast= Toast.makeText(getApplicationContext(), "Set your username", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
        if(!pref.getString("baglantiid","").equals("")){
            karsiId.setText(pref.getString("baglantiid",""));
            baglantiid = pref.getString("baglantiid","");
        }
        if(!pref.getString("baglantifuserid","").equals("")){
            baglantifuserid = pref.getString("baglantifuserid","");
        }
        onun_screenWidth = pref.getInt("onun_ekranx",0);
        onun_screenHeight = pref.getInt("onun_ekrany",0);


        if(benimid != null)
            if (baglantiid != null)
                if(baglantifuserid != null)
                    ServisiBaslat();
    }

    private void IstekKontrol(){
        FirebaseDatabase.getInstance().getReference("Kullanicilar").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                if(snapshot2.hasChild("istekler")){
                    String gelenIstek = "";
                    for(DataSnapshot ds2: snapshot2.child("istekler").getChildren()){
                        gelenIstek = ds2.getKey();
                        if(!gelenIstek.equals("")){
                            String finalGelenIstek = gelenIstek;
                            FirebaseDatabase.getInstance().getReference("Kullanicilar").child(gelenIstek).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild("benim_id")){
                                        istekLayout.setVisibility(View.VISIBLE);
                                        istek.setText("Someone want to connect with you: " + snapshot.child("benim_id").getValue(String.class));


                                        evet.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                snapshot.child("onun_ekranx").getRef().setValue(snapshot2.child("benim_ekranx").getValue(String.class));
                                                snapshot.child("onun_ekrany").getRef().setValue(snapshot2.child("benim_ekrany").getValue(String.class));
                                                snapshot2.child("onun_ekranx").getRef().setValue(snapshot.child("benim_ekranx").getValue(String.class));
                                                snapshot2.child("onun_ekrany").getRef().setValue(snapshot.child("benim_ekrany").getValue(String.class));
                                                edit.putInt("onun_ekranx",Integer.parseInt(snapshot.child("benim_ekranx").getValue(String.class)));
                                                edit.putInt("onun_ekrany",Integer.parseInt(snapshot.child("benim_ekrany").getValue(String.class)));
                                                edit.commit();
                                                snapshot2.child("baglantim").getRef().setValue(finalGelenIstek).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        snapshot.child("baglantim").getRef().setValue(fuser.getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                edit.putString("baglantifuserid", snapshot.getKey());
                                                                edit.putString("baglantiid", snapshot.child("benim_id").getValue(String.class));
                                                                edit.commit();
                                                                SharedPreferencesGetir();
                                                                snapshot2.child("istekler").child(finalGelenIstek).getRef().removeValue();
                                                                snapshot.child("kabul_edildi").getRef().setValue(fuser.getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast toast= Toast.makeText(getApplicationContext(), "You connect: " + snapshot.child("benim_id").getValue(String.class), Toast.LENGTH_SHORT);
                                                                        toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
                                                                        toast.show();
                                                                        ServisiBaslat();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                        hayir.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                snapshot.child("istekler").child(finalGelenIstek).getRef().removeValue();
                                                istekLayout.setVisibility(View.INVISIBLE);
                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
                else
                    istekLayout.setVisibility(View.INVISIBLE);
                if(snapshot2.hasChild("kabul_edildi")){
                    istek_kabul_edildi.setVisibility(View.VISIBLE);
                    BaglantimiGetir();
                    snapshot2.child("kabul_edildi").getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
    /*private void saveBitMap(Bitmap bitmap){
        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/LoversWallpaper/");

        // Make sure the Pictures directory exists.
        if(!path.exists())
        {
            path.mkdirs();
        }

        final File file = new File(path, "LoversWallpaper_ISent.jpg");
        dosya = file;
        try
        {
            final FileOutputStream fos = new FileOutputStream(file);
            final BufferedOutputStream bos = new BufferedOutputStream(fos, 8192);

            //bmp.compress(CompressFormat.JPEG, 100, bos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, bos);

            bos.flush();
            bos.close();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }*/

    public void saveBitMap(Bitmap bitmap) {
        // Bitmap'i galeriye ekleme için yeni bir dosya oluştur
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "LoversWallpaper_ISent");
        //values.put(MediaStore.Images.Media.DESCRIPTION, description);
        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            // OutputStream kullanarak bitmap'i dosyaya yazma
            OutputStream outputStream = getContentResolver().openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            if (outputStream != null) {
                outputStream.close();
            }

            // Galeriye ekleme işlemi
            /*MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    bitmap, title, description);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        getFile(imageUri);
    }

    public void getFile(Uri uri) {
        File file = null;
        String filePath = null;
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(uri, filePathColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        if (filePath != null) {
            file = new File(filePath);
        }

            dosya = file;
    }

    private void Kaydet(Bitmap bitmap){
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"LoversWallpaper");

        String filename = pictureFileDir.getPath() + "LoversWallpaper_ISent.jpg";

        OutputStream fOut = null;
        File file = new File(filename);
        dosya = file;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }// do not forget to close the stream

        try {
            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
            }
        }
        if (requestCode == 1)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                /*Intent intentGalley = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentGalley.setType("image/*");
                intentGalley.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentGalley, "Select Picture"), 2);*/

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            resim.setImageBitmap(photo);


            resim.getLayoutParams().height = onun_screenHeight;
            resim.getLayoutParams().width = onun_screenWidth;
            resim.requestLayout();

            ViewTreeObserver vto = resim.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    resim.getViewTreeObserver().removeOnPreDrawListener(this);

                    resim.getLayoutParams().width = Math.round((resim.getMeasuredHeight() * onun_screenWidth)/onun_screenHeight);
                    resim.requestLayout();

                    return true;
                }
            });

            yazi.getLayoutParams().height = onun_screenHeight;
            yazi.getLayoutParams().width = onun_screenWidth;
            yazi.requestLayout();

            ViewTreeObserver vto2 = yazi.getViewTreeObserver();
            vto2.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    yazi.getViewTreeObserver().removeOnPreDrawListener(this);
                    yazi.getLayoutParams().width = (int) (Math.round((yazi.getMeasuredHeight() * onun_screenWidth)/onun_screenHeight));
                    yazi.requestLayout();
                    return true;
                }
            });
            resimAsil.setImageBitmap(photo);
            anasayfa.setVisibility(View.GONE);
            foto_edit.setVisibility(View.VISIBLE);
        }

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {

            resim.setImageURI(data.getData());
            resim.getLayoutParams().height = onun_screenHeight;
            resim.getLayoutParams().width = onun_screenWidth;
            resim.requestLayout();

            ViewTreeObserver vto = resim.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    resim.getViewTreeObserver().removeOnPreDrawListener(this);
                    resim.getLayoutParams().width = (int) (Math.round((resim.getMeasuredHeight() * onun_screenWidth)/onun_screenHeight));
                    resim.requestLayout();
                    return true;
                }
            });

            yazi.getLayoutParams().height = onun_screenHeight;
            yazi.getLayoutParams().width = onun_screenWidth;
            yazi.requestLayout();

            ViewTreeObserver vto2 = yazi.getViewTreeObserver();
            vto2.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    yazi.getViewTreeObserver().removeOnPreDrawListener(this);
                    yazi.getLayoutParams().width = (int) (Math.round((yazi.getMeasuredHeight() * onun_screenWidth)/onun_screenHeight));
                    yazi.requestLayout();
                    return true;
                }
            });
            resimAsil.setImageURI(data.getData());
            anasayfa.setVisibility(View.GONE);
            foto_edit.setVisibility(View.VISIBLE);
        }
    }

    private void ServisiBaslat() {
        Intent intent = new Intent(getApplicationContext(), ArkaService.class);
        if (!calisiyorMu())
            startForegroundService(intent);
    }
    public static void Degistir(String lockdeger, Uri uri){
        File file = null;
        String filePath = null;
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(uri, filePathColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        if (filePath != null) {
            file = new File(filePath);
        }


        //indi = true;
        if(!servisKapandiMi) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            FirebaseStorage.getInstance().getReference(baglantifuserid).child("wallpaper.jpeg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    try {
                        if (Integer.parseInt(lockdeger) == 1) {
                            //wallpaperManager.setBitmap(bitmap);
                            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                        } else if (Integer.parseInt(lockdeger) == 2) {
                            wallpaperManager.setBitmap(bitmap);
                        } else if (Integer.parseInt(lockdeger) == 3)
                            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    public boolean calisiyorMu(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo info: manager.getRunningServices(Integer.MAX_VALUE)){
            if(ArkaService.class.getName().equals(info.service.getClassName()))
                return true;
        }
        return false;
    }
}