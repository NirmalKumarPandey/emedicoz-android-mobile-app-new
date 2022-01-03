
package com.emedicoz.app.modelo;

import java.util.List;

@SuppressWarnings("unused")
public class SyncReqParam {
    private String user_id;
    private List<String> video_ids;
    private List<String> epub_ids;
    private List<String> pdf_ids;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<String> getVideo_ids() {
        return video_ids;
    }

    public void setVideo_ids(List<String> video_ids) {
        this.video_ids = video_ids;
    }

    public List<String> getEpub_ids() {
        return epub_ids;
    }

    public void setEpub_ids(List<String> epub_ids) {
        this.epub_ids = epub_ids;
    }

    public List<String> getPdf_ids() {
        return pdf_ids;
    }

    public void setPdf_ids(List<String> pdf_ids) {
        this.pdf_ids = pdf_ids;
    }
}
