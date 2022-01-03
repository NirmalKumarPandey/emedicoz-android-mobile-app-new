package com.emedicoz.app.modelo;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubjectWiseResultPart implements Serializable {

    @SerializedName("section_id")
    @Expose
    private String sectionId;
    @SerializedName(Constants.Extras.NAME)
    @Expose
    private String name;
    @SerializedName("part_name")
    @Expose
    private String partName;
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
