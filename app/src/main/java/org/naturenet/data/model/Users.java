package org.naturenet.data.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.Map;

@IgnoreExtraProperties
public class Users extends TimestampedData {

    @Exclude
    public static final String NODE_NAME = "users";

    public static Users createNew(String id, String displayName, String affiliation, String avatar) {
        Users u = new Users();
        u.id = id;
        u.displayName = displayName;
        u.affiliation = affiliation;
        u.avatar = avatar;
        return u;
    }

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
}