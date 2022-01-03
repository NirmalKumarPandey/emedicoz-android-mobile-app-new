package com.emedicoz.app.flashcard.model;

import com.emedicoz.app.utilso.GenericUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainDeck {

    @SerializedName("isSelected")
    @Expose
    private boolean isSelected = false;
    @SerializedName("d_id")
    @Expose
    private String dId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("total_card")
    @Expose
    private String totalCard;
    @SerializedName("read_cards")
    @Expose
    private String readCards;
    @SerializedName("reviewed_today_time")
    @Expose
    private String reviewed_today_time;

    public String getReviewed_today_time() {
        return reviewed_today_time;
    }

    public void setReviewed_today_time(String reviewed_today_time) {
        this.reviewed_today_time = reviewed_today_time;
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public String getReviewed_today() {
        return GenericUtils.isEmpty(reviewed_today) ? "0" : reviewed_today;
    }

    public void setReviewed_today(String reviewed_today) {
        this.reviewed_today = reviewed_today;
    }

    public String getLeft_cards() {
        return GenericUtils.isEmpty(left_cards) ? "0" : left_cards;
    }

    public void setLeft_cards(String left_cards) {
        this.left_cards = left_cards;
    }

    @SerializedName("reviewed_today")
    @Expose
    private String reviewed_today;
    @SerializedName("left_cards")
    @Expose
    private String left_cards;
    @SerializedName("subdeck")
    @Expose
    private List<SubDeck> subdeck = null;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDId() {
        return dId;
    }

    public void setDId(String dId) {
        this.dId = dId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTotalCard() {
        return totalCard;
    }

    public void setTotalCard(String totalCard) {
        this.totalCard = totalCard;
    }

    public String getReadCards() {
        return readCards;
    }

    public void setReadCards(String readCards) {
        this.readCards = readCards;
    }

    public List<SubDeck> getSubdeck() {
        return subdeck;
    }

    public void setSubdeck(List<SubDeck> subdeck) {
        this.subdeck = subdeck;
    }

}
