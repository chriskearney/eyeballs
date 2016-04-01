package com.comandante.eyeballs.motion_events.consumers.local_fs;

import com.comandante.eyeballs.EyeballsConfiguration;
import com.comandante.eyeballs.camera.SaveMotionDetectedListener;
import com.comandante.eyeballs.common.ConcurrentDateFormatAccess;
import com.comandante.eyeballs.model.LocalEventSerializer;
import com.comandante.eyeballs.model.MotionEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.comandante.eyeballs.motion_events.consumers.MotionEventConsumer;
import org.apache.log4j.Logger;
import org.mapdb.BTreeMap;
import org.mapdb.DB;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class LocalFSMotionEventConsumer extends AbstractScheduledService implements MotionEventConsumer {

    private final LinkedBlockingQueue<MotionEvent> events = new LinkedBlockingQueue<MotionEvent>();
    private final BTreeMap<String, MotionEvent> motionEventStore;
    private final DB db;
    private final EyeballsConfiguration eyeballsConfiguration;
    private final ConcurrentDateFormatAccess concurrentDateFormatAccess = new ConcurrentDateFormatAccess();
    private static Logger log = Logger.getLogger(SaveMotionDetectedListener.class.getName());

    public LocalFSMotionEventConsumer(DB db, EyeballsConfiguration eyeballsConfiguration) {
        this.motionEventStore = db
                .createTreeMap("motionEventStore")
                .valueSerializer(new LocalEventSerializer())
                .makeOrGet();
        this.db = db;
        this.eyeballsConfiguration = eyeballsConfiguration;
    }

    @Override
    protected void runOneIteration() throws Exception {
        ArrayList<MotionEvent> flush = Lists.newArrayList();
        events.drainTo(flush);
        HashMap<String, MotionEvent> flushMap = Maps.newHashMap();
        for (MotionEvent e: flush) {
            writeImageToDisk(e);
            flushMap.put(e.getId(), new MotionEvent(e.getId(), e.getTimestamp(), null));
        }
        motionEventStore.putAll(flushMap);
        db.commit();
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(1, 5, TimeUnit.SECONDS);
    }

    public void add(MotionEvent e) {
        this.events.add(e);
    }

    private void writeImageToDisk(MotionEvent motionEvent) throws IOException, ParseException {
        String day = concurrentDateFormatAccess.convertDateToString(motionEvent.getTimestamp());
        File outputImageFile = new File(eyeballsConfiguration.getLocalStorageDirectory() + "/event_images/" + day + "/" + motionEvent.getId() + ".jpg");
        Files.createParentDirs(outputImageFile);
        Files.write(motionEvent.getImage(), outputImageFile);
    }

    private byte[] readImageFromDisk(MotionEvent motionEvent) throws IOException, ParseException {
        String day = concurrentDateFormatAccess.convertDateToString(motionEvent.getTimestamp());
        File inputImageFile = new File(eyeballsConfiguration.getLocalStorageDirectory() + "/event_images/" + day + "/" + motionEvent.getId() + ".jpg");
        return Files.toByteArray(inputImageFile);
    }

    public Optional<MotionEvent> getEvent(String id) {
        MotionEvent motionEvent = motionEventStore.get(id);
        MotionEvent retMotionEvent = null;
        if (motionEvent == null) {
            return Optional.empty();
        }
        try {
            byte[] bytes = readImageFromDisk(motionEvent);
            retMotionEvent = new MotionEvent(motionEvent.getId(), motionEvent.getTimestamp(), bytes);
        } catch (Exception e) {
            log.error("Unable to retrieve motion event image from disk store.", e);
        }
        return Optional.ofNullable(retMotionEvent);
    }
}
