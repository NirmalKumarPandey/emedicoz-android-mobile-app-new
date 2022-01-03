package com.emedicoz.app.utilso;

import com.emedicoz.app.BuildConfig;

public class Constants {
    public static String BASE_API_URL = BuildConfig.BASE_URL;
    public static String BASE_DOMAIN_URL = BuildConfig.BASE_URL_DOMAIN;
    public static final String PUBNUB_PUBLISH_KEY = "pub-c-9144f1f3-ff50-420f-845c-b99117b582eb";     // Replace with your publish key
    public static final String PUBNUB_SUBSCRIBE_KEY = "sub-c-77e03e40-bfa0-11ea-8089-3ec3506d555b";   // Replace with your subscribe key

    public static final String CHANNEL_NAME = "EMEDICOZ_APP";     // replace with more meaningful channel name
    public static final String MULTI_CHANNEL_NAMES = "0xCDCDCDCD,0xEFEFEFEF,0xGHGHGHGH";    // ditto

    public static final String DATASTREAM_PREFS = "com.pubnub.example.android.datastream.pubnubdatastreams.DATASTREAM_PREFS";
    public static final String DATASTREAM_UUID = "com.pubnub.example.android.datastream.pubnubdatastreams.DATASTREAM_UUID";
    public static final int MAX_TRY_VIDEO = 5;
    public static final String SUBSCRIPTION = "subscriptions";
    public static String IS_PURCHASED;
    public static String EXTRA_VIDEO_URL = "EXTRA_VIDEO_URL";
    public static String EXTRA_PLAYBACK_POSITION_MS = "EXTRA_PLAYBACK_POSITION_MS";
    public static String EXTRA_PLAYBACK_QUALITY = "EXTRA_PLAYBACK_QUALITY";
    public static String EXTRA_PLAYBACK_SPEED_TEXT = "EXTRA_PLAYBACK_SPEED_TEXT";

    public static String STATE_PLAYBACK_POSITION_MS = "STATE_PLAYBACK_POSITION_MS";
    public static String EXTRA_PLAYBACK_SPEED_PARAM = "EXTRA_PLAYBACK_SPEED";
    public static String EXTRA_PORTRAIT_MODE = "EXTRA_PORTRAIT_MODE";
    public static String EXTRA_LIVE_VIDEO = "EXTRA_LIVE_VIDEO";

    public static final int MIGRATED_DOWNLOAD_ID = 3333;
    public static String COURSEID = "";
    public static String BOOKMARK_NAME = "";
    public static float RATING = 0;

    //add UX cam for monitoring key
    public static final String CAM_KEY = "mzw09hvkhztxxws";

    public static boolean videoStatus = true;
    public static boolean feedStatus = true;
    public static boolean masterHitStatus = true;
    public static boolean courseStatus = true;

    public static boolean isNewPostAdded = false;
    public static boolean isCommentRefreshed = false;
    public static boolean isPostDeleted = false;
    public static boolean isPostUpdated = false;

    public static class TestType {
        public static final String TEST = "test";
        public static final String Q_BANK = "qbank";
        public static final String QUIZ = "QUIZ";
        public static final String DAILY_CHALLENGE = "DQUIZ";
        public static final String GRAND_TEST = "Grand Test";
        public static final String Mock_Test = "Mock Test";
        public static final String Subject_Wise_Test = "Subject Wise Test";
        public static final String Class_Wise_Test = "Class Wise Test";
        public static final String APP_QUIZ = "dquiz";
        public static final String APP_COURSE = "course";

    }

    public static class StudyType {
        public static final String TEST = "Test Series";
        public static final String DQB = "DQB";
        public static final String CRS = "CRS";
        public static final String EBOOK = "EBook";
        public static final String PREVIOUS_PAPER = "Previous Paper";

        public static final String QBANKS = "qbanks";

        public static final String TESTS = "tests";


    }

