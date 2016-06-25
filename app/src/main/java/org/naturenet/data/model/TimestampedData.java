package org.naturenet.data.model;

import com.google.firebase.database.ServerValue;

public class TimestampedData {
    protected Long created_at = null;
    protected Long updated_at = null;
    public TimestampedData() {
        Object timestamp = ServerValue.TIMESTAMP;
        setCreated_at((Long) timestamp);
        setUpdated_at((Long) timestamp);
    }
    public Object getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }
    public Object getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(Long updated_at) {
        this.updated_at = updated_at;
    }
}