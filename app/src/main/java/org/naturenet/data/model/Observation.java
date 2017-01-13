package org.naturenet.data.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Observation extends TimestampedData {

    @Exclude
    public static final String NODE_NAME = "observations";

    public static Observation createNew(String id, String observer, String projectId, String siteId, String where, List<Double> location, PhotoCaptionContent data) {
        Observation o = new Observation();
        o.id = id;
        o.userId = observer;
        o.projectId = projectId;
        o.siteId = siteId;
        o.where = where;
        o.location = location;
        o.data = data;
        o.source = "android";
        return o;
    }

    public String id;

    @PropertyName("observer")
    public String userId;

    @PropertyName("activity")
    public String projectId;

    @PropertyName("site")
    public String siteId;

    @PropertyName("g")
    public String geohash;

    @PropertyName("l")
    public List<Double> location;

    public PhotoCaptionContent data;

    @Nullable
    public String where;

    @Nullable
    public String status;

    @Nullable
    public String source;

    @Nullable
    public Map<String, Boolean> comments = null;

    @Nullable
    public Map<String, Boolean> likes = null;

    public Observation() {}

    @Exclude
    public boolean isValid() { return !"deleted".equalsIgnoreCase(status); }
}