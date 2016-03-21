package org.naturenet.data.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectInstance {
    @JsonProperty("activity")
    private String mProjectId;
    @JsonIgnore
    private Project mProject;
    @JsonProperty("g")
    private String mGeohash;
    @JsonProperty("l")
    private List<Double> mLocationCoords;
    @JsonProperty("site")
    private String mSiteName;

    public ProjectInstance() {}

    public void setProject(Project project) {
        mProject = project;
    }

    public String getProjectId() {
        return mProjectId;
    }

    public String getGeohash() {
        return mGeohash;
    }

    public List<Double> getLocationCoords() {
        return mLocationCoords;
    }

    public String getSiteName() {
        return mSiteName;
    }

    public Project getProject() {
        return mProject;

    }
}
