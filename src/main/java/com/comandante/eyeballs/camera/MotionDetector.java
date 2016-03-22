package com.comandante.eyeballs.camera;

import com.comandante.eyeballs.common.ImageFormatting;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamExceptionHandler;
import com.github.sarxos.webcam.WebcamMotionListener;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
public class MotionDetector {

    private static final Logger LOG = Logger.getLogger(MotionDetector.class);
    private static final AtomicInteger NT = new AtomicInteger(0);
    private static final ThreadFactory THREAD_FACTORY = new DetectorThreadFactory();
    public static final int DEFAULT_INTERVAL = 500;
    private static final class DetectorThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable runnable) {
            Thread t = new Thread(runnable, String.format("motion-detector-%d", NT.incrementAndGet()));
            t.setUncaughtExceptionHandler(WebcamExceptionHandler.getInstance());
            t.setDaemon(true);
            return t;
        }
    }
    private class Runner implements Runnable {

        @Override
        public void run() {

            running.set(true);

            while (running.get() && webcam.isOpen()) {
                try {
                    detect();
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    WebcamExceptionHandler.handle(e);
                }
            }

            running.set(false);
        }
    }
    private class Inverter implements Runnable {

        @Override
        public void run() {

            int delay = 0;

            while (running.get()) {

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }

                delay = inertia != -1 ? inertia : 2 * interval;

                if (lastMotionTimestamp + delay < System.currentTimeMillis()) {
                    motion = false;
                }
            }
        }
    }
    private final ExecutorService executor = Executors.newFixedThreadPool(2, THREAD_FACTORY);
    private final List<MotionDetectedListener> listeners = new ArrayList<MotionDetectedListener>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile boolean motion = false;
    private BufferedImage previousOriginal = null;
    private BufferedImage previousModified = null;
    private Webcam webcam = null;
    private volatile int interval = DEFAULT_INTERVAL;
    private volatile int inertia = -1;
    private volatile long lastMotionTimestamp = 0;
    private final MotionDetectorAlgorithm detectorAlgorithm;

    public MotionDetector(Webcam webcam, MotionDetectorAlgorithm detectorAlgorithm, int interval) {
        this.webcam = webcam;
        this.detectorAlgorithm = detectorAlgorithm;
        setInterval(interval);
    }
    public MotionDetector(Webcam webcam, int pixelThreshold, double areaThreshold, int interval) {
        this(webcam, new MotionDetectorDefaultAlgorithm(pixelThreshold, areaThreshold), interval);
    }
    public MotionDetector(Webcam webcam, int pixelThreshold, double areaThreshold) {
        this(webcam, pixelThreshold, areaThreshold, DEFAULT_INTERVAL);
    }
    public MotionDetector(Webcam webcam, int pixelThreshold) {
        this(webcam, pixelThreshold, MotionDetectorDefaultAlgorithm.DEFAULT_AREA_THREASHOLD);
    }
    public MotionDetector(Webcam webcam) {
        this(webcam, MotionDetectorDefaultAlgorithm.DEFAULT_PIXEL_THREASHOLD);
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            webcam.open();
            executor.submit(new Runner());
            executor.submit(new Inverter());
        }
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            webcam.close();
            executor.shutdownNow();
        }
    }

    protected void detect() {

        if (!webcam.isOpen()) {
            motion = false;
            return;
        }

        BufferedImage currentOriginal = webcam.getImage();

        if (currentOriginal == null) {
            motion = false;
            return;
        }

        BufferedImage currentModified = detectorAlgorithm.prepareImage(currentOriginal);

        boolean movementDetected = detectorAlgorithm.detect(previousModified, currentModified);

        if (movementDetected) {
            motion = true;
            lastMotionTimestamp = System.currentTimeMillis();
            notifyMotionListeners(currentOriginal);
        }

        previousOriginal = currentOriginal;
        previousModified = currentModified;
    }

    private void notifyMotionListeners(BufferedImage currentOriginal) {
        MotionDetectedEvent wme = new MotionDetectedEvent(this, previousOriginal, currentOriginal, detectorAlgorithm.getArea(), detectorAlgorithm.getCog(), detectorAlgorithm.getPoints());
        for (MotionDetectedListener l : listeners) {
            try {
                l.motionDetected(wme);
            } catch (Exception e) {
                WebcamExceptionHandler.handle(e);
            }
        }
    }

    public boolean addMotionListener(MotionDetectedListener l) {
        return listeners.add(l);
    }

    public WebcamMotionListener[] getMotionListeners() {
        return listeners.toArray(new WebcamMotionListener[listeners.size()]);
    }

    public boolean removeMotionListener(WebcamMotionListener l) {
        return listeners.remove(l);
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {

        if (interval < 100) {
            throw new IllegalArgumentException("Motion check interval cannot be less than 100 ms");
        }

        this.interval = interval;
    }

    public void setPixelThreshold(int threshold) {
        if (detectorAlgorithm instanceof MotionDetectorDefaultAlgorithm) {
            ((MotionDetectorDefaultAlgorithm)detectorAlgorithm).setPixelThreshold(threshold);
        }
    }

    public void setAreaThreshold(double threshold) {
        if (detectorAlgorithm instanceof MotionDetectorDefaultAlgorithm) {
            ((MotionDetectorDefaultAlgorithm)detectorAlgorithm).setAreaThreshold(threshold);
        }
    }

    public void setInertia(int inertia) {
        if (inertia < 0) {
            throw new IllegalArgumentException("Inertia time must not be negative!");
        }
        this.inertia = inertia;
    }

    public void clearInertia() {
        this.inertia = -1;
    }

    public Webcam getWebcam() {
        return webcam;
    }

    public boolean isMotion() {
        if (!running.get()) {
            LOG.warn("Motion cannot be detected when detector is not running!");
        }
        return motion;
    }

    public double getMotionArea() {
        return detectorAlgorithm.getArea();
    }

    public Point getMotionCog() {
        Point cog = detectorAlgorithm.getCog();
        if (cog == null) {
            // detectorAlgorithm hasn't been called so far - get image center
            int w = webcam.getViewSize().width;
            int h = webcam.getViewSize().height;
            cog = new Point(w / 2, h / 2);
        }
        return cog;
    }

    public MotionDetectorAlgorithm getDetectorAlgorithm() {
        return detectorAlgorithm;
    }

    public void setMaxMotionPoints(int i){
        detectorAlgorithm.setMaxPoints(i);
    }

    public int getMaxMotionPoints(){
        return detectorAlgorithm.getMaxPoints();
    }

    public void setPointRange(int i){
        detectorAlgorithm.setPointRange(i);
    }

    public int getPointRange(){
        return detectorAlgorithm.getPointRange();
    }

}
