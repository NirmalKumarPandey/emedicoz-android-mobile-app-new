package com.emedicoz.app.polls.model;

import com.google.gson.annotations.SerializedName;

public class SocketPollsObject {

    @SerializedName("event_type")
    private String event_type;
    @SerializedName("id")
    private String id;

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
