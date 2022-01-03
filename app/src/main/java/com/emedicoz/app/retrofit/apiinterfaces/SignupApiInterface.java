package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SignupApiInterface {
    @FormUrlEncoded
    @POST("data_model/user/dams_user_verification/user_verification")
    Call<JsonObject> getUserDamsVerification(
            @Field(Const.USER_TOKEN) String USER_TOKEN
    );

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
}
