package com.comandante.eyeballs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

public class EyeballsConfiguration extends Configuration {

    @NotNull
    private double areaThreshold = 3;

    @NotNull
    private int pixelDifferentThreshold = 26;

    @NotNull
    private int imageWidth = 640;

    @NotNull
    private int imageHeight = 480;

    @NotNull
    private String localStorageDirectory = "local_storage";

    @NotNull
    private String username = "admin";

    @NotNull
    private String password = "password";

    @NotNull
    private Boolean useAuth = Boolean.FALSE;

    @NotNull
    private Boolean useSftp = Boolean.FALSE;

    private String sftpDestinationHost;

    private String sftpUsername;

    private String sftpDestinationDirectory;

    private int sftpRemotePort = 22;

    @NotNull
    private Boolean useDropbox = Boolean.FALSE;

    private String dropBoxAccessToken;

    @JsonProperty
    public String getDropBoxAccessToken() {
        return dropBoxAccessToken;
    }

    @JsonProperty
    public int getSftpRemotePort() {
        return sftpRemotePort;
    }

    @JsonProperty
    public String getSftpDestinationDirectory() {
        return sftpDestinationDirectory;
    }

    @JsonProperty
    public String getSftpDestinationHost() {
        return sftpDestinationHost;
    }

    @JsonProperty
    public String getSftpUsername() {
        return sftpUsername;
    }

    @JsonProperty
    public int getImageWidth() {
        return imageWidth;
    }

    @JsonProperty
    public int getImageHeight() {
        return imageHeight;
    }

    @JsonProperty
    public String getLocalStorageDirectory() {
        return localStorageDirectory;
    }

    @JsonProperty
    public double getAreaThreshold() {
        return areaThreshold;
    }

    @JsonProperty
    public int getPixelDifferentThreshold() {
        return pixelDifferentThreshold;
    }

    @JsonProperty
    public String getUsername() {
        return username;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public Boolean getUseAuth() {
        return useAuth;
    }

    @JsonProperty
    public Boolean getUseSftp() {
        return useSftp;
    }

    @JsonProperty
    public Boolean getUseDropbox() {
        return useDropbox;
    }
}
