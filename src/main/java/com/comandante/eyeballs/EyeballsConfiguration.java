package com.comandante.eyeballs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

public class EyeballsConfiguration extends Configuration {

    @NotNull
    private int imageWidth = 640;

    @NotNull
    private int imageHeight = 480;

    @NotNull
    private String localStorageDirectory = "local_storage";

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
}
