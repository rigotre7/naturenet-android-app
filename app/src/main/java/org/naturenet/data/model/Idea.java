package org.naturenet.data.model;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Idea extends TimestampedData {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("id")
    private String mId = null;
    @JsonProperty("content")
    private String mText = null;
    @JsonProperty("group")
    private String mPromptName = null;
    @JsonProperty("icon_url")
    private String mImageUrl = null;
    @JsonProperty("status")
    private String mStatus = null;
    @JsonProperty("submitter")
    private String mSubmitterId = null;
    @JsonProperty("likes")
    private Map<String, Object> mLikes = null;

    public Idea() {}

    public Idea(Parcel in) {
        super(in);
        //TODO
    }

    public static final Creator<Idea> CREATOR = new Creator<Idea>() {
        @Override
        public Idea createFromParcel(Parcel in) {
            return new Idea(in);
        }

        @Override
        public Idea[] newArray(int size) {
            return new Idea[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //TODO
    }
}
