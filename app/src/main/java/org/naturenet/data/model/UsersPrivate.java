package org.naturenet.data.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.Map;

public class UsersPrivate implements Serializable {

    @Exclude
    public static final String NODE_NAME = "users-private";

    public String id;

    public String name;

    @Nullable
    public Map<String, Boolean> demographics;

    private UsersPrivate() {}

    public UsersPrivate(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Remove and inherit from TimestampedData when Firebase inheritance bug is fixed.
     */
    @PropertyName("created_at")
    protected Object createdAt = ServerValue.TIMESTAMP;

    @PropertyName("updated_at")
    protected Object updatedAt = ServerValue.TIMESTAMP;

    @Exclude
    @Nullable
    public Long getCreatedAtMillis() {
        return createdAt instanceof Long ? (Long)createdAt : null;
    }

    @Exclude
    @Nullable public Long getUpdatedAtMillis() {
        return createdAt instanceof Long ? (Long)updatedAt : null;
    }
}