    public static class RecordedCourseTemplates {
        public static final int VERTICAL_LIST = 1;
        public static final int HORIZONTAL_LIST = 2;

        public static final int TRENDING_COURSES = 1;
        public static final int STUDENT_VIEWING_COURSES = 2;
        public static final int BEST_SELLING_COURSES = 3;
        public static final int COURSES_AS_PER_SEARCH = 5;

        public static final int COURSE_CATEGORY_TILES = 10;
        public static final int CATEGORIES = 11;
        public static final int IMAGE_BANNERS = 12;
        public static final int RECENTLY_VIEWED_COURSES = 13;

        public static final int LIVE_CLASSES = 14;
        public static final int BROWSE_COMBO_PLAN = 15;
        public static final int BOOK_YOUR_SEAT = 16;

        public static final int MY_LIVE_COURSES = 20;
        public static final int UPCOMING_LIVE_CLASSES = 21;
        public static final int UPCOMING_LECTURE_TODAY = 22;
        public static final int UPCOMING_CLASS = 23;
        public static final int ONGOING_CLASS = 24;


    }

    public static class QuestionType {
        public static final String SINGLE_CHOICE = "SC";
        public static final String MULTIPLE_CHOICE = "MC";
        public static final String MATCH_THE_FOLLOWING = "MF";
        public static final String MULTIPLE_TRUE_FALSE = "MTF";
        public static final String EXTENDED_MATCHING = "EM";
        public static final String TRUE_FALSE = "TF";
        public static final String SEQUENTIAL_ARRANGEMENTS = "SA";
        public static final String MULTIPLE_COMPLETION = "MCT";
        public static final String REASON_ASSERTION = "RA";
        public static final String FILL_IN_THE_BLANKS = "FB";
    }

    public static class Extras {
        public static final String COURSE_ID = "course_id";
        public static final String TEST_QUIZ_ACTION = "TEST_QUIZ_ACTION";
        public static final String QUES_NUM = "QUES_NUM";
        public static final String RATING = "RATING";
        public static final String OPEN_FROM = "OPEN_FROM";
        public static final String TOTAL_QUESTIONS = "TOTAL_QUESTIONS";
        public static final String SUBJECT_NAME = "subject_name";
        public static final String CUSTOM = "CUSTOM";
        public static final String SOLUTION = "SOLUTION";
        public static final String DAILY = "DAILY";
        public static final String BASIC_INFO = "BASIC_INFO";
        public static final String QUIZ_TYPE = "QUIZ_TYPE";
        public static final String SCREEN_TYPE = "SCREEN_TYPE";
        public static final String TITLE_NAME = "TITLE_NAME";
        public static final String SUBJECT_ID = "subject_id";
        public static final String TEST_TYPE = "TEST_TYPE";
        public static final String STUDY_TYPE = "STUDY_TYPE";
        public static final String POSITION = "POSITION";
        public static final String SUB_ID = "sub_id";
        public static final String STREAM_ID = "streamid";
        public static final String STREAM = "stream";
        public static final String BOOKMARK_NAME = "bookmarkname";
        public static final String TAG_NAME = "tagname";
        public static final String QUESTION_LIST = "question_list";
        public static final String TEST_VIDEO_QUIZ_ACTION = "TEST_VIDEO_QUIZ_ACTION";
        public static final String QUESTION_BANK_COURSE_ID = "385";
        public static final String QUESTION_BANK_CAT_ID = "19";
        public static final String TEST_SERIES_CAT_ID = "43";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String PAGE = "page";
        public static final String BOOKMARK = "Bookmark";
        public static final String NAME = "name";
        public static final String ID = "id";
        public static final String NAME_OF_TAB = "nameoftab";
        public static final String TAG_ID = "tag_id";//to add tag related search
        public static final String TOPIC_ID = "topic_id";
        public static final String TOPIC_TITLE = "topic_title";
        public static final String TYPE = "type";
        public static final String Q_TYPE_DQB = "q_type_dqb";
        public static final String MESSAGE = "message";
        public static final String DATE = "date";
        public static final String TAB_NAME = "TAB_NAME";
        public static final String COURSE_CAT = "course_cat";
        public static final String BOOKMARKED_COUNT = "bookmarked_count";
        public static final String CATEGORY_INFO_ID = "CATEGORY_INFO_ID";
        public static final String VIDEO_URL = "videoUrl";
        public static final String AFFILIATE_USER_ID = "AFFILIATE_USER_ID";
        public static final String ALL_FAQ = "ALL FAQ";
        public static final String ALL_REVIEW = "ALL REVIEW";
        public static final String REVIEW = "REVIEW";
        public static final String FAQ = "FAQ";
        public static final String ADAPTER_TYPE = "ADAPTER_TYPE";
        public static final String FOLLOWING = "following";
        public static final String FOLLOWERS = "followers";
        public static final String POST = "post";
        public static final String IS_PURCHASED = "is_purchased";
        public static final String TITLE_FRAG = "titlefrag";
        public static final String STATE = "STATE";
        public static final String DELETE_TEST = "delete_test";
        public static final String DELETE_DQB = "delete_dqb";
        public static final String DELETE_BOOKMARK = "delete_bookmark";
        public static final String CAT_ID = "cat_id";
        public static final String UPDATE_PROFILE = "update_profile";
        public static final String LIVE_POLL = "live_poll";
        public static final String LIVE_CHAT = "live_chat";
    }

