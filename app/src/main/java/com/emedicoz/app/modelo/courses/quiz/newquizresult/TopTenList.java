
package com.emedicoz.app.modelo.courses.quiz.newquizresult;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.SerializedName;


public class TopTenList {

    @SerializedName("creation_time")
    private String mCreationTime;
    @SerializedName(Constants.Extras.NAME)
    private String mName;
    @SerializedName("profile_picture")
    private String mProfilePicture;
    @SerializedName(Const.USER_ID)
    private String mUserId;

    public String getCreationTime() {
        return mCreationTime;
    }

    public void setCreationTime(String creationTime) {
        mCreationTime = creationTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getProfilePicture() {
        return mProfilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        mProfilePicture = profilePicture;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

}
