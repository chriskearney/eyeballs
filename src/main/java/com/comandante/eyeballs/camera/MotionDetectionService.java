package com.comandante.eyeballs.camera;

import com.comandante.eyeballs.EyeballsConfiguration;
import com.github.sarxos.webcam.Webcam;
import com.google.common.util.concurrent.AbstractIdleService;

public class MotionDetectionService extends AbstractIdleService {

    private final MotionDetectedListener motionDetectedListener;
    private final EyeballsConfiguration eyeballsConfiguration;

    private MotionDetector detector;

    public MotionDetectionService(EyeballsConfiguration eyeballsConfiguration, MotionDetectedListener motionDetectedListener) {
        this.eyeballsConfiguration = eyeballsConfiguration;
        this.motionDetectedListener = motionDetectedListener;
    }

    @Override
    protected void startUp() throws Exception {
        detector = new MotionDetector(Webcam.getDefault());
        detector.setInterval(500);
        //DEFAULT_AREA_THREASHOLD = 0.2;
        //AreaThreshold: The percentage threshold of image that has different pixels for motion to be detected (a double 0-100, with default 0.2).
        detector.setAreaThreshold(eyeballsConfiguration.getAreaThreshold());
        //DEFAULT_PIXEL_THREASHOLD = 25;
        //PixelThreshold: Intensity threshold whereby a pixel is deemed to different (an int 0 - 255, with default 25).
        detector.setPixelThreshold(eyeballsConfiguration.getPixelDifferentThreshold());
        detector.addMotionListener(motionDetectedListener);
        detector.start();
    }

    @Override
    protected void shutDown() throws Exception {
        detector.stop();
    }

    public void startAndWait() {
        startAsync();
        awaitRunning();
    }
}

