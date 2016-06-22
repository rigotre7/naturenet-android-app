package org.naturenet.data.model;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project implements Serializable {
    @JsonIgnore
    public static final String NODE_NAME = "activities";

    @JsonProperty("id")
    private String mProjectId;
    @JsonProperty("icon_url")
    private String mIconUrl;
    @JsonProperty("description")
    private String mDescription;
    @JsonProperty("name")
    private String mName;
    @JsonProperty("status")
    private String mStatus;

    @JsonProperty("latest_contribution")
    private Long mLatestContribution;
    @JsonProperty("created_at")
    private Long mCreatedAt;
    @JsonProperty("updated_at")
    private Long mUpdatedAt;

    public Project() {}

    public String getProjectId() {
        return mProjectId;
    }

    public String getIconUrl() {
        return mIconUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getName() {
        return mName;
    }

    public String getStatus() {
        return mStatus;
    }

    public Long getmLatestContribution() {
        return mLatestContribution;
    }

    public Long getmCreatedAt() {
        return mCreatedAt;
    }

    public Long getmUpdatedAt() {
        return mUpdatedAt;
    }
}
