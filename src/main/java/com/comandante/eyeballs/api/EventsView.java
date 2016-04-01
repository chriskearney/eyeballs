package com.comandante.eyeballs.api;

import com.comandante.eyeballs.model.MotionEvent;
import io.dropwizard.views.View;

import javax.ws.rs.core.UriInfo;
import java.util.List;

public class EventsView extends View {

    private static final String TEMPLATE_NAME = "events.ftl";

    public final List<MotionEvent> events;
    private final String baseUrl;
    private boolean displayImage = false;

    public EventsView(List<MotionEvent> events, UriInfo uriInfo) {
        super(TEMPLATE_NAME);
        this.events = events;
        String url = uriInfo.getBaseUri().toString();
        char c = url.charAt(url.length() - 1);
        if (c == '/') {
            url = url.substring(0, url.length()-1);
        }
        baseUrl = url;
    }

    public List<MotionEvent> getEvents() {
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
