package org.naturenet.data.model;

import com.google.firebase.database.ServerValue;

import java.io.Serializable;

public class Comment implements Serializable {
    private String id;
    private String comment;
    private String commenter;
    private String parent;
    private String status;
    private Object created_at = null;
    private Object updated_at = null;
    public Comment() {}
    public Comment(String id, String comment, String commenter, String parent) {
        setId(id);
        setComment(comment);
        setCommenter(commenter);
        setParent(parent);
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
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getCommenter() {
        return commenter;
    }
    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }
    public String getParent() {
        return parent;
    }
    public void setParent(String parent) {
        this.parent = parent;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
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