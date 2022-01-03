package com.emedicoz.app.courses.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
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

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.VideoSolutionAdapter;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.VideoSolutionData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.QuizApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.video.exoplayer2.ExoPlayerFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VideoSolution extends AppCompatActivity implements View.OnClickListener {

    public static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";
    public static final String DRM_LICENSE_URL = "drm_license_url";
    public static final String DRM_KEY_REQUEST_PROPERTIES = "drm_key_request_properties";
    public static final String PREFER_EXTENSION_DECODERS = "prefer_extension_decoders";
    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String EXTENSION_EXTRA = "extension";
    public static final String ACTION_VIEW_LIST =
            "com.google.android.exoplayer.demo.action.VIEW_LIST";
    public static final String URI_LIST_EXTRA = "uri_list";
    public static final String EXTENSION_LIST_EXTRA = "extension_list";
    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";
    private static final String TAG = "VideoSolution";

    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    public boolean bitrate = false;
    public View rootView;
    public ImageButton backNewCuri;
    int orientation = 1;
    NonScrollRecyclerView videoSolutionRV;
    ArrayList<VideoSolutionData> videoSolutionArrayList;
    VideoSolutionAdapter videoSolutionAdapter;
    String test_segment_id = "";
    String video_url = "";
    Toolbar toolbar;
    TextView toolbarTitle;
    LinearLayout ll2;
    Progress mProgress;
    FrameLayout exoPlayerFragment;
    private Handler mainHandler;
    private LinearLayout debugRootView;
    private Button retryButton;
    private boolean inErrorState;
    private boolean shouldAutoPlay;
    // Fields used only for ad playback. The ads loader is loaded via reflection.
    private Object imaAdsLoader; // com.google.android.exoplayer2.ext.ima.ImaAdsLoader
    private Uri loadedAdTagUri;
    private int width, height;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // hideSystemUi();
        Log.e("Config Changed", newConfig.orientation + "");// 1 = Portrait , 2 = LandScape
        if (newConfig.orientation == 2) {
            orientation = 2;
            toolbar.setVisibility(View.GONE);
            videoSolutionRV.setVisibility(View.GONE);
            // ll2.setVisibility(View.GONE);
            //getSupportActionBar().hide();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            rootView.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,
                    getResources().getDisplayMetrics().heightPixels
            ));
        } else {
            orientation = 1;
            toolbar.setVisibility(View.VISIBLE);
            // ll2.setVisibility(View.VISIBLE);
            videoSolutionRV.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            final float scale = getResources().getDisplayMetrics().density;
            int pixels = (int) (200 * scale + 0.5f);
            rootView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));
        }
        //updateMarqueeTextViewPosition(newConfig.orientation);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        shouldAutoPlay = true;


        mainHandler = new Handler();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        setContentView(R.layout.activity_video_solution);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitleTV);
        toolbarTitle.setText("Video Solution");
        //ll2 = findViewById(R.id.ll2);
        test_segment_id = getIntent().getStringExtra(Constants.ResultExtras.TEST_SEGMENT_ID);
        videoSolutionArrayList = new ArrayList<>();

        videoSolutionRV = findViewById(R.id.videoSolutionRV);
        videoSolutionRV.setLayoutManager(new LinearLayoutManager(this));
        videoSolutionRV.setNestedScrollingEnabled(false);
        videoSolutionAdapter = new VideoSolutionAdapter(this, videoSolutionArrayList);
        videoSolutionRV.setAdapter(videoSolutionAdapter);
        exoPlayerFragment = findViewById(R.id.exoplayer_container_layout);
        rootView = findViewById(R.id.rootVideoSolution);
        rootView.setOnClickListener(this);
        debugRootView = findViewById(R.id.controls_root_video_detail);

        backNewCuri = findViewById(R.id.toolbarBackIV);
        backNewCuri.setOnClickListener(v -> onBackPressed());


        retryButton = findViewById(R.id.retry_button_video_detail);
        retryButton.setOnClickListener(this);


        mProgress = new Progress(this);
        mProgress.setCancelable(false);

        networkCallForVideoSolution();
        blink();
    }

    private void networkCallForVideoSolution() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        Log.e(TAG, test_segment_id);
        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Call<JsonObject> response = apiInterface.getVideoSolutionData(SharedPreference.getInstance().getLoggedInUser().getId()
                , test_segment_id);//test_segment_id
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
                            JSONArray data = GenericUtils.getJsonArray(jsonResponse);
                            if (videoSolutionArrayList.size() > 0) videoSolutionArrayList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject dataObj = data.optJSONObject(i);
                                VideoSolutionData videoSolutionData = gson.fromJson(dataObj.toString(), VideoSolutionData.class);
                                videoSolutionArrayList.add(videoSolutionData);
                            }

                            if (videoSolutionArrayList.size() == 0) {
                                Toast.makeText(VideoSolution.this, "No Videos Found", Toast.LENGTH_SHORT).show();
                            }
                            // if (Helper.getStorageInstance(getActivity()).getRecordObject(String.valueOf(course.getId()+"_curriculum")) == null || refreshStatus)
                            setAllDatatoView(videoSolutionArrayList);

                            // refreshStatus = false;

                            //Helper.getStorageInstance(getActivity()).addRecordStore(String.valueOf(course.getId()+"_curriculum"), curriculamArrayList);
                            // mprogress.dismiss();

                        } else {
                            mProgress.dismiss();
                            Toast.makeText(VideoSolution.this, "No Videos Found", Toast.LENGTH_SHORT).show();
                            // errorCallBack(jsonResponse.optString(Const.MESSAGE), API.API_GET_FILE_LIST_CURRICULUM);
                            RetrofitResponse.getApiData(VideoSolution.this, API.API_GET_FILE_LIST_CURRICULUM);
                            // mprogress.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                // Helper.showErrorLayoutForNoNav(API.API_GET_FILE_LIST_CURRICULUM, VideoSolution.this, 1, 1);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

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
        SharedPreference.getInstance().putString("SOLUTION_LINK", "");
    }


    private void loadPlayerWithVideoURL(String url) {
        if (!isFinishing())
            getSupportFragmentManager().beginTransaction().replace(R.id.exoplayer_container_layout,
                    ExoPlayerFragment.newInstance(url)).commit();

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

    public void initializePlayer(String url) {
        video_url = url;
        Intent intent = getIntent();

        String action = intent.getAction();
        Uri[] uris;
        String[] extensions;
        uris = new Uri[]{Uri.parse(url)};
        extensions = new String[]{intent.getStringExtra(EXTENSION_EXTRA)};

        loadPlayerWithVideoURL(url);

        String adTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA);
        if (adTagUriString != null) {
            Uri adTagUri = Uri.parse(adTagUriString);
            if (!adTagUri.equals(loadedAdTagUri)) {
                releaseAdsLoader();
                loadedAdTagUri = adTagUri;
            }
            try {
            } catch (Exception e) {
                showToast(R.string.ima_not_loaded);
            }
        } else {
            releaseAdsLoader();
        }


        inErrorState = false;
        updateButtonVisibilities();


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                height = displayMetrics.heightPixels;
                width = displayMetrics.widthPixels;

                Log.e("Widht & height", height + " -- " + width);
                // to update the Postion of TextView
                // updateMarqueeTextViewPosition(1);
            }
        });
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


    // Player.EventListener implementation


    private void showControls() {
        debugRootView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // If the event was not handled then see if the player view can handle it.
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View view) {
        if (view == retryButton) {
            //  initializePlayer();

        }
    }

    // OnClickListener methods

    @Override
    public void onNewIntent(Intent intent) {

        shouldAutoPlay = true;

        setIntent(intent);
    }

    public void errorCallBack(String jsonString, String apiType) {
        switch (apiType) {
            case API.API_GET_FILE_LIST_CURRICULUM:

                if (jsonString.equalsIgnoreCase(getResources().getString(R.string.internet_error_message)))
                    // Helper.showErrorLayoutForNoNav(apiType, this, 1, 1);

                    if (jsonString.contains(getResources().getString(R.string.something_went_wrong_string)))
                        // Helper.showErrorLayoutForNoNav(apiType, this, 1, 2);
                        break;
            case API.API_GET_COMPLETE_INFO_TEST_SERIES:
                if (jsonString.equalsIgnoreCase(getResources().getString(R.string.internet_error_message)))
                    //  Helper.showErrorLayoutForNoNav(apiType, this, 1, 1);

                    if (jsonString.contains(getResources().getString(R.string.something_went_wrong_string)))
                        //  Helper.showErrorLayoutForNoNav(apiType, this, 1, 2);
                        break;
            default:

        }
    }

    private void setAllDatatoView(ArrayList<VideoSolutionData> videoSolutionArrayList) {
        //mprogress.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgress.dismiss();
            }
        }, 500);
        videoSolutionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    private void blink() {
        final DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        Random R = new Random();

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 2000;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        blink();
                    }
                });
            }
        }).start();
    }
}
