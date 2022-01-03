package com.emedicoz.app.flashcard.model;

import java.util.ArrayList;

public class AllFlashCard {

    private String title;
    private ArrayList<FlashCards> flashCardsArrayList = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<FlashCards> getFlashCardsArrayList() {
        return flashCardsArrayList;
    }

    public void setFlashCardsArrayList(ArrayList<FlashCards> flashCardsArrayList) {
        this.flashCardsArrayList = flashCardsArrayList;
    }

    public AllFlashCard(String title, ArrayList<FlashCards> flashCardsArrayList) {
        this.title = title;
        this.flashCardsArrayList = flashCardsArrayList;
    }
}
