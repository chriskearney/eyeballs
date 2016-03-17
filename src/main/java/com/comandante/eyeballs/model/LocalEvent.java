package com.comandante.eyeballs.model;

import java.util.Date;

public class LocalEvent {

    private String id;
    private Date timestamp;
    private byte[] image;

    public LocalEvent(String id, Date timestamp, byte[] imageData) {
        this.id = id;
        this.timestamp = timestamp;
        this.image = imageData;
    }

    public String getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
