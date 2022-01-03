package com.emedicoz.app.modelo.courses;

import java.io.Serializable;

/**
 * Created by appsquadz on 3/11/17.
 */

public class Review implements Serializable {
    private String text;

    private String rating;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}