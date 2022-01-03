package com.emedicoz.app.pastpaperexplanation.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PPECategoryData implements Serializable {

    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("data")
    @Expose
    private List<PPECategoryChildData> data = null;

    private boolean isSelected = false;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<PPECategoryChildData> getData() {
        return data;
    }

    public void setData(List<PPECategoryChildData> data) {
        this.data = data;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
