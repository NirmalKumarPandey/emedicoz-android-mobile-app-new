package com.emedicoz.app.utilso.network;

import com.emedicoz.app.BuildConfig;
import com.emedicoz.app.utilso.SharedPreference;

import java.util.Calendar;

/**
 * Created by Cbc-03 on 08/10/16.
 **/
public interface API {

    String API_AMAZON_S3_BUCKET_NAME_PROFILE_IMAGES = BuildConfig.AMAZON_S3_BUCKET + "/profile_images";
    String API_AMAZON_S3_BUCKET_NAME_FAN_WALL_IMAGES = BuildConfig.AMAZON_S3_BUCKET + "/fanwall_images";
    String API_AMAZON_S3_BUCKET_NAME_VIDEO_IMAGES = BuildConfig.AMAZON_S3_BUCKET + "/video_thumbnails";
    String AMAZON_S3_BUCKET_NAME_FIREBASE_CHAT = BuildConfig.AMAZON_S3_BUCKET + "/firebase_chat_images";
    String API_AMAZON_S3_BUCKET_NAME_DOCUMENT = BuildConfig.AMAZON_S3_BUCKET + "/doc_folder";
    String API_AMAZON_S3_BUCKET_NAME_FEEDBACK = BuildConfig.AMAZON_S3_BUCKET + "/feedback_images";
    String API_AMAZON_S3_BUCKET_NAME_COMMENT = BuildConfig.AMAZON_S3_BUCKET + "/comment_images";
    String API_AMAZON_S3_FILE_NAME_CREATION = SharedPreference.getInstance().getLoggedInUser().getId() + "_" + Calendar.getInstance().getTimeInMillis();


    //------------------------------------------------------------------------------------------------------------//

    String API_UPDATE_INFO = "data_model/user/registration/account_update";

    String API_OTP = "data_model/user/mobile_auth/send_otp";

    String API_CHANGE_PASSWORD_OTP = "data_model/user/Mobile_auth/send_otp_for_change_password";

    String API_OTP_FOR_MOBILE_CHANGE = "data_model/user/mobile_auth/send_otp_for_mobile_change";

    String API_UPDATE_PASSWORD_WITH_OTP = "data_model/user/registration/update_password_via_otp";

    String API_UPDATE_MOBILE_NUMBER = "data_model/user/User_mobile_change/update_mobile_number";

    String API_FORGET_PASS_DAMS = "data_model/user/registration/forget_password";

    String API_REGISTER_USER = "data_model/user/registration";

    String API_USER_DAMS_VERIFICATION = "data_model/user/dams_user_verification/user_verification";

    String API_USER_LOGIN_AUTHENTICATION = "data_model/user/registration/login_authentication";

    String API_USER_DAMS_LOGIN_AUTHENTICATION = "data_model/user/registration/dams_auth";

    String API_POST_MCQ = "data_model/user/post_mcq/add_mcq";

    String API_POST_NORMAL_VIDEO = "data_model/user/post_text/add_post";

    String API_PEOPLE_YOU_MAY_KNOW = "data_model/fanwall/people_you_may_know/get_list";

    String API_GET_FEEDS_FOR_USER = "data_model/fanwall/fan_wall/get_fan_wall_for_user";

    String API_GET_MASTER_HIT = "data_model/fanwall/master_hit/content";

    String API_LIKE_POST = "data_model/user/post_like/like_post";

    String API_DISLIKE_POST = "data_model/user/post_like/dislike_post";

    String API_REPORT_POST = "data_model/user/Post_report_abuse/report";

    String API_DELETE_POST = "data_model/user/Post_delete/delete_post";

    String API_SHARE_POST = "data_model/user/post_share/share_post";

    String API_ADD_BOOKMARK = "data_model/user/post_bookmarks/add_to_bookmarks";

    String API_REMOVE_BOOKMARK = "data_model/user/post_bookmarks/remove_from_bookmarks";

    String API_BOOKMARK_CATEGORY_LIST = "data_model/fanwall/Fan_wall/bookmark_category_list";

