package com.comandante.eyeballs.api;

import com.comandante.eyeballs.model.LocalEvent;
import com.comandante.eyeballs.storage.LocalEventDatabase;
import com.google.common.collect.Lists;
import io.dropwizard.views.View;

import java.util.List;

public class EventsView extends View {

    private static final String TEMPLATE_NAME = "events.ftl";

    private final LocalEventDatabase localEventDatabase;

    public EventsView(String templateName, LocalEventDatabase localEventDatabase) {
        super(TEMPLATE_NAME);
        this.localEventDatabase = localEventDatabase;
    }

    public List<LocalEvent> getRecentEvents(int num) {
        return Lists.newArrayList(localEventDatabase.getEventQueue().iterator());
    }
}
