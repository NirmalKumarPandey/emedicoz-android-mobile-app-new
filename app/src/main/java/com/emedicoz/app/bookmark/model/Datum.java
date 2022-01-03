
package com.emedicoz.app.bookmark.model;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.SerializedName;


public class Datum {

    @SerializedName(Constants.Extras.ID)
    private String mId;
    @SerializedName("text")
    private String mText;
    @SerializedName("total")
    private String mTotal;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getTotal() {
        return mTotal;
    }

    public void setTotal(String total) {
        mTotal = total;
    }


}
