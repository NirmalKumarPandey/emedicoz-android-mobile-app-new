package com.emedicoz.app.modelo.courses;

import java.io.Serializable;

public class Cards implements Serializable {
    private String tile_name;

    private String c_code;

    private String type;

    private String totoal;

    public String getTile_name() {
        return tile_name;
    }

    public void setTile_name(String tile_name) {
        this.tile_name = tile_name;
    }

    public String getC_code() {
        return c_code;
    }

    public void setC_code(String c_code) {
        this.c_code = c_code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTotoal() {
        return totoal;
    }

    public void setTotoal(String totoal) {
        this.totoal = totoal;
    }
}
