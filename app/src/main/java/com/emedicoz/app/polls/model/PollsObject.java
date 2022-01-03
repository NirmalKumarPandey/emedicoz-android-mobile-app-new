package com.emedicoz.app.polls.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PollsObject {

    @SerializedName("ques_id")
    @Expose
    private Integer quesId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("poll_id")
    @Expose
    private Integer pollId;
    @SerializedName("course_id")
    @Expose
    private Integer courseId;
    @SerializedName("correct_option_id")
    @Expose
    private Integer correctOptionId;
    @SerializedName("answer_status_msg")
    @Expose
    private String answerStatusMsg;
    @SerializedName("options")
    @Expose
    private List<Options> options = null;

    public Integer getQuesId() {
        return quesId;
    }

    public void setQuesId(Integer quesId) {
        this.quesId = quesId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPollId() {
        return pollId;
    }

    public void setPollId(Integer pollId) {
        this.pollId = pollId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(Integer correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    public String getAnswerStatusMsg() {
        return answerStatusMsg;
    }

    public void setAnswerStatusMsg(String answerStatusMsg) {
        this.answerStatusMsg = answerStatusMsg;
    }

    public List<Options> getOptions() {
        return options;
    }

    public void setOptions(List<Options> options) {
        this.options = options;
    }
}
