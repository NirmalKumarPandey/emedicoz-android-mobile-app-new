package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OtpApiInterface {
    @FormUrlEncoded
    @POST("data_model/user/registration")
    Call<JsonObject> registerUser(
            @Field(Constants.Extras.NAME) String name,
            @Field(Const.EMAIL) String email,
            @Field(Const.PASSWORD) String pass,
            @Field(Const.MOBILE) String mob,
            @Field(Const.COUNTRY_CODE) String ccoce,
            @Field(Const.IS_SOCIAL) String issocial,
            @Field(Const.SOCIAL_TYPE) String SOCIAL_TYPE,
            @Field(Const.SOCIAL_TOKEN) String SOCIAL_TOKEN,
            @Field(Const.DEVICE_TYPE) String DEVICE_TYPE,
            @Field(Const.DEVICE_TOKEN) String DEVICE_TOKEN,
            @Field(Const.DAMS_TOKEN) String DAMS_TOKEN,
            @Field(Const.PROFILE_PICTURE) String PROFILE_PICTURE,
            @Field(Const.LOCATION) String LOCATION,
            @Field(Const.REFER_CODE) String REFER_CODE,
            @Field(Const.AFFILIATE_REFERRAL_CODE_BY) String affiliate_referral_code_by
    );

    @FormUrlEncoded
    @POST("data_model/user/mobile_auth/send_otp")
    Call<JsonObject> otpVerification(
            @Field(Const.MOBILE) String MOBILE,
            @Field(Const.COUNTRY_CODE) String COUNTRY_CODE,
            @Field(Const.EMAIL) String EMAIL,
            @Field(Const.IS_OTP) Boolean isOtp //This determines if API send OTP or not
    );

    @FormUrlEncoded
    @POST("data_model/user/mobile_auth/send_otp_for_mobile_change")
    Call<JsonObject> opForMobileChange(
            @Field(Const.USER_ID) String userId,
            @Field(Const.COUNTRY_CODE) String COUNTRY_CODE,
            @Field(Const.MOBILE) String mob,
            @Field(Const.IS_OTP) Boolean isOtp //This determines if API send OTP or not
    );

    @FormUrlEncoded
    @POST("data_model/user/Mobile_auth/send_otp_for_change_password")
    Call<JsonObject> changePasswordOtp(
            @Field(Const.COUNTRY_CODE) String COUNTRY_CODE,
            @Field(Const.MOBILE) String mob,
            @Field(Const.IS_OTP) Boolean isOtp //This determines if API send OTP or not
    );

    @FormUrlEncoded
    @POST("data_model/user/User_mobile_change/update_mobile_number")
    Call<JsonObject> updateMobileNumber(
            @Field(Const.USER_ID) String userId,
            @Field(Const.COUNTRY_CODE) String COUNTRY_CODE,
            @Field(Const.MOBILE) String mob
    );

    @FormUrlEncoded
    @POST("data_model/user/mobile_auth/check_otp_valid")
    Call<JsonObject> validateOtpForMobile(
            @Field(Const.MOBILE) String userId,
            @Field(Const.OTP) String COUNTRY_CODE
    );
}
