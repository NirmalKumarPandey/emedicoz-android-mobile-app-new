package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ReferralApiInterface {
    @FormUrlEncoded
    @POST("data_model/referral-affiliate/affiliate/signup_affiliate")
    Call<JsonObject> getReferralSignUpMe(
            @Field(Const.USER_ID) String userId,
            @Field(Const.FIRST_NAME) String FIRST_NAME,
            @Field(Const.LAST_NAME) String LAST_NAME,
            @Field(Const.REF_EMAIL) String EMAIL,
            @Field(Const.REF_PHONE) String PHONE,
            @Field(Const.PAN_CARD) String PAN_CARD,
            @Field(Const.AADHAAR_CARD) String AADHAAR_CARD,
            @Field(Const.REF_ADDRESS) String ADDRESS,
            @Field(Const.POSTAL_CODE) String POSTAL_CODE,
            @Field(Const.REF_COUNTRY) String COUNTRY,
            @Field(Const.REF_STATE) String STATE,
            @Field(Const.REF_CITY) String CITY,
            @Field(Const.ACC_HOLDER_NAME) String ACC_HOLDER_NAME,
            @Field(Const.BANK_NAME) String BANK_NAME,
            @Field(Const.BANK_BRANCH_NAME) String BANK_BRANCH_NAME,
            @Field(Const.ACC_NUMBER) String ACC_NUMBER,
            @Field(Const.IFSC_CODE) String IFSC_CODE,

            @Field(Const.PAN_IMAGE) String PAN_IMAGE,
            @Field(Const.AADHAR_IMAGE) String AADHAR_IMAGE,
            @Field(Const.BANK_IMAGE) String BANK_IMAGE,
            @Field(Const.INSTRUCTOR_ID) String INSTRUCTOR_ID

    );
    @FormUrlEncoded
    @POST("data_model/referral-affiliate/affiliate/get_affiliate_user_bank_info")
    Call<JsonObject> getAffiliateUserBankInfo(
            @Field(Const.AFFILIATE_USER_ID) String AFFILIATE_USER_ID
    );

    @FormUrlEncoded
    @POST("data_model/referral-affiliate/affiliate/get_affiliate_user_personal_info")
    Call<JsonObject> getAffiliateUserPersonalInfo(
            @Field(Const.AFFILIATE_USER_ID) String AFFILIATE_USER_ID
    );
    @FormUrlEncoded
    @POST("data_model/referral-affiliate/affiliate/get_profile_info")
    Call<JsonObject> getProfileInfo(
            @Field(Const.AFFILIATE_USER_ID) String AFFILIATE_USER_ID
    );
    @FormUrlEncoded
    @POST("data_model/referral-affiliate/affiliate/update_affiliate_bankinfo")
    Call<JsonObject> updateAffiliateBankInfo(
            @Field(Const.ACC_HOLDER_NAME) String ACC_HOLDER_NAME,
            @Field(Const.BANK_NAME) String BANK_NAME,
            @Field(Const.BANK_BRANCH_NAME) String BANK_BRANCH_NAME,
            @Field(Const.ACC_NUMBER) String ACC_NUMBER,
            @Field(Const.IFSC_CODE) String IFSC_CODE,
            @Field(Const.AFFILIATE_USER_ID) String AFFILIATE_USER_ID,
            @Field(Const.BANK_INFO_ID) String BANK_INFO_ID
    );
    @FormUrlEncoded
    @POST("data_model/referral-affiliate/affiliate/update_affiliate_profile_with_bank_info")
    Call<JsonObject> updateAffProfileInfoWithBank(
            @Field(Const.AFFILIATE_USER_ID) String AFFILIATE_USER_ID,
            @Field(Const.BANK_INFO_ID) String BANK_INFO_ID,
            @Field(Const.FIRST_NAME) String FIRST_NAME,
            @Field(Const.LAST_NAME) String LAST_NAME,
            @Field(Const.REF_EMAIL) String EMAIL,
            @Field(Const.REF_PHONE) String PHONE,
            @Field(Const.PAN_CARD) String PAN_CARD,
            @Field(Const.AADHAAR_CARD) String AADHAAR_CARD,
            @Field(Const.REF_ADDRESS) String ADDRESS,
            @Field(Const.POSTAL_CODE) String POSTAL_CODE,
            @Field(Const.REF_COUNTRY) String COUNTRY,
            @Field(Const.REF_STATE) String STATE,
            @Field(Const.REF_CITY) String CITY,
            @Field(Const.ACC_HOLDER_NAME) String ACC_HOLDER_NAME,
            @Field(Const.BANK_NAME) String BANK_NAME,
            @Field(Const.BANK_BRANCH_NAME) String BANK_BRANCH_NAME,
            @Field(Const.ACC_NUMBER) String ACC_NUMBER,
            @Field(Const.IFSC_CODE) String IFSC_CODE,

            @Field(Const.PAN_IMAGE) String PAN_IMAGE,
            @Field(Const.AADHAR_IMAGE) String AADHAR_IMAGE,
            @Field(Const.BANK_IMAGE) String BANK_IMAGE,
            @Field(Const.INSTRUCTOR_ID) String INSTRUCTOR_ID
    );

    @FormUrlEncoded
    @POST("data_model/referral-affiliate/affiliate/my_affiliations")
    Call<JsonObject> myAffiliations(
            @Field(Const.AFFILIATE_USER_ID) String AFFILIATE_USER_ID,
            @Field(Const.PAYMENT_HISTORY_PAGE) int PAGE,
            @Field(Const.PER_PAGE_TOTAL) int PER_PAGE_TOTAL    );
    @FormUrlEncoded
    @POST("data_model/referral-affiliate/affiliate/affiliations_payment_history")
    Call<JsonObject> getAffiliationsPaymentHistory(
            @Field(Const.AFFILIATE_USER_ID) String AFFILIATE_USER_ID,
            @Field(Const.PAYMENT_HISTORY_PAGE) int PAGE,
            @Field(Const.PER_PAGE_TOTAL) int PER_PAGE_TOTAL
    );
    @FormUrlEncoded
    @POST("data_model/referral-affiliate/affiliate/affiliate_encash_request")
    Call<JsonObject> getAffiliateEncashRequest(
            @Field(Const.AFFILIATE_USER_ID) String AFFILIATE_USER_ID,
            @Field(Const.BANK_INFO_ID) String BANK_INFO_ID

    );
    @FormUrlEncoded
    @POST("data_model/referral-affiliate/affiliate/check_affiliate_exist")
    Call<JsonObject> checkAffiliateExist(
            @Field(Const.REF_PHONE) String PHONE
    );
    @FormUrlEncoded
    @POST("data_model/referral-affiliate/affiliate/get_course_discount_by_id")
    Call<JsonObject> getCourseDiscountByReferral(
            @Field(Const.COURSE_ID) String COURSE_ID,
            @Field(Const.USER_ID) String userId
    );

    @FormUrlEncoded
    @POST("data_model/referral-affiliate/affiliate/check_affiliate_referral_code_valid")
    Call<JsonObject> checkAffiliateReferralCode(
            @Field(Const.AFFILIATE_REFERRAL_CODE) String AFFILIATE_REFERRAL_CODE
    );

    @POST("data_model/referral-affiliate/affiliate/get_profile_type_list")
    Call<JsonObject> getProfileTypeList(

    );
}
