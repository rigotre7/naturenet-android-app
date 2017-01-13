package org.naturenet.data.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Comment extends TimestampedData {

    @Exclude
    public static final String NODE_NAME = "comments";

    public static Comment createNew(String id, String comment, String commenter, String parent, String context) {
        Comment c = new Comment();
        c.id = id;
        c.comment = comment;
        c.commenter = commenter;
        c.parent = parent;
        c.context = context;
        c.source = "android";
        return c;
    }

    public String id;

    public String comment;

    public String commenter;

    public String parent;

    public String context;

    @Nullable
    public String source;

    @Nullable
    public String status;

    private Comment() {}

    @Exclude
    public boolean isValid() {
        return !"deleted".equalsIgnoreCase(status);
    }

}