    public static class ResultExtras {
        public static final String TEST_RESULT_DATE = "test_result_date";
        public static final String TEST_SERIES_NAME = "test_series_name";
        public static final String CORRECT_COUNT = "correct_count";
        public static final String INCORRECT_COUNT = "incorrect_count";
        public static final String NON_ATTEMPT = "non_attempt";
        public static final String ACCURACY = "accuracy";
        public static final String CUSTOM_TYPE = "CUSTOM_TYPE";
        public static final String TEXT_PHOTO = "TEXT_PHOTO";
        public static final String VIDEO = "VIDEO";
        public static final String COMPLETE = "COMPLETE";
        public static final String PAUSE = "PAUSE";
        public static final String START = "START";
        public static final String TEST_SEGMENT_ID = "test_segment_id";
        public static final String QUIZ_SEGMENT_ID = "QUIZ_SEGMENT_ID";
    }

    public static class Type {
        /**
         * this segment is used for getting bookmark data based on type
         */
        public static final String TEST_SERIES = "1";
        public static final String DQB = "2";
        public static final String DAILY_CHALLENGE = "3";

        /**
         * Course_Types for used while getting MyCourse,MyTest and MyQbank data from api because it takes parameter as 1,2,3
         */
        public static final String COURSE_TYPE_MY_COURSE = "1";
        public static final String COURSE_TYPE_MY_TEST = "2";
        public static final String COURSE_TYPE_OTHER = "3";

        /**
         * this is default value of stream_id if stream_id is null or empty
         */
        public static final String DEFAULT_STREAM_ID = "1";

        /**
         * BOOKMARK_Types for used while bookmarking test,qbank and daily quiz questions because it takes parameter as 1,2,3
         */
        public static final String BOOKMARK_TYPE_TEST = "1";
        public static final String BOOKMARK_TYPE_QUIZ = "2";
        public static final String BOOKMARK_TYPE_DQUIZ = "3";

        /**
         * this segment is used for replacing the fragment in study section based on type
         */
        public static final String PPE_KIT = "1";
        public static final String STUDY_TYPE_TEST = "2";
        public static final String STUDY_TYPE_DQB = "3";
        public static final String STUDY_TYPE_CRS = "4";
        public static final String STUDY_TYPE_EBOOK = "5";

