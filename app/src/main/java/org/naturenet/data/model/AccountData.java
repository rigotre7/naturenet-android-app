package org.naturenet.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.Maps;

import java.util.Map;

public class AccountData implements Parcelable {
    public Map<String, Boolean> consent = Maps.newHashMap();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountData that = (AccountData) o;

        if (consent != null ? !consent.equals(that.consent) : that.consent != null) return false;
        return !(email != null ? !email.equals(that.email) : that.email != null);

    }

    @Override
    public int hashCode() {
        int result = consent != null ? consent.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AccountData{" +
                "consent=" + consent +
                ", email='" + email + '\'' +
                '}';
    }

    public String email = null;

    public AccountData() {}

    protected AccountData(Parcel in) {
        email = in.readString();
        consent = in.readHashMap(consent.getClass().getClassLoader());
    }

    public static final Creator<AccountData> CREATOR = new Creator<AccountData>() {
        @Override
        public AccountData createFromParcel(Parcel in) {
            return new AccountData(in);
        }

        @Override
        public AccountData[] newArray(int size) {
            return new AccountData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeMap(consent);
    }

    public Map<String, Boolean> getConsent() {
        return consent;

    }

    public String getEmail() {
        return email;
    }
}