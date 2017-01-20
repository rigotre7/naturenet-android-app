package org.naturenet.data.model;

import android.os.Parcel;
import android.support.annotation.Nullable;

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
    public Map<String, Boolean> demographics;

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
}