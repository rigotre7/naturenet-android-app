package org.naturenet.data.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;

public abstract class TimestampedData implements Serializable {

    @PropertyName("created_at")
    public Object createdAt = ServerValue.TIMESTAMP;

    @PropertyName("updated_at")
    public Object updatedAt = ServerValue.TIMESTAMP;

    protected TimestampedData() {}

    @Exclude
    @Nullable
    public Long getCreatedAtMillis() {
        return createdAt instanceof Long ? (Long)createdAt : null;
    }

    @Exclude
    @Nullable
    public Long getUpdatedAtMillis() {
        return updatedAt instanceof Long ? (Long)updatedAt : null;
    }
}