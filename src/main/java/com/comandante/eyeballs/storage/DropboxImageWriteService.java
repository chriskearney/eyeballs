package com.comandante.eyeballs.storage;

import com.comandante.eyeballs.EyeballsConfiguration;
import com.comandante.eyeballs.common.ConcurrentDateFormatAccess;
import com.comandante.eyeballs.model.LocalEvent;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DropboxImageWriteService extends AbstractScheduledService implements MotionEventPersistence {

    private final LinkedBlockingQueue<LocalEvent> events = new LinkedBlockingQueue<LocalEvent>();
    private final ConcurrentDateFormatAccess concurrentDateFormatAccess = new ConcurrentDateFormatAccess();
    private final EyeballsConfiguration eyeballsConfiguration;
    private static Logger log = Logger.getLogger(DropboxImageWriteService.class.getName());
    private final DbxClientV2 dbxClientV2;

    public DropboxImageWriteService(EyeballsConfiguration eyeballsConfiguration) {
        this.eyeballsConfiguration = eyeballsConfiguration;
        DbxRequestConfig config = new DbxRequestConfig("Eyeballs/1.0", Locale.getDefault().toString());
        dbxClientV2 = new DbxClientV2(config, eyeballsConfiguration.getDropBoxAccessToken());
    }

    @Override
    public void add(LocalEvent localEvent) {
        this.events.add(localEvent);
    }

    @Override
    protected void runOneIteration() throws Exception {
        ArrayList<LocalEvent> flush = Lists.newArrayList();
        events.drainTo(flush);
        uploadFiles(flush);
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(5, 5, TimeUnit.SECONDS);
    }

    private void uploadFiles(List<LocalEvent> localEvents) throws IOException {
        for (LocalEvent localEvent : localEvents) {
            try (InputStream in = new ByteArrayInputStream(localEvent.getImage())) {
                String dropboxPath = "/motion_events/" + concurrentDateFormatAccess.convertDateToString(localEvent.getTimestamp());

                createFolder(dropboxPath);

                FileMetadata metadata = dbxClientV2.files().uploadBuilder(dropboxPath + "/" + localEvent.getId() + ".jpg")
                        .withMode(WriteMode.ADD)
                        .withClientModified(localEvent.getTimestamp())
                        .uploadAndFinish(in);

                log.info(metadata.toStringMultiline());
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
