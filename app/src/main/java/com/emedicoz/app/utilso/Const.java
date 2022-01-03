package com.emedicoz.app.utilso;

import android.graphics.Color;

import com.emedicoz.app.BuildConfig;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.internal.bind.JsonTreeReader;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Cbc-03 on 05/23/17.
 */

public interface Const {

    String DAMS_MOPUP_USER_ID = "72";
    String FAN_WALL_LIVE_USER_SHOW_ONLINE_CHILD = "176570";
    //    String TERMS_URL = "http://emedicoz.com/production_dams/terms_and_conditions.php";
    //    String PRIVACY_URL = "http://emedicoz.com/production_dams/privacy_policy.php";
//    String TERMS_URL = "https://www.emedicoz.com/home/tnc";
//    String PRIVACY_URL = "https://www.emedicoz.com/index.php/home/privacy_policy";
//    String TERMS_URL = "http://stgweb.emedicoz.com/terms_condition";
    String TERMS_URL = "https://emedicoz.com/terms_condition";
//    String PRIVACY_URL = "http://stgweb.emedicoz.com/privacy";
    String PRIVACY_URL = "https://emedicoz.com/privacy";
    String AGREEMENT_URL = "http://stgweb.emedicoz.com/web/affiliate_agreement";

    String GOOGLE_PREVIEW_DOC_URL = "https://docs.google.com/gview?embedded=true&url=";
    String REGISTRATION_URL_VSAT = "https://damsonline.in/RegistrationForm/NewRegistration.aspx?rv=wXqRQlikyVJ0lRZQ76183XTo1yk0GM2XUpZLWh6bVrA=";
    String REGISTRATION_URL_FTF = "https://damsonline.in/RegistrationForm/NewRegistration.aspx?rv=wXqRQlikyVJ0lRZQ76183Ya+pl+VFgssUpZLWh6bVrA=";

    String SNACK_BAR_TEXT_COLOR = "#FFFFFF";


