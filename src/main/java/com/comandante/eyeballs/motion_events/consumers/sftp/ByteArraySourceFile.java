package com.comandante.eyeballs.motion_events.consumers.sftp;

import net.schmizz.sshj.xfer.InMemorySourceFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArraySourceFile extends InMemorySourceFile {

    private final String name;
    private final long length;
    private final InputStream inputStream;

    public ByteArraySourceFile(String name, long length, byte[] fileData) {
        this.name = name;
        this.length = length;
        this.inputStream = new ByteArrayInputStream(fileData);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    public static class Builder {
        private String name;
        private byte[] fileData;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder fileData(byte[] fileData) {
            this.fileData = fileData;
            return this;
        }

        public ByteArraySourceFile build() {
            return new ByteArraySourceFile(name, fileData.length, fileData);
        }
    }
}

