package com.comandante.eyeballs;

import com.google.common.collect.EvictingQueue;
import org.mapdb.BTreeMap;
import org.mapdb.DB;

public class EyeballsMotionEventDatabase {

    private final BTreeMap<Long, EyeballMotionEvent> motionEventStore;
    private final MapDbPutService mapDbPutService;

    EvictingQueue<EyeballMotionEvent> eventQueue = EvictingQueue.create(60);

    public EyeballsMotionEventDatabase(DB db) {
        this.motionEventStore = db
                .createTreeMap("motionEventStore")
                .valueSerializer(new EyeballMotionEventSerializer())
                .makeOrGet();
        this.mapDbPutService = new MapDbPutService(motionEventStore, db);
        mapDbPutService.startAsync();
    }

    public void save(EyeballMotionEvent event) {
        eventQueue.add(event);
        mapDbPutService.add(event);
    }

    public BTreeMap<Long, EyeballMotionEvent> getMotionEventStore() {
        return motionEventStore;
    }

    public EvictingQueue<EyeballMotionEvent> getEventQueue() {
        return eventQueue;
    }
}
