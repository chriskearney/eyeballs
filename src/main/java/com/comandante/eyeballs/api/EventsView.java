package com.comandante.eyeballs.api;

import com.comandante.eyeballs.model.LocalEvent;
import com.comandante.eyeballs.storage.LocalEventDatabase;
import com.google.common.collect.Lists;
import io.dropwizard.views.View;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

public class EventsView extends View {

    private static final String TEMPLATE_NAME = "events.ftl";

    public final List<LocalEvent> events;
    private final String baseUrl;


    public EventsView(List<LocalEvent> events, UriInfo uriInfo) {
        super(TEMPLATE_NAME);
        this.events = events;
        baseUrl = uriInfo.getAbsolutePath().toString();
    }

    public List<LocalEvent> getEvents() {
        return events;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
