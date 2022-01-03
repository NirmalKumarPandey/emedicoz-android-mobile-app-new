package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NotificationApiInterface {
    @FormUrlEncoded
    @POST("data_model/notification_genrator/activity_logger/make_activity_viewed")
    Call<JsonObject> notificationstatechange(
            @Field(Const.USER_ID) String USER_ID,
            @Field(Constants.Extras.ID) String ID
    );
}
