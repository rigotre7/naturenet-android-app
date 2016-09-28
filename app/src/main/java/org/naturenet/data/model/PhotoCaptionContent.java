package org.naturenet.data.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class PhotoCaptionContent implements Serializable {

    public String image;
    public String text;

    public PhotoCaptionContent() {}
}