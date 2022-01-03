package com.emedicoz.app.modelo.liveclass;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LiveClassIndex {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("info")
    @Expose
    private String info;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
