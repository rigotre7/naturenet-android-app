package org.naturenet.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.ServerValue;

public abstract class TimestampedData implements Parcelable {

    @PropertyName("created_at")
    public Object createdAt = ServerValue.TIMESTAMP;

    @PropertyName("updated_at")
    public Object updatedAt = ServerValue.TIMESTAMP;

    protected TimestampedData() {};

    protected TimestampedData(Parcel in) {
        Long created = (Long)in.readValue(Long.class.getClassLoader());
        if (created != null) {
            this.createdAt = created;
        }
        Long updated = (Long)in.readValue(Long.class.getClassLoader());
        if (updated != null) {
            this.updatedAt = updated;
        }
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeValue(getCreatedAtMillis());
        parcel.writeValue(getUpdatedAtMillis());
    }
}