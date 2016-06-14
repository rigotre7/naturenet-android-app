package org.naturenet.data.model;

import java.io.Serializable;
import java.util.Map;

public class Users implements Serializable {
    private String email, password, uid, full_name, display_name, affiliation, avatar;
    Map<String, String> created_at, updated_at;
    public Users() {}
    public Users(String email, String password, String uid, String full_name, String display_name, String affiliation, String avatar) {
        setEmail(email);
        setPassword(password);
        setUid(uid);
        setFull_name(full_name);
        setDisplay_name(display_name);
        setAffiliation(affiliation);
        setAvatar(avatar);
    }
    public Users(String email, String password, String uid, String full_name, String display_name, String affiliation, Map<String, String> timestamp) {
        setEmail(email);
        setPassword(password);
        setUid(uid);
        setFull_name(full_name);
        setDisplay_name(display_name);
        setAffiliation(affiliation);
        setCreated_at(timestamp);
        setUpdated_at(timestamp);
        setAvatar("Default");
    }
    @Override
    public String toString() {
        return "Users{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", uid='" + uid + '\'' +
                ", full_name='" + full_name + '\'' +
                ", display_name='" + display_name + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", avatar='" + avatar + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getUid() {
        return uid;
    }
    public String getFull_name() {
        return full_name;
    }
    public String getDisplay_name() {
        return display_name;
    }
    public String getAffiliation() {
        return affiliation;
    }
    public String getAvatar() {
        return avatar;
    }
    public Map<String, String> getCreated_at() {
        return created_at;
    }
    public Map<String, String> getUpdated_at() {
        return updated_at;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }
    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public void setCreated_at(Map<String, String> created_at) {
        this.created_at = created_at;
    }
    public void setUpdated_at(Map<String, String> updated_at) {
        this.updated_at = updated_at;
    }
}