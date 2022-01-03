package com.emedicoz.app.utilso;

import android.content.Context;
import android.content.SharedPreferences;

import com.emedicoz.app.modelo.User;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.modelo.courses.quiz.QuizModel;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.referralcode.model.ProfileInfoResponse;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.response.MasterRegistrationResponse;
import com.emedicoz.app.response.PostResponse;
import com.google.gson.Gson;

public class SharedPreference {
    public static final String MY_PREFERENCES = "MY_PREFERENCES";
    public static final int MODE = Context.MODE_PRIVATE;
    private static SharedPreference pref;
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;

    private SharedPreference() {
        sharedPreference = eMedicozApp.getAppContext().getSharedPreferences(MY_PREFERENCES, MODE);
        editor = sharedPreference.edit();
    }

    public static SharedPreference getInstance() {
        if (pref == null) {
            pref = new SharedPreference();
        }
        return pref;
    }

    public String getString(String key) {
        return sharedPreference.getString(key, "");
    }

    public void putString(String key, String value) {
        editor.putString(key, value).commit();
    }


    public int getInt(String key) {
        return sharedPreference.getInt(key, 0);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value).commit();
    }


    public long getLong(String key) {
        return sharedPreference.getLong(key, 0l);
    }


    public void putLong(String key, long value) {
        editor.putLong(key, value).commit();
    }


    public float getFloat(String key) {
        return sharedPreference.getFloat(key, 0.5f);
    }


    public void putFloat(String key, float value) {
        editor.putFloat(key, value).commit();
    }

    public boolean getBoolean(String key) {
        //    editor.putBoolean(key, value).commit();
        return sharedPreference.getBoolean(key, false);
    }


    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public void feedStatus(String key, String value){
        editor.putString(key, value).commit();
    }

    public String getStatus(String key, String name) {
        //    editor.putBoolean(key, value).commit();
        return sharedPreference.getString(key, "");
    }

    public void testDailyQuizSerieId(String key, String value){
        editor.putString(key, value).commit();
    }
    public String getTestSerieIdDailyQuiz(String key, String name) {
        //    editor.putBoolean(key, value).commit();
        return sharedPreference.getString(key, "");
    }

    public void saveTestSerieId(String key, String value){
        editor.putString(key, value).commit();
    }

    public String getTestSerieId(String key, String name) {
        //    editor.putBoolean(key, value).commit();
        return sharedPreference.getString(key, "");
    }

    public void saveCardConst(String key, String value){
        editor.putString(key, value).commit();
    }
    public String getCardCons(String key, String name) {
        //    editor.putBoolean(key, value).commit();
        return sharedPreference.getString(key, "");
    }

    public void saveStudyTestSeriesId(String key, String value){
        editor.putString(key, value).commit();
    }
    public String getStudyTestSeriesId(String key, String value){
        return sharedPreference.getString(key, "");
    }

    public void saveMyCourseStatus(String key, String value){
        editor.putString(key, value).commit();
    }

    public String getMyCourseStatus(String key, String name) {
        //    editor.putBoolean(key, value).commit();
        return sharedPreference.getString(key, "");
    }


    public void saveMyCourseId(String key, String value){
        editor.putString(key, value).commit();
    }
    public String getMyCourseId(String key, String value){
        return sharedPreference.getString(key, "");
    }
    public void saveDailyQuizStatus(String key, String value){
        editor.putString(key, value).commit();
    }

    public String getDailyQuizStatus(String key, String name) {
        //    editor.putBoolean(key, value).commit();
        return sharedPreference.getString(key, "");
    }

    public void saveQbankTestStatus(String key, String value){
        editor.putString(key, value).commit();
    }

    public String getQbankTestStatus(String key, String name) {
        //    editor.putBoolean(key, value).commit();
        return sharedPreference.getString(key, "");
    }

    public void saveStudyTestStatus(String key, String value){
        editor.putString(key, value).commit();
    }

    public String getStudyTestStatus(String key, String name){
        return sharedPreference.getString(key, "");
    }
    public void saveQbankAttempOneTime(String key, String value){
        editor.putString(key, value).commit();
    }

    public String getQbankAttempOneTime(String key, String name){
        return sharedPreference.getString(key, "");
    }

    public void setMasterHitData(MasterFeedsHitResponse response) {
        editor.putString(Const.MASTER_FEED_HIT_RESPONSE, new Gson().toJson(response));
        editor.commit();
    }

    public void setMasterRegistrationData(MasterRegistrationResponse response) {
        editor.putString(Const.MASTER_REGISTRATION_HIT_RESPONSE, new Gson().toJson(response));
        editor.commit();
    }

    public void ClearLoggedInUser() {
        editor.putString(Const.USER_LOGGED_IN, new Gson().toJson(new User()));
        editor.commit();
    }

    public Video getLikeUpdate() {
        Video post = null;
        String postJson = sharedPreference.getString(Const.VIDEO, null);
        if (postJson != null && postJson.trim().length() > 0)
            post = new Gson().fromJson(postJson, Video.class);
        return post;
    }

    public void setLikeUpdate(Video video) {
        editor.putString(Const.VIDEO, new Gson().toJson(video));
        editor.commit();
    }

    public Video getCommentUpdate() {
        Video post = null;
        String postJson = sharedPreference.getString(Const.COMMENT, null);
        if (postJson != null && postJson.trim().length() > 0)
            post = new Gson().fromJson(postJson, Video.class);
        return post;
    }

    public void setCommentUpdate(Video video) {
        editor.putString(Const.COMMENT, new Gson().toJson(video));
        editor.commit();
    }

    public Video getViewUpdate() {
        Video post = null;
        String postJson = sharedPreference.getString(Const.VIEWS, null);
        if (postJson != null && postJson.trim().length() > 0)
            post = new Gson().fromJson(postJson, Video.class);
        return post;
    }

    public void setViewUpdate(Video video) {
        editor.putString(Const.VIEWS, new Gson().toJson(video));
        editor.commit();
    }

    public User getLoggedInUser() {
        User user = null;
        String userJson = sharedPreference.getString(Const.USER_LOGGED_IN, null);
        if (userJson != null && userJson.trim().length() > 0)
            user = new Gson().fromJson(userJson, User.class);
        return user;
    }

    public void setLoggedInUser(User user) {
        editor.putString(Const.USER_LOGGED_IN, new Gson().toJson(user));
        editor.commit();
    }

    public ProfileInfoResponse getAffiliateProfileInfo() {
        ProfileInfoResponse profileInfoResponse = null;
        String profileInfo = sharedPreference.getString(Const.AFFILIATE_SIGNED, null);
        if (profileInfo != null && profileInfo.trim().length() > 0)
            profileInfoResponse = new Gson().fromJson(profileInfo, ProfileInfoResponse.class);
        return profileInfoResponse;
    }

    public void setAffiliateProfileInfo(ProfileInfoResponse affiliateProfileInfo) {
        editor.putString(Const.AFFILIATE_SIGNED, new Gson().toJson(affiliateProfileInfo));
        editor.commit();
    }

    public QuizModel getCurrentQuiz() {
        QuizModel quizModel = null;
        String userJson = sharedPreference.getString(Const.QUIZ_MODEL, null);
        if (userJson != null && userJson.trim().length() > 0)
            quizModel = new Gson().fromJson(userJson, QuizModel.class);
        return quizModel;
    }

    public void setCurrentQuiz(QuizModel quiz) {
        editor.putString(Const.QUIZ_MODEL, new Gson().toJson(quiz));
        editor.commit();
    }

    public MasterFeedsHitResponse getMasterHitResponse() {
        MasterFeedsHitResponse response = null;
        String responsestr = sharedPreference.getString(Const.MASTER_FEED_HIT_RESPONSE, null);
        if (responsestr != null && responsestr.trim().length() > 0)
            response = new Gson().fromJson(responsestr, MasterFeedsHitResponse.class);
        return response;
    }

    public ResultTestSeries getResultTestSeries() {
        ResultTestSeries response = null;
        String responsestr = sharedPreference.getString(Const.RESULT_TEST_SERIES, null);
        if (responsestr != null && responsestr.trim().length() > 0)
            response = new Gson().fromJson(responsestr, ResultTestSeries.class);
        return response;
    }

    public void setResultTestSeries(ResultTestSeries response) {
        editor.putString(Const.RESULT_TEST_SERIES, new Gson().toJson(response));
        editor.commit();
    }

    public MasterRegistrationResponse getRegistrationResponse() {
        MasterRegistrationResponse response = null;
        String responsestr = sharedPreference.getString(Const.MASTER_REGISTRATION_HIT_RESPONSE, null);
        if (responsestr != null && responsestr.trim().length() > 0)
            response = new Gson().fromJson(responsestr, MasterRegistrationResponse.class);
        return response;
    }

    public PostResponse getPost() {
        PostResponse post = null;
        String postJson = sharedPreference.getString(Const.POST, null);
        if (postJson != null && postJson.trim().length() > 0)
            post = new Gson().fromJson(postJson, PostResponse.class);
        return post;
    }

    public void setPost(PostResponse post) {
        editor.putString(Const.POST, new Gson().toJson(post));
        editor.commit();
    }

    public boolean contains(String key) {
        return sharedPreference.contains(key);
    }

    public void remove(String key) {
        editor.remove(key).commit();
    }

    public String getString(String s, String name) {
        return sharedPreference.getString(s, name);
    }
}
