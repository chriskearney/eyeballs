package com.comandante.eyeballs;

import java.util.Date;

public class EventsApiResponse {

    private Long eventId;
    private Date timestamp;

    public EventsApiResponse(Long eventId, Date timestamp) {
        this.eventId = eventId;
        this.timestamp = timestamp;
    }

    public Long getEventId() {
        return eventId;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
