package com.emedicoz.app.ui.podcast;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface LoginApiInterface {
    @FormUrlEncoded
    @POST("data_model/user/registration/dams_auth")
    Call<JsonObject> damsLoginAuthentication(
            @Field( Constants.DAMS_USERNAME) String DAMS_USERNAME,
            @Field( Constants.DAMS_PASSWORD) String DAMS_PASSWORD,
            @Field( Constants.IS_SOCIAL) String IS_SOCIAL,
            @Field( Constants.DEVICE_TYPE) String DEVICE_TYPE,
            @Field(Constants.DEVICE_TOKEN) String DEVICE_TOKEN,
            @Field(Constants.REFER_CODE) String REFER_CODE
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/get_new_jwt_token")
    Call<JsonObject> refreshToken(
            @Field(Constants.DAMS_USERNAME) String DAMS_USERNAME,
            @Field(Constants.DAMS_USER_TOKEN) String DAMS_USER_TOKEN
    );

    @FormUrlEncoded
    @POST("data_model/findurl/get_url")
    Call<JsonObject> getDownloadableLink(
            @Field(Constants.USER_ID) String userId,
            @Field(Constants.FILE_URL) String fileUrl
    );

    @FormUrlEncoded
    @POST("data_model/user/registration/login_authentication")
    Call<JsonObject> userLoginAuthentication(
            @Field(Constants.EMAIL) String email,
            @Field(Constants.PASSWORD) String pass,
            @Field(Constants.IS_SOCIAL) String IS_SOCIAL,
            @Field(Constants.SOCIAL_TYPE) String SOCIAL_TYPE,
            @Field(Constants.SOCIAL_TOKEN) String SOCIAL_TOKEN,
            @Field(Constants.DEVICE_TYPE) String DEVICE_TYPE,
            @Field(Constants.DEVICE_TOKEN) String DEVICE_TOKEN
    );
}
