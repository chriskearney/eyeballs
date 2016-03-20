package com.comandante.eyeballs.camera;

import com.comandante.eyeballs.common.ImageFormatting;
import com.comandante.eyeballs.model.LocalEvent;
import com.comandante.eyeballs.storage.LocalEventDatabase;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class SaveMotionDetectedListener implements MotionDetectedListener {
    private final LocalEventDatabase eyeballsMotionEventDatabase;
    private static Logger log = Logger.getLogger(SaveMotionDetectedListener.class.getName());

    public SaveMotionDetectedListener(LocalEventDatabase eyeballsMotionEventDatabase) {
        this.eyeballsMotionEventDatabase = eyeballsMotionEventDatabase;
    }

    @Override
    public void motionDetected(MotionDetectedEvent wme) {
        log.info("Start processing of motion event.");
        Date timestamp = new Date();
        BufferedImage image = wme.getCurrentOriginal();
        ImageFormatting.writeDate(image, timestamp);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
        } catch (IOException e) {
            log.error("Unable to write image.", e);
        }
        byte[] imageData = baos.toByteArray();
        eyeballsMotionEventDatabase.save(new LocalEvent(UUID.randomUUID().toString(), timestamp, imageData));
        log.info("Motion Event Detected of Strength: " + wme.getArea());
    }
}
