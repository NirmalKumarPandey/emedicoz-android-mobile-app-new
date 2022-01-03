
package com.emedicoz.app.testmodule.model;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BasicInfo implements Serializable {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName(Constants.ResultExtras.TEST_SERIES_NAME)
    @Expose
    private String testSeriesName;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("subject_name")
    @Expose
    private String subject_name;
    @SerializedName("difficulty_level")
    @Expose
    private String difficultyLevel;
    @SerializedName("test_price")
    @Expose
    private String testPrice;
    @SerializedName("test_type")
    @Expose
    private String testType;
    @SerializedName("backend_user_id")
    @Expose
    private String backendUserId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("instruction")
    @Expose
    private String instruction;
    @SerializedName("session")
    @Expose
    private String session;
    @SerializedName("total_questions")
    @Expose
    private String totalQuestions;
    @SerializedName("consider_time")
    @Expose
    private String considerTime;
    @SerializedName("time_in_mins")
    @Expose
    private String timeInMins;
    @SerializedName("negative_marking")
    @Expose
    private String negativeMarking;
    @SerializedName("total_marks")
    @Expose
    private String totalMarks;
    @SerializedName("marks_per_question")
    @Expose
    private String marksPerQuestion;
    @SerializedName("shuffle")
    @Expose
    private String shuffle;
    @SerializedName("answer_shuffle")
    @Expose
    private String answerShuffle;
    @SerializedName("mandatory_check")
    @Expose
    private String mandatoryCheck;
    @SerializedName("allow_user_move")
    @Expose
    private String allowUserMove;
    @SerializedName("time_boundation")
    @Expose
    private String timeBoundation;
    @SerializedName("show_question_time")
    @Expose
    private String showQuestionTime;
    @SerializedName("pass_message")
    @Expose
    private String passMessage;
    @SerializedName("general_message")
    @Expose
    private String generalMessage;
    @SerializedName("fail_message")
    @Expose
    private String failMessage;
    @SerializedName("pass_percentage")
    @Expose
    private String passPercentage;
    @SerializedName("allow_duplicate_rank")
    @Expose
    private String allowDuplicateRank;
    @SerializedName("skip_rank")
    @Expose
    private String skipRank;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("publish")
    @Expose
    private String publish;
    @SerializedName("reward_points")
    @Expose
    private String rewardPoints;
    @SerializedName("set_type")
    @Expose
    private String setType;
    @SerializedName("test_type_master")
    @Expose
    private String testTypeMaster;
    @SerializedName("test_start_date")
    @Expose
    private String testStartDate;
    @SerializedName("test_end_date")
    @Expose
    private String testEndDate;
    @SerializedName(Constants.ResultExtras.TEST_RESULT_DATE)
    @Expose
    private String testResultDate;
    @SerializedName("video_solution")
    @Expose
    private String videoSolution;
    @SerializedName("bookmark_count")
    @Expose
    private String bookmark_count;
    @SerializedName("is_vod")
    @Expose
    private String isVod;
    @SerializedName("marking_scheme")
    @Expose
    private String markingScheme;
    @SerializedName("display_v_solution")
    @Expose
    private String displayVSolution;
    @SerializedName("display_reattempt")
    @Expose
    private String displayReattempt;
    @SerializedName("display_bookmark")
    @Expose
    private String display_bookmark;
    @SerializedName("template_html")
    @Expose
    private String templateHtml;
    @SerializedName("parts")
    @Expose
    private List<Part> parts = null;
    @SerializedName("section")
    @Expose
    private List<Section> section = null;
    @SerializedName("display_guess")
    @Expose
    private String display_guess;

    private String display_qid;

    private String display_bubble;

    private long timeRemaining = 0;

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTestSeriesName() {
        return testSeriesName;
    }

    public void setTestSeriesName(String testSeriesName) {
        this.testSeriesName = testSeriesName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getTestPrice() {
        return testPrice;
    }

    public void setTestPrice(String testPrice) {
        this.testPrice = testPrice;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getBackendUserId() {
        return backendUserId;
    }

    public void setBackendUserId(String backendUserId) {
        this.backendUserId = backendUserId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(String totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getConsiderTime() {
        return considerTime;
    }

    public void setConsiderTime(String considerTime) {
        this.considerTime = considerTime;
    }

    public String getTimeInMins() {
        return timeInMins;
    }

    public void setTimeInMins(String timeInMins) {
        this.timeInMins = timeInMins;
    }

    public String getNegativeMarking() {
        return negativeMarking;
    }

    public void setNegativeMarking(String negativeMarking) {
        this.negativeMarking = negativeMarking;
    }

    public String getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(String totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getMarksPerQuestion() {
        return marksPerQuestion;
    }

    public void setMarksPerQuestion(String marksPerQuestion) {
        this.marksPerQuestion = marksPerQuestion;
    }

    public String getShuffle() {
        return shuffle;
    }

    public void setShuffle(String shuffle) {
        this.shuffle = shuffle;
    }

    public String getAnswerShuffle() {
        return answerShuffle;
    }

    public void setAnswerShuffle(String answerShuffle) {
        this.answerShuffle = answerShuffle;
    }

    public String getMandatoryCheck() {
        return mandatoryCheck;
    }

    public void setMandatoryCheck(String mandatoryCheck) {
        this.mandatoryCheck = mandatoryCheck;
    }

    public String getAllowUserMove() {
        return allowUserMove;
    }

    public void setAllowUserMove(String allowUserMove) {
        this.allowUserMove = allowUserMove;
    }

    public String getTimeBoundation() {
        return timeBoundation;
    }

    public void setTimeBoundation(String timeBoundation) {
        this.timeBoundation = timeBoundation;
    }

    public String getShowQuestionTime() {
        return showQuestionTime;
    }

    public void setShowQuestionTime(String showQuestionTime) {
        this.showQuestionTime = showQuestionTime;
    }

    public String getPassMessage() {
        return passMessage;
    }

    public void setPassMessage(String passMessage) {
        this.passMessage = passMessage;
    }

    public String getGeneralMessage() {
        return generalMessage;
    }

    public void setGeneralMessage(String generalMessage) {
        this.generalMessage = generalMessage;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    public String getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(String passPercentage) {
        this.passPercentage = passPercentage;
    }

    public String getAllowDuplicateRank() {
        return allowDuplicateRank;
    }

    public void setAllowDuplicateRank(String allowDuplicateRank) {
        this.allowDuplicateRank = allowDuplicateRank;
    }

    public String getSkipRank() {
        return skipRank;
    }

    public void setSkipRank(String skipRank) {
        this.skipRank = skipRank;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

    public String getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(String rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public String getSetType() {
        return setType;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }

    public String getTestTypeMaster() {
        return testTypeMaster;
    }

    public void setTestTypeMaster(String testTypeMaster) {
        this.testTypeMaster = testTypeMaster;
    }

    public String getTestStartDate() {
        return testStartDate;
    }

    public void setTestStartDate(String testStartDate) {
        this.testStartDate = testStartDate;
    }

    public String getTestEndDate() {
        return testEndDate;
    }

    public void setTestEndDate(String testEndDate) {
        this.testEndDate = testEndDate;
    }

    public String getTestResultDate() {
        return testResultDate;
    }

    public void setTestResultDate(String testResultDate) {
        this.testResultDate = testResultDate;
    }

    public String getVideoSolution() {
        return videoSolution;
    }

    public void setVideoSolution(String videoSolution) {
        this.videoSolution = videoSolution;
    }

    public String getIsVod() {
        return isVod;
    }

    public void setIsVod(String isVod) {
        this.isVod = isVod;
    }

    public String getMarkingScheme() {
        return markingScheme;
    }

    public void setMarkingScheme(String markingScheme) {
        this.markingScheme = markingScheme;
    }

    public String getDisplayVSolution() {
        return displayVSolution;
    }

    public void setDisplayVSolution(String displayVSolution) {
        this.displayVSolution = displayVSolution;
    }

    public String getDisplayReattempt() {
        return displayReattempt;
    }

    public void setDisplayReattempt(String displayReattempt) {
        this.displayReattempt = displayReattempt;
    }

    public String getTemplateHtml() {
        return templateHtml;
    }

    public void setTemplateHtml(String templateHtml) {
        this.templateHtml = templateHtml;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public List<Section> getSection() {
        return section;
    }

    public void setSection(List<Section> section) {
        this.section = section;
    }

    public String getDisplay_qid() {
        return display_qid;
    }

    public void setDisplay_qid(String display_qid) {
        this.display_qid = display_qid;
    }

    public String getDisplay_bubble() {
        return display_bubble;
    }

    public void setDisplay_bubble(String display_bubble) {
        this.display_bubble = display_bubble;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getBookmark_count() {
        return bookmark_count;
    }

    public void setBookmark_count(String bookmark_count) {
        this.bookmark_count = bookmark_count;
    }

    public String getDisplay_bookmark() {
        return display_bookmark;
    }

    public void setDisplay_bookmark(String display_bookmark) {
        this.display_bookmark = display_bookmark;
    }

    public String getDisplay_guess() {
        return display_guess;
    }

    public void setDisplay_guess(String display_guess) {
        this.display_guess = display_guess;
    }
}
