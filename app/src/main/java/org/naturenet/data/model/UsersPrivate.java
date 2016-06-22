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
public class UsersPrivate extends TimestampedData implements Serializable {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("id")
    private String mId = null;
    @JsonProperty("name")
    private String mName = null;
    public UsersPrivate() {}
    public UsersPrivate(String mId, String mName) {
        this.mId = mId;
        this.mName = mName;
    }
    public String getmId() {
        return mId;
    }
    public void setmId(String mId) {
        this.mId = mId;
    }
    public String getmName() {
        return mName;
    }
    public void setmName(String mName) {
        this.mName = mName;
    }
}