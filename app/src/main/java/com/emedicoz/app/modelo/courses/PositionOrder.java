
package com.emedicoz.app.modelo.courses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class PositionOrder {

    @Expose
    private Long banners = 0L;
    @SerializedName("book_your_seat")
    private Long bookYourSeat = 0L;
    @Expose
    private Long categories = 0L;
    @SerializedName("combo_plan")
    private Long comboPlan = 0L;
    @SerializedName("live_course")
    private Long liveCourse = 0L;

    @Expose
    private Long qlinks = 0L;
    @SerializedName("recently_viewed")
    private Long recentlyViewed = 0L;

    public Long getBanners() {
        return banners;
    }

    public void setBanners(Long banners) {
        this.banners = banners;
    }

    public Long getBookYourSeat() {
        return bookYourSeat;
    }

    public void setBookYourSeat(Long bookYourSeat) {
        this.bookYourSeat = bookYourSeat;
    }

    public Long getCategories() {
        return categories;
    }

    public void setCategories(Long categories) {
        this.categories = categories;
    }

    public Long getComboPlan() {
        return comboPlan;
    }

    public void setComboPlan(Long comboPlan) {
        this.comboPlan = comboPlan;
    }

    public Long getLiveCourse() {
        return liveCourse;
    }

    public void setLiveCourse(Long liveCourse) {
        this.liveCourse = liveCourse;
    }

    public Long getQlinks() {
        return qlinks;
    }

    public void setQlinks(Long qlinks) {
        this.qlinks = qlinks;
    }

    public Long getRecentlyViewed() {
        return recentlyViewed;
    }

    public void setRecentlyViewed(Long recentlyViewed) {
        this.recentlyViewed = recentlyViewed;
    }

}