    String API_BOOKMARK_LIST = "data_model/fanwall/Fan_wall/bookmark_list";

    String API_ADD_COMMENT = "data_model/user/post_comment/add_comment";

    String API_EDIT_COMMENT = "data_model/user/post_comment/update_comment";

    String API_DELETE_COMMENT = "data_model/user/post_comment/delete_comment";

    String API_GET_COMMENT_LIST = "data_model/user/post_comment/get_post_comment";

    String API_GET_MASTER_REGISTRATION_HIT = "data_model/user/registration/get_all_category_db";

    String API_GET_COURSE_LIST_ZERO_LEVEL = "data_model/user/User_category_handling/get_category_basic";

    String API_GET_COURSE_LIST_FIRST_LEVEL = "data_model/user/User_category_handling/get_category_basic_level_one";

    String API_GET_COURSE_LIST_SECOND_LEVEL = "data_model/user/User_category_handling/get_category_basic_level_two";

    String API_GET_COURSE_INTERESTED_IN = "data_model/user/User_category_handling/get_intersted_courses";

    String API_STREAM_REGISTRATION = "data_model/user/registration/stream_registration";

    String API_ADD_PODCAST = "data_model/podcasts/podcast/add_podcast";

    String API_GET_STREAM_SUBJECT_LIST = "data_model/podcasts/podcast/get_stream_subject_list";

    String API_GET_USER = "data_model/user/Registration/get_active_user/";

    String API_FOLLOW = "data_model/user/user_follow/follow_user";

    String API_UNFOLLOW = "data_model/user/user_follow/unfollow_user";

    String API_MAKE_AN_EXPERT = "data_model/user/user_expert_control/make_user_expert";

    String API_REMOVE_EXPERT = "data_model/user/user_expert_control/remove_user_expert";

    String API_SUBMIT_QUERY = "data_model/user/user_queries/submit_query";

    String API_FOLLOWING_LIST = "data_model/user/user_follow/following_list";

    String API_FOLLOWERS_LIST = "data_model/user/user_follow/followers_list";

    String API_FEEDS_BANNER = "data_model/fanwall/Fan_wall_banner/get_fan_wall_banner";

    String API_SINGLE_POST_FOR_USER = "data_model/fanwall/fan_wall/get_single_post_data_for_user";

    String API_GET_TAG_LISTS = "data_model/user/post_meta_tags/get_list_tags/";

//    String API_GET_USER_NOTIFICATIONS = "data_model/notification_genrator/activity_logger/get_user_activity";

    String API_GET_USER_NOTIFICATIONS = "data_model/notification_genrator/activity_logger/get_user_activities";

    String API_CHANGE_NOTIFICATION_STATE = "data_model/notification_genrator/activity_logger/make_activity_viewed";

    String API_GET_LIVE_STREAM = "data_model/fanwall/live_stream/top_video_stream";

    String API_GET_APP_VERSION = "data_model/version/version/get_version";

    String API_GET_NOTIFICATION_COUNT = "data_model/notification_genrator/activity_logger/all_notification_counter";

    String API_UPDATE_DEVICE_TOKEN = "data_model/user/registration/update_device_info";

    String API_UPDATE_DAMS_TOKEN = "data_model/user/registration/update_dams_id_user";

    String API_REQUEST_VIDEO_LINK = "data_model/fanwall/fan_wall/on_request_create_video_link";

    String API_EDIT_MCQ_POST = "data_model/user/post_mcq/edit_mcq";

    String API_EDIT_NORMAL_POST = "data_model/user/post_text/edit_post";

    String API_ALL_NOTIFICATION_READ = "data_model/notification_genrator/Activity_logger/set_all_read";

    String API_LIKES_COUNT_LIST = "data_model/user/post_like/get_post_like_users";

    String API_SINGLE_COMMENT_DATA = "data_model/user/post_comment/get_single_comment_data";

    String API_UPDATE_BANNER_HIT_COUNT = "data_model/fanwall/Fan_wall_banner/update_banner_hit_count";

