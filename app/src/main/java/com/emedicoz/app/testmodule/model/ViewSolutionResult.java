package com.emedicoz.app.testmodule.model;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ViewSolutionResult implements Serializable {
    @SerializedName("section_title")
    @Expose
    private String sectionTitle;
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("question_type")
    @Expose
    private String questionType;
    @SerializedName("subject_id")
    @Expose
    private String subjectId;
    @SerializedName(Constants.Extras.NAME)
    @Expose
    private String name;
    @SerializedName("option_1")
    @Expose
    private String option1;
    @SerializedName("option_2")
    @Expose
    private String option2;
    @SerializedName("option_3")
    @Expose
    private String option3;
    @SerializedName("option_4")
    @Expose
    private String option4;
    @SerializedName("option_5")
    @Expose
    private String option5;

    @SerializedName("option_6")
    @Expose
    private String option6;

    @SerializedName("option_7")
    @Expose
    private String option7;

    @SerializedName("option_8")
    @Expose
    private String option8;

    @SerializedName("option_9")
    @Expose
    private String option9;

    @SerializedName("option_10")
    @Expose
    private String option10;

    @SerializedName("option_11")
    @Expose
    private String option11;

    @SerializedName("option_12")
    @Expose
    private String option12;

    @SerializedName("option_13")
    @Expose
    private String option13;

    @SerializedName("option_14")
    @Expose
    private String option14;

    @SerializedName("option_15")
    @Expose
    private String option15;

    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("total_attempt")
    @Expose
    private String totalAttempt;
    @SerializedName("total_right")
    @Expose
    private String totalRight;
    @SerializedName("total_wrong")
    @Expose
    private String totalWrong;
    @SerializedName("user_answer")
    @Expose
    private String userAnswer;
    @SerializedName("is_bookmarked")
    @Expose
    private String is_bookmarked;
    @SerializedName("is_selected")
    @Expose
    private boolean is_selected;
    private String video_url;
    private ArrayList<String> tfAnswerArrayList;
    @Expose
    private String question_reference;
    @Expose
    private String question_reference_icon;

    public String getQuestion_reference() {
        return question_reference;
    }

    public void setQuestion_reference(String question_reference) {
        this.question_reference = question_reference;
    }

    public String getQuestion_reference_icon() {
        return question_reference_icon;
    }

    public void setQuestion_reference_icon(String question_reference_icon) {
        this.question_reference_icon = question_reference_icon;
    }

    public boolean Is_selected() {
        return is_selected;
    }

    public void set_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getOption5() {
        return option5;
    }

    public void setOption5(String option5) {
        this.option5 = option5;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getTotalAttempt() {
        return totalAttempt;
    }

    public void setTotalAttempt(String totalAttempt) {
        this.totalAttempt = totalAttempt;
    }

    public String getTotalRight() {
        return totalRight;
    }

    public void setTotalRight(String totalRight) {
        this.totalRight = totalRight;
    }

    public String getTotalWrong() {
        return totalWrong;
    }

    public void setTotalWrong(String totalWrong) {
        this.totalWrong = totalWrong;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getOption6() {
        return option6;
    }

    public void setOption6(String option6) {
        this.option6 = option6;
    }

    public String getOption7() {
        return option7;
    }

    public void setOption7(String option7) {
        this.option7 = option7;
    }

    public String getOption8() {
        return option8;
    }

    public void setOption8(String option8) {
        this.option8 = option8;
    }

    public String getOption9() {
        return option9;
    }

    public void setOption9(String option9) {
        this.option9 = option9;
    }

    public String getOption10() {
        return option10;
    }

    public void setOption10(String option10) {
        this.option10 = option10;
    }

    public String getOption11() {
        return option11;
    }

    public void setOption11(String option11) {
        this.option11 = option11;
    }

    public String getOption12() {
        return option12;
    }

    public void setOption12(String option12) {
        this.option12 = option12;
    }

    public String getOption13() {
        return option13;
    }

    public void setOption13(String option13) {
        this.option13 = option13;
    }

    public String getOption14() {
        return option14;
    }

    public void setOption14(String option14) {
        this.option14 = option14;
    }

    public String getOption15() {
        return option15;
    }

    public void setOption15(String option15) {
        this.option15 = option15;
    }

    public String getIs_bookmark() {
        return is_bookmarked;
    }

    public void setIs_bookmark(String is_bookmark) {
        this.is_bookmarked = is_bookmark;
    }

    public ArrayList<String> getTfAnswerArrayList() {
        return tfAnswerArrayList;
    }

    public void setTfAnswerArrayList(ArrayList<String> tfAnswerArrayList) {
        this.tfAnswerArrayList = tfAnswerArrayList;
    }
}
