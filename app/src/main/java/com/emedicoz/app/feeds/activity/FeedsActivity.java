package com.emedicoz.app.feeds.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.courses.fragment.MyCoursesFragment;
import com.emedicoz.app.courses.fragment.StudyFragment;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.feeds.fragment.FeedsFragment;
import com.emedicoz.app.login.activity.SignInActivity;
import com.emedicoz.app.modelo.Banner;
import com.emedicoz.app.modelo.Data;
import com.emedicoz.app.modelo.DownloadDataResp;
import com.emedicoz.app.modelo.Epub;
import com.emedicoz.app.modelo.Pdf;
import com.emedicoz.app.modelo.People;
import com.emedicoz.app.modelo.SyncReqParam;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.modelo.Videos;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.CourseCategory;
import com.emedicoz.app.recordedCourses.fragment.RecordedCoursesFragment;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.LiveCourseInterface;
import com.emedicoz.app.retrofit.apiinterfaces.RegFragApis;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.testmodule.activity.VideoTestBaseActivity;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.emedicoz.app.video.activity.VideoDetail;
import com.emedicoz.app.video.fragment.DVLFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.Const.COURSE;

public class FeedsActivity extends BaseABNavActivity {

    public static boolean isNewPostAdded = false;
    public static boolean isCommentRefreshed = false;
    public static boolean isPostDeleted = false;
    public static boolean isPostUpdated = false;
    final int REQUEST_READ_PHONE_STATE = 100;
    final int REQUEST_READ_PHONE_STATE1 = 101;
    public BroadcastReceiver mReceiver;
    public MasterFeedsHitResponse masterFeedsHitResponse;
    String frag_type = "";
    String frag_type_study = "";
    CourseCategory courseCategory;
    String type;

    private final List<String> arrVideo = new ArrayList<>();
    private final List<String> arrEpub = new ArrayList<>();
    private final List<String> arrPdf = new ArrayList<>();
    private Progress mProgress;


    public static void changeFollowingExpert(People people, int type) {
        MasterFeedsHitResponse masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();
        if (masterFeedsHitResponse != null && masterFeedsHitResponse.getExpert_list() != null
                && !GenericUtils.isListEmpty(masterFeedsHitResponse.getExpert_list())) {
            for (People ppl : masterFeedsHitResponse.getExpert_list()) {
                if (ppl.getId().equalsIgnoreCase(people.getId()))
                    ppl.setWatcher_following(type != 0);
            }
        }
        SharedPreference.getInstance().setMasterHitData(masterFeedsHitResponse);
    }

    public static void changeFollowingPeople(People people, int type) {
        MasterFeedsHitResponse masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();
        if (masterFeedsHitResponse != null && masterFeedsHitResponse.getPeople_you_may_know_list() != null
                && !GenericUtils.isListEmpty(masterFeedsHitResponse.getPeople_you_may_know_list())) {
            for (People ppl : masterFeedsHitResponse.getPeople_you_may_know_list()) {
                if (ppl.getId().equalsIgnoreCase(people.getId()))
                    ppl.setWatcher_following(type != 0);
            }
        }
        SharedPreference.getInstance().setMasterHitData(masterFeedsHitResponse);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("FeedsActivity", "destroyed");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (fragment instanceof ReferralSignUp || fragment instanceof HelpSupportFragment)
//            fragment.onActivityResult(requestCode, resultCode, data);

    }

    public Banner getBannerData() {

        masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();

        if (masterFeedsHitResponse != null && masterFeedsHitResponse.getBanner_list() != null
                && !GenericUtils.isListEmpty(masterFeedsHitResponse.getBanner_list())) {
            Random random = new Random();

            return masterFeedsHitResponse.getBanner_list().get(random.nextInt(masterFeedsHitResponse.getBanner_list().size()));

        }
        return null;
    }

    // type:
    // 1: if we need all the list(this will be shown in view all people page)
    // or
    // 2: only limited list as per the percentage(this will be shown to feeds)
    public ArrayList<People> getPeopleYMKData(int type) {

        masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();

        ArrayList<People> peopleArrayList = new ArrayList<>();
        if (masterFeedsHitResponse != null && masterFeedsHitResponse.getPeople_you_may_know_list() != null
                && !GenericUtils.isListEmpty(masterFeedsHitResponse.getPeople_you_may_know_list())) {
            int oneTimeCount = 0;
            if (type == 0) {
                if (masterFeedsHitResponse.getPeople_you_may_know_list().size() > 20)
                    oneTimeCount = masterFeedsHitResponse.getPeople_you_may_know_list().size() * 30 / 100; // 30% of total size of list will go at once.
                else
                    oneTimeCount = masterFeedsHitResponse.getPeople_you_may_know_list().size(); // if size of list is lest than 20 then we will take the same size as list
                int i;
                Collections.shuffle(masterFeedsHitResponse.getPeople_you_may_know_list());

                for (i = 0; i < oneTimeCount; i++) {
                    peopleArrayList.add(masterFeedsHitResponse.getPeople_you_may_know_list().get(i));
                }
            } else if (type == 1) {
                peopleArrayList.addAll(masterFeedsHitResponse.getPeople_you_may_know_list());
            }
            return peopleArrayList;
        }
        return peopleArrayList;

    }

