package org.naturenet.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.firebase.client.ServerValue;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimestampedData {
    @JsonProperty("created_at")
    protected Object mCreatedAt = ServerValue.TIMESTAMP;
    @JsonProperty("updated_at")
    protected Object mUpdatedAt = ServerValue.TIMESTAMP;

    protected TimestampedData() {}

    public void setCreatedAtMillis(Long time) {
        mCreatedAt = time;
    }
    
    public void setUpdatedAtMillis(Long time) {
        mUpdatedAt = time;
    }

    public Long getCreatedAtMillis() {
        return ( mCreatedAt instanceof Long ) ? (Long) mCreatedAt : null;
    }
    public Long getUpdatedAtMillis() {
        return ( mUpdatedAt instanceof Long ) ? (Long) mUpdatedAt : null;
    }
}