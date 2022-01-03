package com.emedicoz.app.courses.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.adapter.CurriculumFileRecyclerAdapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Curriculam;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.utilso.AES;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.emedicoz.app.video.exoplayer2.ExoPlayerFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tonyodev.fetch.request.RequestInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewCurriculumActivity extends AppCompatActivity implements View.OnClickListener, eMedicozDownloadManager.SavedOfflineVideoCallBack {

    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";
    private static final String TAG = "NewCurriculumActivity";
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    public ArrayList<String> curriculumStatus = new ArrayList<>();

    public View rootView;
    public ImageView bannerIV;
    ImageButton backNewCuri;
    public Curriculam.File_meta fileMeta;
    public String videoDuration = "0";
    public long totalRemainingTime = 0;
    public TextView courseDescriptionTV;
    TextView descriptionTV;
    TextView durationTV;
    String videoUrl = "";
    eMedicozDownloadManager.SavedOfflineVideoCallBack savedofflineListener;
    SingleCourseData singleCourseData;
    Button readMoreBtn;
    ArrayList<Curriculam> curriculamArrayList;
    int courseDescriptionLength = 50;
    String des = "";
    RecyclerView recyclerView;
    ClickableSpan readMoreClick;
    ClickableSpan readLessClick;
    CurriculumFileRecyclerAdapter curriculumFileRecyclerAdapter;
    ImageView downloadIV;
    Toolbar toolbar;
    TextView toolbarTitle;
    TextView goToTopTV;
    LinearLayout ll2;
    Progress mProgress;
    int orientation = 1;
    FrameLayout exoPlayerFragment;
    private LinearLayout debugRootView;
    private Button retryButton;
    private boolean inErrorState;
    private Object imaAdsLoader; // com.google.android.exoplayer2.ext.ima.ImaAdsLoader
    private Uri loadedAdTagUri;
    private int width;
    private int height;

    public static int math(float f) {
        int c = (int) ((f) + 0.5f);
        float n = f + 0.5f;
        return (n - c) % 2 == 0 ? (int) f : c;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e(getString(R.string.config_changed), newConfig.orientation + "");// 1 = Portrait , 2 = LandScape
        if (newConfig.orientation == 2) {
            orientation = 2;
            toolbar.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            rootView.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,
                    getResources().getDisplayMetrics().heightPixels));
        } else {
            orientation = 1;
            toolbar.setVisibility(View.VISIBLE);
            ll2.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            final float scale = getResources().getDisplayMetrics().density;
            int pixels = (int) (200 * scale + 0.5f);
            rootView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(getString(R.string.response_string), getString(R.string.on_saved_instance_state));
        outState.putSerializable("data", singleCourseData);
        Log.d(getString(R.string.response_string), getString(R.string.on_saved_instance_state) + new Gson().toJson(singleCourseData));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        singleCourseData = (SingleCourseData) savedInstanceState.getSerializable("data");
        Log.d(getString(R.string.response_string), getString(R.string.on_restore_instance_state) + new Gson().toJson(singleCourseData));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, getString(R.string.on_create_method));
        GenericUtils.manageScreenShotFeature(this);

        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
