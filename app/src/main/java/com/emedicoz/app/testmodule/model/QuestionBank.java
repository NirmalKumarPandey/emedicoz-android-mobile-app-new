
package com.emedicoz.app.testmodule.model;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestionBank implements Serializable {

    public int clickedCount = 0;
    public int clickedCount2 = 0;
    public ArrayList<String> mtfAnswer = new ArrayList<>();
    public ArrayList<String> fibAnswer = new ArrayList<>();
    public String answerFIB = "";
    @SerializedName("part")
    @Expose
    private String part;
    @SerializedName("section_id")
    @Expose
    private String sectionId;
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("stream_id")
    @Expose
    private String streamId;
    @SerializedName("sub_stream_id")
    @Expose
    private String subStreamId;
    @SerializedName("subject_id")
    @Expose
    private String subjectId;
    @SerializedName("topic_id")
    @Expose
    private String topicId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("question_type")
    @Expose
    private String questionType;
    @SerializedName("negative_marks")
    @Expose
    private String negativeMarks;
    @SerializedName("marks")
    @Expose
    private String marks;
    @SerializedName("difficulty_level")
    @Expose
    private String difficultyLevel;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("option_1")
    @Expose
    private String option1;
    @SerializedName("option_1_attempt")
    @Expose
    private String option1Attempt;
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
    @SerializedName("option_2_attempt")
    @Expose
    private String option2Attempt;
    @SerializedName("option_3_attempt")
    @Expose
    private String option3Attempt;
    @SerializedName("option_4_attempt")
    @Expose
    private String option4Attempt;
    @SerializedName("option_5_attempt")
    @Expose
    private String option5Attempt;
    @SerializedName("option_6_attempt")
    @Expose
    private String option6Attempt;
    @SerializedName("option_7_attempt")
    @Expose
    private String option7Attempt;
    @SerializedName("option_8_attempt")
    @Expose
    private String option8Attempt;
    @SerializedName("option_9_attempt")
    @Expose
    private String option9Attempt;
    @SerializedName("option_10_attempt")
    @Expose
    private String option10Attempt;
    @SerializedName("option_11_attempt")
    @Expose
    private String option11Attempt;
    @SerializedName("option_12_attempt")
    @Expose
    private String option12Attempt;
    @SerializedName("option_13_attempt")
    @Expose
    private String option13Attempt;
    @SerializedName("option_14_attempt")
    @Expose
    private String option14Attempt;
    @SerializedName("option_15_attempt")
    @Expose
    private String option15Attempt;
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("is_verified")
    @Expose
    private String isVerified;
    @SerializedName("uploaded_by")
    @Expose
    private String uploadedBy;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("total_attempt")
    @Expose
    private String totalAttempt;
    @SerializedName("total_right")
    @Expose
    private String totalRight;
    @SerializedName("total_wrong")
    @Expose
    private String totalWrong;
    @SerializedName("for_quiz")
    @Expose
    private String forQuiz;
    @SerializedName("screen_type")
    @Expose
    private String screenType;
    @SerializedName("paragraph_text")
    @Expose
    private String paragraphText;
    @SerializedName("subject_name")
    @Expose
    private String subjectName;
    @SerializedName("video_url")
    @Expose
    private String video_url;
    @SerializedName("is_bookmarked")
    @Expose
    private String isBookmarked;
    @SerializedName("is_bookmark")
    @Expose
    private String is_bookmark;
    @SerializedName("is_video_based")
    @Expose
    private String is_video_based;
    @Expose
    private String question_reference;
    @Expose
    private String question_reference_icon;

    @Expose
    private String sharelink;

    private boolean isanswer = false;
    private boolean isselctedanswer = false;
    private boolean isMultipleAnswer = false;
    private ArrayList<Integer> selectedValue;
    private ArrayList<String> tfAnswerArrayList;
    private ArrayList<String> mtfArrayList1;
    private String anspositions = "-1";
    private ArrayList answers = new ArrayList();
    private ArrayList<String> mtfArrayList2;
    private boolean answered = false;
    private int answerPosition = -1;
    private String multipleAnswerPosition = "-1";
    private int selectedanswerPosition = -1;
    private int count = 0;
    private boolean iswronganswer = false;
    private int wronganswerPosition = -1;
    private boolean isMarkForReview;
    private String isguess = "0";
    private boolean isChecked = false;
    private boolean isPause = false;
    private int totalTimeSpent = 0;
    private boolean isanswerRight;

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public boolean isMarkForReview() {
        return isMarkForReview;
    }

    public void setMarkForReview(boolean markForReview) {
        isMarkForReview = markForReview;
    }

    public String getIsguess() {
        return isguess;
    }

    public void setIsguess(String isguess) {
        this.isguess = isguess;
    }

    public void setIsanswer(boolean isanswer, int position, String anspositions, ArrayList answers) {
        this.isanswer = isanswer;
        this.answerPosition = position;
        this.anspositions = anspositions;
        this.answers = answers;
    }

    public void setIsanswer(boolean isanswer, int position, String anspositions) {
        this.isanswer = isanswer;
        this.answerPosition = position;
        this.anspositions = anspositions;

    }

    public void setIsanswer(boolean isanswer, int position, ArrayList answers) {
        this.isanswer = isanswer;
        this.answerPosition = position;
        this.answers = answers;

    }

    public String getIs_video_based() {
        return is_video_based;
    }

    public void setIs_video_based(String is_video_based) {
        this.is_video_based = is_video_based;
    }

    @Override
    public boolean equals(Object anotherObject) {
        if (!(anotherObject instanceof QuestionBank)) {
            return false;
        }
        QuestionBank p = (QuestionBank) anotherObject;
        return (this.id == p.id);
    }

    public String getIs_bookmark() {
        return is_bookmark;
    }

    public void setIs_bookmark(String is_bookmark) {
        this.is_bookmark = is_bookmark;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public int getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(int totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public boolean isIsanswerRight() {
        return isanswerRight;
    }

    public void setIsanswerRight(boolean isanswerRight) {
        this.isanswerRight = isanswerRight;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
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

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getSubStreamId() {
        return subStreamId;
    }

    public void setSubStreamId(String subStreamId) {
        this.subStreamId = subStreamId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
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

    public String getNegativeMarks() {
        return negativeMarks;
    }

    public void setNegativeMarks(String negativeMarks) {
        this.negativeMarks = negativeMarks;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption1Attempt() {
        return option1Attempt;
    }

    public void setOption1Attempt(String option1Attempt) {
        this.option1Attempt = option1Attempt;
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

    public String getOption2Attempt() {
        return option2Attempt;
    }

    public void setOption2Attempt(String option2Attempt) {
        this.option2Attempt = option2Attempt;
    }

    public String getOption3Attempt() {
        return option3Attempt;
    }

    public void setOption3Attempt(String option3Attempt) {
        this.option3Attempt = option3Attempt;
    }

    public String getOption4Attempt() {
        return option4Attempt;
    }

    public void setOption4Attempt(String option4Attempt) {
        this.option4Attempt = option4Attempt;
    }

    public String getOption5Attempt() {
        return option5Attempt;
    }

    public void setOption5Attempt(String option5Attempt) {
        this.option5Attempt = option5Attempt;
    }

    public String getOption6Attempt() {
        return option6Attempt;
    }

    public void setOption6Attempt(String option6Attempt) {
        this.option6Attempt = option6Attempt;
    }

    public String getOption7Attempt() {
        return option7Attempt;
    }

    public void setOption7Attempt(String option7Attempt) {
        this.option7Attempt = option7Attempt;
    }

    public String getOption8Attempt() {
        return option8Attempt;
    }

    public void setOption8Attempt(String option8Attempt) {
        this.option8Attempt = option8Attempt;
    }

    public String getOption9Attempt() {
        return option9Attempt;
    }

    public void setOption9Attempt(String option9Attempt) {
        this.option9Attempt = option9Attempt;
    }

    public String getOption10Attempt() {
        return option10Attempt;
    }

    public void setOption10Attempt(String option10Attempt) {
        this.option10Attempt = option10Attempt;
    }

    public String getOption11Attempt() {
        return option11Attempt;
    }

    public void setOption11Attempt(String option11Attempt) {
        this.option11Attempt = option11Attempt;
    }

    public String getOption12Attempt() {
        return option12Attempt;
    }

    public void setOption12Attempt(String option12Attempt) {
        this.option12Attempt = option12Attempt;
    }

    public String getOption13Attempt() {
        return option13Attempt;
    }

    public void setOption13Attempt(String option13Attempt) {
        this.option13Attempt = option13Attempt;
    }

    public String getOption14Attempt() {
        return option14Attempt;
    }

    public void setOption14Attempt(String option14Attempt) {
        this.option14Attempt = option14Attempt;
    }

    public String getOption15Attempt() {
        return option15Attempt;
    }

    public void setOption15Attempt(String option15Attempt) {
        this.option15Attempt = option15Attempt;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getForQuiz() {
        return forQuiz;
    }

    public void setForQuiz(String forQuiz) {
        this.forQuiz = forQuiz;
    }

    public String getScreenType() {
        return screenType;
    }

    public void setScreenType(String screenType) {
        this.screenType = screenType;
    }

    public String getParagraphText() {
        return paragraphText;
    }

    public void setParagraphText(String paragraphText) {
        this.paragraphText = paragraphText;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getIsBookmarked() {
        return isBookmarked;
    }

    public void setIsBookmarked(String isBookmarked) {
        this.isBookmarked = isBookmarked;
    }

    public boolean isanswer() {
        return isanswer;
    }

    public boolean isMultipleAnswer() {
        return isMultipleAnswer;
    }

    public void setIsanswer(boolean isanswer, int position) {
        this.isanswer = isanswer;
        this.answerPosition = position;

    }

    public void setIsMultipleAnswer(boolean isMultipleAnswer, String position) {
        this.isMultipleAnswer = isMultipleAnswer;
        this.multipleAnswerPosition = position;

    }

    public boolean isselectedanswer() {
        return isselctedanswer;
    }

    public void setselectedanswer(boolean isanswer, int position) {
        this.isselctedanswer = isanswer;
        this.selectedanswerPosition = position;

    }

    public boolean iswronganswer() {
        return iswronganswer;
    }

    public void setIswronganswer(boolean iswronganswer, int position) {
        this.iswronganswer = iswronganswer;
        this.wronganswerPosition = position;

    }

    public ArrayList getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList answers) {
        this.answers = answers;
    }

    public int getAnswerPosttion() {
        return answerPosition;
    }

    public String getMultipleAnswerPosition() {
        return multipleAnswerPosition;
    }

    public int getselectedAnswerPosttion() {
        return selectedanswerPosition;
    }

    public int getwrongAnswerPosttion() {
        return wronganswerPosition;
    }

    public ArrayList<Integer> getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(ArrayList<Integer> selectedValue) {
        this.selectedValue = selectedValue;
    }

    public ArrayList<String> getTfAnswerArrayList() {
        return tfAnswerArrayList;
    }

    public void setTfAnswerArrayList(ArrayList<String> tfAnswerArrayList) {
        this.tfAnswerArrayList = tfAnswerArrayList;
    }

    public ArrayList<String> getMtfArrayList1() {
        return mtfArrayList1;
    }

    public void setMtfArrayList1(ArrayList<String> mtfArrayList1) {
        this.mtfArrayList1 = mtfArrayList1;
    }

    public ArrayList<String> getMtfArrayList2() {
        return mtfArrayList2;
    }

    public void setMtfArrayList2(ArrayList<String> mtfArrayList2) {
        this.mtfArrayList2 = mtfArrayList2;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ArrayList<String> getFibAnswer() {
        return fibAnswer;
    }

    public void setFibAnswer(ArrayList<String> fibAnswer) {
        this.fibAnswer = fibAnswer;
    }

    public String getAnswerFIB() {
        return answerFIB;
    }

    public void setAnswerFIB(String answerFIB) {
        this.answerFIB = answerFIB;
    }

    public String getColor1() {
        if (answerPosition == -1)
            return "0";
        else if (answerPosition == 0)
            return "2";
        else if (answerPosition != -1 && answerPosition != 0)
            return "1";
        else
            return "3";

    }

    public String getQuestion_reference_icon() {
        return question_reference_icon;
    }

    public void setQuestion_reference_icon(String question_reference_icon) {
        this.question_reference_icon = question_reference_icon;
    }

    public String getQuestion_reference() {
        return question_reference;
    }

    public void setQuestion_reference(String question_reference) {
        this.question_reference = question_reference;
    }

    public String getSharelink() {
        return sharelink;
    }

    public void setSharelink(String sharelink) {
        this.sharelink = sharelink;
    }
}
