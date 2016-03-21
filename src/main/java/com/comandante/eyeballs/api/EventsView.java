package com.comandante.eyeballs.api;

import com.comandante.eyeballs.model.LocalEvent;
import com.comandante.eyeballs.storage.LocalEventDatabase;
import com.google.common.collect.Lists;
import io.dropwizard.views.View;

import java.util.List;

public class EventsView extends View {

    private static final String TEMPLATE_NAME = "events.ftl";

    public final List<LocalEvent> events;

    public EventsView(List<LocalEvent> events) {
        super(TEMPLATE_NAME);
        this.events = events;
    }

    public List<LocalEvent> getEvents() {
        return events;
    }
}