    int[] color = {Color.parseColor("#F44336"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#E91E63"),
            Color.parseColor("#FF9800"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#FF5722"),
            Color.parseColor("#603e94"),
            Color.parseColor("#8C9EFF"),
            Color.parseColor("#00695C")};

    int[] numberPadColor = {Color.parseColor("#7e7e7e"),
            Color.parseColor("#00a651"),
            Color.parseColor("#f3333a"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#ff8b00")};

    int[] studyColor = {Color.parseColor("#ff0000"),
            Color.parseColor("#7030a0"),
            Color.parseColor("#f79646"),
            Color.parseColor("#00b0f0"),
            Color.parseColor("#00b050")};

    String AMAZON_S3_BUCKET_NAME_VIDEO = BuildConfig.AMAZON_S3_BUCKET;
    String AMAZON_S3_BUCKET_NAME_PROFILE_IMAGES = API.API_AMAZON_S3_BUCKET_NAME_PROFILE_IMAGES;
    String AMAZON_S3_BUCKET_NAME_FANWALL_IMAGES = API.API_AMAZON_S3_BUCKET_NAME_FAN_WALL_IMAGES;
    String AMAZON_S3_BUCKET_NAME_VIDEO_IMAGES = API.API_AMAZON_S3_BUCKET_NAME_VIDEO_IMAGES;
    String AMAZON_S3_BUCKET_NAME_FIREBASE_CHAT = API.AMAZON_S3_BUCKET_NAME_FIREBASE_CHAT;
    String AMAZON_S3_BUCKET_NAME_DOCUMENT = API.API_AMAZON_S3_BUCKET_NAME_DOCUMENT;
    String AMAZON_S3_BUCKET_NAME_FEEDBACK = API.API_AMAZON_S3_BUCKET_NAME_FEEDBACK;
    String AMAZON_S3_BUCKET_NAME_COMMENT = API.API_AMAZON_S3_BUCKET_NAME_COMMENT;
    String AMAZON_S3_FILE_NAME_CREATION = API.API_AMAZON_S3_FILE_NAME_CREATION;

    // PAYTM Credentials
    String PAYTM_MID = BuildConfig.PAYTM_MID;
    String PAYTM_WEBSITE = BuildConfig.PAYTM_WEBSITE;
    String CHANNELID = BuildConfig.PAYTM_CHANNEL_ID;
    String SERVER_TYPE = BuildConfig.PAYTM_SERVER_TYPE;
    String INDUSTRYTYPE_ID = BuildConfig.PAYTM_INDUSTRY_TYPE_ID;
    String CALLBACKURL = BuildConfig.PAYTM_CALLBACK_URL;


    String PARAMETER_SEP = "&";
    String PARAMETER_EQUALS = "=";

    String LAST_POS = "last_pos";
    String CHAPTER_POS = "chapter_pos";


    //OFFLINE DATA CONSTANTS
    String OFFLINE_VIDEOTAGS_DATA = "offline_videotag_data";
    String NOTIFICATION_DATA = "notification_data";
    String OFFLINE_FEEDS = "offline_feeds";
    String OFFLINE_COURSE = "offline_course";
    String OFFLINE_COURSE_SEE_ALL = "offline_course_see_all";
    String OFFLINE_SAVEDNOTES = "offline_savednotes";
    String OFFLINE_Landing = "offline_landing";
    String OFFLINE_DOWNLOADEDIDS = "offline_downloadedids";
    String COURSE_LOCK_STATUS = "course_lock_status";
    //VARIABLES FOR REFRESHING FEEDS IN CERTAIN CONDITIONS
    String POST = "post";
    String POST_CHAT_NODE = "post_chat_node";
    String ALREADY_TAGGED_PEOPLE = "already_tagged_people";
    String BOOKMARK_APPEND = "?bookmark_request=yes";
    String IS_WATCHER = "?is_watcher=";
    String IS_EXPERT = "?get_expert_post=1";
    String GET_ONLY_FEED = "?get_only_feed=";
    String IS_FOLLOWER_POST = "?get_follower_post=1";
    String FOLLOWER_ID = "follower_id";
    String FILE_NAME = "file_name";
    // global topic to receive app wide push notifications
    int REQUEST_TAKE_GALLERY_VIDEO = 2221;
    int REQUEST_TAKE_GALLERY_IMAGE = 2222;
    int REQUEST_TAKE_GALLERY_DOC = 23;

    // broadcast receiver intent filters
    String NOTIFICATION_CODE = "notification_code";
    String NOTIFICATION_COUNT = "notification_count";
    String CART_COUNT = "cart_count";
    String COMMENT_POST = "post_comment";
    String LIKE_POST = "post_like";
    String SHARE_POST = "post_share";
    String TAGGED_ON_POST = "post_tagged_on";
    String TAGGED_ON_COMMENT = "comment_tagged_on";
    String FOLLOWING_USER = "following";
    String POST_EXPERT = "post_for_expert";

    String DECK_ID = "deck_id";
    // id to handle the notification in the notification tray
    int NOTIFICATION_ID = 100;
    int NOTIFICATION_ID_BIG_IMAGE = 101;
    String IS_NOTIFICATION_BLOCKED = "is_notification_blocked";
    String SHARED_PREF = "ah_firebase";
    String FIREBASE_TOKEN_ID = "firebase_token_id";
    String PATH = "path";
    String TOPIC = "topic";
    String EXAMPREP = "examprep";
    String CONTENT_TYPE = "content_type";
    String LIST = "list";
    String EXAMPREPLAST = "exampreplast";
    String MAIN_ID = "main_id";
    String SINGLE_STUDY = "single_study";
    String COURSE = "course";
    String UPCOMING = "upcoming";
    String ONGOING = "ongoing";
    String MyCoursesDTO = "mycoursedto";
    String IS_FROM_COMMENT_ACTIVITY = "is_from_comment_activity";

    ////////////////////////////////
    String IMAGE_PATH = "image_path";
    String SIGNUP = "SIGNUP";
    String LOGIN = "LOGIN";
    String IS_USER_LOGGED_IN = "is_user_logged_in";
    String IS_USER_REGISTRATION_DONE = "is_user_reg_done";
    String IS_PHONE_VERIFIED = "is_phone_verified";
    String IS_FEED_STATUS = "is_feed_status";
    String QBANK_TEST_SERIES_ID = "test_series_id";
    String STUDY_TEST_SERIES_ID = "study_test_series_id";
    String DIALY_QUIZ_TEST_SERIES_ID = "daily_quiz_series_id";
    String USER_LOGGED_IN = "user_logged_in";
    String MASTER_FEED_HIT_RESPONSE = "master_feed_hit_response";
    String MASTER_REGISTRATION_HIT_RESPONSE = "master_registration_hit_response";
    String DEVICE_TYPE_ANDROID = "1";
    String SOCIAL_TYPE_TRUE = "1";
    String SOCIAL_TYPE_FALSE = "0";
    String MODERATOR_SELECTED_STREAM = "moderator_selected_stream";
    String VERSION_CODE = "version_code";

    String QUIZ_MODEL = "quiz_model";
    String QUIZ_RESULT = "QUIZ_RESULT";
    String QUIZ_START = "QUIZ_START";
    String BOOKMARK_LIST = "bookmark_list";
    String VIEW_ANALYSIS = "view_analysis";

    String TOTAL_TIMES = "times";
    String STATUS = "status";
    String MY_COURSE_RATING = "my_course_rating";
    String MY_COURSE_ID = "my_course_id";
    String DAILY_QUIZ_TEST = "daily_quiz_test";
    String QBANK_TEST_STATUS = "qbank_test_status";
    String STUDY_TEST_STATUS = "study_test_status";
    String QBANK_ATTEMPT = "qbank_attempt";
    String ACTION_BUTTON_START = "actionbuttonstart";
    String ACTION_BUTTON_PAUSE = "actionbuttonpause";
    String ACTION_BUTTON_COMPLETE = "actionbuttoncomplete";
    String IS_COMPLETE = "is_complete";
    String IS_STATE_CHANGE = "is_state_change";
    String IS_LANDING_DATA = "is_landing_data";
    String IS_STREAM_CHANGE = "is_stream_change";
    String TRUE = "true";
    String FALSE = "false";
    String DATA = "data";
    String ERROR = "error";
    String TITLE = "title";
    String DESCRIPTION = "description";
    String CATEGORY = "category";
    String TITLE_DATA = "title_data";
    String COUNTER = "counter";
    String AUTH_MESSAGE = "auth_message";
    String AUTH_CODE = "auth_code";// this auth code is to check the session expiration of the user logged in.
    String EXPIRY_AUTH_CODE = "100100";// this auth code is to check the session expiration of the user logged in.
    String SOFT_UPDATE_APP_CODE = "111111";
    String FORCE_UPDATE_APP_CODE = "111112";
    String SHOW_HARD_POP_UP = "500005";
    String SHOW_SOFT_POP_UP = "500006";

    String EMAIL = "email";
    String PASSWORD = "password";
    String MOBILE = "mobile";
    String GENDER = "gender";
    String HLS = "hls";
    String DAMS_USERNAME = "dams_username";
    String DAMS_USER_TOKEN = "user_token";
    String DAMS_PASSWORD = "dams_password";
    String RESULT_TEST_SERIES = "result_test_series";
    String MARKING_SCHEME = "marking_scheme";
    //Referral String Constant
    String FIRST_NAME = "first_name";
    String LAST_NAME = "last_name";
    String REF_EMAIL = "email";
    String REF_PHONE = "phone";
    String PAN_CARD = "pancard";
    String AADHAAR_CARD = "aadhar";
    String REF_ADDRESS = "address";
    String POSTAL_CODE = "postal_code";
    String REF_COUNTRY = "country";
    String REF_STATE = "state";
    String REF_CITY = "city";
    String ACC_HOLDER_NAME = "account_holder_name";
    String BANK_NAME = "bank_name";
    String BANK_BRANCH_NAME = "bank_branch_name";
    String ACC_NUMBER = "account_number";
    String IFSC_CODE = "ifsc_code";

    String PAN_IMAGE = "pan_image";
    String AADHAR_IMAGE = "aadhar_image";
    String BANK_IMAGE = "bank_image";
    String INSTRUCTOR_ID = "profile_type";

    //UserBankInfoString
    String AFFILIATE_USER_ID = "affiliate_user_id";
    String BANK_INFO_ID = "bank_info_id";
    String PROFILE_IMAGE = "profile_image";
    String PAYMENT_HISTORY_PAGE = "page";
    String PER_PAGE_TOTAL = "per_page_total";
    String AFFILIATE_REFERRAL_CODE = "affiliate_referral_code";

    String SINGLE_COURSE_IDS = "single_course_ids";
    String CURRICULAM_IDS = "curriculam_ids";
    String COURSESEEALL_IDS = "course_see_all_ids";
    String SUBSCRIPTION_IDS = "subscription_ids";
    String SUBSCRIPTION_ID = "subscription_id";
    String SUBSCRIPTION_SELECTED = "subscription_selected";
    String DVL_DATA = "dvl_data";
    String CRS_FILES = "crs_files";


    //image from facebook
    String PICTURE = "picture";
    String URL = "url";
    String FILE_URL = "file_url";
    String VIDEO_CHANNEL = "video_channel";
    String FEED_SECTION = "feed_section";
    String COURSE_SECTION = "course_section";

    String IMGURL = "img_url";// image from google+
    String PROFILE_PICTURE = "profile_picture";// dams local variable

    String IS_DAMS_USER = "isdamsuser";
    String DAMS_TOKEN = "dams_tokken";
    String USER_TOKEN = "user_tokken";
    String LOGGED_IN_USER_TOKEN = "logged_in_user_token";

    String DEVICE_TYPE = "device_type";// 0 for none , 1 for android , 2 for IOS
    String DEVICE_TOKEN = "device_tokken";

    String LOCATION = "location";
    String LAST_KNOWN_LOCATION = "last_known_location";
    String IP_ADDRESS = "ip";
    String LAST_IP_ADDRESS = "ip_address";

    String AFFILIATE_CODE = "affiliate_code";
    String AFFILIATE_REFERRAL_CODE_BY = "affiliate_referral_code_by";
    String IS_SOCIAL = "is_social";// 0 for no , 1 for yes
    String SOCIAL_TYPE = "social_type";// 0 for none , 1 for facebook , 2 for gmail
    String SOCIAL_TOKEN = "social_tokken";
    String SOCIAL_TYPE_FACEBOOK = "1";
    String SOCIAL_TYPE_GMAIL = "2";
//    String SOCIAL_TYPE_LINKEDIN = "3";

    String OTP = "otp";
    String IS_OTP = "is_otp";
    String FRAG_TYPE = "frag_type";
    String FRAG_TYPE_STUDY = "frag_type_study";
    String finalResponse = "finalResponse";

    String SUBJECT_DATA = "subject data";
    String moduleData = "moduleData";
    String TOPIC_DATA = "topic data";
    String PEOPLE_LIST_FEEDS = "people_list_feed";
    String PEOPLE_LIST_COMMONS = "people_list_common";
    String PEOPLE_LIST_FEEDS_COMMONS = "people_list_feeds_common";
    String COMMON_PEOPLE_TYPE = "common_people_type";
    String YOUTUBE_ID = "youtube_id";

    //CONSTANTS FOR THE FRAGMENTS
    String CHANGEPASSWORD = "ChangePassword";
    String FORGETPASSWORD = "forgetpassword";
    String FORGETPASSWORDDAMS = "forgetpassworddams";
    String MOBILEVERIFICATION = "MobileVerification";
    String OTPVERIFICATION = "OtpVerification";
    String REGISTRATION = "registration";
    String CHANGE_STREAM = "change_stream";
    String ADD_PODCAST = "add_podcast";
    String COURSES = "Courses";
    String ALLCOURSES = "All Courses";
    String LEADERBOARD = "My Scorecard";
    String DAILY_QUIZ = "Daily Challenge";
    String DAILY_CHALLENGE = "Daily Challenge";
    String REWARDPOINTS = "Reward Points";
    String SELECT_SUBJECT = "Select Subject";
    String SELECT_TOPIC = "Select Topic";
    String SELECT_MODE = "Select Mode";
    String SELECT_TAG = "Select Tag";
    String MYCOURSES = "My Courses";
    String MYSCORECARD = "My Scorecard";
    String LIVE_CLASSES = "Live Classes";
    String MYQBANK = "My QBank";
    String MYTEST = "My Test";
    String MYCART = "My Cart";
    String PODCAST = "Podcast";
    String QBANK = "QBank";
    String MYORDERS = "My Orders";
    String MYDOWNLOAD = "My Downloads";
    String FEEDS = "Feeds";
    String SPECIALITIES = "Specialities";
    String SAVEDNOTES = "Saved Notes";
    String MY_BOOKMARKS = "My Bookmarks";
    String BOOKMARKS = "My BookMarks";
    String Cpr = "CPR";
    String VATHAM_ID = "vatham_id";
    String IS_PURCHASED = "is_purchased";
    String TIMER = "timer";
    String CPR_INVOICE = "cpr_invoice";
    String FEEDBACK = "Feedback";
    String NEWS_AND_ARTICLE = "News And Article";
    String VIDEOS = "Videos";
    String NOTES_TEST = "Notes & Test";
    String RATEUS = "Rate Us";
    String APPSETTING = "App Settings";
    String TERMS = "Terms And Condition";
    String TEST_QUIZ = "test_quiz";
    String PRIVACY = "Privacy Policy";

    String LOGOUT = "Logout";
    String PROFILE = "PROFILE";
    String COMMENT = "comment";
    String REWARD_TRANSACTION_FRAGMENT = "reward_transaction_fragment";
    String COMMENT_LIST = "comment_list";
    String NOTIFICATION = "notification";
    String YOUTUBE = "youtube";
    String VIMEO_ID = "vimeo_id";
    String INVITEDBY = "invitedBy";
    String POSTID_REFFERED = "postId";
    String REFER_CODE = "refer_code";
    String SEEALL_INSTRUCTOR_COURSE = "instructor_course";

    String SUBTITLECOURSE = "subtitlecourse";
    String SUBTITLE = "subtitle";
    String FEED_PREFERENCE = "feedpreference";
    String IS_PROFILE_CHANGED = "is_profile_changed";

    //HEADER INFORMATION
    String DEVICE_ID = "device_id";
    String SESSION_ID = "session_id";
    String SETUP_VERSION = "setup_version";
    String DEVICE_INFO = "device_info";

    //CONSTANTS FOR THE FRAGMENT MANAGER TO ADD TO THE STACK AND RETREIVE
    String REGISTRATIONFRAGMENT = "RegistrationFragment";
    String CHANGESTREAMFRAGMENT = "ChangeStreamFragment";
    String SUBSTREAMFRAGMENT = "subStreamFragment";
    String SPCIALISATIONFRAGMENT = "specializationFragment";
    String INTERESTEDCOURSESFRAGMENT = "CourseInterestedInFragment";

    String NO_INTERNET = "Please check your internet connection.";

    //FEEDS CONSTANTS
    String COMMENT_ID = "comment_id";
    String POST_ID = "post_id";
    String POST_TYPE = "post_type";
    String MESSAGE_TARGET = "message_target";
    String PARENT_ID = "parent_id";
    String AFFILIATE_SIGNED = "affiliate_signed";
    String POST_TEXT_TYPE_TEXT = "text";
    String POST_TEXT_TYPE_YOUTUBE_TEXT = "youtube_text";

    String POST_TYPE_LETS_TALK = "user_post_type_lets_talk";
    String POST_TYPE_LIVE_STREAM = "user_post_type_livestream";
    String POST_TYPE_MCQ = "user_post_type_mcq";
    String POST_TYPE_NORMAL = "user_post_type_normal";
    String POST_TYPE_PEOPLEYMK = "user_post_type_people";
    String POST_TYPE_MEET_THE_EXPERT = "user_post_type_expert_people";
    String POST_TYPE_SUGGESTED_VIDEOS = "user_post_type_suggested_videos";
    String POST_TYPE_SUGGESTED_COURSES = "user_post_type_suggested_courses"; // need to change
    String POST_TYPE_BANNER = "user_post_type_banner";

    String POST_COMMENT = "comment";

    String POST_FRAG = "post_frag";
    String COMMON_PEOPLE_LIST = "like_list";
    String COMMON_PEOPLE_VIEWALL = "list_view_all";
    String COMMON_EXPERT_PEOPLE_VIEWALL = "list_expert_view_all";
    String FOLLOW_THE_EXPERT_FIRST = "follow_the_expert_first";

    String VID_IDS = "video_ids";
    String PDF_IDS = "pdf_ids";
    String EPUB_IDS = "epub_ids";
    //POST MCQ CONSTANTS
    String USER_ID = "user_id";// primary id of logged in user
    String USER_FOR = "user_for";
    String COLOR_CODE = "color_code";// primary id of logged in user
    String LAST_POST_ID = "last_post_id";//last post id for pagination
    String LAST_FOLLOW_ID = "last_follow_id";//last post id for followers/ following
    String LAST_LIKE_ID = "last_id";//last like id for pagination
    String SEARCH_TEXT = "search_text";//to add the text search for feeds
    String SEARCH_CONTENT = "search_content";//to add the text search for videos
    String LAST_COURSE_ID = "last_course_id";//last course id for search course pagination
    String LAST_ACTIVITY_ID = "last_activity_id";//last notification id for pagination
    String LAST_COMMENT_ID = "last_comment_id";//last comment id for pagination
    String LAST_REVIEW_ID = "last_review_id";//last review id for pagination on Instructor
    String LAST_ID = "last_id";//last review id for pagination on courseReviews // also to be used for the pagination in getting the list of reward points
    String LAST = "last";
    String QUESTION = "question";
    String QUIZ_COURSE = "quiz_course";
    String ANSWER_ONE = "answer_one";
    String ANSWER_TWO = "answer_two";
    String ANSWER_THREE = "answer_three";
    String ANSWER_FOUR = "answer_four";
    String ANSWER_FIVE = "answer_five";
    String RIGHT_ANSWER = "right_answer";
    String TAG_VIDEO = "tag_video";
    String QUIZSTATUS = "quiz_status";
    String RESUME = "resume";
    String FINISH = "finish";
    String user_id = "user_id";

    String COURSE_LIST = "course_list";

    String LINK = "link";
    String IMAGE = "image";
    String VIDEO = "video";

    //Todo course File Type for Download & Delete purpose //
    String VIDEO_Courses = "video";
    String PDF = "pdf";
    String PPT = "ppt";
    String DOC = "doc";
    String EPUB = "epub";
    String TEST_DATA = "Test data";
    String TEST_COURSE = "test_course";
    String QBANK_COURSE = "qbank_course";
    String XLS = "xls";
    String AUDIO = "audio";
    String TEST_EPUB_PDF = "test_epub_pdf";

    String IMAGES = "images";
    String POSITION = "position";
    String FILE = "file";
    String TAGLIST_OFFLINE = "taglist_offline";
    String TAG_PEOPLE = "tag_people";
    String REMOVE_TAG_PEOPLE = "remove_tag_people";
    String POST_TAG = "post_tag";
    String DELETE_META = "delete_meta";
    String YOUTUBE_VIDEO = "youtube_video";

    String TEXT = "text";
    String VIDEO_LINK = "video_link";
    String VIDEO_STREAM = "stream";
    String VIDEO_LIVE = "live";
    String VIDEO_LIVE_MULTI = "live_multi";

    //REGISTRATION COSTANTS
    String MASTER_ID = "master_id";
    String MASTER_ID_LEVEL_ONE = "master_id_level_one";
    String MASTER_ID_LEVEL_TWO = "master_id_level_two";
    String OPTIONAL_TEXT = "optional_text";
    String INTERESTED_COURSE = "interested_course";
    String COUNTRY_CODE = "c_code";

    //VIDEOS COSTANTS
    String VIDEO_ID = "video_id";
    String LAST_VIDEO_COMMENT_ID = "last_comment_id";
    String SUB_CAT = "sub_cat";
    String SORT_BY = "sort_by";
    String LAST_VIDEO_ID = "last_video_id";
    String PAGE_SEGMENT = "page_segment";
    String VIEWS = "views";
    String PODCAST_ID = "podcast_id";


    String OPTION = "option";
    String OPTION_VALUE = "option_value";
    String RESULT_AS = "resultAs";
    String LEARNERS = " Learners";
    String LEARNER = " Learner";
    String SEEALL_COURSE = "seeall_course_list";
    String SEEALL_COURSE_LEFT = "seeall_course_list_left";
    String COURSE_CATEGORY = "course_categry";
    String CATEGORY_ID = "cat_id";
    String COURSE_ID = "course_id";
    String COURSE_TYPE = "course_type";
    String IS_COMBO = "is_combo";
    String COURSE_PRICE = "course_price";
    String COURSE_DISCOUNT = "course_discount";
    String SINGLE_COURSE = "single_course";
    String DETAIL_COMBO = "detail_combo";
    String COMBO_COURSE = "combo_course";
    String RATING = " Ratings";
    String COURSE_DESC = "course_des";
    String REVIEWS = " Reviews";
    String INSTR_REVIEWS = " Instr_Reviews";
    String REVIEW = " Review";
    String FAQ = "faq";
    String INSTR = "instructor";
    String INSTR_ID = "instructor_id";
    String CURRICULUM = "curriculum";
    String RATINGS = "rating";
    String RESULT_SCREEN = "resultscreen";
    String RESULT_SUBJECT_WISE = "resultsubjectwise";
    String RESULT_AWAIT = "resultawait";
    String COURSE_INIT_PAYMENT_TOKEN = "pre_transaction_id";
    String COURSE_FINAL_PAYMENT_TOKEN = "post_transaction_id";
    String CHECK_SUM = "checksumhash";
    String TESTSERIES_ID = "test_series_id";
    String TESTSERIES = "test_series";
    String TESTSERIESBASE = "test_series_base";
    String TIME_SPENT = "time_spent";
    String QUESTION_JSON = "question_dump";

    String RESUME_WINDOW = "resumeWindow";
    String RESUME_POSITION = "resumePosition";

    String BANNER_ID = "banner_id";
    String REASON_ID = "reason_id";
    String IS_PAYMENT_DONE = "is_payment_done";
    String COURSE_INVOICE = "course_invoice";
    String COUPON_CODE = "coupon_code";
    String ANSWER = "answer";
    String MCQ_ID = "mcq_id";
    String RESULT_BASIC = "RESULT_BASIC";
    String TOTAL_QUESTIONS = "TOTAL_QUESTIONS";

    //For test series
    String ORDER = "order";
    String QUESTIONID = "question_id";
    String ONSCREEN = "onscreen";
    String GUESS = "guess";
    String PART = "part";
    String SECTIONID = "section_id";
    String ISCORRECT = "is_correct";
    String ISMARKFORREVIEW = "review";

    //PAYTM CONSTANTS FOR EMEDICOZ PAYMENT CALLS
    String CALLBACK_URL = "CALLBACK_URL";
    String WEBSITE = "WEBSITE";
    String TXN_AMOUNT = "TXN_AMOUNT";
    String CHANNEL_ID = "CHANNEL_ID";
    String CUST_ID = "CUST_ID";
    String INDUSTRY_TYPE_ID = "INDUSTRY_TYPE_ID";
    String ORDER_ID = "ORDER_ID";
    String MID = "MID";
    String CHECKSUMHASH = "CHECKSUMHASH";
    String MOBILE_NO = "MOBILE_NO";
    String EMAIL_PAYTM = "EMAIL";

    String ADDTO_MYCOURSE = "Add to My Course";
    String ALL_REVIEWS = "All Reviews";
    String SERVER = "SERVER";
    String ORDERID = "ORDERID";
    String SOLUTION = "Solution";
    String RE_ATTEMPT = "re_attempt";
    String POINTS_RATE = "points_rate";
    String PENALTY = "penalty";
    String TOTAL_PRICE = "total_price";
    String POINTS_USED = "points_used";
    String PAYMENT_MODE = "payment_mode";
    String PAYMENT_META = "payment_meta";
    String IS_COMLETE_PAYMENT = "is_complete_payment";
    String PAYMENT_ATTEMPT = "attempt";
    String COUPON_APPLIED = "coupon_applied";
    String TAX = "tax";
    String CONTEXT = "context";
    String CURRENCY_INDIAN = "currency_indian";
    String ALLCATEGORY = "all_category";
    String PAY_VIA = "pay_via";
    String CCAVENUE = "CCAVENUE";
    String PAYTM = "PAY_TM";
    String QBANK_RESULT = "QBANK_RESULT";
    String SPACE = "                                                                              ";  // to concatinate with spaces
    String ANDROID_VERSION = "Android Version :- ";
    String DEVICE_MODEL = "Model Name :- ";
    String APP_VERSION = "App Version :- ";
    String HTML = "<head><head></head><body><center><br>Thank you for shopping with us. Your credit card has been charged and your transaction is successful. We will be shipping your order to you soon.<br><br><table cellspacing=\"4\" cellpadding=\"4\"><tbody><tr><td>order_id</td><td>2844712755a1ace8292adde3e89c69</td></tr><tr><td>tracking_id</td><td>307003926976</td></tr><tr><td>bank_ref_no</td><td>1530276789364</td></tr><tr><td>order_status</td><td>Success</td></tr><tr><td>failure_message</td><td></td></tr><tr><td>payment_mode</td><td>Net Banking</td></tr><tr><td>card_name</td><td>AvenuesTest</td></tr><tr><td>status_code</td><td>null</td></tr><tr><td>status_message</td><td>Y</td></tr><tr><td>currency</td><td>INR</td></tr><tr><td>amount</td><td>2360.0</td></tr><tr><td>billing_name</td><td>a</td></tr><tr><td>billing_address</td><td>a</td></tr><tr><td>billing_city</td><td>a</td></tr><tr><td>billing_state</td><td>a</td></tr><tr><td>billing_zip</td><td>a</td></tr><tr><td>billing_country</td><td>India</td></tr><tr><td>billing_tel</td><td>9971530690</td></tr><tr><td>billing_email</td><td>aa@gmail.com</td></tr><tr><td>delivery_name</td><td>a</td></tr><tr><td>delivery_address</td><td>a</td></tr><tr><td>delivery_city</td><td>a</td></tr><tr><td>delivery_state</td><td>a</td></tr><tr><td>delivery_zip</td><td>aa</td></tr><tr><td>delivery_country</td><td>India</td></tr><tr><td>delivery_tel</td><td>9879848944</td></tr><tr><td>merchant_param1</td><td></td></tr><tr><td>merchant_param2</td><td></td></tr><tr><td>merchant_param3</td><td></td></tr><tr><td>merchant_param4</td><td></td></tr><tr><td>merchant_param5</td><td></td></tr><tr><td>vault</td><td>N</td></tr><tr><td>offer_type</td><td>null</td></tr><tr><td>offer_code</td><td>null</td></tr><tr><td>discount_value</td><td>0.0</td></tr><tr><td>mer_amount</td><td>2360.0</td></tr><tr><td>eci_value</td><td>null</td></tr><tr><td>retry</td><td>N</td></tr><tr><td>response_code</td><td>0</td></tr><tr><td>billing_notes</td><td></td></tr><tr><td>trans_date</td><td>29/06/2018 18:23:28</td></tr><tr><td>bin_country</td><td>       </td></tr></tbody></table><br></center></body></head>";
    String TEST_SERIES_ID = "testseriesid";
    String STATE = "state";
    String LAST_VIEW = "last_view";
    String QUESTION_DUMP = "question_dump";
    String STARTMODULE = "start_module";
    String NO_OF_CARD = "no_of_card";
    String FLASH_CARD_LIST = "flash_card_list";
    String CARD_ID = "card_id";
    String BOOKMARK = "bookmark";
    String SUSPEND = "suspend";
    String NEXT_TIME = "next_time";
    String SUBDECK = "subdeck";
    String DECK = "deck";
    String SUBDECK_ID = "subdeck_id";
    String SUBTOPIC = "subtopic";
    String LEFT_CARDS = "left_cards";
    String REVIEWED_CARD = "reviewed_today";
    String STUDIED_CARD_TIME = "studied_card_time";
    String CITY = "city";
    String COLLEGE = "college";
    String CHANGE_PASSWORD = "change_password";
    String USERNAME = "username";
    String OLD_PASSWORD = "oldpassword";
    String NEW_PASSWORD = "newpassword";
    String ALL_CARDS = "all_cards";
    String COUNTRY_ID = "country_id";
    String CITY_ID = "city_id";
    String STATE_ID = "state_id";
    String COLLEGE_ID = "college_id";
    String COLLEGE_NAME = "college_name";
    String COUNTRY_NAME = "country_name";
    String COUNTRY = "country";
    String CARD_SEQUENCE_STATUS = "card_sequence_status";
    String RANDOM = "random";
    String PREVIOUS = "previous";
    String NEXT = "next";
    String PROGRESS = "progress";
    String TIME_TAKEN = "time_taken";
    String DECKS = "decks";
    String ALL = "ALL";
    String REVIEWED_TODAY_TIME = "reviewed_today_time";
    String ANDROID = "android";
    String ANIN = "ANIN";
    String NO_INTERNET_MSG_TEST = "failed to connect to /35.154.208.22:80";
    String UNANSWERED = "unanswered";
    String DVL_SECTION = "dvl_section";
    String CRS_SECTION = "crs_section";
    String PACKAGES = "pakages";
    String GST = "gst";
    String POINTS_CONVERSION_RATE = "points_conversion_rate";
    String EBOOK_SECTION = "ebook_section";
    String ALL_FEED = "all_feed";
    String FACULTY_FEED = "faculty_feed";
    String FOLLOW_FEED = "follow_feed";
    String DRAWER = "drawer";
    String CATEGORY_INFO = "category_info";
    String APP_VIEW_TYPE = "app_view_type";
    String SHOW_MORE = "Show More";
    String TOPICS = "topics";
    String CREATE_CUSTOM_MODULE = "Create Custom Module";
    String OUT_OF = "out of ";
    String RESPONSE = "response";
    String CONFIRM_DELETE_DOWNLOAD = "Are you sure you want to delete the download?";
    String DELETE_DOWNLOAD = "Delete Download";
    String QUESTION_BANK = "QUESTION_BANK";
    String JUST_NOW = "Just Now";
    String VIEW = " View";
    String PURCHASED = "purchased";
    String RENEW = "renew";
    String FREE = "free";
    String PAID = "paid";
    String VIDEO_TYPE = "video_type";
    String VIDEO_PREVIEW_DURATION = "video_preview_duration";
    String COURSE_VIDEO_TYPE = "1";


    String REG_COURSE = "reg_course";
    String REG_STUDENT = "reg_student";
    String REG_PAYMENT = "reg_payment";
    String F_2_F = "f_2_f_classes";
    String VSAT = "vsat_classes";
    String ONE_TIME = "one_time";
    String INSTALLMENT_PLAN = "installment_plan";
    String DARK_MODE = "dark_mode";
    String ORDER_DETAIL = "order_detail";
    String SUCCESSFULLY_PAYMENT_DONE = "successfully_payment_done";
    String VIEW_DETAILS = "view_details";
    String INVOICE = "invoice";
    String COMBO = "combo";
    String TRANSACTION_STATEMENT = "transaction_statement";
    String SUBSCRIPTION_CODE = "subscription_code";
    String STATEMENT = "statment";
    String GST_INCLUDE = "gst_include";
    String NON_DAMS = "non_dams";
    String CHILD_COURSES = "child_courses";
    String EXPIRY = "expiry";
    String DISCOUNT_AMOUNT = "discount_amount";


    String TOTAL = "total";
    String TEST = "test";
    String NETWORK_CALL_FOR_BOOKMARK = "Network call for bookmark";
    String REDEEM = "redeem";
    String COUPON = "coupon";
    String REFERRAl = "referral";
    String IS_MODULE_START = "is_module_start";
    String LAYER = "layer";
    String CAT_ID = "cat_id";
    String SYNC_COMPLETE = "sync_complete";
    String AFFILIATE = "affiliate";
    String RECENT_COURSE_ID = "recent_course_ids";
    String RECENTLY_VIEWED_COURSE = "recently_viewed_course";
    String STUDENT_ARE_VIEWING = "student_are_viewing";
    String MORE_COURSE = "more_courses";
    String INSTRUCTOR_COURSES = "instructor_Courses";
    String INSTRUCTOR_NAME = "instructor_Name";
    String ERROR_ALERT_INTENT_FILTER = "com.emedicoz.app.INTENT_DISPLAY_ERROR";
    String VIDEO_DATA = "video_data";
    String NOTES_DATA = "notes_data";
    String EPUB_DATA = "epub_data";
    String SEEALL_CLASSES = "seeall_classes";
    String IS_FROM_LIVE_COURSE = "isFromLiveCourse";
    String CHAT_NODE = "chat_node";
    String CONTACT = "contact";
    String ACADEMIC = "academic";
    String FOLLOW = "follow";
    String USER = "user";
    String EPISODE = "episode";
    String CHANNEL = "channel";
    String ADDRESS_ID = "address_id";
    String NEWSARTICLE ="News And Articles" ;
    String CARD = "card";

    String BOOK_ID = "book_id";// primary id of logged in user
    String BOOK_TYPE = "type";// primary id of logged in user


    class AvenuesParams {
        public static final String COMMAND = "command";
        public static final String ACCESS_CODE = "access_code";
        public static final String MERCHANT_ID = "merchant_id";
        public static final String ORDER_ID = "order_id";
        public static final String AMOUNT = "amount";
        public static final String CURRENCY = "currency";
        public static final String ENC_VAL = "enc_val";
        public static final String REDIRECT_URL = "redirect_url";
        public static final String CANCEL_URL = "cancel_url";
        public static final String RSA_KEY_URL = "rsa_key_url";
    }

    class PollsSocket {

        public static final String GET_QUESTION = "get_question";
        public static final String GET_RESULT = "get_result";
    }

    class Deeplink {

        public static final String COURSE_INFO = "course-info";
        public static final String LIVE_CLASS_VIDEO = "live-class-video";
    }


    public static final String personalDetail = "personalDetail";
    public static final String bankDetail = "bankDetail";
}

