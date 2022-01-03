package com.emedicoz.app.modelo.custommodule;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SubjectData implements Serializable {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName(Constants.Extras.NAME)
    @Expose
    private String name;
    @SerializedName("isChecked")
    @Expose
    private boolean isChecked;
    @SerializedName("topics")
    @Expose
    private List<Topic> topics = null;
    private int count = 0;
    private Boolean allchecked = false;

    public Boolean getAllchecked() {
        return allchecked;
    }

    public void setAllchecked(Boolean allchecked) {
        this.allchecked = allchecked;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
