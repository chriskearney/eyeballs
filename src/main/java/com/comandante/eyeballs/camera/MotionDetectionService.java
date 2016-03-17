package com.comandante.eyeballs.camera;

import com.github.sarxos.webcam.Webcam;
import com.google.common.util.concurrent.AbstractIdleService;

public class MotionDetectionService extends AbstractIdleService {

    private final MotionDetectedListener motionDetectedListener;
    private MotionDetector detector;

    public MotionDetectionService(MotionDetectedListener motionDetectedListener) {
        this.motionDetectedListener = motionDetectedListener;
    }

    @Override
    protected void startUp() throws Exception {
        detector = new MotionDetector(Webcam.getDefault());
        detector.setInterval(2000);
        //DEFAULT_AREA_THREASHOLD = 0.2;
        //AreaThreshold: The percentage threshold of image that has different pixels for motion to be detected (a double 0-100, with default 0.2).
        detector.setAreaThreshold(10);
        //DEFAULT_PIXEL_THREASHOLD = 25;
        //PixelThreshold: Intensity threshold whereby a pixel is deemed to different (an int 0 - 255, with default 25).
        detector.setPixelThreshold(25);
        detector.addMotionListener(motionDetectedListener);
        detector.start();
    }

    @Override
    protected void shutDown() throws Exception {
        detector.stop();
    }

    public MotionDetector getDetector() {
        return detector;
    }
}

