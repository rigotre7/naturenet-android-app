package org.naturenet.data.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Project implements Serializable {
    @Exclude
    public static final String NODE_NAME = "activities";
    private String id;
    private String icon_url;
    private String description;
    private String name;
    private String status;
    private Long latest_contribution;
    private Object created_at;
    private Object updated_at;
    public Project() {}
    public Project(String id, String icon_url, String description, String name, String status, Long latest_contribution, Object created_at, Object updated_at) {
        setId(id);
        setIcon_url(icon_url);
        setDescription(description);
        setName(name);
        setStatus(status);
        setLatest_contribution(latest_contribution);
        setCreated_at(created_at);
        setUpdated_at(updated_at);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getIcon_url() {
        return icon_url;
    }
    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Long getLatest_contribution() {
        return latest_contribution;
    }
    public void setLatest_contribution(Long latest_contribution) {
        this.latest_contribution = latest_contribution;
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