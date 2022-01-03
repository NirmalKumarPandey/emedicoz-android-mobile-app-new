package com.emedicoz.app.modelo.liveclass.courses;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EpubData {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("image_icon")
    @Expose
    private String imageIcon;
    @SerializedName("total")
    @Expose
    private List<NotesTestTotal> total = null;
    @SerializedName("epub_layer3")
    @Expose
    private List<EpubLayer3Data> epubLayer3 = null;

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

    public String getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;
    }

    public List<NotesTestTotal> getTotal() {
        return total;
    }

    public void setTotal(List<NotesTestTotal> total) {
        this.total = total;
    }

    public List<EpubLayer3Data> getEpubLayer3() {
        return epubLayer3;
    }

    public void setEpubLayer3(List<EpubLayer3Data> epubLayer3) {
        this.epubLayer3 = epubLayer3;
    }

}
