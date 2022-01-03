
package com.emedicoz.app.testmodule.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {

    @SerializedName("basic_info")
    @Expose
    private BasicInfo basicInfo;
    @SerializedName("question_bank")
    @Expose
    private List<QuestionBank> questionBank = null;
    @SerializedName("user_info")
    @Expose
    private UserInfo userInfo;
    @SerializedName("question_dump")
    @Expose
    private List<QuestionDump> questionDump = null;
    @SerializedName("time_spent")
    @Expose
    private String timeSpent;
    @SerializedName("active_ques")
    @Expose
    private String activeQues;

    public BasicInfo getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(BasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    public List<QuestionBank> getQuestionBank() {
        return questionBank;
    }

    public void setQuestionBank(List<QuestionBank> questionBank) {
        this.questionBank = questionBank;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public List<QuestionDump> getQuestionDump() {
        return questionDump;
    }

    public void setQuestionDump(List<QuestionDump> questionDump) {
        this.questionDump = questionDump;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getActiveQues() {
        return activeQues;
    }

    public void setActiveQues(String activeQues) {
        this.activeQues = activeQues;
    }

}
