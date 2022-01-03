package com.emedicoz.app.modelo.courses.quiz;

import com.emedicoz.app.modelo.Parts;
import com.emedicoz.app.modelo.QuestionDump;
import com.emedicoz.app.utilso.GenericUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Abhi on 11/11/2017.
 */

public class ResultTestSeries implements Serializable {
    private String result;
    @SerializedName("non_attempt")
    private String nonAttempt;

    private String marks;

    private ArrayList<Parts> parts;

    @SerializedName("total_user_attempt")
    private String totalUserAttempt;

    @SerializedName("total_test_series_time")
    private String totalTestSeriesTime;

    private String id;

    private String topperScore;

    private String accuracy;

    private String percentile;

    private String attempted;

    private String subject;

    @SerializedName("set_type")
    private String setType;

    @SerializedName("incorrect_count")
    private String incorrectCount;

    @SerializedName("marks_per_question")
    private String marksPerQuestion;

    @SerializedName("average_marks1")
    private AverageMarks averageMarks;

    @SerializedName("total_questions")
    private String totalQuestions;

    // private String question_dump;

    @SerializedName("question_dump")
    private ArrayList<QuestionDump> questionDumps;

    @SerializedName("test_series_id")
    private String testSeriesId;

    @SerializedName("test_segment_id")
    private String testSegmentId;

    @SerializedName("time_spent")
    private String timeSpent;

    @SerializedName("user_rank")
    private String userRank;

    private String percentage;

    @SerializedName("skip_rank")
    private String skipRank;

    @SerializedName("bookmark_count")
    private String bookmarkCount;

    @SerializedName("subject_name")
    private String subjectName;

    private String image;

    @SerializedName("test_complete_date")
    private String testCompleteDate;

    @SerializedName("reward_points")
    private String rewardPoints;

    @SerializedName("marking_scheme")
    private String markingScheme;

    @SerializedName("creation_time")
    private String creationTime;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("test_type")
    private String testType;

    @SerializedName("test_series_name")
    private String testSeriesName;

    @SerializedName("correct_count")
    private String correctCount;

    @SerializedName(value = "total_series_marks", alternate = {"total_marks"})
    private String testSeriesMarks;

    @SerializedName("best_score")
    private String bestScore;

    @SerializedName("avg_score")
    private String avgScore;

    @SerializedName("test_result_date")
    private String testResultDate;

    @SerializedName("guess_count")
    private String guessCount;

    @SerializedName("correct_guess")
    private String correctGuess;

    @SerializedName("avg_rating")
    private String avgRating;

    @SerializedName("display_reattempt")
    private String displayReattempt;

    @SerializedName("display_v_solution")
    private String displayVSolution;

    @SerializedName("display_pdf_solution")
    private String displayPdfSolution;

    @SerializedName("pdf_solution")
    private String pdfSolution;

    @SerializedName("top_ten_list")
    private ArrayList<LeaderBoardUserModel> topTenList;

    public String getMarksPerQuestion() {
        return marksPerQuestion;
    }

    public void setMarksPerQuestion(String marksPerQuestion) {
        this.marksPerQuestion = marksPerQuestion;
    }

    public String getCorrectGuess() {
        return correctGuess;
    }

    public void setCorrectGuess(String correctGuess) {
        this.correctGuess = correctGuess;
    }

    public String getGuessCount() {
        return guessCount;
    }

    public void setGuessCount(String guessCount) {
        this.guessCount = guessCount;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getSkipRank() {
        return skipRank;
    }

    public void setSkipRank(String skipRank) {
        this.skipRank = skipRank;
    }

    public ArrayList<LeaderBoardUserModel> getTopTenList() {
        return topTenList;
    }

    public void setTopTenList(ArrayList<LeaderBoardUserModel> topTenList) {
        this.topTenList = topTenList;
    }

    public String getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(String totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getTestSeriesName() {
        return testSeriesName;
    }

    public void setTestSeriesName(String testSeriesName) {
        this.testSeriesName = testSeriesName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public String getTotalUserAttempt() {
        return totalUserAttempt;
    }

    public void setTotalUserAttempt(String totalUserAttempt) {
        this.totalUserAttempt = totalUserAttempt;
    }

    public String getTotalTestSeriesTime() {
        return totalTestSeriesTime;
    }

    public void setTotalTestSeriesTime(String totalTestSeriesTime) {
        this.totalTestSeriesTime = totalTestSeriesTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIncorrectCount() {
        return incorrectCount;
    }

    public void setIncorrectCount(String incorrectCount) {
        this.incorrectCount = incorrectCount;
    }

/*    public String getQuestion_dump() {
        return question_dump;
    }

    public void setQuestion_dump(String question_dump) {
        this.question_dump = question_dump;
    }*/

    public String getTestSeriesId() {
        return testSeriesId;
    }

    public void setTestSeriesId(String testSeriesId) {
        this.testSeriesId = testSeriesId;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getUserRank() {
        return userRank;
    }

    public void setUserRank(String userRank) {
        this.userRank = userRank;
    }

    public String getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(String rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public String getCreationTime() {
        if (!GenericUtils.isEmpty(creationTime))
            return creationTime;
        else
            return "0";
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(String correctCount) {
        this.correctCount = correctCount;
    }

    public String getTestSeriesMarks() {
        return testSeriesMarks;
    }

    public void setTestSeriesMarks(String testSeriesMarks) {
        this.testSeriesMarks = testSeriesMarks;
    }

    public ArrayList<QuestionDump> getQuestionDump() {
        return questionDumps;
    }

    public void setQuestionDump(ArrayList<QuestionDump> question_dump) {
        this.questionDumps = question_dump;
    }

    public String getTestResultDate() {
        return testResultDate;
    }

    public void setTestResultDate(String testResultDate) {
        this.testResultDate = testResultDate;
    }

    public ArrayList<Parts> getParts() {
        return parts;
    }

    public void setParts(ArrayList<Parts> parts) {
        this.parts = parts;
    }

    public String getSetType() {
        return setType;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getPercentile() {
        return percentile;
    }

    public void setPercentile(String percentile) {
        this.percentile = percentile;
    }

    public String getAttempted() {
        return attempted;
    }

    public void setAttempted(String attempted) {
        this.attempted = attempted;
    }

    public AverageMarks getAverageMarks() {
        return averageMarks;
    }

    public void setAverageMarks(AverageMarks averageMarks) {
        this.averageMarks = averageMarks;
    }

    public String getDisplayReattempt() {
        return displayReattempt;
    }

    public void setDisplayReattempt(String displayReattempt) {
        this.displayReattempt = displayReattempt;
    }

    public String getDisplayVSolution() {
        return displayVSolution;
    }

    public void setDisplayVSolution(String displayVSolution) {
        this.displayVSolution = displayVSolution;
    }

    public String getBookmarkCount() {
        return bookmarkCount;
    }

    public void setBookmarkCount(String bookmarkCount) {
        this.bookmarkCount = bookmarkCount;
    }

    public String getTestCompleteDate() {
        return testCompleteDate;
    }

    public void setTestCompleteDate(String testCompleteDate) {
        this.testCompleteDate = testCompleteDate;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
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

    public String getMarkingScheme() {
        return markingScheme;
    }

    public void setMarkingScheme(String markingScheme) {
        this.markingScheme = markingScheme;
    }

    public String getTopperScore() {
        return topperScore;
    }

    public void setTopperScore(String topperScore) {
        this.topperScore = topperScore;
    }

    public String getTestSegmentId() {
        return testSegmentId;
    }

    public void setTestSegmentId(String testSegmentId) {
        this.testSegmentId = testSegmentId;
    }
}
