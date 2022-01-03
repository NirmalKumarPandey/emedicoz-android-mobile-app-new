package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface InstructorFragApiInterface {
    @FormUrlEncoded
    @POST("data_model/courses/Instructor/get_instructor_profile")
    Call<JsonObject> getinstructorData(
            @Field(Const.USER_ID) String userId,
            @Field(Const.INSTR_ID) String INSTR_ID
    );

    @FormUrlEncoded
    @POST("data_model/courses/course_instructor_reviews/add_review_to_instructor")
    Call<JsonObject> getinstructorReviewCourse(
            @Field(Const.USER_ID) String userId,
            @Field(Const.INSTR_ID) String INSTR_ID,
            @Field(Const.RATINGS) String RATINGS,
            @Field(Const.TEXT) String TEXT
    );

    @FormUrlEncoded
    @POST("data_model/courses/course_instructor_reviews/delete_review_from_instructor")
    Call<JsonObject> deleteUserInstructorReviews(
            @Field(Const.USER_ID) String userId,
            @Field(Const.INSTR_ID) String INSTR_ID
    );

    @FormUrlEncoded
    @POST("data_model/courses/course_instructor_reviews/edit_review_to_instructor")
    Call<JsonObject> editUserInstructorReviews(
            @Field(Const.USER_ID) String userId,
            @Field(Const.INSTR_ID) String INSTR_ID,
            @Field(Const.RATINGS) String RATINGS,
            @Field(Const.TEXT) String TEXT
    );
}
