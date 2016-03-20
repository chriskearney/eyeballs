package com.comandante.eyeballs.storage;

import com.comandante.eyeballs.common.ConcurrentDateFormatAccess;
import com.comandante.eyeballs.EyeballsConfiguration;
import com.comandante.eyeballs.model.LocalEvent;
import com.comandante.eyeballs.camera.SaveMotionDetectedListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.util.concurrent.AbstractScheduledService;
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

public class CommitAndImageWriteService extends AbstractScheduledService {

    private final LinkedBlockingQueue<LocalEvent> events = new LinkedBlockingQueue<LocalEvent>();
    private final BTreeMap<String, LocalEvent> motionEventStore;
    private final DB db;
    private final EyeballsConfiguration eyeballsConfiguration;
    private final ConcurrentDateFormatAccess concurrentDateFormatAccess = new ConcurrentDateFormatAccess();
    private static Logger log = Logger.getLogger(SaveMotionDetectedListener.class.getName());

    public CommitAndImageWriteService(BTreeMap<String, LocalEvent> motionEventStore, DB db, EyeballsConfiguration eyeballsConfiguration) {
        this.motionEventStore = motionEventStore;
        this.db = db;
        this.eyeballsConfiguration = eyeballsConfiguration;
    }

    @Override
    protected void runOneIteration() throws Exception {
        ArrayList<LocalEvent> flush = Lists.newArrayList();
        events.drainTo(flush);
        HashMap<String, LocalEvent> flushMap = Maps.newHashMap();
        for (LocalEvent e: flush) {
            writeImageToDisk(e);
            e.setImage(null);
            flushMap.put(e.getId(), e);
        }
        motionEventStore.putAll(flushMap);
        db.commit();
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(1, 5, TimeUnit.SECONDS);
    }

    public void add(LocalEvent e) {
        this.events.add(e);
    }

    private void writeImageToDisk(LocalEvent localEvent) throws IOException, ParseException {
        String day = concurrentDateFormatAccess.convertDateToString(localEvent.getTimestamp());
        File outputImageFile = new File(eyeballsConfiguration.getLocalStorageDirectory() + "/event_images/" + day + "/" + localEvent.getId() + ".jpg");
        Files.createParentDirs(outputImageFile);
        Files.write(localEvent.getImage(), outputImageFile);
    }

    private void readImageFromDisk(LocalEvent localEvent) throws IOException, ParseException {
        String day = concurrentDateFormatAccess.convertDateToString(localEvent.getTimestamp());
        File inputImageFile = new File(eyeballsConfiguration.getLocalStorageDirectory() + "/event_images/" + day + "/" + localEvent.getId() + ".jpg");
        localEvent.setImage(Files.toByteArray(inputImageFile));
    }

    public Optional<LocalEvent> getEvent(String id) {
        LocalEvent localEvent = motionEventStore.get(id);
        if (localEvent == null) {
            return Optional.empty();
        }
        try {
            readImageFromDisk(localEvent);
        } catch (Exception e) {
            log.error("Unable to retrieve motion event image from disk store.", e);
        }
        return Optional.of(localEvent);
    }
}
