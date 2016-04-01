package com.comandante.eyeballs.motion_events;

import com.comandante.eyeballs.model.MotionEvent;
import com.comandante.eyeballs.model.LocalEventSerializer;
import com.comandante.eyeballs.motion_events.consumers.local_fs.LocalFSMotionEventConsumer;
import com.comandante.eyeballs.motion_events.consumers.MotionEventConsumer;
import com.google.common.collect.Lists;
import org.mapdb.BTreeMap;
import org.mapdb.DB;

import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;

public class MotionEventProcessor {

    private final BTreeMap<String, MotionEvent> motionEventStore;
    private final Optional<LocalFSMotionEventConsumer> localFSMotionEventConsumer;
    private final List<MotionEventConsumer> motionEventConsumers;

    public MotionEventProcessor(DB db, List<MotionEventConsumer> motionEventConsumers) {
        this.motionEventStore = db
                .createTreeMap("motionEventStore")
                .valueSerializer(new LocalEventSerializer())
                .makeOrGet();
        this.localFSMotionEventConsumer = getLocalFSMotionEventConsumer(motionEventConsumers);
        this.motionEventConsumers = Lists.newArrayList();
        this.motionEventConsumers.addAll(motionEventConsumers);
    }

    public void save(MotionEvent event) {
        for (MotionEventConsumer motionEventConsumer : motionEventConsumers) {
            motionEventConsumer.add(event);
        }
    }

    public Optional<MotionEvent> getEvent(String id) {
        if (localFSMotionEventConsumer.isPresent()) {
            return localFSMotionEventConsumer.get().getEvent(id);
        }
        return Optional.empty();
    }

    public List<MotionEvent> getRecentEvents(long num) {
        List<MotionEvent> events = Lists.newArrayList();
        if (!localFSMotionEventConsumer.isPresent()) {
            return events;
        }
        ConcurrentNavigableMap<String, MotionEvent> stringLocalEventConcurrentNavigableMap = motionEventStore.descendingMap();
        Set<Map.Entry<String, MotionEvent>> entries = stringLocalEventConcurrentNavigableMap.entrySet();
        int i = 0;
        for (Map.Entry<String, MotionEvent> event : entries) {
            if (i >= num) {
                return events;
            }
            events.add(event.getValue());
            i++;
        }
        return events;
    }

    private static Optional<LocalFSMotionEventConsumer> getLocalFSMotionEventConsumer(List<MotionEventConsumer> motionEventConsumers) {
        LocalFSMotionEventConsumer localFSMotionEventConsumer = null;
        for (MotionEventConsumer motionEventConsumer: motionEventConsumers) {
            if (motionEventConsumer instanceof LocalFSMotionEventConsumer) {
                localFSMotionEventConsumer = (LocalFSMotionEventConsumer) motionEventConsumer;
            }
        }
        return Optional.ofNullable(localFSMotionEventConsumer);
    }
}
