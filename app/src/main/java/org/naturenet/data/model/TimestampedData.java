package org.naturenet.data.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;

/**
 * NOTE: Firebase as of 9.4.0 does not use inherited fields when reading/writing classes due to a bug.
 * When this bug is fixed, all classes with timestamps should inherit from TimestampedData.
 * https://stackoverflow.com/questions/37547399/
 */
public abstract class TimestampedData implements Serializable {

    @PropertyName("created_at")
    protected Object createdAt = ServerValue.TIMESTAMP;

    @PropertyName("updated_at")
    protected Object updatedAt = ServerValue.TIMESTAMP;

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