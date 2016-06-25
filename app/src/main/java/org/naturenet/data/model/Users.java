package org.naturenet.data.model;

import com.google.firebase.database.ServerValue;

import java.io.Serializable;

public class Users implements Serializable {
    private String id = null;
    private String display_name = null;
    private String affiliation = null;
    private String avatar = null;
    private String bio = null;
    private Long latest_contribution = null;
    private Object created_at = null;
    private Object updated_at = null;
    public Users() {}
    public Users(String mId, String mDisplayName, String mAffiliation, String mAvatar) {
        setId(mId);
        setDisplay_name(mDisplayName);
        setAffiliation(mAffiliation);
        setAvatar(mAvatar);
        Object timestamp = ServerValue.TIMESTAMP;
        setCreated_at(timestamp);
        setUpdated_at(timestamp);
    }
    public Users(String id, String displayName, String affiliation, String avatar, String bio, Long latestContribution, Object createdAt, Object updatedAt) {
        setId(id);
        setDisplay_name(displayName);
        setAffiliation(affiliation);
        setAvatar(avatar);
        setBio(bio);
        setLatest_contribution(latestContribution);
        setCreated_at(createdAt);
        setUpdated_at(updatedAt);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDisplay_name() {
        return display_name;
    }
    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }
    public String getAffiliation() {
        return affiliation;
    }
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
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