package com.comandante.eyeballs.motion_events;

import com.comandante.eyeballs.model.MotionEvent;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.Service;
import org.mapdb.BTreeMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;

import static java.util.Objects.requireNonNull;

public class MotionEventProcessor {

    private final List<MotionEventConsumer> motionEventConsumers;
    private final BTreeMap<String, MotionEvent> motionEventStore;
    private final MotionEventPersistence motionEventPersistence;

    private MotionEventProcessor(List<MotionEventConsumer> motionEventConsumers,
                                 BTreeMap<String, MotionEvent> motionEventStore,
                                 MotionEventPersistence motionEventPersistence) {
        this.motionEventConsumers = Lists.newArrayList(motionEventConsumers);
        this.motionEventStore = motionEventStore;
        this.motionEventPersistence = motionEventPersistence;
    }

    public void save(MotionEvent event) {
        for (MotionEventConsumer motionEventConsumer : motionEventConsumers) {
            motionEventConsumer.add(event);
        }
    }

    public Optional<MotionEvent> getEvent(String id) {
        return motionEventPersistence.getEvent(id);
    }

    public List<MotionEvent> getRecentEvents(long num) {
        List<MotionEvent> events = Lists.newArrayList();
        ConcurrentNavigableMap<String, MotionEvent> stringLocalEventConcurrentNavigableMap =
                motionEventStore.descendingMap();
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
        private MotionEventPersistence motionEventPersistence;
        private BTreeMap<String, MotionEvent> motionEventStore;

        public Builder motionEventStore(BTreeMap<String, MotionEvent> motionEventStore) {
            this.motionEventStore = motionEventStore;
            return this;
        }

        public Builder motionEventPersitence(MotionEventPersistence motionEventPersistence) {
            this.motionEventPersistence = motionEventPersistence;
            return this;
        }

        public Builder addMotionEventConsumer(MotionEventConsumer motionEventConsumer) {
            if (motionEventConsumer instanceof Service) {
                AbstractScheduledService consumer = (AbstractScheduledService) motionEventConsumer;
                if (!consumer.isRunning()) {
                    consumer.startAsync();
                }
            }
            this.motionEventConsumers.add(motionEventConsumer);
            return this;
        }

        public MotionEventProcessor build() {
            requireNonNull(motionEventStore);
            return new MotionEventProcessor(motionEventConsumers, motionEventStore, motionEventPersistence);
        }
    }
}
