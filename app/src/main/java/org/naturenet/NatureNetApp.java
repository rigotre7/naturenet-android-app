package org.naturenet;

import android.app.Application;

import com.squareup.picasso.Picasso;

public class NatureNetApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Picasso.with(this).setIndicatorsEnabled(BuildConfig.DEBUG);
    }
}