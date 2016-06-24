package org.naturenet.data.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.firebase.client.ServerValue;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimestampedData {
    @JsonProperty("created_at")
    protected Object mCreatedAt = null;
    @JsonProperty("updated_at")
    protected Object mUpdatedAt = null;
    public TimestampedData() {
        Object timestamp = ServerValue.TIMESTAMP;
        setmCreatedAt(timestamp);
        setmUpdatedAt(timestamp);
    }
    public Object getTimestamp() {
        Object timestamp = ServerValue.TIMESTAMP;
        return timestamp;
    }
    public Object getmCreatedAt() {
        return mCreatedAt;
    }
    public void setmCreatedAt(Object mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }
    public Object getmUpdatedAt() {
        return mUpdatedAt;
    }
    public void setmUpdatedAt(Object mUpdatedAt) {
        this.mUpdatedAt = mUpdatedAt;
    }
}