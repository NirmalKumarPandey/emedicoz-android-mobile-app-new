
package com.emedicoz.app.testmodule.model;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Part implements Serializable {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("test_series_id")
    @Expose
    private String testSeriesId;
    @SerializedName("part_name")
    @Expose
    private String partName;

    @SerializedName("attempt_limit")
    @Expose
    private int attemptLimit;

    private int indexOf;

    public int getAttemptLimit() {
        return attemptLimit;
    }

    public void setAttemptLimit(int attemptLimit) {
        this.attemptLimit = attemptLimit;
    }

    public int getIndexOf() {
        return indexOf;
    }

    public void setIndexOf(int indexOf) {
        this.indexOf = indexOf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestSeriesId() {
        return testSeriesId;
    }

    public void setTestSeriesId(String testSeriesId) {
        this.testSeriesId = testSeriesId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public void setIndex(int indexOf) {
        this.indexOf = indexOf;
    }
}
