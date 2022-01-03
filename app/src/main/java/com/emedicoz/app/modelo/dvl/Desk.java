package com.emedicoz.app.modelo.dvl;

import java.io.Serializable;

public class Desk implements Serializable {

    public String desk_id;
    public String desk_title;

    public String getDesk_id() {
        return desk_id;
    }

    public void setDesk_id(String desk_id) {
        this.desk_id = desk_id;
    }

    public String getDesk_title() {
        return desk_title;
    }

    public void setDesk_title(String desk_title) {
        this.desk_title = desk_title;
    }
}
