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
}
