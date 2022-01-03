package com.emedicoz.app.modelo.courses.Crs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EBookData implements Serializable {
    @SerializedName("is_purchased")
    @Expose
    private String isPurchased;
    @SerializedName("files")
    @Expose
    private ArrayList<EBookFile> files = null;

    public String getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(String isPurchased) {
        this.isPurchased = isPurchased;
    }

    public ArrayList<EBookFile> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<EBookFile> files) {
        this.files = files;
    }
}
