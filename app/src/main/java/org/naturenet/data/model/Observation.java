package org.naturenet.data.model;

import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Observation implements Serializable {
    public static final String NODE_NAME = "observations";
    private String id = null;
    private Object created_at = null;
    private Object updated_at = null;
    private String observer = null;
    private String activity_location = null;
    Data data = null;
    private String g = null; // the geohash of the location where the observation took place
    private Map<String, Double> l = null;
    private Map<String, Boolean> comments = null;
    private Map<String, Boolean> likes = null;
    public Observation() {
        Object timestamp = ServerValue.TIMESTAMP;
        setCreated_at(timestamp);
        setUpdated_at(timestamp);
    }
    public Observation(String id, Object created_at, Object updated_at, String observer, String activity_location, Data data, String g, Map<String, Double> l, Map<String, Boolean> comments, Map<String, Boolean> likes) {
        setId(id);
        setCreated_at(created_at);
        setUpdated_at(updated_at);
        setObserver(observer);
        setActivity_location(activity_location);
        setData(data);
        setG(g);
        setL(l);
        setComments(comments);
        setLikes(likes);
    }
    @Override
    public String toString() {
        String id = "";
        if (this.id != null)
            id = this.id;
        String created_at = "";
        if (this.created_at != null)
            created_at = this.created_at.toString();
        String updated_at = "";
        if (this.updated_at != null)
            updated_at = this.updated_at.toString();
        String observer = "";
        if (this.observer != null)
            observer = this.observer;
        String activity_location = "";
        if (this.activity_location != null)
            activity_location = this.activity_location;
        String dataImage = "";
        if (this.data.getImage() != null)
            dataImage = this.data.getImage();
        String dataText = "";
        if (this.data.getText() != null)
            dataText = this.data.getText();
        String g = "";
        if (this.g != null)
            g = this.g;
        String lat = "";
        if (this.l != null && this.l.get("0") != null)
            lat = this.l.get("0").toString();
        String lon = "";
        if (this.l != null && this.l.get("1") != null)
            lon = this.l.get("1").toString();
        String comments = "";
        if (this.comments != null)
            comments = this.comments.keySet().toString();
        String likes = "";
        if (this.likes != null)
            likes = this.likes.keySet().toString();
        return "Observation{" +
                "id='" + id + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", observer='" + observer + '\'' +
                ", activity_location='" + activity_location + '\'' +
                ", dataImage=" + dataImage +
                ", dataText=" + dataText +
                ", g='" + g + '\'' +
                ", lat=" + lat +
                ", long=" + lon +
                ", comments=" + comments +
                ", likes=" + likes +
                '}';
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Object getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Object created_at) {
        this.created_at = created_at;
    }
    public Object getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(Object updated_at) {
        this.updated_at = updated_at;
    }
    public String getObserver() {
        return observer;
    }
    public void setObserver(String observer) {
        this.observer = observer;
    }
    public String getActivity_location() {
        return activity_location;
    }
    public void setActivity_location(String activity_location) {
        this.activity_location = activity_location;
    }
    public Data getData() {
        return data;
    }
    public void setData(Data data) {
        this.data = data;
    }
    public String getG() {
        return g;
    }
    public void setG(String g) {
        this.g = g;
    }
    public Map<String, Double> getL() {
        return l;
    }
    public void setL(Map<String, Double> l) {
        this.l = l;
    }
    public Map<String, Boolean> getComments() {
        return comments;
    }
    public void setComments(Map<String, Boolean> comments) {
        this.comments = comments;
    }
    public Map<String, Boolean> getLikes() {
        return likes;
    }
    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }
}