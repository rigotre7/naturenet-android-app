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

    public static Idea createNew(String id, String content, String submitter, String type) {
        Idea i = new Idea();
        i.id = id;
        i.content = content;
        i.submitter = submitter;
        i.type = type;
        i.status = "doing";
        i.source = "android";
        i.group = "idea";
        return i;
    }

    public String id;

    public String content;

    public String submitter;

    @Nullable
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
        in.readMap(likes, String.class.getClassLoader());
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
        parcel.writeMap(likes);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Idea idea = (Idea) o;

        if (!id.equals(idea.id)) return false;
        if (!content.equals(idea.content)) return false;
        if (!submitter.equals(idea.submitter)) return false;
        if (status != null ? !status.equals(idea.status) : idea.status != null) return false;
        if (group != null ? !group.equals(idea.group) : idea.group != null) return false;
        if (type != null ? !type.equals(idea.type) : idea.type != null) return false;
        if (image != null ? !image.equals(idea.image) : idea.image != null) return false;
        return source != null ? source.equals(idea.source) : idea.source == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + submitter.hashCode();
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Idea{" +
                super.toString() +
                ", id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", submitter='" + submitter + '\'' +
                ", status='" + status + '\'' +
                ", group='" + group + '\'' +
                ", type='" + type + '\'' +
                ", image='" + image + '\'' +
                ", source='" + source + '\'' +
                ", comments=" + comments +
                ", likes=" + likes +
                '}';
    }
}