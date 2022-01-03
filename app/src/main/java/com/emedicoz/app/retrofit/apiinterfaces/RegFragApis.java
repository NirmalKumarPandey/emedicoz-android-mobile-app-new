package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RegFragApis {
    @FormUrlEncoded
    @POST("data_model/user/User_mobile_change/update_mobile_number")
    Call<JsonObject> updateMobileNumber(
            @Field(Const.USER_ID) String userId,
            @Field(Const.COUNTRY_CODE) String user_info,
            @Field(Const.MOBILE) String mobile
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/stream_registration")
    Call<JsonObject> getStreamRegistration1(
            @Field(Const.USER_ID) String userId,
            @Field(Const.OPTIONAL_TEXT) String OPTIONAL_TEXT,
            @Field(Const.INTERESTED_COURSE) String INTERESTED_COURSE,
            @Field(Constants.Extras.NAME) String NAME,
            @Field(Const.EMAIL) String EMAIL
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/stream_registration")
    Call<JsonObject> getStreamRegistration2(
            @Field(Const.USER_ID) String userId,
            @Field(Const.OPTIONAL_TEXT) String OPTIONAL_TEXT,
            @Field(Const.INTERESTED_COURSE) String INTERESTED_COURSE,
            @Field(Constants.Extras.NAME) String NAME,
            @Field(Const.EMAIL) String EMAIL,
            @Field(Const.PROFILE_PICTURE) String PROFILE_PICTURE
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/stream_registration")
    Call<JsonObject> getStreamRegistration1(
            @Field(Const.USER_ID) String userId,
            @Field(Const.MASTER_ID) String MASTER_ID,
            @Field(Const.MASTER_ID_LEVEL_ONE) String MASTER_ID_LEVEL_ONE,
            @Field(Const.MASTER_ID_LEVEL_TWO) String MASTER_ID_LEVEL_TWO,
            @Field(Const.OPTIONAL_TEXT) String OPTIONAL_TEXT,
            @Field(Const.INTERESTED_COURSE) String INTERESTED_COURSE,
            @Field(Constants.Extras.NAME) String NAME,
            @Field(Const.EMAIL) String EMAIL
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/stream_registration")
    Call<JsonObject> getStreamRegistration2(
            @Field(Const.USER_ID) String userId,
            @Field(Const.MASTER_ID) String MASTER_ID,
            @Field(Const.MASTER_ID_LEVEL_ONE) String MASTER_ID_LEVEL_ONE,
            @Field(Const.MASTER_ID_LEVEL_TWO) String MASTER_ID_LEVEL_TWO,
            @Field(Const.OPTIONAL_TEXT) String OPTIONAL_TEXT,
            @Field(Const.INTERESTED_COURSE) String INTERESTED_COURSE,
            @Field(Constants.Extras.NAME) String NAME,
            @Field(Const.EMAIL) String EMAIL,
            @Field(Const.PROFILE_PICTURE) String PROFILE_PICTURE
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/contact_registration")
    Call<JsonObject> contactRegistration1(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.NAME) String name,
            @Field(Const.EMAIL) String email,
            @Field(Const.MOBILE) String mobile,
            @Field("u_c_id") String userCountryId,
            @Field("u_s_id") String userStateId,
            @Field("u_ct_id") String userCityId,
            @Field(Const.DAMS_TOKEN) String damsToken,
            @Field(Const.PROFILE_PICTURE) String profilePicture
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/contact_registration")
    Call<JsonObject> contactRegistration2(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.Extras.NAME) String name,
            @Field(Const.EMAIL) String email,
            @Field(Const.MOBILE) String mobile,
            @Field("u_c_id") String userCountryId,
            @Field("u_s_id") String userStateId,
            @Field("u_ct_id") String userCityId,
            @Field(Const.PROFILE_PICTURE) String profilePicture
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/academic_registration")
    Call<JsonObject> academicRegistration(
            @Field(Const.USER_ID) String userId,
            @Field(Const.MASTER_ID) String masterId,
            @Field(Const.MASTER_ID_LEVEL_ONE) String masterIdLevelOne,
            @Field(Const.MASTER_ID_LEVEL_TWO) String masterIdLevelTwo,
            @Field(Const.COUNTRY_ID) String countryId,
            @Field(Const.STATE_ID) String stateId,
            @Field(Const.CITY_ID) String cityId,
            @Field(Const.COLLEGE_ID) String collegeId,
            @Field(Const.COLLEGE_NAME) String collegeName
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/course_registration")
    Call<JsonObject> courseRegistration(
            @Field(Const.USER_ID) String userId,
            @Field(Const.INTERESTED_COURSE) String interestedCourse
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/update_stream_by_user")
    Call<JsonObject> saveStreamDataForUser(
            @Field(Const.USER_ID) String userId,
            @Field(Const.MASTER_ID) String MASTER_ID,
            @Field(Const.MASTER_ID_LEVEL_ONE) String MASTER_ID_LEVEL_ONE,
            @Field(Const.MASTER_ID_LEVEL_TWO) String MASTER_ID_LEVEL_TWO
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/update_stream_user")
    Call<JsonObject> saveStreamDataForUser(
            @Field(Const.USER_ID) String userId,
            @Field("stream_id") String MASTER_ID
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/update_dams_id_user")
    Call<JsonObject> updateDamsToken(
            @Field(Const.USER_ID) String userId,
            @Field(Const.DAMS_USERNAME) String DAMS_USERNAME,
            @Field(Const.DAMS_PASSWORD) String DAMS_PASSWORD
    );


    @POST
    Call<JsonObject> getActiveUser(
            @Url String userId
    );

    @POST
    Call<JsonObject> getMCQTestData(
            @Url String url
    );

    @FormUrlEncoded
    @POST("data_model/user/mobile_auth/send_otp_for_mobile_change")
    Call<JsonObject> otpForMobileChange(
            @Field(Const.USER_ID) String userId,
            @Field(Const.MOBILE) String MOBILE,
            @Field(Const.COUNTRY_CODE) String COUNTRY_CODE,
            @Field(Const.IS_OTP) Boolean isOtp //This determines if API send OTP or not
    );
}
