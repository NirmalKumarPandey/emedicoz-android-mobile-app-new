
package com.emedicoz.app.modelo.courses.quiz.newquizresult;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Data {

    @SerializedName(Constants.ResultExtras.CORRECT_COUNT)
    private String mCorrectCount;
    @SerializedName("correct_guess")
    private String mCorrectGuess;
    @SerializedName("creation_time")
    private String mCreationTime;
    @SerializedName("first_attempt")
    private String mFirstAttempt;
    @SerializedName("guess_count")
    private String mGuessCount;
    @SerializedName(Constants.Extras.ID)
    private String mId;
    @SerializedName(Constants.ResultExtras.INCORRECT_COUNT)
    private String mIncorrectCount;
    @SerializedName("incorrect_guess")
    private String mIncorrectGuess;
    @SerializedName("last_view")
    private String mLastView;
    @SerializedName("marks")
    private String mMarks;
    @SerializedName(Constants.ResultExtras.NON_ATTEMPT)
    private String mNonAttempt;
    @SerializedName("question_dump")
    private String mQuestionDump;
    @SerializedName("report_origin")
    private String mReportOrigin;
    @SerializedName("result")
    private String mResult;
    @SerializedName("reward_points")
    private String mRewardPoints;
    @SerializedName("skip_rank")
    private String mSkipRank;
    @SerializedName("state")
    private String mState;
    @SerializedName("test_series_id")
    private String mTestSeriesId;
    @SerializedName("test_series_marks")
    private String mTestSeriesMarks;
    @SerializedName(Constants.ResultExtras.TEST_SERIES_NAME)
    private String mTestSeriesName;
    @SerializedName("test_type")
    private String mTestType;
    @SerializedName("time_spent")
    private String mTimeSpent;
    @SerializedName("top_ten_list")
    private List<TopTenList> mTopTenList;
    @SerializedName("total_test_series_time")
    private String mTotalTestSeriesTime;
    @SerializedName("total_user_attempt")
    private String mTotalUserAttempt;
    @SerializedName(Const.USER_ID)
    private String mUserId;
    @SerializedName("user_rank")
    private String mUserRank;

    public String getCorrectCount() {
        return mCorrectCount;
    }

    public void setCorrectCount(String correctCount) {
        mCorrectCount = correctCount;
    }

    public String getCorrectGuess() {
        return mCorrectGuess;
    }

    public void setCorrectGuess(String correctGuess) {
        mCorrectGuess = correctGuess;
    }

    public String getCreationTime() {
        return mCreationTime;
    }

    public void setCreationTime(String creationTime) {
        mCreationTime = creationTime;
    }

    public String getFirstAttempt() {
        return mFirstAttempt;
    }

    public void setFirstAttempt(String firstAttempt) {
        mFirstAttempt = firstAttempt;
    }

    public String getGuessCount() {
        return mGuessCount;
    }

    public void setGuessCount(String guessCount) {
        mGuessCount = guessCount;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getIncorrectCount() {
        return mIncorrectCount;
    }

    public void setIncorrectCount(String incorrectCount) {
        mIncorrectCount = incorrectCount;
    }

    public String getIncorrectGuess() {
        return mIncorrectGuess;
    }

    public void setIncorrectGuess(String incorrectGuess) {
        mIncorrectGuess = incorrectGuess;
    }

    public String getLastView() {
        return mLastView;
    }

    public void setLastView(String lastView) {
        mLastView = lastView;
    }

    public String getMarks() {
        return mMarks;
    }

    public void setMarks(String marks) {
        mMarks = marks;
    }

    public String getNonAttempt() {
        return mNonAttempt;
    }

    public void setNonAttempt(String nonAttempt) {
        mNonAttempt = nonAttempt;
    }

    public String getQuestionDump() {
        return mQuestionDump;
    }

    public void setQuestionDump(String questionDump) {
        mQuestionDump = questionDump;
    }

    public String getReportOrigin() {
        return mReportOrigin;
    }

    public void setReportOrigin(String reportOrigin) {
        mReportOrigin = reportOrigin;
    }

    public String getResult() {
        return mResult;
    }

    public void setResult(String result) {
        mResult = result;
    }

    public String getRewardPoints() {
        return mRewardPoints;
    }

    public void setRewardPoints(String rewardPoints) {
        mRewardPoints = rewardPoints;
    }

    public String getSkipRank() {
        return mSkipRank;
    }

    public void setSkipRank(String skipRank) {
        mSkipRank = skipRank;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getTestSeriesId() {
        return mTestSeriesId;
    }

    public void setTestSeriesId(String testSeriesId) {
        mTestSeriesId = testSeriesId;
    }

    public String getTestSeriesMarks() {
        return mTestSeriesMarks;
    }

    public void setTestSeriesMarks(String testSeriesMarks) {
        mTestSeriesMarks = testSeriesMarks;
    }

    public String getTestSeriesName() {
        return mTestSeriesName;
    }

    public void setTestSeriesName(String testSeriesName) {
        mTestSeriesName = testSeriesName;
    }

    public String getTestType() {
        return mTestType;
    }

    public void setTestType(String testType) {
        mTestType = testType;
    }

    public String getTimeSpent() {
        return mTimeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        mTimeSpent = timeSpent;
    }

    public List<TopTenList> getTopTenList() {
        return mTopTenList;
    }

    public void setTopTenList(List<TopTenList> topTenList) {
        mTopTenList = topTenList;
    }

    public String getTotalTestSeriesTime() {
        return mTotalTestSeriesTime;
    }

    public void setTotalTestSeriesTime(String totalTestSeriesTime) {
        mTotalTestSeriesTime = totalTestSeriesTime;
    }

    public String getTotalUserAttempt() {
        return mTotalUserAttempt;
    }

    public void setTotalUserAttempt(String totalUserAttempt) {
        mTotalUserAttempt = totalUserAttempt;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getUserRank() {
        return mUserRank;
    }

    public void setUserRank(String userRank) {
        mUserRank = userRank;
    }

}
