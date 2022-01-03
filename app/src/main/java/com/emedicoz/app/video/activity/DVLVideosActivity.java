package com.emedicoz.app.video.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.adapter.EBookAdapter;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Crs.EBookData;
import com.emedicoz.app.modelo.dvl.DVLVideo;
import com.emedicoz.app.modelo.dvl.DVLVideoData;
import com.emedicoz.app.modelo.dvl.DvlData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.AES;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.video.adapter.DVLVideoChildAdapter;
import com.emedicoz.app.video.exoplayer2.ExoPlayerFragment;
import com.emedicoz.app.video.fragment.DVLFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DVLVideosActivity extends AppCompatActivity implements View.OnClickListener, MyNetworkCall.MyNetworkCallBack {

    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";
    private static final String TAG = "DVLVideosActivity";

    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    public TextView durationTV;
    public boolean bitrate = false;
    public View rootView;
    public ImageButton backNewCuri;
    public DVLVideo fileMeta;
    public EBookData eBookData;
    public String videoDuration = "0";
    public String watched = "0";
    public long totalRemainingTime = 0;
    public boolean isLimited = true;
    public ImageView imgDVL;
    NonScrollRecyclerView recyclerView;
    DvlData dvlData;
    LinearLayoutManager linearLayoutManager;
    String courseId = "";
    String topicId = "";
    String topicTitle = "";
    String type = "";
    String videoUrl = "";
    Toolbar toolbar;
    TextView toolbarTitle;
    TextView videoName;
    int orientation = 1;
    FrameLayout exoPlayerFragment;
    private LinearLayout debugRootView;
    private Button retryButton;
    private boolean inErrorState;
    // Fields used only for ad playback. The ads loader is loaded via reflection.
    private Object imaAdsLoader; // com.google.android.exoplayer2.ext.ima.ImaAdsLoader
    private Uri loadedAdTagUri;
    private int width;
    private int height;
    MyNetworkCall myNetworkCall;

    public static int math(float f) {
        int c = (int) ((f) + 0.5f);
        float n = f + 0.5f;
        return (n - c) % 2 == 0 ? (int) f : c;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("Config Changed", newConfig.orientation + "");// 1 = Portrait , 2 = LandScape
        if (newConfig.orientation == 2) {
            orientation = 2;
            toolbar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            rootView.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,
                    getResources().getDisplayMetrics().heightPixels));
            videoName.setVisibility(View.GONE);
        } else {
            orientation = 1;
            toolbar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            videoName.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            final float scale = getResources().getDisplayMetrics().density;
            int pixels = (int) (200 * scale + 0.5f);
            rootView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);

        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        setContentView(R.layout.activity_d_v_l_videos);
        if (getIntent() != null) {
            courseId = getIntent().getStringExtra(Const.PARENT_ID);
            topicId = getIntent().getStringExtra(Constants.Extras.TOPIC_ID);
            topicTitle = getIntent().getStringExtra(Constants.Extras.TOPIC_TITLE);
            dvlData = (DvlData) getIntent().getSerializableExtra(Const.DATA);
            type = getIntent().getStringExtra(Constants.Extras.TYPE);
        }
        DVLFragment.IS_BUY_CLICKED = false;
        initView();
    }

    private void initView() {
        myNetworkCall = new MyNetworkCall(this, this);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitleTV);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        rootView = findViewById(R.id.rootDVL);
        imgDVL = findViewById(R.id.imgDVL);
        durationTV = findViewById(R.id.durationTV);
        videoName = findViewById(R.id.videoName);
        debugRootView = findViewById(R.id.controls_root_video_detail);
        exoPlayerFragment = findViewById(R.id.exoplayer_container_layout);
        backNewCuri = findViewById(R.id.toolbarBackIV);

        backNewCuri.setOnClickListener(v -> onBackPressed());
        retryButton = findViewById(R.id.retry_button_video_detail);
        retryButton.setOnClickListener(this);
        rootView.setOnClickListener(this);


        Glide.with(this)
                .load(dvlData.getDesc_header_image())
                .apply(new RequestOptions().placeholder(R.mipmap.courses_blue))
                .into(imgDVL);
        imgDVL.setScaleType(ImageView.ScaleType.FIT_XY);

        if (type.equalsIgnoreCase(Const.DVL_SECTION)) {
            toolbarTitle.setText(getResources().getString(R.string.dams_video_library));
            networkCallForDVLVideos();
        } else if (type.equalsIgnoreCase(Const.EBOOK_SECTION)) {
            toolbarTitle.setText(getResources().getString(R.string.talking_crs));
            networkCallForEBookList();
        } else {
            toolbarTitle.setText(topicTitle);
            networkCallForCourseList();
        }

    }

    private void networkCallForCourseList() {
        myNetworkCall.NetworkAPICall(API.API_GET_CRS_VIDEO, true);
    }

    private void networkCallForEBookList() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        final Progress mProgress = new Progress(this);
        mProgress.setCancelable(false);
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getEBookFiles(SharedPreference.getInstance().getLoggedInUser().getId(), topicId, courseId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            eBookData = gson.fromJson(data.toString(), EBookData.class);
                            EBookAdapter adapter = new EBookAdapter(DVLVideosActivity.this, eBookData.getFiles(), eBookData, dvlData);
                            recyclerView.setAdapter(adapter);
                        } else {
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
                Helper.showExceptionMsg(DVLVideosActivity.this, t);
            }
        });
    }

    private void networkCallForDVLVideos() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        final Progress mProgress = new Progress(this);
        mProgress.setCancelable(false);
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getDvlVideos(SharedPreference.getInstance().getLoggedInUser().getId(), courseId, topicId);
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
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            DVLVideoData dvlVideoData = gson.fromJson(data.toString(), DVLVideoData.class);

                            DVLVideoChildAdapter adapter = new DVLVideoChildAdapter(dvlVideoData.getVideos(), DVLVideosActivity.this, dvlData, dvlVideoData);
                            recyclerView.setAdapter(adapter);
                        } else {
                            mProgress.dismiss();
                            RetrofitResponse.getApiData(DVLVideosActivity.this, API.API_GET_FILE_LIST_CURRICULUM);
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

    public void initializePlayer(String url) {
        videoUrl = url;
        if (fileMeta != null)
            videoName.setText(fileMeta.getTitle());
        if (totalRemainingTime == 0)
            totalRemainingTime = Long.parseLong(videoDuration);
        Intent intent = getIntent();

        loadPlayerWithVideoURL(url, fileMeta == null ? videoUrl : fileMeta.getElementFk(), Const.COURSE_VIDEO_TYPE);

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

    private void loadPlayerWithVideoURL(String url, String id, String type) {
        if (!isFinishing() && !isDestroyed())
            getSupportFragmentManager().beginTransaction().replace(R.id.exoplayer_container_layout,
                    ExoPlayerFragment.newInstance(AES.decrypt(url), id, type)).commit();

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
            //  initializePlayer();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onBackPressed() {
        int orientation1 = getResources().getConfiguration().orientation;
        if (orientation1 == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eMedicozApp.getInstance().playerState = null;
        releaseAdsLoader();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // initializePlayer();
        } else {
            showToast(R.string.storage_permission_denied);
            finish();
        }
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> param = new HashMap<>();
        param.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        param.put(Const.COURSE_ID, courseId);
        param.put(Constants.Extras.TOPIC_ID, topicId);
        Log.e(TAG, "getAPI: " + param);
        return service.postData(apiType, param);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        JSONObject data = GenericUtils.getJsonObject(jsonObject);
        DVLVideoData dvlVideoData = new Gson().fromJson(data.toString(), DVLVideoData.class);

        DVLVideoChildAdapter adapter = new DVLVideoChildAdapter(dvlVideoData.getVideos(), DVLVideosActivity.this, dvlData, dvlVideoData);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {

    }
}
