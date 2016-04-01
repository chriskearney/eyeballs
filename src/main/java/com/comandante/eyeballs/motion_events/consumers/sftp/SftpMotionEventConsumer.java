package com.comandante.eyeballs.motion_events.consumers.sftp;

import com.comandante.eyeballs.EyeballsConfiguration;
import com.comandante.eyeballs.common.ConcurrentDateFormatAccess;
import com.comandante.eyeballs.model.MotionEvent;
import com.comandante.eyeballs.motion_events.consumers.MotionEventConsumer;
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

public class SftpMotionEventConsumer extends AbstractScheduledService implements MotionEventConsumer {

    private final ConcurrentDateFormatAccess concurrentDateFormatAccess = new ConcurrentDateFormatAccess();
    private final LinkedBlockingQueue<MotionEvent> events = new LinkedBlockingQueue<>();
    private final EyeballsConfiguration eyeballsConfiguration;
    private static Logger log = Logger.getLogger(SftpMotionEventConsumer.class.getName());

    public SftpMotionEventConsumer(EyeballsConfiguration eyeballsConfiguration) {
        this.eyeballsConfiguration = eyeballsConfiguration;
    }

    @Override
    public void add(MotionEvent motionEvent) {
        this.events.add(motionEvent);
    }

    @Override
    protected void runOneIteration() throws Exception {
        ArrayList<MotionEvent> flush = Lists.newArrayList();
        events.drainTo(flush);
        if (flush.size() > 0) {
            SSHClient sshClient = configureSsh();
            try {
                transferEvents(sshClient, flush);
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

    private SSHClient configureSsh() throws IOException {
        SSHClient sshClient = new SSHClient();
        sshClient.loadKnownHosts();
        sshClient.connect(eyeballsConfiguration.getSftpDestinationHost(), eyeballsConfiguration.getSftpRemotePort());
        sshClient.authPublickey(eyeballsConfiguration.getSftpUsername());
        sshClient.useCompression();
        return sshClient;
    }

    private void transferEvents(SSHClient sshClient, List<MotionEvent> motionEvents) throws IOException, ParseException {
        for (MotionEvent motionEvent : motionEvents) {
            ByteArraySourceFile byteArraySourceFile = new ByteArraySourceFile.Builder()
                    .fileData(motionEvent.getImage())
                    .name(motionEvent.getId() + ".jpg")
                    .build();
            String day = concurrentDateFormatAccess.convertDateToString(motionEvent.getTimestamp());
            SFTPClient sftpClient = sshClient.newSFTPClient();
            String destinationPath = eyeballsConfiguration.getSftpDestinationDirectory() + "/" + day + "/";
            sftpClient.mkdirs(destinationPath);
            sftpClient.put(byteArraySourceFile, destinationPath);
        }
    }
}
