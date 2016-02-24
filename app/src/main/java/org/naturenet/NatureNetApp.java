package org.naturenet;

import android.app.Application;

import com.firebase.client.Firebase;
import com.firebase.client.Logger;

public class NatureNetApp extends Application {
    private static final String TAG = "NatureNet Application";

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
    }
}
