package com.emedicoz.app.response.registration;

import java.io.Serializable;

public class SubjectListItem implements Serializable {

    private String id;
    private String name;
    private String main_category;
    private String deck;
    private String for_quiz;
    private String image;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMain_category() {
        return main_category;
    }

    public void setMain_category(String main_category) {
        this.main_category = main_category;
    }

    public String getDeck() {
        return deck;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }

    public String getFor_quiz() {
        return for_quiz;
    }

    public void setFor_quiz(String for_quiz) {
        this.for_quiz = for_quiz;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
