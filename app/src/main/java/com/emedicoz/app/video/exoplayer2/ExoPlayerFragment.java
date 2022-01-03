package com.emedicoz.app.video.exoplayer2;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.BuildConfig;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.LiveCourseActivity;
import com.emedicoz.app.courses.activity.LiveStreamActivity;
import com.emedicoz.app.courses.activity.VideoSolution;
import com.emedicoz.app.customviews.ExoSpeedDemo.PlayerActivityNew;
import com.emedicoz.app.feeds.adapter.ReportListAdapter;
import com.emedicoz.app.modelo.PlayerState;
import com.emedicoz.app.modelo.Report_reasons;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SingleFeedviewApis;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.video.activity.DVLVideosActivity;
import com.emedicoz.app.video.activity.VideoDetail;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pallycon.widevinelibrary.PallyconDrmException;
import com.pallycon.widevinelibrary.PallyconEventListener;
import com.pallycon.widevinelibrary.PallyconWVMSDK;
import com.pallycon.widevinelibrary.PallyconWVMSDKFactory;
import com.pallycon.widevinelibrary.UnAuthorizedDeviceException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.emedicoz.app.utilso.Constants.EXTRA_PLAYBACK_POSITION_MS;
import static com.emedicoz.app.utilso.Constants.EXTRA_PLAYBACK_QUALITY;
import static com.emedicoz.app.utilso.Constants.EXTRA_PLAYBACK_SPEED_PARAM;
import static com.emedicoz.app.utilso.Constants.EXTRA_PLAYBACK_SPEED_TEXT;
import static com.emedicoz.app.utilso.Helper.formatSeconds;

