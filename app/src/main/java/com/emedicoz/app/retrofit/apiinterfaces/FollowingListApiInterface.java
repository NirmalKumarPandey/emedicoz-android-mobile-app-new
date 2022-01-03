package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FollowingListApiInterface {
    @FormUrlEncoded
    @POST("data_model/user/user_follow/following_list")
    Call<JsonObject> followinglist(
            @Query("is_watcher") String iswatcher,
            @Field(Const.USER_ID) String userId
    );
}
