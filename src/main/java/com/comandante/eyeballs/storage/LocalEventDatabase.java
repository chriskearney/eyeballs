package com.comandante.eyeballs.storage;

import com.comandante.eyeballs.EyeballsConfiguration;
import com.comandante.eyeballs.model.LocalEvent;
import com.comandante.eyeballs.model.LocalEventSerializer;
import com.google.common.collect.EvictingQueue;
import org.mapdb.BTreeMap;
import org.mapdb.DB;

import java.util.Optional;

public class LocalEventDatabase {

    private final BTreeMap<String, LocalEvent> motionEventStore;
    private final CommitAndImageWriteService commitAndImageWriteService;
    private final EyeballsConfiguration eyeballsConfiguration;

    EvictingQueue<LocalEvent> eventQueue = EvictingQueue.create(60);

    public LocalEventDatabase(DB db, EyeballsConfiguration eyeballsConfiguration) {
        this.eyeballsConfiguration = eyeballsConfiguration;
        this.motionEventStore = db
                .createTreeMap("motionEventStore")
                .valueSerializer(new LocalEventSerializer())
                .makeOrGet();
        this.commitAndImageWriteService = new CommitAndImageWriteService(motionEventStore, db, eyeballsConfiguration);
        commitAndImageWriteService.startAsync();
    }

    public void save(LocalEvent event) {
        eventQueue.add(event);
        commitAndImageWriteService.add(event);
    }

    public EvictingQueue<LocalEvent> getEventQueue() {
        return eventQueue;
    }

    public Optional<LocalEvent> getEvent(String id) {
        return commitAndImageWriteService.getEvent(id);
    }
}
