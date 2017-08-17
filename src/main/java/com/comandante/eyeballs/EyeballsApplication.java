package com.comandante.eyeballs;

import com.comandante.eyeballs.api.BasicAuthenticator;
import com.comandante.eyeballs.api.EyeballsResource;
import com.comandante.eyeballs.camera.PictureTakingService;
import com.comandante.eyeballs.camera.SaveMotionDetectedListener;
import com.comandante.eyeballs.camera.webcam.MotionDetectionService;
import com.comandante.eyeballs.model.LocalEventSerializer;
import com.comandante.eyeballs.model.MotionEvent;
import com.comandante.eyeballs.motion_events.MotionEventProcessor;
import com.comandante.eyeballs.motion_events.consumers.dropbox.DropBoxMotionEventPersistence;
import com.comandante.eyeballs.motion_events.consumers.dropbox.DropboxMotionEventConsumer;
import com.comandante.eyeballs.motion_events.consumers.local_fs.LocalFSMotionEventConsumer;
import com.comandante.eyeballs.motion_events.consumers.sftp.SftpMotionEventConsumer;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public void initialize(Bootstrap<EyeballsConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle<EyeballsConfiguration>());
    }

    @Override
    public void run(EyeballsConfiguration eyeballsConfiguration, Environment environment) throws Exception {

        Security.addProvider(new BouncyCastleProvider());

        createUnderylingStorageDirectories(eyeballsConfiguration);

        if (eyeballsConfiguration.getUseAuth()) {
            environment.jersey().register(new AuthDynamicFeature(
                    new BasicCredentialAuthFilter.Builder<BasicAuthenticator.EyeballUser>()
                            .setAuthenticator(new BasicAuthenticator(eyeballsConfiguration))
                            .setRealm("Eyeballs Motion Detection Server")
                            .buildAuthFilter()));
        }
        DB db = buildMapDb(eyeballsConfiguration);
        BTreeMap<String, MotionEvent> motionEventStore = db.createTreeMap("motionEventStore")
                .valueSerializer(new LocalEventSerializer())
                .makeOrGet();

        Webcam webcam = Webcam.getDefault();
        if (webcam == null) {
            throw new RuntimeException("No webcam present, or not available to the current user.");
        }

        PictureTakingService pictureTakingService = new PictureTakingService(webcam);
        webcam.addWebcamListener(pictureTakingService);

        Dimension[] dimensions = (Dimension[]) Arrays.stream(WebcamResolution.values())
                .map(WebcamResolution::getSize)
                .collect(Collectors.toList())
                .toArray();

        webcam.setCustomViewSizes(dimensions);
        webcam.setViewSize(new Dimension(eyeballsConfiguration.getImageWidth(), eyeballsConfiguration.getImageHeight()));
        webcam.open();

        MotionEventProcessor.Builder processorBuilder = new MotionEventProcessor.Builder();

        if (eyeballsConfiguration.getUseSftp()) {
            processorBuilder.addMotionEventConsumer(new SftpMotionEventConsumer(eyeballsConfiguration));
        }

        if (eyeballsConfiguration.getUseDropbox()) {
            processorBuilder.addMotionEventConsumer(new DropboxMotionEventConsumer(eyeballsConfiguration));
        }

        if (eyeballsConfiguration.getUseLocalPersistence()) {
            LocalFSMotionEventConsumer localFSMotionEventConsumer = new LocalFSMotionEventConsumer(db, eyeballsConfiguration, motionEventStore);
            processorBuilder.addMotionEventConsumer(localFSMotionEventConsumer);
            processorBuilder.motionEventPersitence(localFSMotionEventConsumer);
        }

        if (eyeballsConfiguration.getUseDropboxPersistence()) {
            processorBuilder.motionEventPersitence(new DropBoxMotionEventPersistence(eyeballsConfiguration, motionEventStore));
        }

        processorBuilder.motionEventStore(motionEventStore);

        MotionEventProcessor motionEventProcessor = processorBuilder.build();

        MotionDetectionService motionDetectionService = new MotionDetectionService(eyeballsConfiguration,
                new SaveMotionDetectedListener(motionEventProcessor, eyeballsConfiguration));
        motionDetectionService.startAndWait();

        EyeballsResource eyeballsResource = new EyeballsResource(webcam, motionEventProcessor, pictureTakingService);

        environment.jersey().register(eyeballsResource);
    }

    private static void createUnderylingStorageDirectories(EyeballsConfiguration eyeballsConfiguration) throws IOException {
        File file = new File(eyeballsConfiguration.getLocalStorageDirectory() + "/event_database");
        com.google.common.io.Files.createParentDirs(file);
    }

    private static DB buildMapDb(EyeballsConfiguration eyeballsConfiguration) {
        return DBMaker.newFileDB(new File(eyeballsConfiguration.getLocalStorageDirectory() + "/event_database"))
                .closeOnJvmShutdown()
                .make();
    }
}
