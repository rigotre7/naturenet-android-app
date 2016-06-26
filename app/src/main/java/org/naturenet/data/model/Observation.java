package org.naturenet.data.model;

//import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

public class Observation {
    public static final String NODE_NAME = "observations";
    private static final int LATITUDE = 0;
    private static final int LONGITUDE = 1;
    public Observation() {}
    private String id;
    private String activity_location; // the id of the activity location pair this observation was recorded for
    private ProjectInstance mProjectInstance = null;
    private String observer;
    private String g; // the geohash of the location where the observation took place
    private List<Double> l;

//    /**
//     * freeform data submitted as part of the observation
//     */
//    @JsonIgnore
//    private Map<String, Object> mRawData;
//
//    @JsonProperty("data")
//    private PhotoCaptionContent mData;
//
//    /**
//     * comments by other users on the observation, may be null/not present if there are no comments
//     */
//    @JsonProperty("comments")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private Map<String, Object> mRawComments = null;
//
//    @JsonIgnore
//    private List<Comment> mComments = null;
//
//    @JsonProperty("likes")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private Map<String, Object> mRawLikes = null;
//
//    public List<Comment> getComments() {
//        if(mRawComments == null) {
//            return null;
//        }
//        if(mComments != null) {
//            return mComments;
//        }
//
//        //TODO: converter function
//        //Comment.fromRaw(mComments);
//        return mComments;
//    }
//
//    public String getGeohash() {
//        return mGeohash;
//    }
//
//    public LatLng getLocation() {
//        return new LatLng(mCoordinates.get(LATITUDE), mCoordinates.get(LONGITUDE));
//    }
//
//    public PhotoCaptionContent getData() {
//        return mData;
//    }

}
