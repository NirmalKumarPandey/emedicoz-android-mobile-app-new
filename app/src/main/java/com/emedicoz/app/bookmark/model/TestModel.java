package com.emedicoz.app.bookmark.model;

import java.io.Serializable;

public class TestModel implements Serializable {
    public String id;
    public String question;
    private String bid;
    private String is_video_based;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getIs_video_based() {
        return is_video_based;
    }

    public void setIs_video_based(String is_video_based) {
        this.is_video_based = is_video_based;
    }
}
