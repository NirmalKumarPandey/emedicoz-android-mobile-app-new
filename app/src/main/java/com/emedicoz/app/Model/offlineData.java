package com.emedicoz.app.Model; // Keep it in this package only, It is on LIVE project
// Its because of this model itself gets converted into Byte Stream and saved in local Data store,
// while retrieving it looks always for the same Data Model along with the package name.

// com.emedicoz.app.Model.offlineData

import com.tonyodev.fetch.request.RequestInfo;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 12/13/17.
 */

public class offlineData implements Serializable {


    String id;
    RequestInfo requestInfo;
    long downloadid;
    String link;
    boolean isdownloadinprogress;
    boolean isdownloadcomplete;
    String type;


    public offlineData(String id, String link, boolean isdownloadinprogress, boolean isdownloadcomplete, String type, long downloadid) {
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
}
