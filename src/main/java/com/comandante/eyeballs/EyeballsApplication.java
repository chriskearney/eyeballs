package com.comandante.eyeballs;

import com.comandante.eyeballs.api.EyeballsResource;
import com.comandante.eyeballs.camera.DetectedMotionImageCaptureDetectedListener;
import com.comandante.eyeballs.camera.MotionDetectionService;
import com.comandante.eyeballs.storage.LocalEventDatabase;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDriver;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.awt.*;
import java.io.File;

// For this webcam library to work on ARM you need
// mvn install:install-file -Dfile=./bridj-0.7-20140918.jar -DgroupId=com.nativelibs4java -DartifactId=bridj -Dversion=0.7-20140918 -Dpackaging=jar
// http://stackoverflow.com/questions/31844531/library-webcam-capture-by-sarxos-not-working-on-raspberry

// There is an alternative to using the hacked up version of bridj, you can use the v4l4j-0.9.1-r507.jar driver and webcam-capture-driver-v4l4j-0.3.11-20150713.101304-10.jar
// http://search.maven.org/remotecontent?filepath=com/github/sarxos/v4l4j/0.9.1-r507/v4l4j-0.9.1-r507.jar
// webcam-capture-driver-v4l4j-0.3.11-20150713.101304-10.jar

// KERNEL SHIT THAT MAKES THE USB WEBCAM WORK
// https://www.raspberrypi.org/forums/viewtopic.php?t=79883&p=565951
// You need to edit your boot cmdline.txt file i.e. sudo nano /boot/cmdline.txt and insert the following text somewhere: dwc_otg.fiq_fsm_mask=0x3

//Connect your camera module and configure in accordance to the instruction provided in the Raspberry Pi Documentation (enable it in raspi-config).
//If it's visible as /dev/videoN (N is some number, usually 0 if you do not have UVC devices connected to USB), you are free to go ahead. If this file is not available, then execute:
//sudo modprobe bcm2835-v4l2
//I placed this in /etc/rc.local


public class EyeballsApplication extends Application<EyeballsConfiguration> {

    public static void main(String[] args) throws Exception {
        new EyeballsApplication().run(args);
    }

    @Override
    public void run(EyeballsConfiguration eyeballsConfiguration, Environment environment) throws Exception {
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(new Dimension(640,480));
        webcam.open();
        DB db = DBMaker.newFileDB(new File(eyeballsConfiguration.getLocalStorageDirectory() + "/event_database")).closeOnJvmShutdown().make();
        LocalEventDatabase eyeballsMotionEventDatabase = new LocalEventDatabase(db, eyeballsConfiguration);
        DetectedMotionImageCaptureDetectedListener detectedMotionImageCaptureListener = new DetectedMotionImageCaptureDetectedListener(eyeballsMotionEventDatabase);
        MotionDetectionService motionDetectionService = new MotionDetectionService(detectedMotionImageCaptureListener);
        motionDetectionService.startAsync();
        motionDetectionService.awaitRunning();
        EyeballsResource eyeballsResource = new EyeballsResource(webcam, eyeballsMotionEventDatabase, motionDetectionService.getDetector());
        environment.jersey().register(eyeballsResource);
    }
}
