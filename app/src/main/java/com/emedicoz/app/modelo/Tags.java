package com.emedicoz.app.modelo;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 07/06/17.
 */

public class Tags implements Serializable {

    private String id;
    private String text;
    private String position;
    private String master_id;
    private String status;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
