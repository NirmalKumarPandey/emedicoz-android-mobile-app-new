package com.emedicoz.app.modelo.custommodule;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TagData {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("master_id")
    @Expose
    private String masterId;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("status")
    @Expose
    private String status;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}