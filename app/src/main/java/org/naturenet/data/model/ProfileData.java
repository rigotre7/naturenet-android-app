package org.naturenet.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.firebase.client.ServerValue;

public class ProfileData implements Parcelable {
    public String avatar = null;
    public String displayName = null;
    public String affiliation = null;
    public String id = null;
    public boolean isTestUser = false;
    public long createdAt = 0L;
    public long updatedAt = 0L;

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

    public boolean getIs_Test_user() {
        return isTestUser;
    }

    public Object getCreated_at() {
        return (createdAt > 0L) ? createdAt : ServerValue.TIMESTAMP;
    }

    public Object getUpdated_at() {
        return (updatedAt > 0L) ? updatedAt : ServerValue.TIMESTAMP;
    }
}
