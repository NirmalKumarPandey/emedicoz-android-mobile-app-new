package com.emedicoz.app.modelo;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Country {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("sortname")
    @Expose
    private String sortname;
    @SerializedName(Constants.Extras.NAME)
    @Expose
    private String name;
    @SerializedName("phonecode")
    @Expose
    private String phonecode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSortname() {
        return sortname;
    }

    public void setSortname(String sortname) {
        this.sortname = sortname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonecode() {
        return phonecode;
    }

    public void setPhonecode(String phonecode) {
        this.phonecode = phonecode;
    }

}
