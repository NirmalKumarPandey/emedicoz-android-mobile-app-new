package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginApiInterface {
    @FormUrlEncoded
    @POST("data_model/user/registration/dams_auth")
    Call<JsonObject> damsLoginAuthentication(
            @Field(Const.DAMS_USERNAME) String DAMS_USERNAME,
            @Field(Const.DAMS_PASSWORD) String DAMS_PASSWORD,
            @Field(Const.IS_SOCIAL) String IS_SOCIAL,
            @Field(Const.DEVICE_TYPE) String DEVICE_TYPE,
            @Field(Const.DEVICE_TOKEN) String DEVICE_TOKEN,
            @Field(Const.REFER_CODE) String REFER_CODE
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/get_new_jwt_token")
    Call<JsonObject> refreshToken(
            @Field(Const.DAMS_USERNAME) String DAMS_USERNAME,
            @Field(Const.DAMS_USER_TOKEN) String DAMS_USER_TOKEN
    );

    @FormUrlEncoded
    @POST("data_model/findurl/get_url")
    Call<JsonObject> getDownloadableLink(
            @Field(Const.USER_ID) String userId,
            @Field(Const.FILE_URL) String fileUrl
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/login_authentication")
    Call<JsonObject> userLoginAuthentication(
            @Field(Const.EMAIL) String email,
            @Field(Const.PASSWORD) String pass,
            @Field(Const.IS_SOCIAL) String IS_SOCIAL,
            @Field(Const.SOCIAL_TYPE) String SOCIAL_TYPE,
            @Field(Const.SOCIAL_TOKEN) String SOCIAL_TOKEN,
            @Field(Const.DEVICE_TYPE) String DEVICE_TYPE,
            @Field(Const.DEVICE_TOKEN) String DEVICE_TOKEN
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/login_authentication_v2")
    Call<JsonObject> userLoginAuthenticationV2(
            @Field(Const.MOBILE) String mobile,
            @Field(Const.COUNTRY_CODE) String cCode,
            @Field(Const.OTP) String otp,
            @Field(Const.IS_SOCIAL) String IS_SOCIAL,
            @Field(Const.SOCIAL_TYPE) String SOCIAL_TYPE,
            @Field(Const.SOCIAL_TOKEN) String SOCIAL_TOKKEN,
            @Field(Const.DEVICE_TYPE) String DEVICE_TYPE,
            @Field(Const.DEVICE_TOKEN) String DEVICE_TOKKEN
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/login_authentication_v2")
    Call<JsonObject> userLoginAuthenticationV3(
            @Field(Const.EMAIL) String email,
            @Field(Const.OTP) String otp,
            @Field(Const.IS_SOCIAL) String IS_SOCIAL,
            @Field(Const.SOCIAL_TYPE) String SOCIAL_TYPE,
            @Field(Const.SOCIAL_TOKEN) String SOCIAL_TOKEN,
            @Field(Const.DEVICE_TYPE) String DEVICE_TYPE,
            @Field(Const.DEVICE_TOKEN) String DEVICE_TOKEN
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/login_authentication_v2")
    Call<JsonObject> userLoginAuthenticationV4(
            @Field(Const.EMAIL) String email,
            @Field(Const.MOBILE) String mobile,
            @Field(Const.COUNTRY_CODE) String cCode,
            @Field(Const.OTP) String otp,
            @Field(Const.IS_SOCIAL) String IS_SOCIAL,
            @Field(Const.SOCIAL_TYPE) String SOCIAL_TYPE,
            @Field(Const.SOCIAL_TOKEN) String SOCIAL_TOKEN,
            @Field(Const.DEVICE_TYPE) String DEVICE_TYPE,
            @Field(Const.DEVICE_TOKEN) String DEVICE_TOKEN
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/login_authentication_v2")
    Call<JsonObject> userLoginAuthenticationV5(
            @Field(Const.EMAIL) String email,
            @Field(Const.MOBILE) String mobile,
            @Field(Const.COUNTRY_CODE) String cCode,
            @Field(Const.OTP) String otp,
            @Field(Const.IS_SOCIAL) String IS_SOCIAL,
            @Field(Const.SOCIAL_TYPE) String SOCIAL_TYPE,
            @Field(Const.SOCIAL_TOKEN) String SOCIAL_TOKEN,
            @Field(Const.DEVICE_TYPE) String DEVICE_TYPE,
            @Field(Const.DEVICE_TOKEN) String DEVICE_TOKEN,
            @Field(Const.DAMS_TOKEN) String damsToken
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/check_social")
    Call<JsonObject> checkSocial(
            @Field(Const.EMAIL) String email,
            @Field(Const.IS_SOCIAL) String IS_SOCIAL,
            @Field(Const.SOCIAL_TYPE) String SOCIAL_TYPE,
            @Field(Const.SOCIAL_TOKEN) String SOCIAL_TOKEN,
            @Field(Const.DEVICE_TYPE) String DEVICE_TYPE,
            @Field(Const.DEVICE_TOKEN) String DEVICE_TOKEN
    );


    Call<JsonObject> userLoginAuthenticationV2(String mobile, String c_code, String otp, String is_social, String social_type, String social_tokken, String device_tokken);
}
