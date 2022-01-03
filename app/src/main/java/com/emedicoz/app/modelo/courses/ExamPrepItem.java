package com.emedicoz.app.modelo.courses;

import java.io.Serializable;
import java.util.ArrayList;

public class ExamPrepItem implements Serializable {
    private String layer;

    private ArrayList<Lists> list;

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public ArrayList<Lists> getList() {
        return list;
    }

    public void setList(ArrayList<Lists> list) {
        this.list = list;
    }
}

