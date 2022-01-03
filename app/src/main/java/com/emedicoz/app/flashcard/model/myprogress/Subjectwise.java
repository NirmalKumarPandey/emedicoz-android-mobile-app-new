package com.emedicoz.app.flashcard.model.myprogress;


import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Subjectwise {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("deck")
    @Expose
    private String deck;
    @SerializedName("progress")
    @Expose
    private List<SubjectwiseProgress> progress = null;

    public Subjectwise(String id, String title, List<SubjectwiseProgress> progress) {
        this.id = id;
        this.title = title;
        this.progress = progress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeck() {
        return deck;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }

    public List<SubjectwiseProgress> getProgress() {
        return progress;
    }

    public void setProgress(List<SubjectwiseProgress> progress) {
        this.progress = progress;
    }

}
