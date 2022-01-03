package com.emedicoz.app.modelo.liveclass.courses;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PdfData {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("image_icon")
    @Expose
    private String imageIcon;
    @SerializedName("video_type")
    @Expose
    private String videoType;
    @SerializedName("is_live")
    @Expose
    private String isLive;
    @SerializedName("total")
    @Expose
    private List<NotesTestTotal> total = null;
    @SerializedName("node_chat")
    @Expose
    private String nodeChat;
    @SerializedName("live_status")
    @Expose
    private String liveStatus;
    @SerializedName("pdf_layer3")
    @Expose
    private List<PdfLayer3Data> pdfLayer3 = null;

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

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getIsLive() {
        return isLive;
    }

    public void setIsLive(String isLive) {
        this.isLive = isLive;
    }

    public List<NotesTestTotal> getTotal() {
        return total;
    }

    public void setTotal(List<NotesTestTotal> total) {
        this.total = total;
    }

    public String getNodeChat() {
        return nodeChat;
    }

    public void setNodeChat(String nodeChat) {
        this.nodeChat = nodeChat;
    }

    public String getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(String liveStatus) {
        this.liveStatus = liveStatus;
    }

    public List<PdfLayer3Data> getPdfLayer3() {
        return pdfLayer3;
    }

    public void setPdfLayer3(List<PdfLayer3Data> pdfLayer3) {
        this.pdfLayer3 = pdfLayer3;
    }
}
