package com.emedicoz.app.flashcard.model;

public class AddCard {

    private String id;
    private String title;
    private String cardNumber;

    public AddCard(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public AddCard(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