public class ExoPlayerFragment extends Fragment implements View.OnClickListener,
        Player.EventListener, DialogInterface.OnClickListener {

    private static final String TAG = "ExoPlayerFragment";

    private static final int REQUEST_CODE = 101;
    ProgressBar progressBar;
    PlayerView playerView;
    TextView floatingText;
    TextView noVideo;
    TextView tvTimeLeft;
    View layoutRetry;
    View btnRetry;

    public PopupWindow popupWindow;
    public LinearLayout anyView;
    private Unbinder unbinder;
    private Activity context;
    private SimpleExoPlayer player;
    private ImageView fullScreenButton;
    private TextView playbackSpeedButton;
    private boolean playWhenReady = true;
    private int currentWindow;
    private int repeatVideoCounter = 0;
    private long playbackPosition;
    private float playbackSpeed = 1f;
    private LoopingMediaSource loopingSource;
    private Uri mp4videoUrl;

    private DefaultTrackSelector trackSelector;
    private int qualitySelection = 0;
    private int highestVideoQuality;
    private String playbackSpeedText;
    CountDownTimer countDownTimer;
    String videoId;
    String videoType;
    private long totalRemainingTime;
    private String remainingTime = "", watched;

    private CountDownTimer previewVideoTimer;
    private long videoPreviewDuration;
    ArrayList<Report_reasons> reportReasonsArrayList;
    String reportId;
    String reportText;
    String videoQuality;
    private String drmToken = "";

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    public static Fragment newInstance(String url) {
        ExoPlayerFragment playerFragment = new ExoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.Extras.VIDEO_URL, url);
        Log.i("url : ", url != null ? url : "");
        playerFragment.setArguments(bundle);

        return playerFragment;
    }

    public static Fragment newInstance(String url, String videoId, String type) {
        ExoPlayerFragment playerFragment = new ExoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.Extras.VIDEO_URL, url);
        bundle.putString(Const.VIDEO_ID, videoId);
        bundle.putString(Const.VIDEO_TYPE, type);
        playerFragment.setArguments(bundle);

        return playerFragment;
    }

    public static Fragment newInstance(String url, String videoId, String type, String duration,String token) {
        ExoPlayerFragment playerFragment = new ExoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.Extras.VIDEO_URL, url);
        bundle.putString(Const.VIDEO_ID, videoId);
        bundle.putString(Const.VIDEO_TYPE, type);
        bundle.putLong(Const.VIDEO_PREVIEW_DURATION, !GenericUtils.isEmpty(duration) ? Long.parseLong(duration) : 0L);
        bundle.putString("TOKEN",token);
        playerFragment.setArguments(bundle);

        return playerFragment;
    }

    public static Fragment newInstance(String url, String type) {
        ExoPlayerFragment playerFragment = new ExoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.Extras.VIDEO_URL, url);
        bundle.putString(Const.VIDEO_TYPE, type);
        bundle.putLong(Const.VIDEO_PREVIEW_DURATION, 0L);
        playerFragment.setArguments(bundle);

        return playerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments() != null && getArguments().getString(Constants.Extras.VIDEO_URL) != null) {
            mp4videoUrl = Uri.parse(getArguments().getString(Constants.Extras.VIDEO_URL));
            videoId = getArguments().getString(Const.VIDEO_ID);
            videoType = getArguments().getString(Const.VIDEO_TYPE);
            drmToken = getArguments().getString("TOKEN");
            videoPreviewDuration = getArguments().getLong(Const.VIDEO_PREVIEW_DURATION);
        }
    }

    public void setMediaSource(String url) {
        mp4videoUrl = Uri.parse(url);
//        loadPlayer();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle data) {
        View root = inflater.inflate(R.layout.fragment_exo_player, container, false);
        unbinder = ButterKnife.bind(this, root);

        // TODO: 12-10-2020 network call for video duration
/*        Log.e(TAG, "videoId: " + videoId);
        if (!GenericUtils.isEmpty(videoId) && !(getActivity() instanceof LiveCourseActivity))
            networkCallForGetVideoDuration();*/
        /*
        if (getActivity() != null) {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        utilityClass.hideSystemBar();
                    }, 2000);
                } else {
                    utilityClass.showSystemBar();
                }
            });
        }
*/

//        resumePlayer();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        floatingText = view.findViewById(R.id.floatingText_video_detail);
        noVideo = view.findViewById(R.id.no_video);
        tvTimeLeft = view.findViewById(R.id.tv_time_left);
        layoutRetry = view.findViewById(R.id.layout_retry);
        btnRetry = view.findViewById(R.id.retry_button);
            playerView = view.findViewById(R.id.player_view);
            progressBar = view.findViewById(R.id.progress_bar);
        playerView.findViewById(R.id.live_button).setVisibility(View.GONE);
        playerView.findViewById(R.id.exo_duration).setVisibility(View.VISIBLE);
        fullScreenButton = playerView.findViewById(R.id.exo_fullscreen);

        playerView.findViewById(R.id.exo_orientation_lock).setVisibility(View.GONE);
        fullScreenButton.setOnClickListener(this);
        playbackSpeedButton = playerView.findViewById(R.id.exo_playback_speed);
        playbackSpeedButton.setOnClickListener(this);
        playerView.findViewById(R.id.exo_setting).setOnClickListener(this);

        floatingText.setText(SharedPreference.getInstance().getLoggedInUser().getEmail());
        floatingText.measure(0, 0);

        if (getActivity() instanceof LiveStreamActivity || getActivity() instanceof LiveCourseActivity) {
            playerView.findViewById(R.id.live_button).setVisibility(View.VISIBLE);
            playerView.findViewById(R.id.exo_ffwd).setVisibility(View.GONE);
            playerView.findViewById(R.id.exo_rew).setVisibility(View.GONE);

            playerView.findViewById(R.id.exo_position).setVisibility(View.GONE);
            playerView.findViewById(R.id.exo_progress).setVisibility(View.GONE);
            playerView.findViewById(R.id.exo_duration).setVisibility(View.GONE);
            playerView.findViewById(R.id.exo_playback_speed).setVisibility(View.GONE);
        }

        Helper.blink(context, playerView, floatingText);
        btnRetry.setOnClickListener(v -> {
            layoutRetry.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            loadPlayer();
        });

        anyView = playerView.findViewById(R.id.anyLayout);
    }

    /*

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }
*/

    @Override
    public void onResume() {
        super.onResume();
        if (mp4videoUrl == null) return;
        Log.i(TAG, "URL: " + mp4videoUrl);

        PlayerState playerState = eMedicozApp.getInstance().playerState;
        if (playerState != null) {
            playbackPosition = playerState.getPlayer() != null ? playerState.getPlayer().getCurrentPosition() : 0;
            playbackSpeed = playerState.getPlaybackSpeed();
            qualitySelection = playerState.getQualitySelection();
            playbackSpeedText = playerState.getPlaybackSpeedText();

        } else {
            playbackPosition = 0;
            playbackSpeed = 1f;
            qualitySelection = 0;
            playbackSpeedText = "1X";
        }

        resumePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;

            if (previewVideoTimer != null)
                previewVideoTimer.cancel();
            // TODO: 12-10-2020 timer cancel when player release
/*            if (countDownTimer != null)
                countDownTimer.cancel();
            if (!GenericUtils.isEmpty(remainingTime))
                networkCallForPostVideoDuration();*/
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onSaveInstanceState(new Bundle());
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();

        if (player != null)
            player.release();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            playbackPosition = data.getLongExtra(EXTRA_PLAYBACK_POSITION_MS, 0);
            playbackSpeed = data.getFloatExtra(EXTRA_PLAYBACK_SPEED_PARAM, 1f);
            qualitySelection = data.getIntExtra(EXTRA_PLAYBACK_QUALITY, 0);
            playbackSpeedText = data.getStringExtra(EXTRA_PLAYBACK_SPEED_TEXT);

            resumePlayer();
        }
    }

    private void resumePlayer() {

        if (player != null) {
            player.seekTo(playbackPosition);
            player.setPlaybackParameters(new PlaybackParameters(playbackSpeed));
            if (trackSelector != null && trackSelector.getCurrentMappedTrackInfo() != null) {
                DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(0, qualitySelection);
                trackSelector.setSelectionOverride(0, trackSelector.getCurrentMappedTrackInfo().getTrackGroups(0), override);
            }
            player.setPlayWhenReady(true);
        } else {
            loadPlayer();
        }

        playbackSpeedButton.setText(playbackSpeedText);
    }

    private void loadPlayer() {
        releasePlayer();
        // TODO: 1. initialize PallyconWVM SDK
        String siteId = BuildConfig.SITE_ID;
        String siteKey = BuildConfig.SITE_KEY;
        PallyconWVMSDK WVMAgent = null;
        try {
            WVMAgent = PallyconWVMSDKFactory.getInstance(requireActivity());
            WVMAgent.init(requireActivity(), null, siteId, siteKey);
            WVMAgent.setPallyconEventListener(pallyconEventListener);
        } catch (PallyconDrmException e) {
            e.printStackTrace();
        } catch (UnAuthorizedDeviceException e) {
            e.printStackTrace();
        }

        // TODO : 2.set content information
        UUID drmSchemeUuid = UUID.fromString((C.WIDEVINE_UUID).toString());
        String drmLicenseUrl = "http://license.pallycon.com/ri/licenseManager.do";
        //String token = ;

        // TODO : 3.set drm session manager
        DrmSessionManager drmSessionManager = null;
        DefaultRenderersFactory renderersFactory = null;
        try {
            Log.e(TAG, "loadPlayer: "+mp4videoUrl );
            Log.e(TAG, "loadPlayer: "+drmToken );
            // drmSessionManager = WVMAgent.createDrmSessionManager(drmSchemeUuid, drmLicenseUrl, uri, "2b186217-47e6-44a5-a000-b3088f1e7f2e", "9c600cb9-8f3a-4e35-9e93-94a62db30a69", "", false);
            drmSessionManager = WVMAgent.createDrmSessionManagerByToken(
                    drmSchemeUuid,
                    drmLicenseUrl,
                    mp4videoUrl,
                    SharedPreference.getInstance().getLoggedInUser().getId(),
                    "",
                    drmToken,
                    false);
        } catch (PallyconDrmException e) {
            e.printStackTrace();
        }
        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        renderersFactory = new DefaultRenderersFactory(requireActivity(), drmSessionManager);
        player = ExoPlayerFactory.newSimpleInstance(requireActivity(), renderersFactory, trackSelector);
        player.prepare(ExoUtils.buildMediaSource(requireActivity(), mp4videoUrl));
        player.seekTo(playbackPosition);
        player.setPlaybackParameters(new PlaybackParameters(playbackSpeed));
        if (trackSelector != null && trackSelector.getCurrentMappedTrackInfo() != null) {
            DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(0, qualitySelection);
            trackSelector.setSelectionOverride(0, trackSelector.getCurrentMappedTrackInfo().getTrackGroups(0), override);
        }
        player.setPlayWhenReady(true);
        playerView.setPlayer(player);
        player.addListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exo_playback_speed:
                String[] optionsArr = getResources().getStringArray(R.array.PlaybackSpeed);
                AlertDialog alertDialog = new AlertDialog.Builder(requireActivity(), R.style.MyDialogTheme)
                        .setTitle("Select playback speed")
                        .setItems(optionsArr, this)
                        .create();
                alertDialog.show();
                break;
            case R.id.exo_setting:

                setPopUpView();

                break;
            case R.id.exo_fullscreen:
                if (getActivity() instanceof LiveStreamActivity || getActivity() instanceof LiveCourseActivity
                        || getActivity() instanceof VideoDetail || getActivity() instanceof PlayerActivityNew
                        /*|| getActivity() instanceof PostActivity*/ || getActivity() instanceof DVLVideosActivity
                        || getActivity() instanceof VideoSolution) {
                    eMedicozApp.getInstance().savePlayerState = true;
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    else
                        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                } else {
                    Intent intent = new Intent(getActivity(), FullScreenVideoActivity.class);
                    intent.putExtra(Constants.EXTRA_VIDEO_URL, mp4videoUrl.toString());
                    intent.putExtra(EXTRA_PLAYBACK_POSITION_MS, player != null ? player.getCurrentPosition() : 0);
                    intent.putExtra(EXTRA_PLAYBACK_QUALITY, qualitySelection);
                    intent.putExtra(EXTRA_PLAYBACK_SPEED_PARAM, playbackSpeed);
                    intent.putExtra(EXTRA_PLAYBACK_SPEED_TEXT, playbackSpeedButton.getText());
                    intent.putExtra(Constants.EXTRA_LIVE_VIDEO, getActivity() instanceof LiveStreamActivity || getActivity() instanceof LiveCourseActivity);
                    intent.putExtra("TOKEN",drmToken);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                break;
        }
    }

    public void setPopUpView() {
        /*View popupView =  LayoutInflater.from(getActivity()).inflate(R.layout.pop_layout, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setInputMethodMode(INPUT_METHOD_NEEDED);
        popupWindow.showAsDropDown(popupView, 0, 0);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);*/

        /*AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.pop_layout, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.99);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*1.00);

        dialog.show();
        dialog.getWindow().setLayout(width, height);*/


        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_layout, null);
        bottomSheetDialog.setContentView(popupView);


        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) popupView.getParent());


        bottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(popupView.getHeight());//get the height dynamically
        });


        LinearLayout report = bottomSheetDialog.findViewById(R.id.report);
        TextView quality12 = bottomSheetDialog.findViewById(R.id.quality);
        LinearLayout qualityLay = bottomSheetDialog.findViewById(R.id.qualityLay);

        TrackGroupArray trackGroups = null;
        String[] videoQuality, tracks;
        if (trackSelector.getCurrentMappedTrackInfo() != null) {
            trackGroups = trackSelector.getCurrentMappedTrackInfo().getTrackGroups(0);
            int qualityArrSize = Math.min(trackGroups.get(0).length, 3) + 1; // Its fixed for Auto, High, Medium and low

            videoQuality = new String[qualityArrSize];
            tracks = new String[qualityArrSize];

            for (int i = 0, j = 1; i < trackGroups.get(0).length; i++) {
                int qualityValue = trackGroups.get(0).getFormat(i).height;
                if (qualityValue > highestVideoQuality)
                    highestVideoQuality = qualityValue;
                String quality = ExoUtils.getQualityString(qualityValue);
                if (ExoUtils.findIndexOfHighestQuality(videoQuality, quality) == -1) {
                    videoQuality[j] = quality;
                    tracks[j++] = String.valueOf(qualityValue).concat("p");
                }
            }
        } else {
            videoQuality = new String[1];
            tracks = new String[1];
        }

        videoQuality[0] = "Auto";
        tracks[0] = String.valueOf(highestVideoQuality).concat("p");
