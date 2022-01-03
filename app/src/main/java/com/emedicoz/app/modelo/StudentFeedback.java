package com.emedicoz.app.modelo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class StudentFeedback implements Serializable {

    private String avg_rating;
    private LinkedHashMap<Double, Float> feedback;

    public String getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(String avg_rating) {
        this.avg_rating = avg_rating;
    }

    public LinkedHashMap<Double, Float> getFeedback() {
        return feedback;
    }

    public void setFeedback(LinkedHashMap<Double, Float> feedback) {
        this.feedback = feedback;
    }
}
