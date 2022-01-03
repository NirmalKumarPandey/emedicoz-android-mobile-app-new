package com.emedicoz.app.api

import com.emedicoz.app.courses.recordedmodel.RecordedCourseList
import com.emedicoz.app.modelo.SyncReqParam
import com.emedicoz.app.notifications.model.CategoryNotification
import com.emedicoz.app.notifications.model.NotificationCategoryData
import com.emedicoz.app.rating.GetQbankRating
import com.emedicoz.app.rating.Rating
import com.emedicoz.app.reward.model.RewardsPoint
import com.emedicoz.app.reward.model.transmodel.TransactionPoint
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    //----------------call for AppSettingApi----------
    @FormUrlEncoded
    @POST("data_model/user/registration/user_notification_settings")
    open fun getAppSettingUser(
        @Field(Const.USER_ID) userId: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/registration/edit_user_notification")
    fun getAppeditSettingUser(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.OPTION) option: String,
        @Field(Const.OPTION_VALUE) optionvalue: String
    ): Call<JsonObject>
    //-------------------------end------------------------------


    //get app version api call----------------------------
    @POST("data_model/version/version/get_version")
    fun getAppVersion(): Call<JsonObject>

    //get all category
    @POST("data_model/user/registration/get_all_category_db")
    fun getMasterRegistrationResponse(): Call<JsonObject>

    //get trending------------------------
    @FormUrlEncoded
    @POST("data_model/courses/course/get_trending")
    fun getTrendingSearch(
        @Field(Const.USER_ID) userId: String
    ): Call<JsonObject>

    //get content------------------------
    @FormUrlEncoded
    @POST("data_model/fanwall/master_hit/content")
    fun getMasterFeedForUser(
        @Field(Const.USER_ID) userId: String,
        @Field("user_info") user_info: String
    ): Call<JsonObject>

    //check validated dams user api call-----------------------------
    @FormUrlEncoded
    @POST("data_model/fanwall/master_hit/validate_dams")
    fun validateDAMSUser(
        @Field("username") username: String
    ): Call<JsonObject>
    //----------------------------end-----------------------

    //all bookmarkApi calling-------------------------------
    @FormUrlEncoded
    @POST("data_model/fanwall/Fan_wall/bookmark_category_list")
    open fun getBookmarkCategories(
        @Field(Const.USER_ID) userId: String,
        @Field(Constants.Extras.TYPE) type: String,
        @Field("stream") stream: String,
        @Field(Constants.Extras.Q_TYPE_DQB) q_type_dqb: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/fanwall/Fan_wall/bookmark_list")
    fun getBookmarkList(
        @Field(Const.USER_ID) userId: String,
        @Field(Constants.Extras.TYPE) type: String,
        @Field(Constants.Extras.STREAM) stream: String,
        @Field(Const.LAST_POST_ID) last_post_id: String,
        @Field(Constants.Extras.TAG_ID) tag_id: String,
        @Field(Const.SEARCH_TEXT) search_text: String,
        @Field(Const.TESTSERIES_ID) test_series_id: String,
        @Field(Constants.Extras.Q_TYPE_DQB) q_type_dqb: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/fanwall/fan_wall/remove_bookmark")
    fun removeBookmark(
        @Field(Const.USER_ID) userId: String,
        @Field("question_id") questionId: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/fanwall/Fan_wall/get_question_bookmark_by_subject")
    fun allSubjectBookmark(
        @Field(Const.USER_ID) userId: String,
        @Field("subject_id") subject_id: String,
        @Field(Const.LAST_ID) last_post_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/course/get_video_time_access")
    fun getVideoSeekTime(
        @Field(Const.USER_ID) userId: String,
        @Field("video_id") video_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/crs/get_bookmark_count_by_quiz")
    fun getBookmarkCount(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.TESTSERIES_ID) test_series_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/exam/get_courses")
    fun getChildCourseList(
        @Field(Const.USER_ID) userId: String,
        @Field(Constants.Extras.ID) courseId: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/course/get_user_courses_wishlist")
    fun getUserWishlist(
        @Field(Const.USER_ID) userId: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/course/get_recently_viewed_courses")
    fun getRecentlyViewedCourses(
        @Field(Const.USER_ID) userId: String,
        @Field("course_id") courseIds: String
    ): Call<JsonObject>


    @FormUrlEncoded
    @POST("data_model/courses/course/get_all_courses_by_cat")
    fun getCourseByCat(
        @Field(Const.USER_ID) userId: String,
        @Field("cat_id") courseIds: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/live_course/get_all_live_courses_by_cat")
    fun getLiveCourseByCat(
        @Field(Const.USER_ID) userId: String,
        @Field("cat_id") courseIds: String
    ): Call<JsonObject>


    @FormUrlEncoded
    @POST("data_model/courses/live_course/get_live_recently_viewed_courses")
    fun getLiveRecentlyViewedCourses(
        @Field(Const.USER_ID) userId: String,
        @Field("course_id") courseIds: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/course/add_user_course_wishlist")
    fun addCourseToWishlist(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) test_series_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/course/remove_user_course_wishlist")
    fun removeCourseToWishlist(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) test_series_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/Help_support/get_help_support_data")
    fun getHelpAndSupportData(
        @Field(Const.USER_ID) userId: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/Help_support/get_search_help_support_list")
    fun getHelpAndSupportDataWithQuery(
        @Field(Const.USER_ID) userId: String,
        @Field("question") question: String
    ): Call<JsonObject>
    //------------------------------------end of code----------------------

    //start cartcoupon api integrated---------------------------
    @FormUrlEncoded
    @POST("data_model/user/user_cart/get_user_cart_data")
    fun getUserCartData(
        @Field(Const.USER_ID) userId: String
    ): Call<JsonObject?>

    @FormUrlEncoded
    @POST("data_model/user/user_cart/add_course_to_cart")
    fun addCourseToCart(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) courseId: String,
        @Field(Const.SUBSCRIPTION_ID) subscriptionId: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/user_cart/remove_course_from_cart")
    fun removeCourseFromCart(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) courseId: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/user_cart/apply_cart_coupon")
    fun applyCartCoupon(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COUPON_CODE) couponCode: String
    ): Call<JsonObject>

    @FormUrlEncoded //@POST("data_model/user/user_cart/initialize_course_transaction_cart")    // old endpoint
    @POST("data_model/courses/course/initialize_course_transaction_cart")
    fun initializeTransactionCart(
        @Field(Const.LIST) list: String,
        @Field(Const.USER_ID) userId: String,
        @Field(Const.PAY_VIA) payVia: String,
        @Field(Const.POINTS_USED) pointsUsed: String,
        @Field(Const.POINTS_RATE) pointsRate: String,
        @Field(Const.PENALTY) penalty: String,
        @Field(Const.TAX) tax: String,
        @Field(Const.TOTAL_PRICE) totalPrice: String,
        @Field(Const.REFER_CODE) referCode: String,
        @Field(Const.COUPON_APPLIED) couponApplied: String,
        @Field(Const.ADDRESS_ID) addressId: String
    ): Call<JsonObject>
    //--------------------------------------end-----------------------

    //changepassword api call---------------------------------
    @FormUrlEncoded
    @POST("data_model/user/registration/update_password_via_otp")
    fun updatePasswordviaOtp(
        @Field(Const.COUNTRY_CODE) c_code: String,
        @Field(Const.MOBILE) mobile: String,
        @Field(Const.OTP) otp: String,
        @Field(Const.PASSWORD) password: String
    ): Call<JsonObject>
    //---------------------------------------end------------------------

    //start comment api calling--------------------------------
    @FormUrlEncoded
    @POST("data_model/user/post_comment/update_comment")
    open fun editComment(
        @Field(Constants.Extras.ID) ID: String,
        @Field(Const.USER_ID) userId: String,
        @Field(Const.POST_ID) POST_ID: String,
        @Field(Const.POST_COMMENT) POST_COMMENT: String,
        @Field(Const.IMAGE) IMAGE: String,
        @Field(Const.TAG_PEOPLE) TAG_PEOPLE: String,
        @Field(Const.REMOVE_TAG_PEOPLE) REMOVE_TAG_PEOPLE: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/post_comment/delete_comment")
    fun deleteComment(
        @Field(Constants.Extras.ID) ID: String,
        @Field(Const.USER_ID) userId: String,
        @Field(Const.POST_ID) POST_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/post_like/like_comment")
    fun likeComment(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COMMENT_ID) comment_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/post_like/dislike_comment")
    fun dislikeComment(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COMMENT_ID) comment_id: String
    ): Call<JsonObject>
    //-----------------------------end---------------

    //calling for commonfragapi ------------------------------------
    @FormUrlEncoded
    @POST("data_model/user/post_comment/get_single_comment_data")
    fun getSingleCommentData(
        @Field(Const.COMMENT_ID) commentid: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/post_comment/add_comment")
    fun getAddCommentDataForComment(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.POST_ID) postId: String,
        @Field(Const.POST_COMMENT) postcomment: String,
        @Field(Const.IMAGE) image: String,
        @Field(Const.TAG_PEOPLE) tagpeoople: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/post_comment/add_comment")
    fun getAddCommentDataForCommentList(
        @Field(Const.PARENT_ID) PARENT_ID: String,
        @Field(Const.USER_ID) userId: String,
        @Field(Const.POST_ID) postId: String,
        @Field(Const.IMAGE) image: String,
        @Field(Const.POST_COMMENT) postcomment: String,
        @Field(Const.TAG_PEOPLE) tagpeoople: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/post_comment/get_post_comment")
    fun getCommentListForComment(
        @Field(Const.USER_ID) user_id: String,
        @Field(Const.POST_ID) postId: String,
        @Field(Const.LAST_COMMENT_ID) LAST_COMMENT_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/post_comment/get_post_comment")
    fun getCommentListForCommentList(
        @Field(Const.USER_ID) user_id: String,
        @Field(Const.POST_ID) postId: String,
        @Field(Const.PARENT_ID) PARENT_ID: String,
        @Field(Const.LAST_COMMENT_ID) LAST_COMMENT_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/notification_genrator/activity_logger/get_user_activities")
    fun getUserNotification(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.LAST_ACTIVITY_ID) LAST_ACTIVITY_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/user_reward/get_reward_transaction")
    fun getRewardTransaction(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.LAST_ID) LAST_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/notification_genrator/Activity_logger/set_all_read")
    fun getReadNotification(
        @Field(Const.USER_ID) userId: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/post_like/get_post_like_users")
    fun getLikesCount(
        @Query("") IS_WATCHER: String,
        @Field(Const.POST_ID) POST_ID: String,
        @Field(Const.LAST_LIKE_ID) LAST_LIKE_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/fanwall/fan_wall/get_single_post_data_for_user")
    fun getSinglePostdata(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.POST_ID) POST_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/course/get_releated_course")
    fun getRelatedCourse(
        @Field(Const.USER_ID) userId: String,
        @Field("tags") tags: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/quicklinks/get_links")
    fun getCourseCatTile(
        @Field(Const.USER_ID) userId: String
    ): Call<JsonObject>
    //-------------------------------------end-----------------------

    //start--------------------courseinvoiceapi----------------------
    @FormUrlEncoded
    @POST("data_model/courses/course/apply_coupon_code_test")
    open fun applyCouponCode(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) COURSE_ID: String,
        @Field(Const.COUPON_CODE) COUPON_CODE: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/Course/initialize_course_transaction_test")
    fun initializeCoursePayment(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) COURSE_ID: String,
        @Field(Const.POINTS_RATE) POINTS_RATE: String,
        @Field(Const.TAX) TAX: String,
        @Field(Const.POINTS_USED) POINTS_USED: String,
        @Field(Const.COUPON_APPLIED) COUPON_APPLIED: String,
        @Field("refral_code") REFERRAL_CODE: String,
        @Field(Const.PAY_VIA) PAY_VIA: String,
        @Field(Const.CHILD_COURSES) child_courses: String,
        @Field(Const.COURSE_PRICE) COURSE_PRICE: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST
    fun getCheckSumForPaytmLive(
        @Url url: String,
        @Field(Const.MID) MID: String,
        @Field(Const.ORDER_ID) ORDER_ID: String,
        @Field(Const.INDUSTRY_TYPE_ID) INDUSTRY_TYPE_ID: String,
        @Field(Const.CUST_ID) CUST_ID: String,
        @Field(Const.CHANNEL_ID) CHANNEL_ID: String,
        @Field(Const.TXN_AMOUNT) TXN_AMOUNT: String,
        @Field(Const.WEBSITE) WEBSITE: String,
        @Field(Const.CALLBACK_URL) CALLBACK_URL: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST
    fun getCheckSumForPaytm(
        @Url url: String,
        @Field(Const.MID) MID: String,
        @Field(Const.ORDER_ID) ORDER_ID: String,
        @Field(Const.INDUSTRY_TYPE_ID) INDUSTRY_TYPE_ID: String,
        @Field(Const.CUST_ID) CUST_ID: String,
        @Field(Const.CHANNEL_ID) CHANNEL_ID: String,
        @Field(Const.TXN_AMOUNT) TXN_AMOUNT: String,
        @Field(Const.WEBSITE) WEBSITE: String,
        @Field(Const.CALLBACK_URL) CALLBACK_URL: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/Course/complete_course_transaction_test")
    fun completeCoursePayment(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) COURSE_ID: String,
        @Field("course_discount") course_discount: Int,
        @Field(Const.COURSE_PRICE) finalPriceValue: String,
        @Field(Const.COURSE_INIT_PAYMENT_TOKEN) COURSE_INIT_PAYMENT_TOKEN: String,
        @Field(Const.COURSE_FINAL_PAYMENT_TOKEN) COURSE_FINAL_PAYMENT_TOKEN: String,
        @Field(Const.AFFILIATE_REFERRAL_CODE_BY) AFFILIATE_REFERRAL_CODE_BY: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/course/complete_course_transaction_cart")
    fun completeCoursePaymentCart(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.LIST) list: String /*,
            @Field("course_discount") int course_discount,
            @Field(Const.COURSE_ID) String COURSE_ID,
            @Field(Const.COURSE_PRICE) String finalPriceValue*/,
        @Field(Const.COURSE_INIT_PAYMENT_TOKEN) COURSE_INIT_PAYMENT_TOKEN: String,
        @Field(Const.COURSE_FINAL_PAYMENT_TOKEN) COURSE_FINAL_PAYMENT_TOKEN: String,
        @Field(Const.AFFILIATE_REFERRAL_CODE_BY) AFFILIATE_REFERRAL_CODE_BY: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/course/trans_info_update_cart")
    fun updateTransactionInfo(
        @Field(Const.USER_ID) USER_ID: String,
        @Field("STREAM_ID") STREAM_ID: String,
        @Field(Const.LIST) LIST: String,
        @Field(Const.COURSE_INIT_PAYMENT_TOKEN) COURSE_INIT_PAYMENT_TOKEN: String,
        @Field(Const.COURSE_FINAL_PAYMENT_TOKEN) COURSE_FINAL_PAYMENT_TOKEN: String,
        @Field("total_paid") total_paid: String
    ): Call<JsonObject>

    @POST
    fun finalTransactionForPaytm(@Url url: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/user_reward/get_user_rewards")
    fun getRewardPoints(
        @Field(Const.USER_ID) userId: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/live_course/get_course_subscription_byid")
    fun getInstallments(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) courseId: String,
        @Field("is_renew") isRenew: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/Course/free_course_transaction_test")
    fun makeFreeCourseTransaction(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.POINTS_RATE) POINTS_RATE: String,
        @Field(Const.TAX) TAX: String,
        @Field(Const.POINTS_USED) POINTS_USED: String,
        @Field(Const.COUPON_APPLIED) COUPON_APPLIED: String,
        @Field(Const.COURSE_ID) COURSE_ID: String,
        @Field(Const.COURSE_PRICE) COURSE_PRICE: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/My_courses/order_history")
    fun getOrderHistoryData(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.LAST_ID) last_id: String
    ): Call<JsonObject>
    //-----------------------end courseinvoiceapi--------------------------

    //-----------------start course review api calling------------------------
    @FormUrlEncoded
    @POST("data_model/courses/Course_rating_reviews/add_review_to_course")
    open fun addReviewCourse(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) COURSE_ID: String,
        @Field(Const.RATINGS) RATINGS: String,
        @Field(Const.TEXT) TEXT: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/course_instructor_reviews/get_list_of_reviews")
    fun getInstructorReviewList(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.INSTR_ID) INSTR_ID: String,
        @Field(Const.LAST_REVIEW_ID) LAST_REVIEW_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/Course_rating_reviews/get_list_of_reviews")
    fun getCoursereviewList(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) COURSE_ID: String,
        @Field(Const.LAST_REVIEW_ID) LAST_REVIEW_ID: String
    ): Call<JsonObject>

    @POST("data_model/fanwall/Fan_wall_banner/get_course_banner")
    fun getCourseBanner(): Call<JsonObject>
    //------------------------------end-------------------------------

    //calling for curriculum api -----------------------------
    @FormUrlEncoded
    @POST("data_model/courses/Course/get_all_file_info")
    open fun getFileListCurriculum(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) COURSE_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/test_series/get_test_series_with_id_app")
    fun getCompleteInfTestSeries(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.TESTSERIES_ID) TESTSERIES_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/test_series/get_test_series_results")
    fun getCompleteInfTestSeriesResult(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.TESTSERIES_ID) TESTSERIES_ID: String
    ): Call<JsonObject>

/*
    @FormUrlEncoded
    @POST("data_model/courses/Course/get_quiz")
    Call<JsonObject> getSubjectData(
            @Field(Const.USER_ID) String userId
    );
*/

    /*
    @FormUrlEncoded
    @POST("data_model/courses/Course/get_quiz")
    Call<JsonObject> getSubjectData(
            @Field(Const.USER_ID) String userId
    );
*/
    @FormUrlEncoded
    @POST("data_model/courses/crs/get_qbankcourse")
    fun getSubjectData(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) course_id: String
    ): Call<JsonObject>


    @FormUrlEncoded
    @POST("data_model/courses/crs/get_video_course")
    fun getCRSData(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) course_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/crs/getmst")
    fun getTabsData(
        @Field(Const.USER_ID) userId: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/crs/get_videos")
    fun getCRSFiles(
        @Field(Const.USER_ID) userId: String,
        @Field(Constants.Extras.TOPIC_ID) topic_id: String,
        @Field(Const.COURSE_ID) course_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/crs/get_ebook")
    fun getEBookData(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COURSE_ID) course_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/crs/get_files")
    fun getEBookFiles(
        @Field(Const.USER_ID) userId: String,
        @Field(Constants.Extras.TOPIC_ID) topic_id: String,
        @Field(Const.COURSE_ID) course_id: String
    ): Call<JsonObject>
    //-----------------------------end of code------------------------

    //calling for custom module api-----------------------------------
    @FormUrlEncoded
    @POST("data_model/courses/Custom_qbank/get_qbank_subject_v2")
    fun getSubjectList(
        @Field(
            Const.USER_ID
        ) user_id: String
    ): Call<JsonObject>

    @POST("data_model/courses/Custom_qbank/get_tags_list")
    fun getTagList(): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/Custom_qbank/create_qbank")
    fun setModuleData(
        @Field(Const.USER_ID) user_id: String,
        @Field("no_of_ques") no_of_ques: String,
        @Field("defficulty_lvl") defficulty_lvl: String,
        @Field("ques_from") ques_from: String,
        @Field("subject") subject: String,
        @Field("topics") topics: String,
        @Field("mode") mode: String,
        @Field("tags") tags: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/Custom_qbank/check_qbank")
    fun checkModuleData(@Field(Const.USER_ID) user_id: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/Custom_qbank/discard_qbank")
    fun deleteModuleData(
        @Field(Const.USER_ID) user_id: String,
        @Field("test_series_id") test_series_id: String
    ): Call<JsonObject>
    //--------------------------end------------------------

    //calling for daily challenge api--------------------------------------
    @FormUrlEncoded
    @POST("data_model/courses/test_series/get_test_series_daily_quiz_basic_result_vc")
    fun getScoreBoardOfDailyChallenge(
        @Field(Const.USER_ID) userId: String,
        @Field("date") date: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/test_series/get_book_marked_daily_quiz_vc")
    fun  // api to get daily challenge bookmark list
            getDailyChallengeBookmarkList(
        @Field(Const.USER_ID) userId: String
    ): Call<JsonObject>
    //------------------------end-------------------------

    //calling for feed adapter api=--------------------------------
    @FormUrlEncoded
    @POST("data_model/fanwall/Fan_wall_banner/update_banner_hit_count")
    open fun updateBannerHitCount(
        @Field(Const.BANNER_ID) BANNER_ID: String
    ): Call<JsonObject>
    //-----------------------end------------------

    //calling for feed api----------------------------
    @FormUrlEncoded
    @POST
    fun getFeedForUser(
        @Url url: String,
        @Field(Const.USER_ID) userId: String,
        @Field(Const.LAST_POST_ID) last_post_id: String,
        @Field(Constants.Extras.TAG_ID) tag_id: String,
        @Field(Const.SEARCH_TEXT) search_text: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/fanwall/master_hit/content")
    fun getMasterFeedForUser2(
        @Field(Const.USER_ID) userId: String?,
        @Field(Const.SUB_CAT) sub_cat: String?
    ): Call<JsonObject?>?

    @FormUrlEncoded
    @POST("data_model/fanwall/live_stream/top_video_stream")
    fun getLiveStreamForUser(
        @Field(Const.USER_ID) userId: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/notification_genrator/activity_logger/all_notification_counter")
    fun getNotiCountForUser(
        @Field(Const.USER_ID) userId: String
    ): Call<JsonObject>


    @POST
    fun getDataForUser(
        @Url userId: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/user/registration/account_update")
    fun updateDataForUser(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.EMAIL) email: String,
        @Field(Const.PASSWORD) password: String
    ): Call<JsonObject>

    @POST("data_model/courses/exam/get_short_desc_download_items_by_users_vc")
    fun syncDownloadedData(@Body param: SyncReqParam): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/Dailyquiz/get_list")
    fun getDailyQuizData(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.LAST_ID) last_id: String
    ): Call<JsonObject>
    //----------------------end--------------------

    //calling for video details api----------------------------
    @FormUrlEncoded
    @POST("data_model/fanwall/fan_wall/on_request_create_video_link")
    fun requestVideoLink(
        @Field(Constants.Extras.NAME) name: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/video/Video_channel/get_single_video_data")
    fun getSingleVideoData(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.VIDEO_ID) VIDEO_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/video/video_comment/get_video_comment")
    fun getSingleVideoCommmentList(
        @Field(Const.USER_ID) user_id: String,
        @Field(Const.VIDEO_ID) VIDEO_ID: String,
        @Field(Const.PARENT_ID) PARENT_ID: String,
        @Field(Const.LAST_VIDEO_COMMENT_ID) LAST_VIDEO_COMMENT_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/video/video_comment/get_video_comment")
    fun getSingleVideoComment(
        @Field(Const.USER_ID) user_id: String,
        @Field(Const.VIDEO_ID) VIDEO_ID: String,
        @Field(Const.LAST_VIDEO_COMMENT_ID) LAST_VIDEO_COMMENT_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/video/video_comment/add_comment")
    fun addVideoComment(
        @Field(Const.USER_ID) USER_ID: String,
        @Field(Const.VIDEO_ID) VIDEO_ID: String,
        @Field(Const.COMMENT) COMMENT: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/video/video_comment/add_comment")
    fun addVideoCommentList(
        @Field(Const.USER_ID) USER_ID: String,
        @Field(Const.VIDEO_ID) VIDEO_ID: String,
        @Field(Const.PARENT_ID) PARENT_ID: String,
        @Field(Const.COMMENT) COMMENT: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/video/Video_like/like_video")
    fun likeVideoData(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.VIDEO_ID) VIDEO_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/video/Video_like/dislike_video")
    fun dislikeVideoData(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.VIDEO_ID) VIDEO_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/video/Video_channel/add_video_counter")
    fun addViewVideo(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.VIDEO_ID) VIDEO_ID: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/video/video_comment/like_video_comment")
    fun likeVideoComment(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COMMENT_ID) comment_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/video/video_comment/dislike_video_comment")
    fun dislikeVideoComment(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.COMMENT_ID) comment_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/video/Video_channel/add_solution_video_counter")
    fun getVideoSolutionCounter(
        @Field(Const.USER_ID) userId: String,
        @Field(Const.VIDEO_ID) video_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/Dvl/get_dvl")
    fun getDvl(
        @Field(Const.USER_ID) userId: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/courses/dvl/get_videos")
    fun getDvlVideos(
        @Field(Const.USER_ID) user_id: String,
        @Field(Const.COURSE_ID) course_id: String,
        @Field("topic_id") topic_id: String
    ): Call<JsonObject>
    //----------------------end-----------------


    @FormUrlEncoded
    @POST("data_model/courses/Test_series/Qbankrating")
    fun saveQbankRating(
            @Field(Const.USER_ID) user_id: String,
            @Field("test_series_id") test_series_id: String,
            @Field("rating") rating: Float,
            @Field("feedback") feedback: String,
            @Field("test_type") test_type: String

    ): Call<Rating>

    @FormUrlEncoded
    @POST("data_model/courses/Test_series/getQbankrate")
    fun getQbankRating(
            @Field(Const.USER_ID) user_id: String,
            @Field("type") type: String
    ): Call<GetQbankRating>

    @FormUrlEncoded
    @POST("data_model/courses/Course_rating_reviews/apprating")
    fun addRating(
            @Field("userid") userid: String,
            @Field("ratingno") ratingno: Float,
            @Field("feedback") feedback: String,
            @Field("type") type: String
    ): Call<Rating?>?


    @FormUrlEncoded
    @POST("data_model/courses/Test_series/update_apprating")
    fun updateRating(
            @Field("user_id") user_id: String,
            @Field("test_type") test_type: String,
            @Field("rating") rating: Float
    ): Call<Rating>

    //get user reward point api
    @FormUrlEncoded
    @POST("data_model/user/user_reward/get_user_rewards")
    suspend fun getRewardPoint(
            @Field("user_id") user_id: String
    ): Response<RewardsPoint>

    @FormUrlEncoded
    @POST("data_model/user/user_reward/get_coin_transaction")
    suspend fun getCoinTransaction(
            @Field("user_id") user_id: String
    ): Response<TransactionPoint>

    //get notification category
    @POST("data_model/notification_genrator/activity_logger/get_Notification_Category")
    suspend fun getNotificationCategory(

    ): Response<NotificationCategoryData>


    //get category by notification api-----------
    @FormUrlEncoded
    @POST("data_model/notification_genrator/activity_logger/get_user_notification_by_category")
    fun getUserNotificationByCategory(
        @Field("user_type") user_type: String,
        @Field("notification_type") notification_type: String
    ): Call<CategoryNotification>
}