package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface VideoCommentApiInterface {
    @FormUrlEncoded
    @POST("data_model/video/video_comment/update_comment")
    Call<JsonObject> editVideoComment(
            @Field(Constants.Extras.ID) String ID,
            @Field(Const.USER_ID) String userId,
            @Field(Const.VIDEO_ID) String videoid,
            @Field(Const.POST_COMMENT) String comment
    );

    @FormUrlEncoded
    @POST("data_model/video/video_comment/delete_comment")
    Call<JsonObject> deleteVideoComment(
            @Field(Constants.Extras.ID) String ID,
            @Field(Const.USER_ID) String userId,
            @Field(Const.VIDEO_ID) String videoid
    );
}
