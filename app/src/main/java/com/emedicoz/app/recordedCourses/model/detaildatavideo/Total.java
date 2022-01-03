package com.emedicoz.app.recordedCourses.model.detaildatavideo;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Total implements Serializable {

    @Expose
    private Long count;
    @Expose
    private String text;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
