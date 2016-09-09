package org.naturenet.data.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.Map;

@IgnoreExtraProperties
public class Project implements Serializable {

    @Exclude
    public static final String NODE_NAME = "activities";

    public String id;

    @PropertyName("icon_url")
    public String iconUrl;

    public String description;

    public String name;

    @Nullable
    public String status;

    @Nullable
    @PropertyName("latest_contribution")
    public Long latestContribution;

    @Nullable
    public Map<String, Boolean> sites;

    private Project() {}
}