package org.naturenet.data;

import android.graphics.Bitmap;

public class PreviewInfo {
    public String observationImageUrl;
    public String observerAvatarUrl;
    public Bitmap observationImage;
    public Bitmap observerAvatar;
    public String observerName;
    public String affiliation;
    public String observationText;
    public String likesCount;
    public String commentsCount;
    public PreviewInfo() {}
    public PreviewInfo(Bitmap observationImage, Bitmap observerAvatar, String observerName, String affiliation, String observationText, String likesCount, String commentsCount) {
        this.observationImage = observationImage;
        this.observerAvatar = observerAvatar;
        this.observerName = observerName;
        this.affiliation = affiliation;
        this.observationText = observationText;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }
}