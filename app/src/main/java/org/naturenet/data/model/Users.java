package org.naturenet.data.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.Map;

@IgnoreExtraProperties
public class Users implements Serializable {

    public static final String NODE_NAME = "users";

    public String id;

    @PropertyName("display_name")
    public String displayName;

    public String affiliation;

    @Nullable
    public String avatar;

    @Nullable
    public String bio;

    @Nullable
    public Map<String, Boolean> groups;

    @Nullable
    @PropertyName("latest_contribution")
    public Long latestContribution;

    private Users() {}

    public Users(String id, String displayName, String affiliation, String avatar) {
        this.id = id;
        this.displayName = displayName;
        this.affiliation = affiliation;
        this.avatar = avatar;
    }

    /**
     * Remove and inherit from TimestampedData when Firebase inheritance bug is fixed.
     */
    @PropertyName("created_at")
    public Object createdAt = ServerValue.TIMESTAMP;

    @PropertyName("updated_at")
    public Object updatedAt = ServerValue.TIMESTAMP;

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