package com.comandante.eyeballs.camera;

import com.comandante.eyeballs.common.ImageFormatting;
import com.comandante.eyeballs.model.MotionEvent;
import com.comandante.eyeballs.motion_events.MotionEventProcessor;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class SaveMotionDetectedListener implements MotionDetectedListener {
    private final MotionEventProcessor eyeballsMotionEventDatabase;
    private static Logger log = Logger.getLogger(SaveMotionDetectedListener.class.getName());

    public SaveMotionDetectedListener(MotionEventProcessor eyeballsMotionEventDatabase) {
        this.eyeballsMotionEventDatabase = eyeballsMotionEventDatabase;
    }

    @Override
    public void motionDetected(MotionDetectedEvent wme) {
        log.info("Start processing of motion event.");
        Date timestamp = new Date();
        BufferedImage image = ImageFormatting.writeDate(wme.getCurrentOriginal(), timestamp);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
        } catch (IOException e) {
            log.error("Unable to write image.", e);
        }
        byte[] imageData = baos.toByteArray();
        eyeballsMotionEventDatabase.save(new MotionEvent(timestamp.getTime() + "." + convertUUIDtoBase64(), timestamp, imageData));
        log.info("Motion Event Detected of Strength: " + wme.getArea());
    }

    public String convertUUIDtoBase64() {
        UUID uuid = UUID.randomUUID();
        return convertUUIDtoBase64(uuid);
    }

    public String convertUUIDtoBase64(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        String base64EncodedId = Base64.getUrlEncoder().encodeToString(bb.array());
        return base64EncodedId.substring(0, base64EncodedId.length() - 2);
    }
}
