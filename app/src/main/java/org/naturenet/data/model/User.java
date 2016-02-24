package org.naturenet.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private ProfileData profile = new ProfileData();

    private AccountData account = new AccountData();

    public User() {}

    protected User(Parcel in) {
        profile = in.readParcelable(ProfileData.class.getClassLoader());
        account = in.readParcelable(AccountData.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
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

        User user = (User) o;

        if (!profile.equals(user.profile)) return false;
        return account.equals(user.account);

    }

    @Override
    public int hashCode() {
        int result = profile.hashCode();
        result = 31 * result + account.hashCode();
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(profile, flags);
        dest.writeParcelable(account, flags);
    }

    @Override
    public String toString() {
        return "User{" +
                "profile=" + profile +
                ", account=" + account +
                '}';
    }

    public ProfileData getPublic() {
        return profile;
    }

    public AccountData getPrivate() {
        return account;
    }
}
