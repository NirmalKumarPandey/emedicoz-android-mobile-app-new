package com.emedicoz.app.modelo.liveclass.courses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LiveClassCourseData {

    @SerializedName("list")
    @Expose
    private ArrayList<LiveClassCourseList> list = null;

    public ArrayList<LiveClassCourseList> getList() {
        return list;
    }

    public void setList(ArrayList<LiveClassCourseList> list) {
        this.list = list;
    }
}
