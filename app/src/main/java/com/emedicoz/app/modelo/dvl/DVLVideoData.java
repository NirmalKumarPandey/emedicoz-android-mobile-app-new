package com.emedicoz.app.modelo.dvl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class DVLVideoData implements Serializable {
    @SerializedName("is_purchased")
    @Expose
    private String isPurchased;
    @SerializedName("videos")
    @Expose
    private ArrayList<DVLVideo> videos = null;

    public String getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(String isPurchased) {
        this.isPurchased = isPurchased;
    }

    public ArrayList<DVLVideo> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<DVLVideo> videos) {
        this.videos = videos;
    }
}
