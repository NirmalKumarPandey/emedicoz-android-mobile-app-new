package com.emedicoz.app.modelo.courses;

import com.emedicoz.app.modelo.Total;

import java.io.Serializable;
import java.util.ArrayList;

public class Lists implements Serializable {
    private ArrayList<Total> total;

    private String image_icon;

    private String id;

    private String title;

    private String c_code;
    private String is_live;
    private String live_status;
    private String chat_node;

    public String getLive_status() {
        return live_status;
    }

    public void setLive_status(String live_status) {
        this.live_status = live_status;
    }

    public String getChat_node() {
        return chat_node;
    }

    public void setChat_node(String chat_node) {
        this.chat_node = chat_node;
    }

    public String getIs_live() {
        return is_live;
    }

    public void setIs_live(String is_live) {
        this.is_live = is_live;
    }

    public ArrayList<Total> getTotal() {
        return total;
    }

    public void setTotal(ArrayList<Total> total) {
        this.total = total;
    }

    public String getImage_icon() {
        return image_icon;
    }

    public void setImage_icon(String image_icon) {
        this.image_icon = image_icon;
    }

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

    public String getC_code() {
        return c_code;
    }

    public void setC_code(String c_code) {
        this.c_code = c_code;
    }
}
