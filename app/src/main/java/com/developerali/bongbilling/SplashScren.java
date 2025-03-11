package com.developerali.bongbilling;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developerali.bongbilling.Helpers.Helpers;

public class SplashScren extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_scren);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Helpers.getTextFromSharedPref(SplashScren.this, "code") != null){
                    startActivity(new Intent(SplashScren.this, MainActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(SplashScren.this, LoginActivity.class));
                    finish();
                }
            }
        }, 1500);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}