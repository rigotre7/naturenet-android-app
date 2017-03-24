package org.naturenet.data.model;

import android.os.Parcel;
import android.support.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.firebase.database.Exclude;

import java.util.Map;

public class UsersPrivate extends TimestampedData {

    @Exclude
    public static final String NODE_NAME = "users-private";

    public static UsersPrivate createNew(String id, String name) {
        UsersPrivate u = new UsersPrivate();
        u.id = id;
        u.name = name;
        return u;
    }

    public String id;

    public String name;

    @Nullable
    public Map<String, Boolean> demographics = Maps.newHashMap();

    private UsersPrivate() {}

    private UsersPrivate(Parcel in) {
        super(in);
        this.id = in.readString();
        this.name = in.readString();
        in.readMap(this.demographics, null);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeMap(demographics);
    }

    public static final Creator<UsersPrivate> CREATOR = new Creator<UsersPrivate>() {
        @Override
        public UsersPrivate createFromParcel(Parcel in) {
            return new UsersPrivate(in);
        }

        @Override
        public UsersPrivate[] newArray(int size) {
            return new UsersPrivate[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsersPrivate that = (UsersPrivate) o;

        if (!id.equals(that.id)) return false;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UsersPrivate{" +
                super.toString() +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", demographics=" + demographics +
                '}';
    }
}