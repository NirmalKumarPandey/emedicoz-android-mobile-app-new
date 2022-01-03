package com.emedicoz.app.modelo;

import java.io.Serializable;

public class TextMessage implements Serializable {
    public String text_message;

    public String getText_message() {
        return text_message;
    }

    public void setText_message(String text_message) {
        this.text_message = text_message;
    }


}