    String API_SUBMIT_ANSWER_POST_MCQ = "data_model/user/post_mcq/answer_post_mcq";

    String API_COMBO_PACKAGE = "data_model/courses/combo/packages";

    String API_COMMON = "/data_model/Common/common";

    String API_GET_SINGLE_COURSE_INFO_RAW_V2 = "/data_model/courses/course/get_single_course_info_raw_v2";

    String API_PACKAGES = "/data_model/courses/combo/packages";

    String API_TRANSACTION_STATEMENT = "/data_model/courses/My_courses/txn_statment";

    String API_GET_PAST_PAPER_EXPLANATION = "data_model/courses/crs/get_ppe";

    String API_GET_TEST_CATEGORY_WISE = "data_model/courses/crs/get_test_category_wise";

    String API_GET_TEST_SUBJECT_WISE = "data_model/courses/crs/get_test_subject_wise";

    /*###########################################################

        ######  #######  #     #  ######  ######  ######  ######
        #       #     #  #     #  #    #  #       #       #
        #       #     #  #     #  ######  ######  ######  ######
        #       #     #  #     #  # #          #  #            #
        ######	#######  #######  #   #   ######  ######  ######

    ########################################################### */


    String API_GET_LANDING_PAGE_DATA = "data_model/courses/course/get_landing_page_data";

    String API_GET_LIVE_COURSE_LANDING_PAGE_DATA = "data_model/courses/live_course/get_landing_page_data";

    String API_GET_COURSE_BANNER = "data_model/fanwall/Fan_wall_banner/get_course_banner";

    String API_GET_COURSE_CATEGORY = "data_model/courses/quicklinks/get_links";

    String API_GET_BASIC_DATA_VC = "data_model/courses/live_course/get_basic_data_vc";

    String API_GET_VIDEO_DATA_VC = "data_model/courses/exam/get_video_data_vc";

    String API_GET_ALL_TEST_EPUB_PDF_DATA = "data_model/courses/exam/get_all_test__epub_pdf_data";

    String API_GET_COURSES = "data_model/courses/exam/get_courses";

    String API_GET_VIDEO_COURSE = "data_model/courses/crs/get_video_course";

    String API_GET_CRS_VIDEO = "data_model/courses/crs/get_videos";

    String API_COURSE_TYPE_TEST_SERIES = "data_model/courses/Course/test_series_course_type_test";

    String API_SINGLE_COURSE_INFO_RAW = "data_model/courses/course/get_single_course_info_raw";

    String API_GET_ALL_CATEGORY_DATA = "data_model/courses/course/get_all_category_data";

    String API_GET_SEE_ALL_BY_CATEGORY = "data_model/courses/course/get_all_courses_by_cat";
    String API_GET_SEE_ALL_BY_CATEGORY_LIVE_CLASS = "data_model/courses/live_course/get_all_live_courses_by_cat";

    String API_GET_MY_COURSE_DATA = "data_model/courses/my_courses/get_list_of_my_courses";

    String API_GET_INSTRUCTOR_DATA = "data_model/courses/Instructor/get_instructor_profile";

    String API_GET_FILE_LIST_CURRICULUM = "data_model/courses/Course/get_all_file_info";

    String API_GET_FAQ_DATA = "data_model/courses/Course/get_faq";

    String API_APPLY_COUPON_CODE = "data_model/courses/course/apply_coupon_code_test";

    String API_SET_POST_AS_PINNED = "data_model/user/post_pinning/pin_a_post";

    String API_SET_POST_AS_UNPINNED = "data_model/user/Post_pinning/pin_a_post_remove";

    String API_SEARCH_COURSE = "data_model/courses/Course/search_course";

    String API_GET_SOLUTION_TESTSERIES = "data_model/courses/Test_series/get_test_series_result";

    String API_GET_USER_LEADER_BOARD_RESULT_V3 = "data_model/courses/Test_series/get_test_series_basic_result_v3";

    String API_GET_RANK_LIST = "data_model/courses/Test_series_result/get_top_100";

