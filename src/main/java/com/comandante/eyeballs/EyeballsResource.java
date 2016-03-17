package com.comandante.eyeballs;

import com.github.sarxos.webcam.Webcam;
import com.google.common.collect.Lists;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

@Path("/")
public class EyeballsResource {

    private final Webcam webcam;
    private final EyeballsMotionEventDatabase database;
    private final PictureTakingService pictureTakingService;

    public EyeballsResource(Webcam webcam, EyeballsMotionEventDatabase database, PictureTakingService pictureTakingService) {
        this.webcam = webcam;
        this.database = database;
        this.pictureTakingService = pictureTakingService;
    }


    @GET
    @Path("/image")
    @Produces("image/png")
    public Response getImage() throws IOException {
        return Response.ok(pictureTakingService.getImage()).build();
    }

    @GET
    @Path("/event/recent")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EventsApiResponse> getEvents() throws IOException {
        List<EventsApiResponse> currentEntries = Lists.newArrayList();
        for (EyeballMotionEvent next : database.getEventQueue()) {
            currentEntries.add(new EventsApiResponse(next.getTimeStamp().getTime(), next.getTimeStamp()));
        }
        Collections.reverse(currentEntries);
        return currentEntries;
    }

    @GET
    @Path("/event/{eventId}")
    @Produces("image/jpg")
    public Response getEventImage(@PathParam("eventId") String eventId) {
        EyeballMotionEvent eyeballMotionEvent = database.getMotionEventStore().get(Long.parseLong(eventId));
        byte[] pngImageBytes = eyeballMotionEvent.getImageBytes();
        return Response.ok(pngImageBytes).build();
    }
}
