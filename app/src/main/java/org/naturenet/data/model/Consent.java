package org.naturenet.data.model;

import java.io.Serializable;

public class Consent implements Serializable {
    private String update, share, recording, survey;
    public Consent(boolean[] consent) {
        if(consent[0])  setUpdate("true");
        else setUpdate("false");
        if(consent[1])  setShare("true");
        else setShare("false");
        if(consent[2])  setRecording("true");
        else setRecording("false");
        if(consent[3])  setSurvey("true");
        else setSurvey("false");
    }
    public String getUpdate() {
        return update;
    }
    public void setUpdate(String update) {
        this.update = update;
    }
    public String getShare() {
        return share;
    }
    public void setShare(String share) {
        this.share = share;
    }
    public String getRecording() {
        return recording;
    }
    public void setRecording(String recording) {
        this.recording = recording;
    }
    public String getSurvey() {
        return survey;
    }
    public void setSurvey(String survey) {
        this.survey = survey;
    }
}