package com.comandante.eyeballs.api;

import com.comandante.eyeballs.camera.PictureTakingService;
import com.comandante.eyeballs.model.EventsApiResponse;
import com.comandante.eyeballs.model.MotionEvent;
import com.comandante.eyeballs.motion_events.MotionEventProcessor;
import com.github.sarxos.webcam.Webcam;
import com.google.common.collect.Lists;
import io.dropwizard.views.View;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Path("/")
public class EyeballsResource {

    private final Webcam webcam;
    private final MotionEventProcessor database;
    private final PictureTakingService pictureTakingService;

    public EyeballsResource(Webcam webcam, MotionEventProcessor database, PictureTakingService pictureTakingService) {
        this.webcam = webcam;
        this.database = database;
        this.pictureTakingService = pictureTakingService;
    }

    @PermitAll
    @GET
    @Path("/image")
    @Produces("image/png")
    public Response getImage() throws IOException {
        return Response.ok(pictureTakingService.getLatestImage()).build();
    }

    @PermitAll
    @GET
    @Path("/event/recent/{num}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EventsApiResponse> getRecentEvents(@PathParam("num") int num) throws IOException {
        List<EventsApiResponse> currentEntries = Lists.newArrayList();
        for (MotionEvent next : database.getRecentEvents(num)) {
            currentEntries.add(new EventsApiResponse(next.getId(), next.getTimestamp()));
        }
        Collections.reverse(currentEntries);
        return currentEntries;
    }

    @PermitAll
    @GET
    @Path("/event/{eventId}")
    @Produces("image/jpg")
    public Response getEventImage(@PathParam("eventId") String eventId) {
        Optional<MotionEvent> eyeballMotionEvent = database.getEvent(eventId);
        if (!eyeballMotionEvent.isPresent()) {
            return Response.status(404).build();
        }
        byte[] pngImageBytes = eyeballMotionEvent.get().getImage();
        return Response.ok(pngImageBytes).build();
    }

    @PermitAll
    @GET
    @Path("/view/recent_events/{num}")
    @Produces(MediaType.TEXT_HTML)
    public View getRecentEventsView(@Context UriInfo uriInfo,
                                    @PathParam("num") long num) {
        return new EventsView(database.getRecentEvents(num), uriInfo);
    }

    @PermitAll
    @GET
    @Path("/view/recent_events/")
    @Produces(MediaType.TEXT_HTML)
    public View getRecentEventsView(@Context UriInfo uriInfo) {
        EventsView eventsView = new EventsView(database.getRecentEvents(30), uriInfo);
        return eventsView;
    }

    @PermitAll
    @GET
    @Path("/view/recent_events/image")
    @Produces(MediaType.TEXT_HTML)
    public View getRecentEventsViewImage(@Context UriInfo uriInfo) {
        EventsView eventsView = new EventsView(database.getRecentEvents(30), uriInfo);
        eventsView.setDisplayImage(true);
        return eventsView;
    }
}
