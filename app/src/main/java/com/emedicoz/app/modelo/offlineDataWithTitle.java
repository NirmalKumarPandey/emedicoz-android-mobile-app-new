package com.emedicoz.app.modelo;

import com.tonyodev.fetch.request.RequestInfo;

import java.io.Serializable;

public class offlineDataWithTitle implements Serializable {
    String id;
    RequestInfo requestInfo;
    long downloadid;
    String link;
    boolean isdownloadinprogress;
    boolean isdownloadcomplete;
    String type, title;

    public offlineDataWithTitle(String id, String link, boolean isdownloadinprogress, boolean isdownloadcomplete, String type, long downloadid) {
        this.id = id;
        this.link = link;
        this.isdownloadinprogress = isdownloadinprogress;
        this.isdownloadcomplete = isdownloadcomplete;
        this.type = type;
        this.downloadid = downloadid;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public long getDownloadid() {
        return downloadid;
    }

    public void setDownloadid(long downloadid) {
        this.downloadid = downloadid;
    }

    public boolean isIsdownloadcomplete() {

        return isdownloadcomplete;
    }

    public void setIsdownloadcomplete(boolean isdownloadcomplete) {
        this.isdownloadcomplete = isdownloadcomplete;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isIsdownloadinprogress() {
        return isdownloadinprogress;
    }

    public void setIsdownloadinprogress(boolean isdownloadinprogress) {
        this.isdownloadinprogress = isdownloadinprogress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
