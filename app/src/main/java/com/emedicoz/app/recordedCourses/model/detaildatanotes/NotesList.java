package com.emedicoz.app.recordedCourses.model.detaildatanotes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class NotesList implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("data")
    @Expose
    private ArrayList<NotesData> data = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<NotesData> getData() {
        return data;
    }

    public void setData(ArrayList<NotesData> data) {
        this.data = data;
    }
}
