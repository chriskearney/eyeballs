package com.comandante.eyeballs.model;

import java.util.Date;

public class MotionEvent {

    private final String id;
    private final Date timestamp;
    private final byte[] image;

    public MotionEvent(String id, Date timestamp, byte[] imageData) {
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
}
