package com.emedicoz.app.referralcode.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileTypeData {
    @SerializedName("profile_type_list")
    @Expose
    private List<ProfileTypeList> profileTypeList = null;

    public List<ProfileTypeList> getProfileTypeList() {
        return profileTypeList;
    }

    public void setProfileTypeList(List<ProfileTypeList> profileTypeList) {
        this.profileTypeList = profileTypeList;
    }
}
