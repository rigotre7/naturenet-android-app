package org.naturenet.data.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Site {
    public String id;
    public String name;
    public String description;
    @PropertyName("l")
    public ArrayList<Double> location;
    @PropertyName("g")
    public String geohash;

    private Site() {}
}