package com.alvarosantisteban.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Models a a cooking recipe step.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Step implements Parcelable{

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

    protected Step(Parcel in) {
        id = in.readInt();
        shortDescription = in.readString();
        description = in.readString();
        videoUrl = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    @Override
    public int describeContents() {
        return Parcelable.CONTENTS_FILE_DESCRIPTOR;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(shortDescription);
        parcel.writeString(description);
        parcel.writeString(videoUrl);
        parcel.writeString(imageUrl);
    }
}
