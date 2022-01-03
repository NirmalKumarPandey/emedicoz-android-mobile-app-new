package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SubscriptionApiInterface {
    @FormUrlEncoded
    @POST("data_model/cpr/course/get_single_course_info_raw")
    Call<JsonObject> getSubsDataForUser(
            @Field(Const.USER_ID) String userId,
            @Field(Const.VATHAM_ID) String sub_cat
    );
}
