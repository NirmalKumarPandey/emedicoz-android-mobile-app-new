package com.emedicoz.app.modelo.dvl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DVLTopic implements Serializable {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("topic_id")
    @Expose
    private String topicId;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("deck")
    @Expose
    private String deck;
    @SerializedName("sub_img_url")
    @Expose
    private String subImgUrl;
    @SerializedName("completed")
    @Expose
    private String completed;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSubImgUrl() {
        return subImgUrl;
    }

    public void setSubImgUrl(String subImgUrl) {
        this.subImgUrl = subImgUrl;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getDeck() {
        return deck;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }
}
