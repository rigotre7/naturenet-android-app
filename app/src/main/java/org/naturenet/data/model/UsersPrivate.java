package org.naturenet.data.model;

import java.io.Serializable;
import java.util.Map;

public class UsersPrivate implements Serializable {
    private String uid;
    Map<String, String> created_at, updated_at;
    public UsersPrivate() {}
    public UsersPrivate(String uid, Map<String, String> timestamp) {
        setUid(uid);
        setCreated_at(timestamp);
        setUpdated_at(timestamp);
    }
    public String getUid() {
        return uid;
    }
    public Map<String, String> getCreated_at() {
        return created_at;
    }
    public Map<String, String> getUpdated_at() {
        return updated_at;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setCreated_at(Map<String, String> created_at) {
        this.created_at = created_at;
    }
    public void setUpdated_at(Map<String, String> updated_at) {
        this.updated_at = updated_at;
    }
}