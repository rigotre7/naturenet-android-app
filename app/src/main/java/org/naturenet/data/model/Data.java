package org.naturenet.data.model;

import java.io.Serializable;

public class Data implements Serializable {
    private String image = null;
    private String text = null;
    public Data() {}
    public Data(String image, String text) {
        this.image = image;
        this.text = text;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}