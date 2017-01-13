package org.naturenet.data.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;

@IgnoreExtraProperties
public class Site implements Serializable {

    @Exclude
    public static final String NODE_NAME = "sites";

    public String id;

    public String name;

    public String description;

    @PropertyName("l")
    public ArrayList<Double> location;

    @PropertyName("g")
    public String geohash;

    private Site() {}
}