package com.emedicoz.app.modelo.dvl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class DVLCurriculum implements Serializable {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("topics")
    @Expose
    private ArrayList<DVLTopic> topics = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<DVLTopic> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<DVLTopic> topics) {
        this.topics = topics;
    }
}
