package org.naturenet.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotoCaptionContent implements Parcelable {
    @JsonProperty("image")
    private String mPhotoUrl;
    @JsonProperty("description")
    private String mCaption;

    protected PhotoCaptionContent(Parcel in) {
        mPhotoUrl = in.readString();
        mCaption = in.readString();
    }

    public static final Creator<PhotoCaptionContent> CREATOR = new Creator<PhotoCaptionContent>() {
        @Override
        public PhotoCaptionContent createFromParcel(Parcel in) {
            return new PhotoCaptionContent(in);
        }

        @Override
        public PhotoCaptionContent[] newArray(int size) {
            return new PhotoCaptionContent[size];
        }
    };

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPhotoUrl);
        dest.writeString(mCaption);
    }
}
