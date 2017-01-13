package org.naturenet.data.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.util.Map;

public class UsersPrivate extends TimestampedData {

    @Exclude
    public static final String NODE_NAME = "users-private";

    public static UsersPrivate createNew(String id, String name) {
        UsersPrivate u = new UsersPrivate();
        u.id = id;
        u.name = name;
        return u;
    }

    public String id;

    public String name;

    @Nullable
    public Map<String, Boolean> demographics;

    private UsersPrivate() {}
}