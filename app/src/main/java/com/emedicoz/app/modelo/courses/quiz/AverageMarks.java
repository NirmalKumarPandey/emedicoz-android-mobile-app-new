package com.emedicoz.app.modelo.courses.quiz;

import java.io.Serializable;

public class AverageMarks implements Serializable {

    private String avg_time;
    private String avg_correct;
    private String avg_incorrect;
    private String avg_non_attempt;
    private String avg_marks;

    public String getAvg_time() {
        return avg_time;
    }

    public void setAvg_time(String avg_time) {
        this.avg_time = avg_time;
    }

    public String getAvg_correct() {
        return avg_correct;
    }

    public void setAvg_correct(String avg_correct) {
        this.avg_correct = avg_correct;
    }

    public String getAvg_incorrect() {
        return avg_incorrect;
    }

    public void setAvg_incorrect(String avg_incorrect) {
        this.avg_incorrect = avg_incorrect;
    }

    public String getAvg_non_attempt() {
        return avg_non_attempt;
    }

    public void setAvg_non_attempt(String avg_non_attempt) {
        this.avg_non_attempt = avg_non_attempt;
    }

    public String getAvg_marks() {
        return avg_marks;
    }

    public void setAvg_marks(String avg_marks) {
        this.avg_marks = avg_marks;
    }
}
