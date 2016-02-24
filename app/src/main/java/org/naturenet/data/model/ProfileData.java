package org.naturenet.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class ProfileData implements Parcelable {
    private String avatar = null;
    private String displayName = null;
    private String affiliation = null;
    private String id = null;
    private boolean isTestUser = false;
    private long createdAt = 0L;
    private long updatedAt = 0L;

    public ProfileData() {}

    protected ProfileData(Parcel in) {
        avatar = in.readString();
        displayName = in.readString();
        affiliation = in.readString();
        id = in.readString();
        isTestUser = in.readByte() != 0;
        createdAt = in.readLong();
        updatedAt = in.readLong();
    }

    public static final Creator<ProfileData> CREATOR = new Creator<ProfileData>() {
        @Override
        public ProfileData createFromParcel(Parcel in) {
            return new ProfileData(in);
        }

        @Override
        public ProfileData[] newArray(int size) {
            return new ProfileData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProfileData that = (ProfileData) o;

        if (isTestUser != that.isTestUser) return false;
        if (createdAt != that.createdAt) return false;
        if (updatedAt != that.updatedAt) return false;
        if (avatar != null ? !avatar.equals(that.avatar) : that.avatar != null) return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (affiliation != null ? !affiliation.equals(that.affiliation) : that.affiliation != null) return false;
        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        int result = avatar != null ? avatar.hashCode() : 0;
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (affiliation != null ? affiliation.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (isTestUser ? 1 : 0);
        result = 31 * result + (int) (createdAt ^ (createdAt >>> 32));
        result = 31 * result + (int) (updatedAt ^ (updatedAt >>> 32));
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(avatar);
        dest.writeString(displayName);
        dest.writeString(affiliation);
        dest.writeString(id);

        dest.writeByte((byte) (isTestUser ? 1 : 0));
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIsTestUser(boolean isTestUser) {
        this.isTestUser = isTestUser;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAvatar() {

        return avatar;
    }

    public String getDisplay_name() {
        return displayName;
    }

    public String getAffiliation() {
        return affiliation;
    }

    @Override
    public String toString() {
        return "ProfileData{" +
                "avatar='" + avatar + '\'' +
                ", displayName='" + displayName + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", id='" + id + '\'' +
                ", isTestUser=" + isTestUser +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public String getId() {
        return id;
    }

    public boolean isTest_user() {
        return isTestUser;
    }

    public long getCreated_at() {
        return createdAt;
    }

    public long getUpdated_at() {
        return updatedAt;
    }
}
