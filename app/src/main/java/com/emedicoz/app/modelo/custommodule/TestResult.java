package com.emedicoz.app.modelo.custommodule;

import java.io.Serializable;

public class TestResult implements Serializable {

    private String id;
    private String test_series_id;
    private String user_id;
    private String result;
    private String time_spent;
    private String correct_count;
    private String incorrect_count;
    private String non_attempt;
    private String marks;
    private String test_series_marks;
    private String guess_count;
    private String correct_guess;
    private String incorrect_guess;
    private String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTest_series_id() {
        return test_series_id;
    }

    public void setTest_series_id(String test_series_id) {
        this.test_series_id = test_series_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTime_spent() {
        return time_spent;
    }

    public void setTime_spent(String time_spent) {
        this.time_spent = time_spent;
    }

    public String getCorrect_count() {
        return correct_count;
    }

    public void setCorrect_count(String correct_count) {
        this.correct_count = correct_count;
    }

    public String getIncorrect_count() {
        return incorrect_count;
    }

    public void setIncorrect_count(String incorrect_count) {
        this.incorrect_count = incorrect_count;
    }

    public String getNon_attempt() {
        return non_attempt;
    }

    public void setNon_attempt(String non_attempt) {
        this.non_attempt = non_attempt;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getTest_series_marks() {
        return test_series_marks;
    }

    public void setTest_series_marks(String test_series_marks) {
        this.test_series_marks = test_series_marks;
    }

    public String getGuess_count() {
        return guess_count;
    }

    public void setGuess_count(String guess_count) {
        this.guess_count = guess_count;
    }

    public String getCorrect_guess() {
        return correct_guess;
    }

    public void setCorrect_guess(String correct_guess) {
        this.correct_guess = correct_guess;
    }

    public String getIncorrect_guess() {
        return incorrect_guess;
    }

    public void setIncorrect_guess(String incorrect_guess) {
        this.incorrect_guess = incorrect_guess;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
