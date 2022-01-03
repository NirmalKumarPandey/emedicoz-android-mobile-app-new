package com.emedicoz.app.testmodule.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ViewSolutionData implements Serializable {

    @SerializedName("result")
    @Expose
    private ArrayList<ViewSolutionResult> result = null;
    @SerializedName("parts")
    @Expose
    private List<Part> parts = null;
    @SerializedName("user_info")
    @Expose
    private UserInfo userInfo;

    @SerializedName("set_type")
    private String set_type;

    public ArrayList<ViewSolutionResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<ViewSolutionResult> result) {
        this.result = result;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getSet_type() {
        return set_type;
    }

    public void setSet_type(String set_type) {
        this.set_type = set_type;
    }
}
