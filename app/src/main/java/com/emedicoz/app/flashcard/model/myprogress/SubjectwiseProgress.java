package com.emedicoz.app.flashcard.model.myprogress;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubjectwiseProgress {

    @SerializedName("read_card")
    @Expose
    private Integer readCard;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("days")
    @Expose
    private String days;
    @SerializedName("day1")
    @Expose
    private String day1;

    public SubjectwiseProgress(Integer readCard, String date, String days) {
        this.readCard = readCard;
        this.date = date;
        this.days = days;
    }

    public Integer getReadCard() {
        return readCard;
    }

    public void setReadCard(Integer readCard) {
        this.readCard = readCard;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getDay1() {
        return day1;
    }

    public void setDay1(String day1) {
        this.day1 = day1;
    }


}
