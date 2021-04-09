package com.dawabag.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dawabag.MainActivity;
import com.dawabag.R;
import com.dawabag.helpers.PrefManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // change notification bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }

        prefManager = new PrefManager(getApplicationContext());

        if (Build.VERSION.SDK_INT >= 23) {
            getAllPermissions();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    move();
                }
            }, 1000);
        }
    }

    private void move() {
        if(prefManager.isFirstTimeLaunch()) {
            prefManager.setFirstTimeLaunch(false);
            startActivity(new Intent(getApplicationContext(), IntroductionActivity.class));
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void getAllPermissions() {
        String[] PERMISSIONS = new String[]{Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(this, PERMISSIONS, 10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 10:
                Log.d("veer", "case 10");
                if (hasAllPermissionsGranted(grantResults)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            move();
                        }
                    }, 1000);
                } else {
                    Log.d("veer", "denied");
                    // Permission Denied
                    //ResourceElements.showDialogOk(SplashActivity.this, "Alert", "You have to accept all permission to use applicaiton");
                    getAllPermissions();
                }
                break;
            default:
                Log.d("veer", "defaule");
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
}