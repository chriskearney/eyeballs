package com.comandante.eyeballs.motion_events.consumers.dropbox;

import com.comandante.eyeballs.EyeballsConfiguration;
import com.comandante.eyeballs.common.ConcurrentDateFormatAccess;
import com.comandante.eyeballs.model.MotionEvent;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.comandante.eyeballs.motion_events.consumers.MotionEventConsumer;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DropboxMotionEventConsumer extends AbstractScheduledService implements MotionEventConsumer {

    private final LinkedBlockingQueue<MotionEvent> events = new LinkedBlockingQueue<MotionEvent>();
    private final ConcurrentDateFormatAccess concurrentDateFormatAccess = new ConcurrentDateFormatAccess();
    private static Logger log = Logger.getLogger(DropboxMotionEventConsumer.class.getName());
    private final DbxClientV2 dbxClientV2;

    public DropboxMotionEventConsumer(EyeballsConfiguration eyeballsConfiguration) {
        dbxClientV2 = new DbxClientV2(new DbxRequestConfig("Eyeballs/1.0", Locale.getDefault().toString()),
                        eyeballsConfiguration.getDropBoxAccessToken());
    }

    @Override
    public void add(MotionEvent motionEvent) {
        this.events.add(motionEvent);
    }

    @Override
    protected void runOneIteration() throws Exception {
        ArrayList<MotionEvent> flush = Lists.newArrayList();
        events.drainTo(flush);
        uploadFiles(flush);
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(5, 5, TimeUnit.SECONDS);
    }

    private void uploadFiles(List<MotionEvent> motionEvents) throws IOException {
        for (MotionEvent motionEvent : motionEvents) {
            try (InputStream in = new ByteArrayInputStream(motionEvent.getImage())) {
                String dropboxPath = "/com/comandante/eyeballs/motion_events/" + concurrentDateFormatAccess.convertDateToString(motionEvent.getTimestamp());
                createFolder(dropboxPath);

                FileMetadata metadata = dbxClientV2.files().uploadBuilder(dropboxPath + "/" + motionEvent.getId() + ".jpg")
                        .withMode(WriteMode.ADD)
                        .withClientModified(motionEvent.getTimestamp())
                        .uploadAndFinish(in);

                log.debug(metadata.toStringMultiline());
            } catch (Exception e) {
                log.error("Error uploading to Dropbox. ", e);
            }
        }
    }

    private void createFolder(String dropboxPath) {
        try {
            dbxClientV2.files().createFolder(dropboxPath);
        } catch (Exception e) {
            // Gulp.
            log.trace("Create folder response.", e);
        }

    }
}
