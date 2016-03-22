package com.comandante.eyeballs.camera;

import com.comandante.eyeballs.common.ImageFormatting;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.google.common.collect.EvictingQueue;

import java.awt.image.BufferedImage;
import java.util.Date;

public class PictureTakingService implements WebcamListener {

    private final EvictingQueue<BufferedImage> cachedPreppedImage = EvictingQueue.create(1);
    private final Webcam webcam;

    public PictureTakingService(Webcam webcam) {
        this.webcam = webcam;
    }

    @Override
    public void webcamImageObtained(WebcamEvent we) {
        BufferedImage image = ImageFormatting.writeDate(we.getImage(), new Date());
        cachedPreppedImage.add(image);
    }

    @Override
    public void webcamDisposed(WebcamEvent we) {

    }

    @Override
    public void webcamClosed(WebcamEvent we) {

    }

    @Override
    public void webcamOpen(WebcamEvent we) {

    }

    public BufferedImage getLatestImage() {
        if (cachedPreppedImage.size() == 0) {
            BufferedImage bufferedImage = ImageFormatting.writeDate(webcam.getImage(), new Date());
            cachedPreppedImage.add(bufferedImage);
        }
        return cachedPreppedImage.element();
    }
}
