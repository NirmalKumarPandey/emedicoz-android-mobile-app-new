package com.emedicoz.app.dailychallenge.model;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DCArchiveData {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("test_series_id")
    @Expose
    private String testSeriesId;
    @SerializedName(Const.USER_ID)
    @Expose
    private String userId;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("time_spent")
    @Expose
    private String timeSpent;
    @SerializedName(Constants.ResultExtras.CORRECT_COUNT)
    @Expose
    private String correctCount;

    @SerializedName("dq_title")
    @Expose
    private String dqTitle;
    @SerializedName(Constants.ResultExtras.INCORRECT_COUNT)
    @Expose
    private String incorrectCount;
    @SerializedName(Constants.ResultExtras.NON_ATTEMPT)
    @Expose
    private String nonAttempt;
    @SerializedName("marks")
    @Expose
    private String marks;
    @SerializedName("negative_marks")
    @Expose
    private String negativeMarks;
    @SerializedName("percentage")
    @Expose
    private String percentage;
    @SerializedName("test_series_marks")
    @Expose
    private String testSeriesMarks;
    @SerializedName("guess_count")
    @Expose
    private String guessCount;
    @SerializedName("correct_guess")
    @Expose
    private String correctGuess;
    @SerializedName("incorrect_guess")
    @Expose
    private String incorrectGuess;
    @SerializedName("total_test_series_time")
    @Expose
    private String totalTestSeriesTime;
    @SerializedName("reward_points")
    @Expose
    private String rewardPoints;
    @SerializedName("first_attempt")
    @Expose
    private String firstAttempt;
    @SerializedName("creation_time")
    @Expose
    private String creationTime;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("last_view")
    @Expose
    private String lastView;
    @SerializedName("report_origin")
    @Expose
    private String reportOrigin;
    @SerializedName("attempt_one")
    @Expose
    private String attemptOne;
    @SerializedName("subject_name")
    @Expose
    private String subjectName;
    @SerializedName("total_questions")
    @Expose
    private String totalQuestions;
    @SerializedName("marking_scheme")
    @Expose
    private String markingScheme;
    @SerializedName(Constants.ResultExtras.TEST_SERIES_NAME)
    @Expose
    private String testSeriesName;
    @SerializedName("avg_rating")
    @Expose
    private String avgRating;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("display_v_solution")
    @Expose
    private String displayVSolution;
    @SerializedName("display_reattempt")
    @Expose
    private String displayReattempt;
    @SerializedName("set_type")
    @Expose
    private String setType;
    @SerializedName("test_type")
    @Expose
    private String testType;
    @SerializedName("skip_rank")
    @Expose
    private String skipRank;
    @SerializedName(Constants.ResultExtras.TEST_RESULT_DATE)
    @Expose
    private String testResultDate;
    @SerializedName("marks_per_question")
    @Expose
    private String marksPerQuestion;
    @SerializedName("total_marks")
    @Expose
    private String totalMarks;
    @SerializedName("negative_marking")
    @Expose
    private String negativeMarking;
    @SerializedName("display_pdf_solution")
    @Expose
    private String displayPdfSolution;
    @SerializedName("pdf_solution")
    @Expose
    private String pdfSolution;
    @SerializedName("parts")
    @Expose
    private List<Object> parts = null;
    @SerializedName("best_score")
    @Expose
    private String bestScore;
    @SerializedName("avg_score")
    @Expose
    private String avgScore;
    @SerializedName("attempted")
    @Expose
    private String attempted;
    @SerializedName(Constants.ResultExtras.ACCURACY)
    @Expose
    private String accuracy;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("total_user_attempt")
    @Expose
    private String totalUserAttempt;
    @SerializedName("user_rank")
    @Expose
    private String userRank;
    @SerializedName("test_segment_id")
    @Expose
    private String testSegmentId;

    public String getTestSegmentId() {
        return testSegmentId;
    }

    public void setTestSegmentId(String testSegmentId) {
        this.testSegmentId = testSegmentId;
    }

    @SerializedName("average_marks")
    @Expose
    private Boolean averageMarks;
    @SerializedName("average_marks1")
    @Expose
    private ArchiveAverageMarks archiveAverageMarks;
    @SerializedName("percentile")
    @Expose
    private String percentile;
    @SerializedName("bookmark_count")
    @Expose
    private String bookmarkCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestSeriesId() {
        return testSeriesId;
    }

    public void setTestSeriesId(String testSeriesId) {
        this.testSeriesId = testSeriesId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(String correctCount) {
        this.correctCount = correctCount;
    }

    public String getIncorrectCount() {
        return incorrectCount;
    }

    public void setIncorrectCount(String incorrectCount) {
        this.incorrectCount = incorrectCount;
    }

    public String getNonAttempt() {
        return nonAttempt;
    }

    public void setNonAttempt(String nonAttempt) {
        this.nonAttempt = nonAttempt;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getNegativeMarks() {
        return negativeMarks;
    }

    public void setNegativeMarks(String negativeMarks) {
        this.negativeMarks = negativeMarks;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getTestSeriesMarks() {
        return testSeriesMarks;
    }

    public void setTestSeriesMarks(String testSeriesMarks) {
        this.testSeriesMarks = testSeriesMarks;
    }

    public String getGuessCount() {
        return guessCount;
    }

    public void setGuessCount(String guessCount) {
        this.guessCount = guessCount;
    }

    public String getCorrectGuess() {
        return correctGuess;
    }

    public void setCorrectGuess(String correctGuess) {
        this.correctGuess = correctGuess;
    }

    public String getIncorrectGuess() {
        return incorrectGuess;
    }

    public void setIncorrectGuess(String incorrectGuess) {
        this.incorrectGuess = incorrectGuess;
    }

    public String getTotalTestSeriesTime() {
        return totalTestSeriesTime;
    }

    public void setTotalTestSeriesTime(String totalTestSeriesTime) {
        this.totalTestSeriesTime = totalTestSeriesTime;
    }

    public String getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(String rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public String getFirstAttempt() {
        return firstAttempt;
    }

    public void setFirstAttempt(String firstAttempt) {
        this.firstAttempt = firstAttempt;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLastView() {
        return lastView;
    }

    public void setLastView(String lastView) {
        this.lastView = lastView;
    }

    public String getReportOrigin() {
        return reportOrigin;
    }

    public void setReportOrigin(String reportOrigin) {
        this.reportOrigin = reportOrigin;
    }

    public String getAttemptOne() {
        return attemptOne;
    }

    public void setAttemptOne(String attemptOne) {
        this.attemptOne = attemptOne;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(String totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getMarkingScheme() {
        return markingScheme;
    }

    public void setMarkingScheme(String markingScheme) {
        this.markingScheme = markingScheme;
    }

    public String getTestSeriesName() {
        return testSeriesName;
    }

    public void setTestSeriesName(String testSeriesName) {
        this.testSeriesName = testSeriesName;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getSetType() {
        return setType;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getSkipRank() {
        return skipRank;
    }

    public void setSkipRank(String skipRank) {
        this.skipRank = skipRank;
    }

    public String getTestResultDate() {
        return testResultDate;
    }

    public void setTestResultDate(String testResultDate) {
        this.testResultDate = testResultDate;
    }

    public String getMarksPerQuestion() {
        return marksPerQuestion;
    }

    public void setMarksPerQuestion(String marksPerQuestion) {
        this.marksPerQuestion = marksPerQuestion;
    }

    public String getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(String totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getNegativeMarking() {
        return negativeMarking;
    }

    public void setNegativeMarking(String negativeMarking) {
        this.negativeMarking = negativeMarking;
    }

    public String getDisplayPdfSolution() {
        return displayPdfSolution;
    }

    public void setDisplayPdfSolution(String displayPdfSolution) {
        this.displayPdfSolution = displayPdfSolution;
    }

    public String getPdfSolution() {
        return pdfSolution;
    }

    public void setPdfSolution(String pdfSolution) {
        this.pdfSolution = pdfSolution;
    }

    public List<Object> getParts() {
        return parts;
    }

    public void setParts(List<Object> parts) {
        this.parts = parts;
    }

    public String getBestScore() {
        return bestScore;
    }

    public void setBestScore(String bestScore) {
        this.bestScore = bestScore;
    }

    public String getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(String avgScore) {
        this.avgScore = avgScore;
    }

    public String getAttempted() {
        return attempted;
    }

    public void setAttempted(String attempted) {
        this.attempted = attempted;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTotalUserAttempt() {
        return totalUserAttempt;
    }

    public void setTotalUserAttempt(String totalUserAttempt) {
        this.totalUserAttempt = totalUserAttempt;
    }

    public String getUserRank() {
        return userRank;
    }

    public void setUserRank(String userRank) {
        this.userRank = userRank;
    }

    public Boolean getAverageMarks() {
        return averageMarks;
    }

    public void setAverageMarks(Boolean averageMarks) {
        this.averageMarks = averageMarks;
    }

    public ArchiveAverageMarks getArchiveAverageMarks() {
        return archiveAverageMarks;
    }

    public void setAverageMarks1(ArchiveAverageMarks archiveAverageMarks) {
        this.archiveAverageMarks = archiveAverageMarks;
    }

    public String getPercentile() {
        return percentile;
    }

    public void setPercentile(String percentile) {
        this.percentile = percentile;
    }

    public String getBookmarkCount() {
        return bookmarkCount;
    }

    public void setBookmarkCount(String bookmarkCount) {
        this.bookmarkCount = bookmarkCount;
    }

    public String getDqTitle() {
        return dqTitle;
    }

    public void setDqTitle(String dqTitle) {
        this.dqTitle = dqTitle;
    }
}
