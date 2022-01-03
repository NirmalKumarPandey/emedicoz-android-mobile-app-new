package com.emedicoz.app.modelo;

public class Poll {
    String question = "";
    long correctAnswer = 0;
    String answer = "";
    String userAnsweredId = "";
    String option1 = "";
    String option2 = "";
    String option3 = "";
    String option4 = "";
    String totalUsersAttempted = "";
    String usersAttemptedOption1 = "";
    String usersAttemptedOption2 = "";
    String usersAttemptedOption3 = "";
    String usersAttemptedOption4 = "";
    long isPollEnded = 0;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public long getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(long correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUserAnsweredId() {
        return userAnsweredId;
    }

    public void setUserAnsweredId(String userAnsweredId) {
        this.userAnsweredId = userAnsweredId;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getTotalUsersAttempted() {
        return totalUsersAttempted;
    }

    public void setTotalUsersAttempted(String totalUsersAttempted) {
        this.totalUsersAttempted = totalUsersAttempted;
    }

    public String getUsersAttemptedOption1() {
        return usersAttemptedOption1;
    }

    public void setUsersAttemptedOption1(String usersAttemptedOption1) {
        this.usersAttemptedOption1 = usersAttemptedOption1;
    }

    public String getUsersAttemptedOption2() {
        return usersAttemptedOption2;
    }

    public void setUsersAttemptedOption2(String usersAttemptedOption2) {
        this.usersAttemptedOption2 = usersAttemptedOption2;
    }

    public String getUsersAttemptedOption3() {
        return usersAttemptedOption3;
    }

    public void setUsersAttemptedOption3(String usersAttemptedOption3) {
        this.usersAttemptedOption3 = usersAttemptedOption3;
    }

    public String getUsersAttemptedOption4() {
        return usersAttemptedOption4;
    }

    public void setUsersAttemptedOption4(String usersAttemptedOption4) {
        this.usersAttemptedOption4 = usersAttemptedOption4;
    }

    public long getIsPollEnded() {
        return isPollEnded;
    }

    public void setIsPollEnded(long isPollEnded) {
        this.isPollEnded = isPollEnded;
    }
}
