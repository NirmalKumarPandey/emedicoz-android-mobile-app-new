package com.emedicoz.app.flashcard.model;

import com.emedicoz.app.utilso.GenericUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubDeck {

    @SerializedName("sd_id")
    @Expose
    private String sdId;
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
    @SerializedName("review_today")
    @Expose
    private String review_today;

    public String getReviewToday() {
        return GenericUtils.isEmpty(review_today) ? "0" : review_today;
    }

    public void setReviewed_today(String review_today) {
        this.review_today = review_today;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    @SerializedName("topics")
    @Expose
    private List<Topic> topics = null;

    public String getSdId() {
        return sdId;
    }

    public void setSdId(String sdId) {
        this.sdId = sdId;
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

}
