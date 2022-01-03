package com.emedicoz.app.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SubjectWiseResultData implements Serializable {

    @SerializedName("part")
    @Expose
    private ArrayList<SubjectWiseResultPart> part = null;
    @SerializedName("subject")
    @Expose
    private ArrayList<SubjectWiseResultSubject> subject = null;

    public ArrayList<SubjectWiseResultPart> getPart() {
        return part;
    }

    public void setPart(ArrayList<SubjectWiseResultPart> part) {
        this.part = part;
    }

    public ArrayList<SubjectWiseResultSubject> getSubject() {
        return subject;
    }

    public void setSubject(ArrayList<SubjectWiseResultSubject> subject) {
        this.subject = subject;
    }


}
