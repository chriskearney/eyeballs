package com.comandante.eyeballs;

import com.comandante.eyeballs.api.EyeballsResource;
import com.comandante.eyeballs.camera.DetectedMotionImageCaptureDetectedListener;
import com.comandante.eyeballs.camera.MotionDetectionService;
import com.comandante.eyeballs.storage.LocalEventDatabase;
import com.github.sarxos.webcam.Webcam;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.awt.*;
import java.io.File;

// For this webcam library to work on ARM you need
// mvn install:install-file -Dfile=./bridj-0.7-20140918.jar -DgroupId=com.nativelibs4java -DartifactId=bridj -Dversion=0.7-20140918 -Dpackaging=jar

// MORE KERNEL SHIT WITH THE LOGIC WEBCAM
// https://www.raspberrypi.org/forums/viewtopic.php?t=79883&p=565951
// You need to edit your boot cmdline.txt file i.e. sudo nano /boot/cmdline.txt and insert the following text somewhere: dwc_otg.fiq_fsm_mask=0x3

public class EyeballsApplication extends Application<EyeballsConfiguration> {

    public static void main(String[] args) throws Exception {
        new EyeballsApplication().run(args);
    }

    @Override
    public void run(EyeballsConfiguration eyeballsConfiguration, Environment environment) throws Exception {
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(new Dimension(640,480));
        webcam.open();
        DB db = DBMaker.newFileDB(new File("event_database")).closeOnJvmShutdown().make();
        LocalEventDatabase eyeballsMotionEventDatabase = new LocalEventDatabase(db, eyeballsConfiguration);
        DetectedMotionImageCaptureDetectedListener detectedMotionImageCaptureListener = new DetectedMotionImageCaptureDetectedListener(eyeballsMotionEventDatabase);
        MotionDetectionService motionDetectionService = new MotionDetectionService(detectedMotionImageCaptureListener);
        motionDetectionService.startAsync();
        motionDetectionService.awaitRunning();
        EyeballsResource eyeballsResource = new EyeballsResource(webcam, eyeballsMotionEventDatabase, motionDetectionService.getDetector());
        environment.jersey().register(eyeballsResource);
    }
}
