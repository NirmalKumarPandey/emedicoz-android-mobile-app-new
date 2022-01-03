package com.emedicoz.app.flashcard.model.myprogress;

import com.emedicoz.app.flashcard.model.myprogress.SsubjectWiseCard.Subdeck;

public class SubjectWiseCard {

    private int view_type;
    private String deck;
    private Subdeck subdeck;

    public SubjectWiseCard(int view_type, String deck) {
        this.deck = deck;
        this.view_type = view_type;
    }

    public SubjectWiseCard(int view_type, Subdeck subdeck) {
        this.view_type = view_type;
        this.subdeck = subdeck;
    }


    public Subdeck getSubdeck() {
        return subdeck;
    }

    public void setSubdeck(Subdeck subdeck) {
        this.subdeck = subdeck;
    }

    public String getDeck() {
        return deck;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }

    public int getView_type() {
        return view_type;
    }

    public void setView_type(int view_type) {
        this.view_type = view_type;
    }
}
