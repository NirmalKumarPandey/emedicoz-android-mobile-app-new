package com.emedicoz.app.retrofit.models.noticount;

import com.google.gson.annotations.SerializedName;

public class Count {
    @SerializedName("counter")
    private String counter;

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }
}
