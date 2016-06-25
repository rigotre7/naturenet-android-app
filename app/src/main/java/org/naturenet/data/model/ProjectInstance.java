package org.naturenet.data.model;

import com.google.firebase.database.Exclude;

import java.util.List;

public class ProjectInstance {
    private String activity;
    @Exclude
    private Project mProject;
    private String g;
    private List<Double> l;
    private String site;
    public ProjectInstance() {}
    public String getActivity() {
        return activity;
    }
    public void setActivity(String activity) {
        this.activity = activity;
    }
    public Project getmProject() {
        return mProject;
    }
    public void setmProject(Project mProject) {
        this.mProject = mProject;
    }
    public String getG() {
        return g;
    }
    public void setG(String g) {
        this.g = g;
    }
    public List<Double> getL() {
        return l;
    }
    public void setL(List<Double> l) {
        this.l = l;
    }
    public String getSite() {
        return site;
    }
    public void setSite(String site) {
        this.site = site;
    }
}