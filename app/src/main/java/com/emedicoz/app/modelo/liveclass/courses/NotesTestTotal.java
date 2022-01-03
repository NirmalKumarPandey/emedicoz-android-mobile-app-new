package com.emedicoz.app.modelo.liveclass.courses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotesTestTotal {
    @SerializedName("count")
    @Expose
    private String count;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
