package com.emedicoz.app.flashcard.model;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Topic {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;

    private String sdId;

    public Topic(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public Topic(String id, String sdId, String title) {
        this.id = id;
        this.sdId = sdId;
        this.title = title;
    }

    public String getSdId() {
        return sdId;
    }

    public void setSdId(String sdId) {
        this.sdId = sdId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}