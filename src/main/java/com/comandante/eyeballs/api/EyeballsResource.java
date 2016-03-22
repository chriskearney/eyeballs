package com.comandante.eyeballs.api;

import com.comandante.eyeballs.camera.PictureTakingService;
import com.comandante.eyeballs.model.EventsApiResponse;
import com.comandante.eyeballs.model.LocalEvent;
import com.comandante.eyeballs.storage.LocalEventDatabase;
import com.github.sarxos.webcam.Webcam;
import com.google.common.collect.Lists;
import io.dropwizard.views.View;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Path("/")
public class EyeballsResource {

    private final Webcam webcam;
    private final LocalEventDatabase database;
    private final PictureTakingService pictureTakingService;

    public EyeballsResource(Webcam webcam, LocalEventDatabase database, PictureTakingService pictureTakingService) {
        this.webcam = webcam;
        this.database = database;
        this.pictureTakingService = pictureTakingService;
    }


    @GET
    @Path("/image")
    @Produces("image/png")
    public Response getImage() throws IOException {
        return Response.ok(pictureTakingService.getLatestImage()).build();
    }

    @GET
    @Path("/event/last/image")
    @Produces("image/png")
    public Response getLastMotionImage() throws IOException {
        return Response.ok(pictureTakingService.getLatestImage()).build();
    }

    @GET
    @Path("/event/recent")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EventsApiResponse> getRecentEvents() throws IOException {
        List<EventsApiResponse> currentEntries = Lists.newArrayList();
        for (LocalEvent next : database.getRecentEvents(10)) {
            currentEntries.add(new EventsApiResponse(next.getId(), next.getTimestamp()));
        }
        Collections.reverse(currentEntries);
        return currentEntries;
    }

    @GET
    @Path("/event/{eventId}")
    @Produces("image/jpg")
    public Response getEventImage(@PathParam("eventId") String eventId) {
        Optional<LocalEvent> eyeballMotionEvent = database.getEvent(eventId);
        if (!eyeballMotionEvent.isPresent()) {
            return Response.status(404).build();
        }
        byte[] pngImageBytes = eyeballMotionEvent.get().getImage();
        return Response.ok(pngImageBytes).build();
    }

    @GET
    @Path("/view/recent_events/{num}")
    @Produces(MediaType.TEXT_HTML)
    public View getRecentEventsView(@Context UriInfo uriInfo,
                                    @PathParam("num") long num) {
        return new EventsView(database.getRecentEvents(num), uriInfo);
    }

    @GET
    @Path("/view/recent_events/")
    @Produces(MediaType.TEXT_HTML)
    public View getRecentEventsView(@Context UriInfo uriInfo) {
        EventsView eventsView = new EventsView(database.getRecentEvents(30), uriInfo);
        return eventsView;
    }

    @GET
    @Path("/view/recent_events/image")
    @Produces(MediaType.TEXT_HTML)
    public View getRecentEventsViewImage(@Context UriInfo uriInfo) {
        EventsView eventsView = new EventsView(database.getRecentEvents(30), uriInfo);
        eventsView.setDisplayImage(true);
        return eventsView;
    }
}
