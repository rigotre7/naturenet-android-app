package org.naturenet.data.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotoCaptionContent {

    @Override
    public String toString() {
        return "PhotoCaptionContent{" +
                "mImageUrl='" + mImageUrl + '\'' +
                ", mCaption='" + mCaption + '\'' +
                '}';
    }

    @JsonProperty("image")
    private String mImageUrl;
    @JsonProperty("text")
    private String mCaption;

    protected PhotoCaptionContent() {}

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mPhotoUrl) {
        this.mImageUrl = mPhotoUrl;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String mCaption) {
        this.mCaption = mCaption;
    }
}