    String API_GET_CUSTOM_VIEW_SOLUTION_DATA = "data_model/courses/custom_qbank/get_test_series_result";

    String API_GET_VIEW_SOLUTION_DATA = "data_model/courses/test_series/get_test_series_result";

    String API_GET_TRENDING_SEARCH = "data_model/courses/course/get_trending";

    String API_GET_REFERRAL_DISCOUNT = "data_model/referral-affiliate/affiliate/get_course_discount_by_id";

    String API_GET_REFERRAL_CODE_VALID = "data_model/referral-affiliate/affiliate/check_affiliate_referral_code_valid";
    /*################################################

        Videos

    ################################################ */


    String API_GET_VIDEO_COMMENT = "data_model/video/video_comment/get_video_comment";
    //End Videos

    String API_ADD_REVIEW_COURSE = "data_model/courses/Course_rating_reviews/add_review_to_course";

    String API_GET_INSTRUCTOR_REVIEW_LIST = "data_model/courses/course_instructor_reviews/get_list_of_reviews";

    String API_GET_COURSE_REVIEW_LIST = "data_model/courses/Course_rating_reviews/get_list_of_reviews";

    String API_INITIALIZE_COURSE_PAYMENT = "data_model/courses/Course/initialize_course_transaction_test";

    String API_MAKE_FREE_COURSE_TRANSACTION = "data_model/courses/Course/free_course_transaction_test";

    String API_COMPLETE_COURSE_PAYMENT = "data_model/courses/Course/complete_course_transaction_test";

    String API_UPDATE_TRANSACTION_INFO = "data_model/courses/course/trans_info_update";

    String API_DELETE_USER_COURSE_REVIEWS = "data_model/courses/Course_rating_reviews/delete_review_from_course";

    String API_EDIT_USER_COURSE_REVIEWS = "data_model/courses/Course_rating_reviews/edit_review_to_course";

    String API_GET_COMPLETE_INFO_TEST_SERIES = "data_model/courses/test_series/get_test_series_with_id_app";

    String API_COMPLETE_INFO_TESTSERIES = "data_model/courses/Test_series/save_test";

    String API_GET_USER_GIVEN_TESTSERIES = "data_model/courses/Test_series/get_user_given_test_series";

    String API_GET_USER_LEADERBOARD_RESULT = "data_model/courses/Test_series/get_test_series_basic_result_v3";

    String API_GET_SUBJECT_ANALYSIS = "data_model/courses/Test_series_result/get_subject_analysis";

    String API_ADD_INSTRUCTOR_REVIEW_COURSE = "data_model/courses/course_instructor_reviews/add_review_to_instructor";

    String API_EDIT_USER_INSTRUCTOR_REVIEWS = "data_model/courses/course_instructor_reviews/edit_review_to_instructor";

    String API_DELETE_USER_INSTRUCTOR_REVIEWS = "data_model/courses/course_instructor_reviews/delete_review_from_instructor";

    String API_GET_REWARD_POINTS = "data_model/user/user_reward/get_user_rewards";

    // Staging Paytm Txn API
    String API_FINAL_TRANSACTION_FOR_PAYTM = "Paytm/TxnStatus.php?MID=%s&ORDERID=%s&SERVER=%s";
    String API_GET_CHECKSUM_FOR_PAYTM = "Paytm/generateChecksum.php?SERVER=%s";
    String API_FINAL_TRANS_FOR_PAYTM = "Paytm/TxnStatus.php?MID=%s&ORDERID=%s&SERVER=%s";


    String API_GET_REPORT_ABUSE_LIST = "data_model/user/Post_report_abuse/get_all_report_reasons";

    String API_GET_FLASHCARD_DECK = "data_model/flashcard/flashcard/deck";

    String API_GET_FLASHCARD_ALL_CARDS = "data_model/flashcard/flashcard/all_cards";

    String API_GET_REVIEW_CARDS = "data_model/flashcard/flashcard/review_card";

    String API_UPDATE_FLASH_CARD = "data_model/flashcard/flashcard/update_flashcard";

