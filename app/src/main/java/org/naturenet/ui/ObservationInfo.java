package org.naturenet.ui;

import org.naturenet.data.model.Observation;

public class ObservationInfo {
    Observation observation;
    String observerAvatar;
    String observerName;
    String observerAffiliation;
    public ObservationInfo() {}
    public ObservationInfo(Observation observation, ObserverInfo observer) {
        this.observation = observation;
        this.observerAvatar = observer.getObserverAvatar();
        this.observerName = observer.getObserverName();
        this.observerAffiliation = observer.getObserverAffiliation();
    }
    public Observation getObservation() {
        return observation;
    }
    public void setObservation(Observation observation) {
        this.observation = observation;
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