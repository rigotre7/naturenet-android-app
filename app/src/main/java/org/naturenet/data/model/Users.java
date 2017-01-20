package org.naturenet.data.model;

import android.os.Parcel;
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

    private Users(Parcel in) {
        super(in);
        this.id = in.readString();
        this.displayName = in.readString();
        this.affiliation = in.readString();
        this.avatar = in.readString();
        this.bio = in.readString();
        in.readMap(this.groups, null);
        this.latestContribution = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(id);
        parcel.writeString(displayName);
        parcel.writeString(affiliation);
        parcel.writeString(avatar);
        parcel.writeString(bio);
        parcel.writeMap(groups);
        parcel.writeLong(latestContribution == null ? 0L : latestContribution);
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };
}