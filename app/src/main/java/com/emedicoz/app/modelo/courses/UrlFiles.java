package com.emedicoz.app.modelo.courses;

import java.io.Serializable;

public class UrlFiles implements Serializable {

    private String url;
    private String size;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
