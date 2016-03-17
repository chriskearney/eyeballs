package com.comandante.eyeballs.camera;

import com.comandante.eyeballs.model.LocalEvent;
import com.comandante.eyeballs.storage.LocalEventDatabase;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class DetectedMotionImageCaptureDetectedListener implements MotionDetectedListener {
    private final LocalEventDatabase eyeballsMotionEventDatabase;
    private static Logger log = Logger.getLogger(DetectedMotionImageCaptureDetectedListener.class.getName());

    public DetectedMotionImageCaptureDetectedListener(LocalEventDatabase eyeballsMotionEventDatabase) {
        this.eyeballsMotionEventDatabase = eyeballsMotionEventDatabase;
    }

    @Override
    public void motionDetected(MotionDetectedEvent wme) {
        log.info("Start processing of motion event.");
        BufferedImage image = wme.getCurrentOriginal();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] imageData = baos.toByteArray();
        eyeballsMotionEventDatabase.save(new LocalEvent(UUID.randomUUID().toString(), new Date(), imageData));
        log.info("Motion Event Detected of strength: " + wme.getArea());
    }
}