    String API_CARD_SUSPEND = "data_model/flashcard/flashcard/card_suspend";

    String API_CARD_BOOKMARK = "data_model/flashcard/flashcard/card_bookmark";

    String API_READ_CARD = "data_model/flashcard/flashcard/read_card";

    String API_REVIEW_CARD_SUBDECK = "data_model/flashcard/flashcard/review_card_subdeck";

    String API_REVIEW_CARD_DECK = "data_model/flashcard/flashcard/review_card_deck";

    String API_SIDEBAR = "data_model/flashcard/flashcard/sidebar";

    String API_GET_STATES = "data_model/user/registration/states";

    String API_GET_CITIES = "data_model/user/registration/cities";

    String API_GET_COLLEGES = "data_model/user/registration/colleges";

    String API_GET_COUNTRIES = "data_model/user/registration/countries";

    String API_GET_CHANGE_DAMS_PASSWORD = "data_model/user/registration/change_dams_password";

    String API_GET_UPDATE_COLLEGE_INFO = "data_model/user/registration/update_college_info";

    String API_ADD_NEW_CARD = "data_model/flashcard/flashcard/add_new_card";

    String API_ADD_CARD_SUBDECK = "data_model/flashcard/flashcard/add_card_subdeck";

    String API_GET_PROGRESS = "data_model/flashcard/flashcard/progress";

    String API_GET_PROGRESS_FORCAST = "data_model/flashcard/flashcard/forcast";

    String API_GET_SUBJECT_FORCAST = "data_model/flashcard/flashcard/subject_forcast";

    String API_GET_SUBJECT_WISE_CARD_PROGRESS = "data_model/flashcard/flashcard/subject_wise_card_progress";


    /*################################################

        Videos

    ################################################*/
    String API_GET_SINGLE_VIDEO_DATA = "data_model/video/Video_channel/get_single_video_data";
    String API_GET_SINGLE_CAT_VIDEO_DATA = "data_model/video/Video_channel/get_videos_for_tag_list";

    String API_GET_POST_COMMENT = "data_model/video/video_comment/get_video_comment";
    String API_ADD_VIDEO_COMMENT = "data_model/video/video_comment/add_comment";
    String API_LIKE_VIDEO = "data_model/video/Video_like/like_video";
    String API_DISLIKE_VIDEO = "data_model/video/Video_like/dislike_video";
    String API_ADD_VIEW_VIDEO = "data_model/video/Video_channel/add_video_counter";

    String API_DELETE_VIDEO_COMMENT = "data_model/video/video_comment/delete_comment";
    String API_EDIT_VIDEO_COMMENT = "data_model/video/video_comment/update_comment";


    //NOTIFICATION SETTINGS
    String API_GET_NOTIFICATION_SETTING = "data_model/user/registration/user_notification_settings";
    String API_EDIT_NOTIFICATION_SETTING = "data_model/user/registration/edit_user_notification";


    String API_GET_REWARD_TRANSACTION = "data_model/user/user_reward/get_reward_transaction";


    /*################################################

       CPR

    ################################################*/
    String API_GET_SUBSCRIPTION = "data_model/cpr/course/get_single_course_info_raw";
    String API_GET_COMPLETE_INFO_CUSTOM_TEST_SERIES = "data_model/courses/Custom_qbank/get_test_series_with_id_app";

    String API_STUDY_TAB_DATA = "data_model/courses/crs/getmst";

    String API_QBANK_COURSE_DATA = "data_model/courses/crs/get_qbankcourse";

    String API_CHECK_QBANK = "data_model/courses/Custom_qbank/check_qbank";

    String API_DVL_DATA = "data_model/courses/Dvl/get_dvl";

    //Ebook
    String API_GET_EBOOK_DATA = "data_model/courses/crs/get_ebook";

    //clear data
    String API_CLEAR_DATA = "data_model/courses/test_series/clear_data";

    //order history
    String API_GET_ORDER_HISTORY = "data_model/courses/My_courses/order_history";


}