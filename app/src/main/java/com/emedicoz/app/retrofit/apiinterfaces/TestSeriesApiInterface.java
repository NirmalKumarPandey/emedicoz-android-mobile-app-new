package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TestSeriesApiInterface {

    @FormUrlEncoded
    @POST("data_model/courses/test_series/get_test_series_result")
    Call<JsonObject> getViewSolutionData(
            @Field(Const.USER_ID) String userId,
            @Field(Constants.ResultExtras.TEST_SEGMENT_ID) String test_segment_id
    );

    @FormUrlEncoded
    @POST("data_model/courses/custom_qbank/get_test_series_result")
    Call<JsonObject> getCustomViewSolutionData(
            @Field(Const.USER_ID) String userId
    );

}
