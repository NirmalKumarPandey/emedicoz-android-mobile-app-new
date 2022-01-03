
package com.emedicoz.app.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class Data {

    @SerializedName("epubs")
    private List<Epub> mEpubs;
    @SerializedName("pdfs")
    private List<Pdf> mPdfs;
    @SerializedName("videos")
    private List<Videos> mVideos;

    public List<Epub> getEpubs() {
        return mEpubs;
    }

    public void setEpubs(List<Epub> epubs) {
        mEpubs = epubs;
    }

    public List<Pdf> getPdfs() {
        return mPdfs;
    }

    public void setPdfs(List<Pdf> pdfs) {
        mPdfs = pdfs;
    }

    public List<Videos> getVideos() {
        return mVideos;
    }

    public void setVideos(List<Videos> videos) {
        mVideos = videos;
    }

}
