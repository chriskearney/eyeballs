package com.comandante.eyeballs.motion_events;

import com.comandante.eyeballs.model.MotionEvent;
import com.comandante.eyeballs.motion_events.consumers.local_fs.LocalFSMotionEventConsumer;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.Service;

import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;

public class MotionEventProcessor {

    private final Optional<LocalFSMotionEventConsumer> localFSMotionEventConsumer;
    private final List<MotionEventConsumer> motionEventConsumers;

    private MotionEventProcessor(Optional<LocalFSMotionEventConsumer> localFSMotionEventConsumer,
                                 List<MotionEventConsumer> motionEventConsumers) {
        this.localFSMotionEventConsumer = localFSMotionEventConsumer;
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
        ConcurrentNavigableMap<String, MotionEvent> stringLocalEventConcurrentNavigableMap =
                localFSMotionEventConsumer.get().getMotionEventStore().descendingMap();
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

    public static class Builder {
        private List<MotionEventConsumer> motionEventConsumers = Lists.newArrayList();
        private LocalFSMotionEventConsumer localFSMotionEventConsumer;

        public Builder addMotionEventConsumer(MotionEventConsumer motionEventConsumer) {
            if (motionEventConsumer instanceof Service) {
                AbstractScheduledService consumer = (AbstractScheduledService) motionEventConsumer;
                if (!consumer.isRunning()) {
                    consumer.startAsync();
                }
            }
            if (motionEventConsumer instanceof LocalFSMotionEventConsumer) {
                localFSMotionEventConsumer = (LocalFSMotionEventConsumer) motionEventConsumer;
            }
            this.motionEventConsumers.add(motionEventConsumer);
            return this;
        }

        public MotionEventProcessor build() {
            return new MotionEventProcessor(Optional.ofNullable(localFSMotionEventConsumer), motionEventConsumers);
        }
    }
}
