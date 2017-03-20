package org.naturenet.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;

import com.google.common.collect.Lists;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Site implements Parcelable {

    @Exclude
    public static final String NODE_NAME = "sites";

    public String id;

    public String name;

    public String description;

    @PropertyName("l")
    public ArrayList<Double> location = Lists.newArrayList();

    @PropertyName("g")
    public String geohash;

    /**
     * Private no-arg constructor needed for Firebase serialization. Do not use.
     */
    @SuppressWarnings("unused")
    private Site() {}

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    Site(String id, String name, String description, ArrayList<Double> location, String geohash) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
    }

    protected Site(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        in.readList(this.location, null);
        this.geohash = in.readString();
    }

    public static final Creator<Site> CREATOR = new Creator<Site>() {
        @Override
        public Site createFromParcel(Parcel in) {
            return new Site(in);
        }

        @Override
        public Site[] newArray(int size) {
            return new Site[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeList(location);
        parcel.writeString(geohash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Site site = (Site) o;

        if (!id.equals(site.id)) return false;
        if (!name.equals(site.name)) return false;
        return description.equals(site.description);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Site{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location=" + location +
                ", geohash='" + geohash + '\'' +
                '}';
    }
}