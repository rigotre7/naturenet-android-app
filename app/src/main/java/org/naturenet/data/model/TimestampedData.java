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
public class TimestampedData implements Parcelable {
    @JsonProperty("created_at")
    protected Long mCreatedAt = null;

    @JsonProperty("updated_at")
    protected Long mUpdatedAt = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("created_at")
    protected Map mCreatedPlaceholder = ServerValue.TIMESTAMP;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("updated_at")
    protected Map mUpdatedPlaceholder = ServerValue.TIMESTAMP;

    protected TimestampedData(Parcel in) {
    }

    public static final Creator<TimestampedData> CREATOR = new Creator<TimestampedData>() {
        @Override
        public TimestampedData createFromParcel(Parcel in) {
            return new TimestampedData(in);
        }

        @Override
        public TimestampedData[] newArray(int size) {
            return new TimestampedData[size];
        }
    };

    public Long getCreatedAtMillis() {
        return ( mCreatedAt instanceof Long ) ? (Long) mCreatedAt : null;
    }

    public Long getUpdatedAtMillis() {
        return ( mUpdatedAt instanceof Long ) ? (Long) mUpdatedAt : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        boolean isTimestamped = mCreatedAt instanceof Long;
        dest.writeByte(isTimestamped ? (byte) 0 : (byte) 1);
        if(isTimestamped) {
            dest.writeLong(mCreatedAt.longValue());
        }
    }
}
