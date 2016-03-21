package org.naturenet.data.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
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
}