        /**
         * SET_TYPE is used to check whether it's Test,Quiz ar Daily Challenge
         */
        public static final String SET_TYPE_TEST = "0";
        public static final String SET_TYPE_QUIZ = "1";
        public static final String SET_TYPE_DQUIZ = "2";

    }

    public static class TestStatus {
        public static final String ALL = "All";
        public static final String COMPLETED = "Completed";
        public static final String UNATTEMPTED = "Unattempted";
        public static final String PAUSED = "Paused";
        public static final String ACTIVE = "Active";
        public static final String UPCOMING = "Upcoming";
        public static final String MISSED = "Missed";
        public static final String CORRECT = "Correct";
        public static final String INCORRECT = "Incorrect";
        public static final String SKIPPED = "Skipped";
        public static final String BOOKMARKED = "Bookmarked";
    }

    public static class ChatExtras {

        public static final String CHAT_PALTFORM = "chat_platform";
        public static final String PUBNUB = "PUBNUB";
        public static final String FIREBASE = "FIREBASE";
        public static final String ONLINE = "online";
        public static final String PROFILE_PICTURE = "profile_picture";
        public static final String TIMESTAMP = "timestamp";
        public static final String SENDER = "sender";
        public static final String ERP_TOKEN = "erp_token";
        public static final String ANDROID = "android";
        public static final String PROFILE_PIC = "profile_pic";
    }

    public static class SharedPref {
        public static final String ALL = "All";
        public static String STREAM_ID = "streamid";

        public static String SEARCHED_QUERY = "searched_query";
        public static String COURSE_SEARCHED_QUERY = "course_searched_query";
        public static String USER_INFO = "user_info";

    }

    public static class FlashCardExtras {
        public static final String TIME_TAKEN = "time_taken";
        public static final String READ_CARD = "read_card";
        public static final String LONG_INTERVAL = "long_interval";
        public static final String CURRENT_INTERVAL = "current_interval";
        public static final String AVG = "avg";
    }

    public static class CustomModuleExtras {
        public static final String NUMBER_OF_QUESTION = "no_of_ques";
        public static final String DIFFICULTY_LEVEL = "defficulty_lvl";
        public static final String QUES_FROM = "ques_from";
        public static final String SUBJECT = "subject";
        public static final String TOPICS = "topics";
        public static final String TAGS = "tags";
        public static final String IS_TAG = "IS_TAG";
    }

    public static class DailyChallengeExtras {
        public static final String NO_OF_QUESTION = "NO_OF_QUESTION";
        public static final String TIME = "TIME";
        public static final String SUBJECT = "SUBJECT";
        public static final String RANK = "RANK";
    }

    public static class CourseType {
        public static final String PDF = "pdf";
        public static final String EPUB = "epub";
        public static final String VIDEO = "video";

    }

    public static class MY_DOWNLOAD_TABS {
        public static final String VIDEO = "Videos";
        public static final String PDF = "pdfs";
        public static final String EPUB = "epubs";
        public static final String PODCAST = "podcasts";

    }

    public static class OrderStatus {
        public static final String PENDING = "Pending";
        public static final String SUCCESSFUL = "Successful";
        public static final String FAILED = "Failed";
        public static final String REFUND_REQUEST = "Refund Request";
        public static final String REFUNDED = "Refunded";
    }

    public static class PastPaperExtras {
        public static final String EXAM_WISE = "EXAM_WISE";
        public static final String SUBJECT_WISE = "SUBJECT_WISE";
        public static final String PPE_DATA = "PPE_DATA";
    }

    public class ScreenName {
        public static final String DAILY_CHALLENGE = "DAILY_CHALLENGE";
        public static final String AFFILIATE = "AFFILIATE";
        public static final String FAN_WALL = "FAN_WALL";
        public static final String COURSES = "COURSES";
        public static final String PODCAST = "PODCAST";
        public static final String STUDY = "STUDY";
        public static final String APP_SETTING = "APP_SETTING";
    }
}
