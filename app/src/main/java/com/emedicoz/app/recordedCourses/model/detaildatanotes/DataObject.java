package com.emedicoz.app.recordedCourses.model.detaildatanotes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class DataObject implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("module_name")
    @Expose
    private String moduleName;
    @SerializedName("list")
    @Expose
    private ArrayList<NotesList> list = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public ArrayList<NotesList> getList() {
        return list;
    }

    public void setList(ArrayList<NotesList> list) {
        this.list = list;
    }
}


