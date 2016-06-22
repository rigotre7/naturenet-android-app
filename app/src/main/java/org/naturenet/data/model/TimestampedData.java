package org.naturenet.data.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.firebase.client.ServerValue;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimestampedData {
    @JsonProperty("created_at")
    protected Long mCreatedAt = null;
    @JsonProperty("updated_at")
    protected Long mUpdatedAt = null;
    public TimestampedData() {
        setmCreatedAt(getTimestampLong());
        setmUpdatedAt(getTimestampLong());
    }
    public Long getTimestampLong() {
        Map<String, String> timestamp = ServerValue.TIMESTAMP;
        Long timestampLong = null;
        // Code for getting a Long type value into 'timestampLong' from the Map object 'timestamp'

        if (timestampLong == null) timestampLong = 10000000l; // Default value to test

        return timestampLong;
    }
    public Long getmCreatedAt() {
        return mCreatedAt;
    }
    public void setmCreatedAt(Long mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }
    public Long getmUpdatedAt() {
        return mUpdatedAt;
    }
    public void setmUpdatedAt(Long mUpdatedAt) {
        this.mUpdatedAt = mUpdatedAt;
    }
}