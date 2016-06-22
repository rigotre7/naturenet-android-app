package org.naturenet.data.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Users extends TimestampedData implements Serializable {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("id")
    private String mId = null;
    @JsonProperty("display_name")
    private String mDisplayName = null;
    @JsonProperty("affiliation")
    private String mAffiliation = null;
    @JsonProperty("avatar")
    private String mAvatar = null;
    @JsonProperty("bio")
    private String mBio = null;
    @JsonProperty("latest_contribution")
    private Long mLatestContribution = null;
    public Users() {}
    public Users(String mId, String mDisplayName, String mAffiliation) {
        this.mId = mId;
        this.mDisplayName = mDisplayName;
        this.mAffiliation = mAffiliation;
    }
    public String getmId() {
        return mId;
    }
    public void setmId(String mId) {
        this.mId = mId;
    }
    public String getmDisplayName() {
        return mDisplayName;
    }
    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }
    public String getmAffiliation() {
        return mAffiliation;
    }
    public void setmAffiliation(String mAffiliation) {
        this.mAffiliation = mAffiliation;
    }
    public String getmAvatar() {
        return mAvatar;
    }
    public void setmAvatar(String mAvatar) {
        this.mAvatar = mAvatar;
    }
    public String getmBio() {
        return mBio;
    }
    public void setmBio(String mBio) {
        this.mBio = mBio;
    }
    public Long getmLatestContribution() {
        return mLatestContribution;
    }
    public void setmLatestContribution(Long mLatestContribution) {
        this.mLatestContribution = mLatestContribution;
    }
}