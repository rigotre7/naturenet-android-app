package org.naturenet.data.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;

@IgnoreExtraProperties
public class Comment implements Serializable {

    public static final String NODE_NAME = "comments";

    public String id;

    public String comment;

    public String commenter;

    public String parent;

    public String context;

    private Comment() {}

    public Comment(String id, String comment, String commenter, String parent, String context) {
        this.id = id;
        this.comment = comment;
        this.commenter = commenter;
        this.parent = parent;
        this.context = context;
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