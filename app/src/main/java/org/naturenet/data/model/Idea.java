package org.naturenet.data.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.Map;

public class Idea extends TimestampedData {

    @Exclude
    public static final String NODE_NAME = "ideas";

    public static Idea createNew(String id, String content, String submitter) {
        Idea i = new Idea();
        i.id = id;
        i.content = content;
        i.submitter = submitter;
        i.status = "doing";
        i.source = "android";
        return i;
    }

    public String id;

    public String content;

    public String submitter;

    public String status;

    @Nullable
    public String group;

    @Nullable
    public String type;

    @Nullable
    @PropertyName("icon_url")
    public String image;

    @Nullable
    public String source;

    @Nullable
    public Map<String, Boolean> comments = null;

    @Nullable
    public Map<String, Boolean> likes = null;

    private Idea() {}

    @Exclude
    public boolean isValid() {
        return !"deleted".equalsIgnoreCase(status);
    }
}