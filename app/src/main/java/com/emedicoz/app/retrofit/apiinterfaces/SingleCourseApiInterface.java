package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SingleCourseApiInterface {
    @FormUrlEncoded
    @POST("data_model/courses/course/get_single_course_info_raw_v2")
    Call<JsonObject> getSingleCourseInfoRaw(
            @Field(Const.USER_ID) String userId,
            @Field(Const.COURSE_ID) String COURSE_ID,
            @Field(Const.COURSE_TYPE) String courseType,
            @Field(Const.IS_COMBO) String isCombo,
            @Field(Const.PARENT_ID) String corse);

    @FormUrlEncoded
    @POST("data_model/courses/Course_rating_reviews/add_review_to_course")
    Call<JsonObject> addReviewCourse(
            @Field(Const.USER_ID) String userId,
            @Field(Const.COURSE_ID) String COURSE_ID,
            @Field(Const.RATINGS) String RATINGS,
            @Field(Const.TEXT) String TEXT);

    @FormUrlEncoded
    @POST("data_model/courses/Course_rating_reviews/delete_review_from_course")
    Call<JsonObject> deleteUserCourseReview(
            @Field(Const.USER_ID) String userId,
            @Field(Const.COURSE_ID) String COURSE_ID);

    @FormUrlEncoded
    @POST("data_model/courses/Course_rating_reviews/edit_review_to_course")
    Call<JsonObject> editUserCourseReviews(
            @Field(Const.USER_ID) String userId,
            @Field(Const.COURSE_ID) String COURSE_ID,
            @Field(Const.RATINGS) String RATINGS,
            @Field(Const.TEXT) String TEXT);

    @FormUrlEncoded
    @POST("data_model/courses/Course/free_course_transaction_test")
    Call<JsonObject> makeFreeCourseTransaction(
            @Field(Const.USER_ID) String userId,
            @Field(Const.COURSE_ID) String COURSE_ID,
            @Field(Const.POINTS_RATE) String POINTS_RATE,
            @Field(Const.TAX) String TAX,
            @Field(Const.POINTS_USED) String POINTS_USED,
            @Field(Const.POINTS_RATE) String COUPON_APPLIED,
            @Field(Const.COURSE_PRICE) String COURSE_PRICE);

    @FormUrlEncoded
    @POST("data_model/courses/Course/test_series_course_type_test")
    Call<JsonObject> getTestData(
            @Field(Const.USER_ID) String userId,
            @Field(Const.COURSE_ID) String COURSE_ID);

    @FormUrlEncoded
    @POST("data_model/courses/crs/get_test_v2")
    Call<JsonObject> getStudyTestData(
            @Field(Const.USER_ID) String userId,
            @Field(Const.COURSE_ID) String course_id,
            @Field("search") String search);

    @FormUrlEncoded
    @POST("data_model/video/Video_duration/get_course_video_duration")
    Call<JsonObject> getVideoDuration(
            @Field(Const.USER_ID) String userId,
            @Field("video_id") String video_id,
            @Field(Constants.Extras.TYPE) String type);

    @FormUrlEncoded
    @POST("data_model/video/Video_duration/post_course_video_duration")
    Call<JsonObject> postVideoDuration(
            @Field(Const.USER_ID) String userId,
            @Field("video_id") String video_id,
            @Field(Constants.Extras.TYPE) String type,
            @Field("watched") String watched);

    @FormUrlEncoded
    @POST("data_model/courses/course/get_single_poll_question_details")
    Call<JsonObject> getPollQuestionDetails(
            @Field(Const.USER_ID) String userId,
            @Field("question_id") String questionId,
            @Field("video_id") String video_id);

    @FormUrlEncoded
    @POST("data_model/courses/course/user_submit_poll_question_answer")
    Call<JsonObject> submitPollResponse(
            @Field(Const.USER_ID) String userId,
            @Field("question_id") String questionId,
            @Field("video_id") String video_id,
            @Field("option_id") String optionId);

    @FormUrlEncoded
    @POST("data_model/courses/course/get_poll_question_submission_details")
    Call<JsonObject> getPollResults(
            @Field(Const.USER_ID) String userId,
            @Field("question_id") String questionId,
            @Field("video_id") String video_id);

    @FormUrlEncoded
    @POST("data_model/courses/my_courses/update_content_view_status")
    Call<JsonObject> updateContentViewStatus(
            @Field(Const.USER_ID) String userId,
            @Field("course_id") String courseId,
            @Field("topic_id") String topicId,
            @Field("content_id") String contentId,
            @Field("is_completed") String isCompleted);

}
