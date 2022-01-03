package com.emedicoz.app.modelo.liveclass.courses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NoteTestData {
    @SerializedName("layer")
    @Expose
    private String layer;
    @SerializedName("list")
    @Expose
    private List<NotesTestList> list = null;

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public List<NotesTestList> getList() {
        return list;
    }

    public void setList(List<NotesTestList> list) {
        this.list = list;
    }

}
