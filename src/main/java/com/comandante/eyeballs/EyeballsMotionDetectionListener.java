package com.comandante.eyeballs;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionEvent;
import com.github.sarxos.webcam.WebcamMotionListener;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class EyeballsMotionDetectionListener implements WebcamMotionListener {
    private final EyeballsMotionEventDatabase eyeballsMotionEventDatabase;
    private final Webcam webcam;

    private static Logger log = Logger.getLogger(EyeballsMotionDetectionListener.class.getName());

    public EyeballsMotionDetectionListener(EyeballsMotionEventDatabase eyeballsMotionEventDatabase, Webcam webcam) {
        this.eyeballsMotionEventDatabase = eyeballsMotionEventDatabase;
        this.webcam = webcam;
    }

    @Override
    public void motionDetected(WebcamMotionEvent wme) {
        log.info("Start processing of motion event.");
        BufferedImage image = webcam.getImage();
        ImageDateWriter.writeDate(image);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] imageData = baos.toByteArray();
        eyeballsMotionEventDatabase.save(new EyeballMotionEvent(new Date(), imageData));
        log.info("Motion Event Detected of strength: " + wme.getArea());
    }
}
