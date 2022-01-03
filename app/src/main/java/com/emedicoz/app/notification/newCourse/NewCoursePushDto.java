package com.emedicoz.app.notification.newCourse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewCoursePushDto {

    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("message")
    @Expose
    private PushMessage message;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public PushMessage getMessage() {
        return message;
    }

    public void setMessage(PushMessage message) {
        this.message = message;
    }
}
