package com.example.kennexcorp.ujrespond.activitiy;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.kennexcorp.ujrespond.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashScreen extends Activity {

    private static final int RequestPermissionCode = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (CheckingPermissionIsEnabledOrNot()) {
            Log.e("permissions", "permission granted");
            startSplash();
        }else {
            RequestMultiplePermission();
        }
        startSplash();

    }

    private void startSplash() {
        Thread splashThread = new Thread() {
            public void run() {
                try {
                    sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                        startActivity(new Intent(SplashScreen.this, Login.class));

                }
            }
        };
        splashThread.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean locationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean internetPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (internetPermission && readPermission && locationPermission) {
                        Toast.makeText(SplashScreen.this, "Permissions granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SplashScreen.this, "Permission Denied, You have to grant all permissions", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void RequestMultiplePermission() {
        ActivityCompat.requestPermissions(SplashScreen.this, new String[]{
                ACCESS_FINE_LOCATION,
                INTERNET,
                READ_EXTERNAL_STORAGE,
        }, RequestPermissionCode);
    }

    public boolean CheckingPermissionIsEnabledOrNot() {
        int locationPermission = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int InternetPermission = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int readPermission = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return InternetPermission == PackageManager.PERMISSION_GRANTED
                && readPermission == PackageManager.PERMISSION_GRANTED
                && locationPermission == PackageManager.PERMISSION_GRANTED;
    }
}
