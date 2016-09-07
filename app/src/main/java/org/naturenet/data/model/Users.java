package org.naturenet.data.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;

@IgnoreExtraProperties
public class Users implements Serializable {

    public String id;

    @PropertyName("display_name")
    public String displayName;

    public String affiliation;

    @Nullable
    public String avatar;

    @Nullable
    public String bio;

    @PropertyName("latest_contribution")
    public Long latestContribution = 0L;

    @PropertyName("created_at")
    public Object createdAt = ServerValue.TIMESTAMP;

    @PropertyName("updated_at")
    public Object updatedAt = ServerValue.TIMESTAMP;

    private Users() {}

    public Users(String id, String displayName, String affiliation, String avatar) {
        this.id = id;
        this.displayName = displayName;
        this.affiliation = affiliation;
        this.avatar = avatar;
    }
}