/*        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                GenericUtils.manageScreenShotFeature(activity);*/

        setContentView(R.layout.activity_new_curriculum);
        savedofflineListener = this;
        if (getIntent().getExtras() != null) {
            singleCourseData = (SingleCourseData) getIntent().getExtras().getSerializable(Const.COURSE_DESC);
        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitleTV);

        if (singleCourseData != null && singleCourseData.getTitle() != null) {
            toolbarTitle.setText(singleCourseData.getTitle());
        }

        ll2 = findViewById(R.id.ll2);
        curriculamArrayList = new ArrayList<>();
        courseDescriptionTV = findViewById(R.id.newcuricourseDescriptionTV);
        descriptionTV = findViewById(R.id.newcuridescriptionTV);
        readMoreBtn = findViewById(R.id.newcurireadMoreBtn);
        goToTopTV = findViewById(R.id.gototopTV);
        downloadIV = findViewById(R.id.downloadIV);
        durationTV = findViewById(R.id.durationTV);
        bannerIV = findViewById(R.id.newcuribannerimageIV);
        recyclerView = findViewById(R.id.newcuricurriculumExpListLL);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rootView = findViewById(R.id.rootNewCuri);
        rootView.setOnClickListener(this);
        debugRootView = findViewById(R.id.controls_root_video_detail);
        exoPlayerFragment = findViewById(R.id.exoplayer_container_layout);
        backNewCuri = findViewById(R.id.toolbarBackIV);
        backNewCuri.setOnClickListener(v -> onBackPressed());

        retryButton = findViewById(R.id.retry_button_video_detail);
        retryButton.setOnClickListener(this);

        if (singleCourseData != null) {
            des = singleCourseData.getDescription();
            if (des.length() > courseDescriptionLength) {
                des = des.substring(0, courseDescriptionLength) + "...";
                descriptionTV.setText(String.format("%s %s", HtmlCompat.fromHtml(des, HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Read More</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                readMoreClick = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        descriptionTV.setText(String.format("%s %s", HtmlCompat.fromHtml(des, HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Read Less</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                        Helper.makeLinks(descriptionTV, "Read Less", readLessClick);
                    }
                };

                readLessClick = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        descriptionTV.setText(String.format("%s %s", HtmlCompat.fromHtml(des, HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Read More</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                        Helper.makeLinks(descriptionTV, "Read More", readMoreClick);
                    }
                };
                Helper.makeLinks(descriptionTV, "Read More", readMoreClick);
            } else {
                descriptionTV.setText(HtmlCompat.fromHtml(des, HtmlCompat.FROM_HTML_MODE_LEGACY));
            }
        }
        mProgress = new Progress(this);
        mProgress.setCancelable(false);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    goToTopTV.setVisibility(View.VISIBLE);
                } else {
                    goToTopTV.setVisibility(View.GONE);
                }
            }
        });
        goToTopTV.setOnClickListener(view -> recyclerView.smoothScrollToPosition(0));

    }

    private void networkCallForListCurriculum() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getFileListCurriculum(SharedPreference.getInstance().getLoggedInUser().getId()
                , singleCourseData.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            mProgress.dismiss();
                            JSONArray data = GenericUtils.getJsonArray(jsonResponse);
                            if (!curriculamArrayList.isEmpty()) curriculamArrayList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject dataObj = data.optJSONObject(i);
                                Curriculam curriculam = gson.fromJson(dataObj.toString(), Curriculam.class);
                                curriculamArrayList.add(curriculam);
                            }

                            if (!curriculumStatus.contains(singleCourseData.getId())) {
                                curriculumStatus.add(singleCourseData.getId());
                            }
                            setAllDatatoView(curriculamArrayList);

                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_FILE_LIST_CURRICULUM);
                            RetrofitResponse.getApiData(NewCurriculumActivity.this, API.API_GET_FILE_LIST_CURRICULUM);
                            mProgress.dismiss();
                        }
                    } catch (JSONException e) {
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
    public void onStart() {
        super.onStart();

        networkCallForListCurriculum();
    }

    @Override
    public void onResume() {
        super.onResume();


        if (curriculumFileRecyclerAdapter != null && SharedPreference.getInstance().getBoolean(Const.IS_STATE_CHANGE)) {
            SharedPreference.getInstance().putBoolean(Const.IS_STATE_CHANGE, false);
            String isCompleted = SharedPreference.getInstance().getString(Const.IS_COMPLETE);
            if (singleCourseData.getCurriculam().getFile_meta().size() > SharedPreference.getInstance().getInt(Const.LAST_POS)) {
                Curriculam.File_meta metaData = singleCourseData.getCurriculam().getFile_meta().get(SharedPreference.getInstance().getInt(Const.LAST_POS));
                switch (isCompleted) {
                    case "0":
                        metaData.setIs_paused("0");
                        break;
                    case "1":
                        metaData.setIs_paused("1");
                        break;
                    default:
                        metaData.setIs_paused("");
                        break;
                }
            }
            curriculumFileRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: onPause is called");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: onStop is called");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseAdsLoader();
        SharedPreference.getInstance().putString("LINK", "");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializePlayer(videoUrl);
        } else {
            showToast(R.string.storage_permission_denied);
            finish();
        }
    }

    public void initializePlayer(String url) {
        videoUrl = url;
        if (fileMeta != null) {
            courseDescriptionTV.setText(fileMeta.getTitle());
            descriptionTV.setText(String.format("Duration : %s mins", fileMeta.getDuration()));
            readMoreBtn.setVisibility(View.GONE);
        }
        if (totalRemainingTime == 0)
            totalRemainingTime = Long.parseLong(videoDuration);
        Log.e(TAG, "initializePlayer: " + url);
        Intent intent = getIntent();
        loadPlayerWithVideoURL(url);
        String adTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA);
        if (adTagUriString != null) {
            Uri adTagUri = Uri.parse(adTagUriString);
            if (!adTagUri.equals(loadedAdTagUri)) {
                releaseAdsLoader();
                loadedAdTagUri = adTagUri;
            }
        } else {
            releaseAdsLoader();
        }

        inErrorState = false;
        updateButtonVisibilities();

        new Handler().post(() -> {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
            width = displayMetrics.widthPixels;

            Log.e("Widht & height", height + " -- " + width);
        });
    }


    @Override
    public void updateUIForDownloadedVideo(RequestInfo requestInfo, long id) {

    }

    @Override
    public void updateProgressUI(Integer value, int status, long id) {

    }

    @Override
    public void onStartEncoding() {

    }

    @Override
    public void onEncodingFinished() {

    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void updateButtonVisibilities() {
        debugRootView.removeAllViews();

        retryButton.setVisibility(inErrorState ? View.VISIBLE : View.GONE);
        debugRootView.setVisibility(inErrorState ? View.VISIBLE : View.GONE);

        debugRootView.addView(retryButton);

    }


    private void releaseAdsLoader() {
        if (imaAdsLoader != null) {
            try {
                Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");
                Method releaseMethod = loaderClass.getMethod("release");
                releaseMethod.invoke(imaAdsLoader);
            } catch (Exception e) {
                // Should never happen.
                throw new IllegalStateException(e);
            }
            imaAdsLoader = null;
            loadedAdTagUri = null;
        }
    }

    // OnClickListener methods

    @Override
    public void onClick(View view) {
        if (view == retryButton) {
            initializePlayer(SharedPreference.getInstance().getString("LINK"));

        }
    }

    @Override
    public void onNewIntent(Intent intent) {

        setIntent(intent);
    }

    public void errorCallBack(String jsonString, String apiType) {
        switch (apiType) {
            case API.API_GET_FILE_LIST_CURRICULUM:
                if (Helper.getStorageInstance(this).getRecordObject(singleCourseData.getId() + "_curriculum") != null) {
                    curriculamArrayList = (ArrayList<Curriculam>) Helper.getStorageInstance(this).getRecordObject(singleCourseData.getId() + "_curriculum");
                    setAllDatatoView(curriculamArrayList);
                }
                break;
            case API.API_GET_COMPLETE_INFO_TEST_SERIES:
                Toast.makeText(this, jsonString, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

        }
    }

    private void setAllDatatoView(ArrayList<Curriculam> curriculam) {
        Glide.with(this).load(singleCourseData.getDesc_header_image()).into(bannerIV);
        if (curriculumFileRecyclerAdapter == null) {
            curriculumFileRecyclerAdapter = new CurriculumFileRecyclerAdapter(singleCourseData, this, curriculam);
            recyclerView.setAdapter(curriculumFileRecyclerAdapter);
        } else {
            curriculumFileRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (orientation != 2) {
            super.onBackPressed();
        }
    }

    private void loadPlayerWithVideoURL(String url) {
        getSupportFragmentManager().beginTransaction().replace(R.id.exoplayer_container_layout,
                ExoPlayerFragment.newInstance(AES.decrypt(url))).commit();

    }
}
