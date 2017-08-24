package com.alvarosantisteban.bakingapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Models a a cooking recipe step.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Step {

    private final int id;
    private final String shortDescription;
    private final String description;
    private final String videoUrl;
    private final String imageUrl;

    @JsonIgnoreProperties(ignoreUnknown=true)
    public Step(int id, String shortDescription, String description, String videoUrl, String imageUrl) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
    }

    @JsonCreator
    public static Step from(
            @JsonProperty("id") int id,
            @JsonProperty("shortDescription") String shortDescription,
            @JsonProperty("description") String description,
            @JsonProperty("videoURL") String videoUrl,
            @JsonProperty("thumbnailURL") String imageUrl) {
        return new Step(id, shortDescription, description, videoUrl, imageUrl);
    }

    public int getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
