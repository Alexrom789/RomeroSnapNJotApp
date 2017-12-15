package com.aromero.app;

import java.io.Serializable;

public class Note implements Serializable {
    public Note() {
    }

    private String title;
    private String description;
    private String photoBase64;
    private long timestamp;

    public Note(String title, String description, String photoBase64, long timestamp) {
        this.title = title;
        this.description = description;
        this.photoBase64 = photoBase64;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
