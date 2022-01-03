package com.emedicoz.app.referralcode.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyAffiliationData {
    @SerializedName("per_page_total")
    @Expose
    private String perPageTotal;
    @SerializedName("page")
    @Expose
    private String page;
    @SerializedName("total_records")
    @Expose
    private int totalRecords;
    @SerializedName("my_affiliations")
    @Expose
    private List<MyAffiliation> myAffiliations = null;

    public String getPerPageTotal() {
        return perPageTotal;
    }

    public void setPerPageTotal(String perPageTotal) {
        this.perPageTotal = perPageTotal;
    }

    public MyAffiliationData withPerPageTotal(String perPageTotal) {
        this.perPageTotal = perPageTotal;
        return this;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public MyAffiliationData withPage(String page) {
        this.page = page;
        return this;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public MyAffiliationData withTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
        return this;
    }

    public List<MyAffiliation> getMyAffiliations() {
        return myAffiliations;
    }

    public void setMyAffiliations(List<MyAffiliation> myAffiliations) {
        this.myAffiliations = myAffiliations;
    }

    public MyAffiliationData withMyAffiliations(List<MyAffiliation> myAffiliations) {
        this.myAffiliations = myAffiliations;
        return this;
    }
}
