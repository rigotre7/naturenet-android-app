package org.naturenet.data.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Site {
    public String id;
    public String name;
    public String description;
    public ArrayList<Double> l;
    public String g;

    public Site() {}
}