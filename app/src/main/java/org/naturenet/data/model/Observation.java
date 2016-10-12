package org.naturenet.data.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Observation implements Serializable {
    public static final String NODE_NAME = "observations";

    public String id;

    @PropertyName("observer")
    public String userId;

    @PropertyName("activity")
    public String projectId;

    @PropertyName("site")
    public String siteId;

    public String where;

    @PropertyName("g")
    public String geohash;

    @PropertyName("l")
    public List<Double> location;

    public PhotoCaptionContent data;

    public Map<String, Boolean> comments = null;

    public Map<String, Boolean> likes = null;

    public Observation() {}

    public Observation(String id, String observer, String projectId, String siteId, String where, List<Double> location, PhotoCaptionContent data) {
        this.id = id;
        this.userId = observer;
        this.projectId = projectId;
        this.siteId = siteId;
        this.where = where;
        this.location = location;
        this.data = data;
    }

    /**
     * Remove and inherit from TimestampedData when Firebase inheritance bug is fixed.
     */
    @PropertyName("created_at")
    public Object createdAt = ServerValue.TIMESTAMP;

    @PropertyName("updated_at")
    public Object updatedAt = ServerValue.TIMESTAMP;

    @Exclude
    @Nullable
    public Long getCreatedAtMillis() {
        return createdAt instanceof Long ? (Long)createdAt : null;
    }

    @Exclude
    @Nullable
    public Long getUpdatedAtMillis() {
        return updatedAt instanceof Long ? (Long)updatedAt : null;
    }
}