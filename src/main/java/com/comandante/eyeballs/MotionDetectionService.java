package com.comandante.eyeballs;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionListener;
import com.google.common.util.concurrent.AbstractIdleService;

public class MotionDetectionService extends AbstractIdleService {

    private final WebcamMotionListener webcamMotionListener;
    private WebcamMotionDetector detector;

    public MotionDetectionService(WebcamMotionListener webcamMotionListener) {
        this.webcamMotionListener = webcamMotionListener;
    }

    @Override
    protected void startUp() throws Exception {
        detector = new WebcamMotionDetector(Webcam.getDefault());
        detector.setInterval(500);
        detector.addMotionListener(webcamMotionListener);
        detector.start();
    }

    @Override
    protected void shutDown() throws Exception {
        detector.stop();
    }
}

