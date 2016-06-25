package org.naturenet.data.model;

public class PhotoCaptionContent {
    private String image;
    private String text;
    protected PhotoCaptionContent() {}
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