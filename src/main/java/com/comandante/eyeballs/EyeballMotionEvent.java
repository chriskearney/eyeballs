package com.comandante.eyeballs;

import java.util.Date;

public class EyeballMotionEvent {

    private Date timeStamp;
    private byte[] imageBytes;

    public EyeballMotionEvent(Date timeStamp, byte[] imageBytes) {
        this.timeStamp = timeStamp;
        this.imageBytes = imageBytes;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }
}
