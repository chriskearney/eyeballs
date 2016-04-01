package com.comandante.eyeballs.motion_events;

import com.comandante.eyeballs.model.MotionEvent;

import java.util.Optional;

public interface MotionEventPersistence {

    Optional<MotionEvent> getEvent(String id);

}
