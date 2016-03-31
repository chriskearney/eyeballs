package com.comandante.eyeballs.storage;

import com.comandante.eyeballs.EyeballsConfiguration;
import com.comandante.eyeballs.common.ConcurrentDateFormatAccess;
import com.comandante.eyeballs.model.LocalEvent;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractScheduledService;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SftpImageWriteService extends AbstractScheduledService implements MotionEventPersistence {

    private final ConcurrentDateFormatAccess concurrentDateFormatAccess = new ConcurrentDateFormatAccess();
    private final LinkedBlockingQueue<LocalEvent> events = new LinkedBlockingQueue<>();
    private final EyeballsConfiguration eyeballsConfiguration;
    private SSHClient sshClient;
    private static Logger log = Logger.getLogger(SftpImageWriteService.class.getName());

    public SftpImageWriteService(EyeballsConfiguration eyeballsConfiguration) {
        this.eyeballsConfiguration = eyeballsConfiguration;
    }

    @Override
    public void add(LocalEvent localEvent) {
        this.events.add(localEvent);
    }

    @Override
    protected void runOneIteration() throws Exception {
        ArrayList<LocalEvent> flush = Lists.newArrayList();
        events.drainTo(flush);
        if (flush.size() > 0) {
            try {
                configureSsh();
                transferEvents(flush);
            } catch (Exception e) {
                log.error("Problem uploading motion events via SCP.", e);
            } finally {
                sshClient.disconnect();
            }
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(5, 5, TimeUnit.SECONDS);
    }

    private void configureSsh() throws IOException {
        sshClient = new SSHClient();
        sshClient.loadKnownHosts();
        sshClient.connect(eyeballsConfiguration.getSftpDestinationHost(), eyeballsConfiguration.getSftpRemotePort());
        sshClient.authPublickey(eyeballsConfiguration.getSftpUsername());
        sshClient.useCompression();
    }

    private void transferEvents(List<LocalEvent> localEvents) throws IOException, ParseException {
        for (LocalEvent localEvent : localEvents) {
            ByteArraySourceFile byteArraySourceFile = new ByteArraySourceFile.Builder()
                    .fileData(localEvent.getImage())
                    .name(localEvent.getId() + ".jpg")
                    .build();
            String day = concurrentDateFormatAccess.convertDateToString(localEvent.getTimestamp());
            SFTPClient sftpClient = sshClient.newSFTPClient();
            String destinationPath = eyeballsConfiguration.getSftpDestinationDirectory() + "/" + day + "/";
            sftpClient.mkdirs(destinationPath);
            sftpClient.put(byteArraySourceFile, destinationPath);
        }
    }
}
