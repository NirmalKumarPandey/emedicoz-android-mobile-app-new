package com.emedicoz.app.modelo.courses;

import java.io.Serializable;
import java.util.ArrayList;

public class Enc implements Serializable {

    private ArrayList<UrlFiles> files;
    private String token;

    public ArrayList<UrlFiles> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<UrlFiles> files) {
        this.files = files;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
