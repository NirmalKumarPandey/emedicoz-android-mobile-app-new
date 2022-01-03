package com.emedicoz.app.flashcard.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FlashCards implements Parcelable {

    @SerializedName("isShow")
    @Expose
    private boolean isShow = false;
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("explanation")
    @Expose
    private String explanation;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("deck_id")
    @Expose
    private String deckId;
    @SerializedName("subdeck_id")
    @Expose
    private String subdeckId;
    @SerializedName(Constants.Extras.TYPE)
    @Expose
    private String type;
    @SerializedName("tags")
    @Expose
    private String tags;
    @SerializedName("enable_status")
    @Expose
    private String enableStatus;
    @SerializedName("creation_time")
    @Expose
    private String creationTime;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("deck")
    @Expose
    private String deck;
    @SerializedName("subdeck")
    @Expose
    private String subdeck;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("added_time")
    @Expose
    private String addedTime;
    @SerializedName("view_time")
    @Expose
    private String viewTime;
    @SerializedName("next_time")
    @Expose
    private String nextTime;
    @SerializedName("is_bookmarked")
    @Expose
    private String isBookmarked;
    @SerializedName("is_suspended")
    @Expose
    private String is_suspended;
    private boolean isSelected = false;
    private boolean isComplete = false;
    private boolean alreadyComplete = false;
    private boolean isNewPosition = false;


    protected FlashCards(Parcel in) {
        isShow = in.readByte() != 0;
        id = in.readString();
        question = in.readString();
        answer = in.readString();
        explanation = in.readString();
        title = in.readString();
        deckId = in.readString();
        subdeckId = in.readString();
        type = in.readString();
        tags = in.readString();
        enableStatus = in.readString();
        creationTime = in.readString();
        createdBy = in.readString();
        deck = in.readString();
        subdeck = in.readString();
        topic = in.readString();
        addedTime = in.readString();
        viewTime = in.readString();
        nextTime = in.readString();
        isBookmarked = in.readString();
        is_suspended = in.readString();
        isSelected = in.readByte() != 0;
        isComplete = in.readByte() != 0;
        alreadyComplete = in.readByte() != 0;
        isNewPosition = in.readByte() != 0;
    }

    public static final Creator<FlashCards> CREATOR = new Creator<FlashCards>() {
        @Override
        public FlashCards createFromParcel(Parcel in) {
            return new FlashCards(in);
        }

        @Override
        public FlashCards[] newArray(int size) {
            return new FlashCards[size];
        }
    };

    public boolean isNewPosition() {
        return isNewPosition;
    }

    public void setNewPosition(boolean newPosition) {
        isNewPosition = newPosition;
    }

    public boolean isAlreadyComplete() {
        return alreadyComplete;
    }

    public void setAlreadyComplete(boolean alreadyComplete) {
        this.alreadyComplete = alreadyComplete;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public String getSubdeckId() {
        return subdeckId;
    }

    public void setSubdeckId(String subdeckId) {
        this.subdeckId = subdeckId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(String enableStatus) {
        this.enableStatus = enableStatus;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDeck() {
        return deck;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }

    public String getSubdeck() {
        return subdeck;
    }

    public void setSubdeck(String subdeck) {
        this.subdeck = subdeck;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(String addedTime) {
        this.addedTime = addedTime;
    }

    public String getViewTime() {
        return viewTime;
    }

    public void setViewTime(String viewTime) {
        this.viewTime = viewTime;
    }

    public String getNextTime() {
        return nextTime;
    }

    public void setNextTime(String nextTime) {
        this.nextTime = nextTime;
    }

    public String getIsBookmarked() {
        return isBookmarked;
    }

    public void setIsBookmarked(String isBookmarked) {
        this.isBookmarked = isBookmarked;
    }

    public String getIs_suspended() {
        return is_suspended;
    }

    public void setIs_suspended(String is_suspended) {
        this.is_suspended = is_suspended;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeByte((byte) (isShow ? 1 : 0));
        dest.writeString(id);
        dest.writeString(question);
        dest.writeString(answer);
        dest.writeString(explanation);
        dest.writeString(title);
        dest.writeString(deckId);
        dest.writeString(subdeckId);
        dest.writeString(type);
        dest.writeString(tags);
        dest.writeString(enableStatus);
        dest.writeString(creationTime);
        dest.writeString(createdBy);
        dest.writeString(deck);
        dest.writeString(subdeck);
        dest.writeString(topic);
        dest.writeString(addedTime);
        dest.writeString(viewTime);
        dest.writeString(nextTime);
        dest.writeString(isBookmarked);
        dest.writeString(is_suspended);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeByte((byte) (isComplete ? 1 : 0));
        dest.writeByte((byte) (alreadyComplete ? 1 : 0));
        dest.writeByte((byte) (isNewPosition ? 1 : 0));
    }

}
