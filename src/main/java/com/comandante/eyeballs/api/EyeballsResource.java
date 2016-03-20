package com.comandante.eyeballs.api;

import com.comandante.eyeballs.camera.MotionDetector;
import com.comandante.eyeballs.model.EventsApiResponse;
import com.comandante.eyeballs.model.LocalEvent;
import com.comandante.eyeballs.storage.LocalEventDatabase;
import com.github.sarxos.webcam.Webcam;
import com.google.common.collect.Lists;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Path("/")
public class EyeballsResource {

    private final Webcam webcam;
    private final LocalEventDatabase database;
    private final MotionDetector motionDetector;

    public EyeballsResource(Webcam webcam, LocalEventDatabase database, MotionDetector motionDetector) {
        this.webcam = webcam;
        this.database = database;
        this.motionDetector = motionDetector;
    }


    @GET
    @Path("/image")
    @Produces("image/png")
    public Response getImage() throws IOException {
        return Response.ok(motionDetector.getLastImage()).build();
    }

    @GET
    @Path("/event/last/image")
    @Produces("image/png")
    public Response getLastMotionImage() throws IOException {
        return Response.ok(motionDetector.getLastMotionImage()).build();
    }

    @GET
    @Path("/event/recent")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EventsApiResponse> getEvents() throws IOException {
        List<EventsApiResponse> currentEntries = Lists.newArrayList();
        for (LocalEvent next : database.getEventQueue()) {
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
}