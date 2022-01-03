package com.emedicoz.app.modelo.liveclass.courses;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DescriptionTile {

    @SerializedName("tile_name")
    @Expose
    private String tileName;
    @SerializedName("totoal")
    @Expose
    private String totoal;
    @SerializedName(Constants.Extras.TYPE)
    @Expose
    private String type;
    @SerializedName("c_code")
    @Expose
    private String cCode;

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }

    public String getTotoal() {
        return totoal;
    }

    public void setTotoal(String totoal) {
        this.totoal = totoal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCCode() {
        return cCode;
    }

    public void setCCode(String cCode) {
        this.cCode = cCode;
    }

}
