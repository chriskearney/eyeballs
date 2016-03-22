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
    private boolean displayImage = false;

    public EventsView(List<LocalEvent> events, UriInfo uriInfo) {
        super(TEMPLATE_NAME);
        this.events = events;
        String url = uriInfo.getBaseUri().toString();
        char c = url.charAt(url.length() - 1);
        if (c == '/') {
            url = url.substring(0, url.length()-1);
        }
        baseUrl = url;
    }

    public List<LocalEvent> getEvents() {
        return events;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean isDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(boolean displayImage) {
        this.displayImage = displayImage;
    }
}
