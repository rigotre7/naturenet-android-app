package org.naturenet.util;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import timber.log.Timber;

public class ForestFire extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (Log.WARN <= priority) {
            FirebaseCrash.log(message);
            if (t != null) {
                FirebaseCrash.report(t);
            }
        }
    }
}