package com.comandante.eyeballs.model;

import java.util.Date;

public class EventsApiResponse {

    private String eventId;
    private Date timestamp;

    public EventsApiResponse(String eventId, Date timestamp) {
        this.eventId = eventId;
        this.timestamp = timestamp;
    }

    public String getEventId() {
        return eventId;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
