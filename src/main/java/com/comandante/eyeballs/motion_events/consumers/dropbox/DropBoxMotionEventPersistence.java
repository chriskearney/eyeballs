package com.comandante.eyeballs.motion_events.consumers.dropbox;

import com.comandante.eyeballs.EyeballsConfiguration;
import com.comandante.eyeballs.common.ConcurrentDateFormatAccess;
import com.comandante.eyeballs.model.MotionEvent;
import com.comandante.eyeballs.motion_events.MotionEventPersistence;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import org.apache.log4j.Logger;
import org.mapdb.BTreeMap;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Optional;

public class DropBoxMotionEventPersistence implements MotionEventPersistence {

    private final ConcurrentDateFormatAccess concurrentDateFormatAccess = new ConcurrentDateFormatAccess();
    private static Logger log = Logger.getLogger(DropboxMotionEventConsumer.class.getName());
    private final DbxClientV2 dbxClientV2;
    private final BTreeMap<String, MotionEvent> motionEventStore;

    public DropBoxMotionEventPersistence(EyeballsConfiguration eyeballsConfiguration, BTreeMap<String, MotionEvent> motionEventStore) {
        dbxClientV2 = new DbxClientV2(new DbxRequestConfig("Eyeballs/1.0", Locale.getDefault().toString()),
                eyeballsConfiguration.getDropBoxAccessToken());
        this.motionEventStore = motionEventStore;
    }

    @Override
    public Optional<MotionEvent> getEvent(String id) {
        MotionEvent motionEvent = motionEventStore.get(id);
        try {
            String dropboxPath = "/motion_events/" + concurrentDateFormatAccess.convertDateToString(motionEvent.getTimestamp());
            DbxDownloader<FileMetadata> dbxDownloader = dbxClientV2.files().download(dropboxPath + "/" + motionEvent.getId() + ".jpg");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            dbxDownloader.download(byteArrayOutputStream);
            return Optional.of(new MotionEvent(id, motionEvent.getTimestamp(), byteArrayOutputStream.toByteArray()));
        } catch (Exception e) {
            log.error("Problem retrieving motion event: " + motionEvent.getId() + " from dropbox.", e);
            return Optional.empty();
        }
    }
}
