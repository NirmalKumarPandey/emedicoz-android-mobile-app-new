package com.emedicoz.app.modelo.courses;

import java.io.Serializable;

public class Bookmark implements Serializable {

    private String id;
    private String time;
    private String info;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
//
//    @Override
//    public String toString() {
//        return new ToStringBuilder(this).append(Const.ID, id).append("time", time).append("info", info).toString();
//    }

}
