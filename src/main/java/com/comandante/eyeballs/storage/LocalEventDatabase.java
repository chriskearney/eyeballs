package com.comandante.eyeballs.storage;

import com.comandante.eyeballs.EyeballsConfiguration;
import com.comandante.eyeballs.model.LocalEvent;
import com.comandante.eyeballs.model.LocalEventSerializer;
import com.google.common.collect.Lists;
import org.mapdb.BTreeMap;
import org.mapdb.DB;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;

public class LocalEventDatabase {

    private final BTreeMap<String, LocalEvent> motionEventStore;
    private final CommitAndImageWriteService commitAndImageWriteService;
    private final List<MotionEventPersistence> motionEventPersistences;

    public LocalEventDatabase(DB db, EyeballsConfiguration eyeballsConfiguration, List<MotionEventPersistence> motionEventPersistences) {
        this.motionEventStore = db
                .createTreeMap("motionEventStore")
                .valueSerializer(new LocalEventSerializer())
                .makeOrGet();
        this.commitAndImageWriteService = new CommitAndImageWriteService(motionEventStore, db, eyeballsConfiguration);
        commitAndImageWriteService.startAsync();
        this.motionEventPersistences = Lists.newArrayList();
        this.motionEventPersistences.add(commitAndImageWriteService);
        this.motionEventPersistences.addAll(motionEventPersistences);
    }

    public void save(LocalEvent event) {
        for (MotionEventPersistence motionEventPersistence: motionEventPersistences) {
            motionEventPersistence.add(event);
        }
    }

    public Optional<LocalEvent> getEvent(String id) {
        return commitAndImageWriteService.getEvent(id);
    }

    public List<LocalEvent> getRecentEvents(long num) {
        List<LocalEvent> events = Lists.newArrayList();
        ConcurrentNavigableMap<String, LocalEvent> stringLocalEventConcurrentNavigableMap = motionEventStore.descendingMap();
        Set<Map.Entry<String, LocalEvent>> entries = stringLocalEventConcurrentNavigableMap.entrySet();
        int i = 0;
        for (Map.Entry<String, LocalEvent> event : entries) {
            if (i >= num) {
                return events;
            }
            events.add(event.getValue());
            i++;
        }
        return events;
    }
}