//        String[] finalVideoQuality = ExoUtils.removeNullFromArray(videoQuality);
//        TrackGroupArray finalTrackGroups = trackGroups;

        if (qualitySelection == 0) {
            quality12.setText(videoQuality[0] + "(" + tracks[0] + ")");
        } else {
            quality12.setText(videoQuality[qualitySelection] + "(" + tracks[qualitySelection] + ")");
        }


        assert qualityLay != null;
        qualityLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                showQualityPopUp();
            }
        });

        assert report != null;
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                networkCallForReportList();

            }
        });


        bottomSheetDialog.show();
    }

    private void networkCallForReportList() {
        reportReasonsArrayList = new ArrayList<Report_reasons>();
        SingleFeedviewApis apis = ApiClient.createService(SingleFeedviewApis.class);
        Call<JsonObject> response = apis.getAbuseList();
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONArray arrJson = GenericUtils.getJsonObject(jsonResponse).optJSONArray("report_reasons");
                            if (arrJson != null) {
                                for (int i = 0; i < arrJson.length(); i++) {
                                    JSONObject jsonObject1 = arrJson.optJSONObject(i);
                                    Report_reasons reportReasons = new Gson().fromJson(jsonObject1.toString(), Report_reasons.class);
                                    reportReasonsArrayList.add(reportReasons);
                                }
                            }
                            showReportPostView();
                        } else {
                            Toast.makeText(requireActivity(), jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(requireActivity(), jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void showReportPostView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.report_layout, null);

//        builder.setView(view);
//        AlertDialog dialog = builder.create();
//        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
//        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
//
//        dialog.show();
//        dialog.getWindow().setLayout(width, height);


        final Dialog reportPost = new Dialog(getActivity());
        reportPost.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reportPost.setCanceledOnTouchOutside(false);
        reportPost.setContentView(v);
        reportPost.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        reportPost.show();


        RecyclerView reportPostList;
        CardView submitCard, upperCard;
        ImageView cross;
        EditText writefeedbackET;


        reportPostList = v.findViewById(R.id.reasonListReport);
        submitCard = v.findViewById(R.id.bottomCard);
        upperCard = v.findViewById(R.id.upperCard);
        cross = v.findViewById(R.id.crossimageIV);
        writefeedbackET = v.findViewById(R.id.writefeedbackET);

        if (reportPostList.getVisibility() == View.GONE) {
            reportPostList.setVisibility(View.VISIBLE);
            writefeedbackET.setVisibility(View.VISIBLE);
            upperCard.setVisibility(View.VISIBLE);
        }


        reportPostList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        ReportListAdapter reportAbuseListAdapter = new ReportListAdapter(getActivity(), reportReasonsArrayList, ExoPlayerFragment.this);
        reportPostList.setAdapter(reportAbuseListAdapter);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportPost.dismiss();
            }
        });


        submitCard.setTag(R.id.questions, v);
        submitCard.setTag(R.id.optionsAns, reportPost);
        submitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = (View) view.getTag(R.id.questions);
                Dialog reportDialog = (Dialog) view.getTag(R.id.optionsAns);
                if (!TextUtils.isEmpty(((EditText) v.findViewById(R.id.writefeedbackET)).getText().toString().trim())) {
                    reportText = ((EditText) v.findViewById(R.id.writefeedbackET)).getText().toString().trim();
                    networkCallForReportPost();
                    reportDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please enter your feedback about this post", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public void setReportId(String id) {
        reportId = id;
        Log.e("ReportId :", reportId);
    }

    private void networkCallForReportPost() {
        SingleFeedviewApis apis = ApiClient.createService(SingleFeedviewApis.class);
        Call<JsonObject> response = apis.getReportPost(SharedPreference.getInstance().getLoggedInUser().getId(),
                videoId, reportId, reportText);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
//                            Toast.makeText(getContext(), jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "Video Reported.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "This video is already reported", Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(getContext(), jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void showQualityPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Select Video Quality");

        TrackGroupArray trackGroups = null;
        String[] videoQuality, tracks;
        if (trackSelector.getCurrentMappedTrackInfo() != null) {
            trackGroups = trackSelector.getCurrentMappedTrackInfo().getTrackGroups(0);
            int qualityArrSize = Math.min(trackGroups.get(0).length, 3) + 1; // Its fixed for Auto, High, Medium and low

            videoQuality = new String[qualityArrSize];
            tracks = new String[qualityArrSize];

            for (int i = 0, j = 1; i < trackGroups.get(0).length; i++) {
                int qualityValue = trackGroups.get(0).getFormat(i).height;
                if (qualityValue > highestVideoQuality)
                    highestVideoQuality = qualityValue;
                String quality = ExoUtils.getQualityString(qualityValue);
                if (ExoUtils.findIndexOfHighestQuality(videoQuality, quality) == -1) {
                    videoQuality[j] = quality;
                    tracks[j++] = String.valueOf(qualityValue).concat("p");
                }
            }
        } else {
            videoQuality = new String[1];
            tracks = new String[1];
        }

        videoQuality[0] = "Auto";
        tracks[0] = String.valueOf(highestVideoQuality).concat("p");
        String[] finalVideoQuality = ExoUtils.removeNullFromArray(videoQuality);
        TrackGroupArray finalTrackGroups = trackGroups;
        builder.setSingleChoiceItems(finalVideoQuality, qualitySelection, (dialog, checkedQuality1) -> {
            qualitySelection = checkedQuality1;
            int index = 0;
            if (finalVideoQuality.length == 1) {
                dialog.dismiss();
                return;
            }
            if (checkedQuality1 == 0) {
                int tempIndex = ExoUtils.findIndexOfHighestQuality(tracks, String.valueOf(highestVideoQuality));
                index = tempIndex < 1 ? 0 : tempIndex - 1;

            } else
                index = checkedQuality1 - 1;

            DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(0, index);
            trackSelector.setSelectionOverride(0, finalTrackGroups, override);


            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        PlayerState playerState = new PlayerState();
        playerState.setPlayer(playerView.getPlayer());
        playerState.setPlaybackSpeed(playbackSpeed);
        playbackSpeedText = playbackSpeedButton.getText().toString();
        playerState.setPlaybackSpeedText(playbackSpeedText);
        playerState.setQualitySelection(qualitySelection);

        eMedicozApp.getInstance().playerState = playerState;
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray
            trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if (playWhenReady && playbackState == Player.STATE_READY) {
            // media actually playing
            startTimer();
        } else if (playWhenReady) {
            stopTimer();
            // might be idle (plays after prepare()),
            // buffering (plays when data available)
            // or ended (plays when seek away from end)
        } else {
            stopTimer();
            // player paused in any state
        }

        if (playbackState == Player.STATE_ENDED) {
            if (getActivity() instanceof PlayerActivityNew && ((PlayerActivityNew) getActivity()) != null) {
                ((PlayerActivityNew) getActivity()).callUpdateContentViewStatusApi();
            }
        }

/*        if (!GenericUtils.isEmpty(remainingTime)) {
            if (playWhenReady && playbackState == Player.STATE_READY) {
                progressBar.setVisibility(View.GONE);
                noVideo.setVisibility(View.GONE);
                if (isPlaying()) {
                    setTimer(totalRemainingTime);
                }
            } else if (playWhenReady) {
                progressBar.setVisibility(View.GONE);
                noVideo.setVisibility(View.GONE);
                if (countDownTimer != null) countDownTimer.cancel();
            } else {
                if (countDownTimer != null) countDownTimer.cancel();
            }
        } else {
            if (playWhenReady) {
                progressBar.setVisibility(View.GONE);
                noVideo.setVisibility(View.GONE);
            }
        }*/
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

        if (getActivity() instanceof LiveStreamActivity)
            ((LiveStreamActivity) getActivity()).inErrorState = true;
        else if (getActivity() instanceof LiveCourseActivity)
            ((LiveCourseActivity) getActivity()).inErrorState = true;
        else if (getActivity() instanceof VideoDetail)
            ((VideoDetail) getActivity()).inErrorState = true;
        else if (getActivity() instanceof PlayerActivityNew)
            ((PlayerActivityNew) getActivity()).inErrorState = true;

        if (Objects.requireNonNull(error.getMessage()).contains("Unable to connect to")) {
            if (layoutRetry != null)
                layoutRetry.setVisibility(View.VISIBLE);
            playbackPosition = player.getCurrentPosition();
        }
        if (Constants.MAX_TRY_VIDEO != repeatVideoCounter) {
            if (!isBehindLiveWindow(error)) {
                player.stop();
                if (loopingSource != null)
                    player.prepare(loopingSource);
            }

            player.setRepeatMode(Player.REPEAT_MODE_ONE);
            player.setPlayWhenReady(true);
            repeatVideoCounter++;
        } else {
            releasePlayer();
        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {

            case 0:
                playbackSpeed = 0.5f;
                break;
            case 1:
                playbackSpeed = 1.0f;
                break;
            case 2:
                playbackSpeed = 1.25f;
                break;
            case 3:
                playbackSpeed = 1.5f;
                break;
            case 4:
                playbackSpeed = 1.7f;
                break;
            case 5:
                playbackSpeed = 2.0f;
                break;
            case 6:
                playbackSpeed = 2.5f;
                break;
            case 7:
                playbackSpeed = 3.0f;
                break;
        }

        if (player != null) {
            playbackSpeedButton.setText(getResources().getStringArray(R.array.PlaybackSpeedText)[which]);
            PlaybackParameters param = new PlaybackParameters(playbackSpeed);
            player.setPlaybackParameters(param);
        }
    }

    private void startTimer() {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        if (videoPreviewDuration != 0)
            previewVideoTimer = new CountDownTimer((videoPreviewDuration + 2) * 1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    String ff = "VOD Time\n <font color='red'>" + formatSeconds((int) millisUntilFinished / 1000) + "</font>";
                    Log.e("TAG", "onTick: timeLeft: " + ff);
                    videoPreviewDuration--;
                    tvTimeLeft.setText(String.format("Time left: %s", formatSeconds((int) millisUntilFinished / 1000)));
                }

                public void onFinish() {
                    Log.e(TAG, "onFinish: ");
                    releasePlayer();
                    noVideo.setVisibility(View.VISIBLE);
                    noVideo.setText(R.string.videoo_preview_limit);
                }

            }.start();
    }

    private void stopTimer() {
        if (previewVideoTimer != null)
            previewVideoTimer.cancel();
    }

    // TODO : must implement PallyconEventListener
    private PallyconEventListener pallyconEventListener = new PallyconEventListener() {
        @Override
        public void onDrmKeysLoaded(Map<String, String> licenseInfo) {
        }

        @Override
        public void onDrmSessionManagerError(Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDrmKeysRestored() {
        }

        @Override
        public void onDrmKeysRemoved() {
        }
    };


    //---------------------------START VIEW COUNT CODE:-This can be used later--------------

/*    public void networkCallForGetVideoDuration() {
if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e(TAG, "networkCallForGetVideoDuration: ");
        Progress mprogress = new Progress(context);
        mprogress.setCancelable(false);
        mprogress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Log.e(TAG, "networkCallForGetVideoDuration: " + "videoId " + videoId + "type " + videoType);
        Call<JsonObject> response = apiInterface.getVideoDuration(SharedPreference.getInstance().getLoggedInUser().getId()
                , videoId, videoType);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    mprogress.dismiss();
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e(TAG, "onResponse: " + jsonResponse.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            Log.e(TAG, "onResponse: " + Objects.requireNonNull(jsonResponse.optJSONObject("data")).optString("remaning_time"));
                            remainingTime = Objects.requireNonNull(jsonResponse.optJSONObject("data")).optString("remaning_time");
                            watched = Objects.requireNonNull(jsonResponse.optJSONObject("data")).optString("watched");
                            totalRemainingTime = TimeUnit.MINUTES.toMillis(Long.parseLong(remainingTime));
                        } else {
                            //    RetrofitResponse.GetApiData(context, API.API_GET_FILE_LIST_CURRICULUM);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "failed");
                mprogress.dismiss();
            }
        });
    }*/

/*    private void setTimer(long time) {

        countDownTimer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                String ff = "VOD Time\n <font color='red'>" + formatSeconds((int) millisUntilFinished / 1000) + "</font>";
                Log.e("TAG", "onTick: timeFinished: " + ff);
                if (durationTV!=null) {
                    durationTV.setVisibility(View.VISIBLE);
                    durationTV.setText("Duration : " + formatSeconds((int) millisUntilFinished / 1000));
                }
                totalRemainingTime = millisUntilFinished;
            }

            public void onFinish() {
                Log.e(TAG, "onFinish: ");
                releasePlayer();
                noVideo.setVisibility(View.VISIBLE);
                noVideo.setText("You have completed your view limit...");
            }

        }.start();
    }*/

/*    public boolean isPlaying() {
        return playerView.getPlayer().getPlaybackState() == Player.STATE_READY && playerView.getPlayer().getPlayWhenReady();
    }

    public void networkCallForPostVideoDuration() {
    if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        long watch = TimeUnit.MINUTES.toMillis(Long.parseLong(remainingTime)) - totalRemainingTime;
        Log.e(TAG, "watch: "+TimeUnit.MILLISECONDS.toMinutes(watch) );
        long totalWatched = Long.parseLong(watched) + (TimeUnit.MILLISECONDS.toMinutes(watch));
        Log.e(TAG, "totalWatched" + totalWatched);
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.postVideoDuration(SharedPreference.getInstance().getLoggedInUser().getId()
                , videoId, videoType, String.valueOf(totalWatched));
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

                        } else {
                            //RetrofitResponse.GetApiData(context, API.API_GET_FILE_LIST_CURRICULUM);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }*/

//-------------------------------------END VIEW COUNT CODE---------------------------------------

}
