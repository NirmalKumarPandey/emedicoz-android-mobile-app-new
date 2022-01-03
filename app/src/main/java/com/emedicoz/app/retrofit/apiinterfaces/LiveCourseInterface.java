package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LiveCourseInterface {
    @FormUrlEncoded
    @POST("data_model/courses/live_course/get_landing_page_data")
    Call<JsonObject> getLiveCourseLandingPageData(
            @Field(Const.USER_ID) String userId
    );


    @FormUrlEncoded
    @POST("data_model/courses/live_course/enable_upcoming_class_reminder")
    Call<JsonObject> remindMeForLiveClass(
            @Field(Const.USER_ID) String userId,
            @Field("course_id") String courseId,
            @Field("video_id") String videoId,
            @Field("live_on") String liveOn
    );

    @FormUrlEncoded
    @POST("data_model/courses/live_course/get_basic_data_vc")
    Call<JsonObject> getBasicData(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id
    );

    @FormUrlEncoded
    @POST("data_model/courses/course/get_video_details_page")
    Call<JsonObject> getVideoDetails(
            @Field(Const.USER_ID) String userId,
            @Field(Const.VIDEO_ID) String videoId
    );

    @FormUrlEncoded
    @POST("data_model/courses/course/get_single_course_details")
    Call<JsonObject> getCourseDetailData(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id
    );

    @FormUrlEncoded
    @POST("data_model/courses/exam/get_video_data_vc")
    Call<JsonObject> getVideoData(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id,
            @Field("layer") String layer
    );

    @FormUrlEncoded
    @POST("data_model/courses/exam/get_courses")
    Call<JsonObject> getCourseData(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id
    );

    @FormUrlEncoded
    @POST("data_model/courses/exam/get_all_test__epub_pdf_data")
    Call<JsonObject> getTestEpubPdfData(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id,
            @Field("layer") String layer
    );

    @FormUrlEncoded
    @POST("data_model/courses/course/get_course_video_data")
    Call<JsonObject> getVideoDataOfCourseDetail(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id
    );

    @FormUrlEncoded
    @POST("data_model/courses/course/get_course_notes_and_test_data")
    Call<JsonObject> getTestEpubPdfDataOfCourseDetail(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id
    );

    @FormUrlEncoded
    @POST("data_model/courses/exam/get_video_data")
    Call<JsonObject> getVideoData2(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id,
            @Field("layer") String layer,
            @Field("subject_id") String subject_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/exam/get_video_data")
    Call<JsonObject> getVideoData3(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id,
            @Field("layer") String layer,
            @Field("subject_id") String subject_id,
            @Field("topic_id") String topic_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/exam/get_test_data")
    Call<JsonObject> getTestData2(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id,
            @Field("layer") String layer,
            @Field("sub_id") String sub_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/exam/get_epub")
    Call<JsonObject> getEpubData2(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id,
            @Field("layer") String layer,
            @Field("subject_id") String subject_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/exam/get_epub")
    Call<JsonObject> getEpubData3(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id,
            @Field("layer") String layer,
            @Field("subject_id") String subject_id,
            @Field("topic_id") String topic_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/exam/get_pdf_data")
    Call<JsonObject> getPdfData2(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id,
            @Field("layer") String layer,
            @Field("subject_id") String subject_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/exam/get_pdf_data")
    Call<JsonObject> getPdfData3(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id,
            @Field("layer") String layer,
            @Field("subject_id") String subject_id,
            @Field("topic_id") String topic_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/course/filter_course_reviews_by_rating")
    Call<JsonObject> getReviewsByFilter(
            @Field(Const.USER_ID) String userId,
            @Field("course_id") String courseId,
            @Field("rating") String rating
    );

    @FormUrlEncoded
    @POST("data_model/courses/course/on_request_create_video_link")
    Call<JsonObject> onRequestCreateVideoLink(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.ID) String id,
            @Field(Constants.Extras.NAME) String name
    );

}
