package com.emedicoz.app.modelo.liveclass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TotalCountVideo {
    @SerializedName("count")
    @Expose
    private String count;
    @SerializedName("text")
    @Expose
    private String text;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
