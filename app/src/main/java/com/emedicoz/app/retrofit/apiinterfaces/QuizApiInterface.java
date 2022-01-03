package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface QuizApiInterface {
    @FormUrlEncoded
    @POST("data_model/courses/Test_series/save_test")
    Call<JsonObject> getInstructorData(
            @Field(Const.USER_ID) String userId,
            @Field(Const.TESTSERIES_ID) String TESTSERIES_ID,
            @Field(Const.TIME_SPENT) String TIME_SPENT,
            @Field(Const.QUESTION_JSON) String QUESTION_JSON
    );

    @FormUrlEncoded
    @POST("data_model/courses/Test_series/get_test_series_basic_result_v3")
    Call<JsonObject> getUserleaderBoardResult_v3(
            @Field(Const.USER_ID) String userId,
            @Field("test_segment_id") String test_segment_id
    );


    @FormUrlEncoded
    @POST("data_model/courses/Test_series/get_test_series_result")
    Call<JsonObject> getTestSeriesSolutionResult(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.ResultExtras.TEST_SEGMENT_ID) String test_segment_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/Test_series/get_test_series_video_solution")
    Call<JsonObject> getVideoSolutionData(
            @Field(Const.USER_ID) String userId,
            @Field("test_segment_id") String test_segment_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/Test_series_result/get_subject_analysis")
    Call<JsonObject> getSubjectWiseResult(
            @Field(Const.USER_ID) String userId,
            @Field("result_id") String test_segment_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/Test_series_result/get_top_100")
    Call<JsonObject> getRankList(
            @Field(Const.USER_ID) String userId,
            @Field("test_series_id") String test_series_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/Test_series/question_bookmark")
    Call<JsonObject> bookmarkTestSeries(
            @Field(Const.USER_ID) String userId,
            @Field("question_id") String question_id,
            @Field("q_type") String q_type);

    @FormUrlEncoded
    @POST("data_model/courses/Test_series/save_aiims_1")
    Call<JsonObject> submitAiimsTestSeries(
            @FieldMap HashMap<String, String> finalResponse

    );

    @FormUrlEncoded
    @POST("data_model/courses/custom_qbank/save_quiz")
    Call<JsonObject> submitCustomTestSeries(
            @FieldMap HashMap<String, String> finalResponse


    );

    @FormUrlEncoded
    @POST("data_model/courses/Test_series/rate_test_series")
    Call<JsonObject> rateQbank(
            @Field(Const.USER_ID) String userId,
            @Field("test_series_id") String test_series_id,
            @Field("rating") String rating

    );
}