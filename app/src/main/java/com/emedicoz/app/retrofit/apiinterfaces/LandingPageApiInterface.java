package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LandingPageApiInterface {
    @FormUrlEncoded
    @POST("data_model/courses/course/get_landing_page_data_v3_vc")
    Call<JsonObject> getLandingPageDataV3(
            @Field(Const.USER_ID) String userId
    );

    @FormUrlEncoded
    @POST("data_model/courses/course/get_landing_page_recorded_data")
    Call<JsonObject> getRecordedCourseData(
            @Field(Const.USER_ID) String userId,
            @Field("filter_type") String filterType,
            @Field("keyword") String searchedKeyword
    );

    @FormUrlEncoded
    @POST("data_model/courses/my_courses/get_my_courses_listing")
    Call<JsonObject> getRecordedMyCourses(
            @Field(Const.USER_ID) String userId,
            @Field("is_live") String isLive,
            @Field("course_type") String courseType,
            @Field("filter_type") String filterType,
            @Field("keyword") String searchedKeyword

    );


    @FormUrlEncoded
    @POST("data_model/courses/live_course/get_landing_page_live_courses_data")
    Call<JsonObject> getLiveCourseData(
            @Field(Const.USER_ID) String userId,
            @Field("filter_type") String filterType,
            @Field("keyword") String searchedKeyword

    );

    @FormUrlEncoded
    @POST("data_model/courses/Course/get_faq")
    Call<JsonObject> getFaqPageData(
            @Field(Const.USER_ID) String userId,
            @Field("course_id") String course_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/my_courses/get_list_of_my_courses")
    Call<JsonObject> getMyCourseData(
            @Field(Const.USER_ID) String userId,
            @Field(Const.COURSE_TYPE) String course_type
    );

    @FormUrlEncoded
    @POST("data_model/courses/Test_series/get_user_given_test_series")
    Call<JsonObject> getUserGivenTestSeries(
            @Field(Const.USER_ID) String userId,
            @Field("start") String start
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/update_dams_id_user")
    Call<JsonObject> updateDamsToken(
            @Field(Const.USER_ID) String userId,
            @Field("dams_username") String dams_username,
            @Field("dams_password") String dams_password
    );

    @FormUrlEncoded
    @POST("data_model/courses/course/get_all_category_data")
    Call<JsonObject> getAllCatData(
            @Field(Const.USER_ID) String userId,
            @Field("cat_id") String cat_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/Course/search_course")
    Call<JsonObject> getSearchCourseData(
            @Field(Const.USER_ID) String userId,
            @Field("search_content") String search_content,
            @Field("last_course_id") String last_course_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/my_courses/my_courses_tabs_visibility")
    Call<JsonObject> getTabsVisibility(
            @Field(Const.USER_ID) String userId
    );

}
