
package com.emedicoz.app.modelo;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class DownloadDataResp {

    @SerializedName("data")
    private Data mData;

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }


}
