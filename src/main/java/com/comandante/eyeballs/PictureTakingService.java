package com.comandante.eyeballs;

import com.github.sarxos.webcam.Webcam;
import com.google.common.collect.EvictingQueue;
import com.google.common.util.concurrent.AbstractScheduledService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

public class PictureTakingService extends AbstractScheduledService {

    private final Webcam webcam;
    EvictingQueue<byte[]> queue = EvictingQueue.create(1);

    public PictureTakingService(Webcam webcam) {
        this.webcam = webcam;
    }

    @Override
    protected void runOneIteration() throws Exception {
        BufferedImage image = webcam.getImage();
        ImageDateWriter.writeDate(image);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        queue.add(baos.toByteArray());
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, 1, TimeUnit.SECONDS);
    }

    public byte[] getImage() {
        return queue.element();
    }
}
