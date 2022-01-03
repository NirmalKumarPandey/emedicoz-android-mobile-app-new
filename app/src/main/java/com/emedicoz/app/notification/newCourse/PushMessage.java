package com.emedicoz.app.notification.newCourse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PushMessage {

    @SerializedName("notification_code")
    @Expose
    private Integer notificationCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getNotificationCode() {
        return notificationCode;
    }

    public void setNotificationCode(Integer notificationCode) {
        this.notificationCode = notificationCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