    // type:
    // 1: if we need all the list(this will be shown in view all people page)
    // or
    // 2: only limited list as per the percentage(this will be shown to feeds)
    public ArrayList<People> getExpertPeopleData(int type) {
        masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();

        ArrayList<People> expertArrayList = new ArrayList<>();
        if (masterFeedsHitResponse != null && masterFeedsHitResponse.getExpert_list() != null
                && !GenericUtils.isListEmpty(masterFeedsHitResponse.getExpert_list())) {
            int oneTimeCount = 0;
            if (type == 0) {
                if (masterFeedsHitResponse.getExpert_list().size() > 20)
                    oneTimeCount = masterFeedsHitResponse.getExpert_list().size() * 30 / 100; // 30% of total size of list will go at once.
                else
                    oneTimeCount = masterFeedsHitResponse.getExpert_list().size(); // if size of list is lest than 20 then we will take the same size as list
                int i;
                Collections.shuffle(masterFeedsHitResponse.getExpert_list());
                for (i = 0; i < oneTimeCount; i++) {
                    expertArrayList.add(masterFeedsHitResponse.getExpert_list().get(i));
                }
            } else if (type == 1) {
                expertArrayList.addAll(masterFeedsHitResponse.getExpert_list());
            }

            // TODO to show Myself as Expert in "MEET the Expert"
            if (SharedPreference.getInstance().getLoggedInUser() != null &&
                    !TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getIs_expert()) &&
                    SharedPreference.getInstance().getLoggedInUser().getIs_expert().equals("1")) {

                People people = new People();
                people.setId(SharedPreference.getInstance().getLoggedInUser().getId());
                people.setName(SharedPreference.getInstance().getLoggedInUser().getName());
                people.setProfile_picture(SharedPreference.getInstance().getLoggedInUser().getProfile_picture());
                people.setFollowers_count(SharedPreference.getInstance().getLoggedInUser().getFollowers_count());
                people.setSpecification(SharedPreference.getInstance().getLoggedInUser().getUser_registration_info().getMaster_id_level_one_name());
                expertArrayList.add(0, people);
            }
            return expertArrayList;
        }
        return expertArrayList;
    }


    public ArrayList<Video> getVideoData() {
        masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();

        ArrayList<Video> videoArrayList = new ArrayList<>();
        if (masterFeedsHitResponse != null && masterFeedsHitResponse.getSuggested_videos() != null
                && !GenericUtils.isListEmpty(masterFeedsHitResponse.getSuggested_videos())) {
            int oneTimeCount = 0;

            if (masterFeedsHitResponse.getSuggested_videos().size() > 20)
                oneTimeCount = masterFeedsHitResponse.getSuggested_videos().size() * 30 / 100; // 30% of total size of list will go at once.
            else
                oneTimeCount = masterFeedsHitResponse.getSuggested_videos().size(); // if size of list is lest than 20 then we will take the same size as list
            int i;
            Collections.shuffle(masterFeedsHitResponse.getSuggested_videos());
            for (i = 0; i < oneTimeCount; i++) {
                videoArrayList.add(masterFeedsHitResponse.getSuggested_videos().get(i));
            }

            return videoArrayList;
        }
        return videoArrayList;

    }

    public ArrayList<Course> getCourseData() {
        masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();

        ArrayList<Course> courseArrayList = new ArrayList<>();
        if (masterFeedsHitResponse != null && masterFeedsHitResponse.getSuggested_course() != null
                && !GenericUtils.isListEmpty(masterFeedsHitResponse.getSuggested_course())) {
            int oneTimeCount = 0;

            if (masterFeedsHitResponse.getSuggested_course().size() > 20)
                oneTimeCount = masterFeedsHitResponse.getSuggested_course().size() * 30 / 100; // 30% of total size of list will go at once.
            else
                oneTimeCount = masterFeedsHitResponse.getSuggested_course().size(); // if size of list is lest than 20 then we will take the same size as list
            int i;
            Collections.shuffle(masterFeedsHitResponse.getSuggested_course());
            for (i = 0; i < oneTimeCount; i++) {
                courseArrayList.add(masterFeedsHitResponse.getSuggested_course().get(i));
            }

            return courseArrayList;
        }
        return courseArrayList;

    }

    @Override
    protected void initViews() {

        if (getIntent().getExtras() != null) {
            type = getIntent().getExtras().getString(Constants.Extras.TYPE);
            if (getIntent().getExtras().getString(Const.FRAG_TYPE) != null) {
                frag_type = getIntent().getExtras().getString(Const.FRAG_TYPE);
                frag_type_study = getIntent().getExtras().getString(Const.FRAG_TYPE_STUDY);
                courseCategory = (CourseCategory) getIntent().getExtras().getSerializable(Const.COURSE_CATEGORY);
            }
        }
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");//
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        int permissionCheck3 = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        int permissionCheck4 = ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO");
        int permissionCheck7 = ContextCompat.checkSelfPermission(this, "android.permission.READ_PROFILE");

        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck2 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck3 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck4 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck7 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {"android.permission.WRITE_EXTERNAL_STORAGE",
                            "android.permission.READ_EXTERNAL_STORAGE",
                            "android.permission.CAMERA",
                            "android.permission.RECORD_AUDIO",
                            "android.permission.READ_PROFILE"}, REQUEST_READ_PHONE_STATE);
        } else {
            //else condition code
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                break;

            default:
                break;
        }
    }

    @Override
    protected Fragment getFragment() {
        if (frag_type.equals(""))
            frag_type = Const.ALLCOURSES;

        if (frag_type.equalsIgnoreCase(Const.MYCOURSES)) {
            changeLeftPanelColor(Const.MYCOURSES);
            return MyCoursesFragment.newInstance(frag_type);
        } else if (frag_type.equalsIgnoreCase(Const.ALLCOURSES)) {
            return RecordedCoursesFragment.newInstance(getString(R.string.recorded_courses));

        } else if (frag_type.equals(Const.QBANK)) {
            changeTabColor(Constants.TestType.Q_BANK);
            changeLeftPanelColor(Constants.TestType.Q_BANK);

            if (frag_type_study != null && frag_type_study.equals(Constants.StudyType.TESTS)) {
                return StudyFragment.newInstance(Constants.StudyType.TESTS);
            } else if (frag_type_study != null && frag_type_study.equals(Constants.StudyType.QBANKS)) {
                return StudyFragment.newInstance(Constants.StudyType.QBANKS);
            } else if (frag_type_study != null && frag_type_study.equals(Constants.StudyType.CRS)) {
                return StudyFragment.newInstance(Constants.StudyType.CRS);
            } else {
                return StudyFragment.newInstance();
            }
        }

        if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase(Const.VIDEOS)) {
            postFAB.setVisibility(View.VISIBLE);
            toolbarTitleTV.setText(getString(R.string.video));

            // Set TextView layout margin 25 pixels to all side
            // Left Top Right Bottom Margin
            changeTabColor(Const.VIDEOS);
            return DVLFragment.newInstance();
        } else {
            toolbarTitleTV.setText(getString(R.string.app_name));
            SharedPreference.getInstance().putString(Constants.SharedPref.SEARCHED_QUERY, "");
            return FeedsFragment.newInstance();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgress = new Progress(this);
        mProgress.setCancelable(false);

        if (!SharedPreference.getInstance().getBoolean(Const.SYNC_COMPLETE)) {
            fetchDownloadedFiles();
            syncDownloadedData();
        }
//        networkCallForGetLiveClassData("1224");

        // Handles the deeplink incase user redirects to login and after login redirects here
        if (getIntent() != null && getIntent().hasExtra("appLinkData") &&
                getIntent().getStringExtra("appLinkData") != null) {
            callDeepLinkAfterLogin(getIntent().getStringExtra("appLinkData"));
        }

        handleRedirection(getIntent());
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleRedirection(intent);
    }

    private void handleRedirection(Intent intent) {
        if (intent == null) return;
        String appLinkAction = intent.getAction();
        Uri notificationData = intent.getData();

       /* if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            String recipeId = appLinkData.getLastPathSegment();
            Uri appData = Uri.parse("www.emedicoz.com").buildUpon().appendPath(recipeId).build();
            redirectToScreen(appData);
        }*/

        if (notificationData != null) {

            if (SharedPreference.getInstance().getBoolean(Const.IS_USER_LOGGED_IN) &&
                    SharedPreference.getInstance().getBoolean(Const.IS_USER_REGISTRATION_DONE)) {

                switch (Objects.requireNonNull(notificationData.getLastPathSegment())) {

                    case Const.Deeplink.COURSE_INFO:

                        String courseId = notificationData.getQueryParameter("Id");
                        System.out.println("http course id " + courseId);
                        Course course = new Course();
                        course.setId(courseId);
                        intent = new Intent(this, CourseDetailActivity.class);
                        intent.putExtra(COURSE, course);
                        intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES);
                        startActivity(intent);
                        break;

                    case Const.Deeplink.LIVE_CLASS_VIDEO:
                        String videoId = notificationData.getQueryParameter("Id");
                        networkCallForGetLiveClassData(videoId);
                        break;
                    default:
                        networkCallForGetTestData(Objects.requireNonNull(notificationData).toString());
                        break;
                }
            } else {
                intent = new Intent(FeedsActivity.this, SignInActivity.class);
                intent.putExtra("appLinkData", notificationData.toString());
                startActivity(intent);
                finish();

                Toast.makeText(this, "Please login or register to continue", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void redirectToScreen(Uri appData) {
        if (appData.toString().contains(Const.COURSES))
            customNavigationClick(Const.ALLCOURSES);
        else if (appData.toString().contains(Const.FEEDS))
            customNavigationClick(Const.FEEDS);
    }

    public void networkCallForGetTestData(String url) {
        mProgress.show();
        RegFragApis apis = ApiClient.createService(RegFragApis.class);
        Call<JsonObject> response = apis.getMCQTestData(url);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            boolean resultPublished = false;
                            String testType = "0";
                            String testSeriesId = "0";
                            String testSegmentId = "0";

                            if (!resultPublished) {
                                if (testType.equalsIgnoreCase("1")) {
                                    Intent quizView = new Intent(FeedsActivity.this, VideoTestBaseActivity.class);
                                    quizView.putExtra(Const.STATUS, false);
                                    quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                                    startActivity(quizView);
                                } else {
                                    Intent quizView = new Intent(FeedsActivity.this, TestBaseActivity.class);
                                    quizView.putExtra(Const.STATUS, false);
                                    quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                                    startActivity(quizView);
                                }
                            } else {
                                Intent resultScreen = new Intent(FeedsActivity.this, QuizActivity.class);
                                resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                                resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                                startActivity(resultScreen);
                            }
                        } else {
                            RetrofitResponse.getApiData(FeedsActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
//                Helper.showErrorLayoutForNoNav(url, FeedsActivity.this, 1, 1);
            }
        });
    }

    public void networkCallForGetLiveClassData(String videoId) {
        mProgress.show();
        LiveCourseInterface apis = ApiClient.createService(LiveCourseInterface.class);
        Call<JsonObject> response = apis.getVideoDetails(SharedPreference.getInstance().getLoggedInUser().getId(), videoId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            Video video = new Gson().fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), Video.class);

                            Intent intent = new Intent(FeedsActivity.this, VideoDetail.class);
                            intent.putExtra(Const.DATA, video);
                            intent.putExtra("is_bookmarked", video.getIs_bookmarked());
                            intent.putExtra(com.emedicoz.app.utilso.Constants.Extras.TYPE, Const.VIDEO);
                            FeedsActivity.this.startActivity(intent);

                        } else {
                            RetrofitResponse.getApiData(FeedsActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT) != 0) {
                    notifyTV.setVisibility(View.GONE);
                    notifyTV.setText(String.valueOf(SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT)));
                } else {
                    notifyTV.setVisibility(View.GONE);
                }
            }
        };
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment instanceof FeedsFragment) {
            if (isNewPostAdded) {
                ((FeedsFragment) fragment).refreshFeedList(true);
                ((FeedsFragment) fragment).lastPostId = "";
                isNewPostAdded = false;
            }
            if (isCommentRefreshed) {
                if (((FeedsFragment) fragment).feedRVAdapter != null)
                    ((FeedsFragment) fragment).feedRVAdapter.itemChangeDatePostId(SharedPreference.getInstance().getPost(), 0);
                isCommentRefreshed = false;
            }
            if (isPostDeleted) {
                if (((FeedsFragment) fragment).feedRVAdapter != null)
                    ((FeedsFragment) fragment).feedRVAdapter.itemChangeDatePostId(SharedPreference.getInstance().getPost(), 1);
                isPostDeleted = false;
            }
            if (isPostUpdated) {
                if (((FeedsFragment) fragment).feedRVAdapter != null)
                    ((FeedsFragment) fragment).feedRVAdapter.itemChangeDatePostId(SharedPreference.getInstance().getPost(), 0);
                isPostUpdated = false;
            }
        }

    }

    public void syncDownloadedData() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.syncDownloadedData(getReqParam());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                SharedPreference.getInstance().putBoolean(Const.SYNC_COMPLETE, true);
                Gson gson = new Gson();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            Data respData = gson.fromJson(jsonObject, DownloadDataResp.class).getData();

                            for (int i = 0; i < respData.getVideos().size(); i++) {
                                Videos vidData = respData.getVideos().get(i);
                                if (!GenericUtils.isEmpty(vidData.getVideoUrl()) && vidData.getVideoUrl().contains("http"))
                                    eMedicozDownloadManager.getInstance().
                                            createNewDownloadRequest(FeedsActivity.this, vidData.getVideoId(), vidData.getVideoUrl(),
                                                    vidData.getVideoUrl().substring(vidData.getVideoUrl().lastIndexOf("/") + 1) + ".mp4",
                                                    Const.VIDEOS, vidData.getVideoId());
//                                    eMedicozDownloadManager.getInstance().linkDownloadedFile(FeedsActivity.this, vidData.getVideoId(),
//                                            vidData.getVideoUrl(), Const.VIDEOS);
                            }

                            for (int i = 0; i < respData.getPdfs().size(); i++) {
                                Pdf pdfData = respData.getPdfs().get(i);
                                if (!GenericUtils.isEmpty(pdfData.getPdfUrl()) && pdfData.getPdfUrl().contains("http"))
                                    eMedicozDownloadManager.getInstance().
                                            createNewDownloadRequest(FeedsActivity.this, pdfData.getPdfId(), pdfData.getPdfUrl(),
                                                    pdfData.getPdfUrl().substring(pdfData.getPdfUrl().lastIndexOf("/") + 1) + ".pdf",
                                                    Const.PDF, pdfData.getPdfId());
//                                eMedicozDownloadManager.getInstance().linkDownloadedFile(FeedsActivity.this, pdfData.getPdfId(),
//                                        pdfData.getPdfUrl(), Const.PDF);
                            }

                            for (int i = 0; i < respData.getEpubs().size(); i++) {
                                Epub epub = respData.getEpubs().get(i);
                                if (!GenericUtils.isEmpty(epub.getEpubUrl()) && epub.getEpubUrl().contains("http"))
                                    eMedicozDownloadManager.getInstance().
                                            createNewDownloadRequest(FeedsActivity.this, epub.getEpubId(), epub.getEpubUrl(),
                                                    epub.getEpubUrl().substring(epub.getEpubUrl().lastIndexOf("/") + 1) + ".epub",
                                                    Const.EPUB, epub.getEpubId());
//                                eMedicozDownloadManager.getInstance().linkDownloadedFile(FeedsActivity.this, epub.getEpubId(),
//                                        epub.getEpubUrl(), Const.EPUB);
                            }

                        } else {
                            RetrofitResponse.getApiData(FeedsActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                            Toast.makeText(FeedsActivity.this, jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                SharedPreference.getInstance().putBoolean(Const.SYNC_COMPLETE, false);
                mProgress.dismiss();
                Helper.showExceptionMsg(FeedsActivity.this, t);
            }
        });

    }

    private SyncReqParam getReqParam() {
        SyncReqParam reqParam = new SyncReqParam();
        reqParam.setUser_id(SharedPreference.getInstance().getLoggedInUser().getId());
        reqParam.setVideo_ids(arrVideo);
        reqParam.setEpub_ids(arrEpub);
        reqParam.setPdf_ids(arrPdf);

        return reqParam;
    }

    private void fetchDownloadedFiles() {

        File[] files = getFilesDir().listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.getPath().contains("mp4"))
                arrVideo.add(calcFileName(file.getPath()).replace(".mp4", ""));

            if (file.getPath().contains("pdf"))
                arrPdf.add(calcFileName(file.getPath()).replace(".pdf", ""));

            if (file.getPath().contains("epub"))
                arrEpub.add(calcFileName(file.getPath()).replace(".epub", ""));

            Log.d("Files", "FileName:" + file.getName());
        }
    }

    private String calcFileName(String path) {
        return path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path;
    }

    private void callDeepLinkAfterLogin(String appLinkData) {

        if (appLinkData.contains(Const.Deeplink.COURSE_INFO)) {

            String courseId = appLinkData.substring(appLinkData.indexOf("Id") + 3);
            System.out.println("http course id " + courseId);
            Course course = new Course();
            course.setId(courseId);
            Intent intent = new Intent(this, CourseDetailActivity.class);
            intent.putExtra(COURSE, course);
            intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES);
            startActivity(intent);
        }
    }
}
