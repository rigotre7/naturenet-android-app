package org.naturenet.data.model;

import android.os.Parcel;
import android.support.annotation.Nullable;

import com.google.common.collect.Maps;
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
    public Map<String, Boolean> comments = Maps.newHashMap();

    @Nullable
    public Map<String, Boolean> likes = Maps.newHashMap();

    private Idea() {}

    private Idea(Parcel in) {
        super(in);
        id = in.readString();
        content = in.readString();
        submitter = in.readString();
        status = in.readString();
        group = in.readString();
        type = in.readString();
        image = in.readString();
        source = in.readString();
    }

    @Exclude
    public boolean isValid() {
        return !"deleted".equalsIgnoreCase(status);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(id);
        parcel.writeString(content);
        parcel.writeString(submitter);
        parcel.writeString(status);
        parcel.writeString(group);
        parcel.writeString(type);
        parcel.writeString(image);
        parcel.writeString(source);
    }

    public static final Creator<Idea> CREATOR = new Creator<Idea>() {
        @Override
        public Idea createFromParcel(Parcel in) {
            return new Idea(in);
        }

        @Override
        public Idea[] newArray(int size) {
            return new Idea[size];
        }
    };
}