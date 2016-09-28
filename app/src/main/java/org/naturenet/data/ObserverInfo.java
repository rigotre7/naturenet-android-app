package org.naturenet.data;

import java.io.Serializable;

public class ObserverInfo implements Serializable {
    String observerId;
    String observerAvatar;
    String observerName;
    String observerAffiliation;
    public void ObserverInfo() {}
    public void ObserverInfo(String observerId, String observerAvatar, String observerName, String observerAffiliation) {
        this.observerId = observerId;
        this.observerAvatar = observerAvatar;
        this.observerName = observerName;
        this.observerAffiliation = observerAffiliation;
    }
    public String getObserverId() {
        return observerId;
    }
    public void setObserverId(String observerId) {
        this.observerId = observerId;
    }
    public String getObserverAvatar() {
        return observerAvatar;
    }
    public void setObserverAvatar(String observerAvatar) {
        this.observerAvatar = observerAvatar;
    }
    public String getObserverName() {
        return observerName;
    }
    public void setObserverName(String observerName) {
        this.observerName = observerName;
    }
    public String getObserverAffiliation() {
        return observerAffiliation;
    }
    public void setObserverAffiliation(String observerAffiliation) {
        this.observerAffiliation = observerAffiliation;
    }
}