package com.comandante.eyeballs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.mapdb.BTreeMap;
import org.mapdb.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MapDbPutService extends AbstractScheduledService {

    private final LinkedBlockingQueue<EyeballMotionEvent> events = new LinkedBlockingQueue<EyeballMotionEvent>();
    private final BTreeMap<Long, EyeballMotionEvent> motionEventStore;
    private final DB db;

    public MapDbPutService(BTreeMap<Long, EyeballMotionEvent> motionEventStore, DB db) {
        this.motionEventStore = motionEventStore;
        this.db = db;
    }

    @Override
    protected void runOneIteration() throws Exception {
        ArrayList<EyeballMotionEvent> flush = Lists.newArrayList();
        events.drainTo(flush);
        HashMap<Long, EyeballMotionEvent> flushMap = Maps.newHashMap();
        for (EyeballMotionEvent e: flush) {
            flushMap.put(e.getTimeStamp().getTime(), e);
        }
        motionEventStore.putAll(flushMap);
        db.commit();
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(1, 1, TimeUnit.MINUTES);
    }

    public void add(EyeballMotionEvent e) {
        this.events.add(e);
    }
}
