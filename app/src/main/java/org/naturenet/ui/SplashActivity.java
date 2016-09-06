package org.naturenet.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.squareup.picasso.Picasso;

import org.naturenet.BuildConfig;
import org.naturenet.R;
import org.naturenet.util.ForestFire;

import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Picasso.with(this).setIndicatorsEnabled(BuildConfig.DEBUG);
        Timber.plant(new ForestFire());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}