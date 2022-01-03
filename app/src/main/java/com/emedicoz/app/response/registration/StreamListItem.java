package com.emedicoz.app.response.registration;

import java.io.Serializable;

public class StreamListItem implements Serializable {
    private String id;
    private String visibilty;
    private String text;
    private String image;
    private String application_name;

    public String getApplication_name() {
        return application_name;
    }

    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVisibilty() {
        return visibilty;
    }

    public void setVisibilty(String visibilty) {
        this.visibilty = visibilty;
    }

    public String getText_name() {
        return text;
    }

    public void setText_name(String text) {
        this.text = text;
    }
}
