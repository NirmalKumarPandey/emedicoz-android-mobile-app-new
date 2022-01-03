
package com.emedicoz.app.retrofit.models;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.SerializedName;


public class CategoryInfo {

    @SerializedName("app_view_type")
    private String mAppViewType;
    @SerializedName(Constants.Extras.ID)
    private String mId;
    @SerializedName("image")
    private String mImage;
    @SerializedName("in_carousel")
    private String mInCarousel;
    @SerializedName(Constants.Extras.NAME)
    private String mName;
    @SerializedName("parent_fk")
    private String mParentFk;
    @SerializedName("position")
    private String mPosition;
    @SerializedName("visibilty")
    private String mVisibilty;

    public String getAppViewType() {
        return mAppViewType;
    }

    public void setAppViewType(String appViewType) {
        mAppViewType = appViewType;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getInCarousel() {
        return mInCarousel;
    }

    public void setInCarousel(String inCarousel) {
        mInCarousel = inCarousel;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getParentFk() {
        return mParentFk;
    }

    public void setParentFk(String parentFk) {
        mParentFk = parentFk;
    }

    public String getPosition() {
        return mPosition;
    }

    public void setPosition(String position) {
        mPosition = position;
    }

    public String getVisibilty() {
        return mVisibilty;
    }

    public void setVisibilty(String visibilty) {
        mVisibilty = visibilty;
    }

}
