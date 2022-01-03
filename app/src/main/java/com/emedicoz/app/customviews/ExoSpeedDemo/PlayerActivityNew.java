/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.emedicoz.app.customviews.ExoSpeedDemo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.VideoDuration;
import com.emedicoz.app.recordedCourses.model.detaildatavideo.VideoItemData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SingleCourseApiInterface;
import com.emedicoz.app.utilso.AES;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.video.exoplayer2.ExoPlayerFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.EventListener;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlayerActivityNew extends AppCompatActivity implements OnClickListener, EventListener {

    public static final String TAG = PlayerActivityNew.class.getSimpleName();

    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";

    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    String url;
    CountDownTimer countDownTimer = null;
    String Urltype;
    String videoType;
    VideoDuration videoDuration;
    Long remainingTime;
    String durationLimit, watched, remaining_time;
    View rootView;
    View toolBar;
    TextView videoTitle;
    FrameLayout exoPlayerFragment;
    private LinearLayout debugRootView;
    private TextView durationTV;
    private Button retryButton;
    public boolean inErrorState;
    private boolean shouldAutoPlay;
    // Fields used only for ad playback. The ads loader is loaded via reflection.
    private int resumeWindow;
    private long resumePosition;
    private Object imaAdsLoader; // com.google.android.exoplayer2.ext.ima.ImaAdsLoader
    private Uri loadedAdTagUri;
    private int width, height;
    private VideoItemData videoData;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("Config Changed", newConfig.orientation + "");
        // 1 = Portrait , 2 = LandScape
        if (newConfig.orientation == 1) {
            toolBar.setVisibility(View.VISIBLE);
            try {
                if (videoData != null) {
                    videoTitle.setVisibility(View.VISIBLE);
                    videoTitle.setText(videoData.getVideoTitle());
                } else {
                    videoTitle.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            final float scale = getResources().getDisplayMetrics().density;
            int pixels = (int) (250 * scale + 0.5f);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels);
            rootView.setLayoutParams(layoutParams);

        } else {

            toolBar.setVisibility(View.GONE);
            videoTitle.setVisibility(View.GONE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,
                    getResources().getDisplayMetrics().heightPixels);
            rootView.setLayoutParams(layoutParams);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);

        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        setContentView(R.layout.player_activity_new);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        url = getIntent().getStringExtra(Const.VIDEO_LINK);
        Urltype = getIntent().getExtras().getString("TYPE");
        videoType = getIntent().getExtras().getString(Const.VIDEO_TYPE);
        videoData = (VideoItemData) getIntent().getExtras().getSerializable(Const.VIDEO_DATA);

//        // Create a PlaylistIt'em that points to your content
        Log.e(TAG, "URL: " + url);
        rootView = findViewById(R.id.root_new);
        toolBar = findViewById(R.id.toolbar);
        videoTitle = findViewById(R.id.vide_title);
        rootView.setOnClickListener(this);
        debugRootView = findViewById(R.id.controls_root_video_detail);
        durationTV = findViewById(R.id.durationTV);


        // to show marquee name on the videos
        exoPlayerFragment = findViewById(R.id.exoplayer_container_layout);
        retryButton = findViewById(R.id.retry_button_video_detail);
        retryButton.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(view -> finish());

        blink();
        if (findViewById(R.id.iv_back) != null)
            findViewById(R.id.iv_back).setOnClickListener(view -> finish());
        initializePlayer();
        try {
            if (videoData != null) {
                videoTitle.setVisibility(View.VISIBLE);
                videoTitle.setText(videoData.getVideoTitle());
            } else {
                String str = url.substring(url.lastIndexOf("/") + 1, url.indexOf(".mp4")).trim();
                videoData = new VideoItemData();
                videoData.setVideoTitle(str);
                if (str != null) {
                    videoTitle.setVisibility(View.VISIBLE);
                    videoTitle.setText(videoData.getVideoTitle());
                } else {
                    videoTitle.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNewIntent(Intent intent) {

        Log.e(TAG, "onNewIntent");

        setIntent(intent);
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
        if (videoDuration != null && videoDuration.getType() != null && videoDuration.getType().equalsIgnoreCase("1"))
            networkCallForPostVideoDuration(videoDuration);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eMedicozApp.getInstance().playerState = null;
/*        if (Helper.isConnected(PlayerActivityNew.this))
            super.onDestroy();
        else
            Toast.makeText(this, "Please check your internet connection..", Toast.LENGTH_SHORT).show();*/
        // PlaybackControlView.lastPositionInMS = 0;
        releaseAdsLoader();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            initializePlayer();
        } else {
            showToast(R.string.storage_permission_denied);
            finish();
        }
    }

    // Activity input

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // If the event was not handled then see if the player view can handle it.
        return super.dispatchKeyEvent(event);
    }

    // OnClickListener methods

    @Override
    public void onClick(View view) {
        if (view == retryButton) {
            initializePlayer();
        } else if (view.getParent() == debugRootView) {

        }
    }

    // PlaybackControlView.VisibilityListener implementation


    // Internal methods


    private void initializePlayer() {
        Intent intent = getIntent();

        try {
            loadPlayerWithVideoURL(url);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

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

//        resumeWindow = SharedPreference.getInstance().getInt(Const.RESUME_WINDOW);
//        resumePosition = SharedPreference.getInstance().getLong(Const.RESUME_POSITION);


        inErrorState = false;
        updateButtonVisibilities();
        new Handler().post(() -> {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
            width = displayMetrics.widthPixels;
            Log.e("Widht & height", height + " -- " + width);
            // to update the Postion of TextView
            //  updateMarqueeTextViewPosition(1);
        });


    }


    private void loadPlayerWithVideoURL(String url) throws IllegalStateException {
        if (!isFinishing()) {
            if (videoData != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.exoplayer_container_layout,
                        ExoPlayerFragment.newInstance(url, videoData.getId(), videoType, videoData.getPreviewVideoDuration(),videoData.getDrmToken())).commit();

            } else {

                getSupportFragmentManager().beginTransaction().replace(R.id.exoplayer_container_layout,
                        ExoPlayerFragment.newInstance(AES.decrypt(url), videoType)).commit();
            }
        }


    }

    /**
     * Returns an ads media source, reusing the ads loader if one exists.
     *
     * @throws Exception Thrown if it was not possible to create an ads media source, for example, due
     *                   to a missing dependency.
     */


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

    // Player.EventListener implementation


    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else
            super.onBackPressed();
    }


    // User controls

    private void updateButtonVisibilities() {
        debugRootView.removeAllViews();

        retryButton.setVisibility(inErrorState ? View.VISIBLE : View.GONE);
        debugRootView.setVisibility(inErrorState ? View.VISIBLE : View.GONE);

        debugRootView.addView(retryButton);

    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    public void networkCallForPostVideoDuration(VideoDuration videoDuration) {
/*        long watchedNew = Integer.parseInt(durationLimit) - (remainingTime / 60 / 1000);
        long totalWatched = Math.round(watchedNew + Integer.parseInt(watched));*/
        if (Helper.isConnected(PlayerActivityNew.this)) {
            int newDuration = Integer.parseInt(GenericUtils.getParsableString(remaining_time));
            Log.e(TAG, "networkCallForPostVideoDuration: duration" + newDuration);
            if (remainingTime == null)
                remainingTime = 0L;
            int newRemainingTime = Math.round(remainingTime / 60 / 1000);
            Log.e(TAG, "networkCallForPostVideoDuration: remainingTime" + newRemainingTime);

            int watched = newDuration - newRemainingTime;
            Log.e(TAG, "networkCallForPostVideoDuration: watched" + watched);
            int totalWatched = 0;
            try {
                totalWatched = watched + Integer.parseInt(this.watched);
            } catch (Exception e) {
                totalWatched = watched;
                e.printStackTrace();
            }
            Log.e(TAG, "networkCallForPostVideoDuration, totalWatched:" + totalWatched);
            final Progress mProgress = new Progress(this);
            mProgress.setCancelable(false);
            if (!isFinishing())
                mProgress.show();
            SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
            Call<JsonObject> response = apiInterface.postVideoDuration(SharedPreference.getInstance().getLoggedInUser().getId()
                    , videoDuration.getVideo_id(), videoDuration.getType(), String.valueOf(totalWatched));
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;
                        Gson gson = new Gson();

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                mProgress.dismiss();
                                JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                                // Toast.makeText(PlayerActivityNew.this, jsonResponse.optString(Const.MESSAGE), Toast.LENGTH_SHORT).show();

                            } else {
                                RetrofitResponse.getApiData(PlayerActivityNew.this, API.API_GET_FILE_LIST_CURRICULUM);
                                // mprogress.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(PlayerActivityNew.this, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mProgress.dismiss();
                    // Helper.showErrorLayoutForNoNav(API.API_GET_FILE_LIST_CURRICULUM, NewCurriculumActivity.this, 1, 1);
                }
            });
        } else {
            Toast.makeText(this, "Please check your internet connection..", Toast.LENGTH_SHORT).show();
        }
    }

    public void networkCallForGetVideoDuration(final VideoDuration videoDuration) {

        if (Helper.isConnected(PlayerActivityNew.this)) {
            final Progress mprogress = new Progress(this);
            mprogress.setCancelable(false);
            mprogress.show();
            SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
            Call<JsonObject> response = apiInterface.getVideoDuration(SharedPreference.getInstance().getLoggedInUser().getId()
                    , videoDuration.getVideo_id(), videoDuration.getType());
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;
                        Gson gson = new Gson();

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                mprogress.dismiss();
                                JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                                // if (videoDuration.getDuration_limit().equals(""))
                                durationLimit = data.optString("duration_limit");
                                remaining_time = data.optString("remaning_time");
                                // else
                                //   durationLimit = String.valueOf(Long.parseLong(videoDuration.getDuration_limit())/60/1000);
                                if (durationLimit.equals(""))
                                    durationLimit = "0";
                                watched = data.optString("watched");
                                remainingTime = (Long.parseLong(durationLimit) - Long.parseLong(watched)) * 60 * 1000;
                                initializePlayer();
                            } else {
                                RetrofitResponse.getApiData(PlayerActivityNew.this, API.API_GET_FILE_LIST_CURRICULUM);
                                // mprogress.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(PlayerActivityNew.this, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mprogress.dismiss();
                    // Helper.showErrorLayoutForNoNav(API.API_GET_FILE_LIST_CURRICULUM, NewCurriculumActivity.this, 1, 1);
                }
            });
        } else {
            Toast.makeText(this, "Please check your internet connection..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private void blink() {
        final DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        Random R = new Random();

        final Handler handler = new Handler();
        new Thread(() -> {
            int timeToBlink = 2000;    //in milissegunds
            try {
                Thread.sleep(timeToBlink);
            } catch (Exception e) {
            }
            handler.post(() -> {
                blink();
            });
        }).start();
    }

    public void callUpdateContentViewStatusApi() {

        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.updateContentViewStatus(SharedPreference.getInstance().getLoggedInUser().getId()
                , videoData.getCourse_id(), videoData.getTopic_id(), videoData.getId(), "1");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);

                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(getApplicationContext(), R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

}
