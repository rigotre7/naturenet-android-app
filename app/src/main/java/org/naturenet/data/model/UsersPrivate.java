package org.naturenet.data.model;

import com.google.firebase.database.ServerValue;

import java.io.Serializable;

public class UsersPrivate implements Serializable {
    private String id = null;
    private String name = null;
    private Object created_at = null;
    private Object updated_at = null;
    public UsersPrivate() {}
    public UsersPrivate(String mId, String mName) {
        setId(mId);
        setName(mName);
        Object timestamp = ServerValue.TIMESTAMP;
        setCreated_at(timestamp);
        setUpdated_at(timestamp);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
}