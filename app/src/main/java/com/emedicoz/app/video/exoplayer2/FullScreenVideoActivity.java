package com.emedicoz.app.video.exoplayer2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.emedicoz.app.BuildConfig;
import com.emedicoz.app.R;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.pallycon.widevinelibrary.PallyconDrmException;
import com.pallycon.widevinelibrary.PallyconEventListener;
import com.pallycon.widevinelibrary.PallyconWVMSDK;
import com.pallycon.widevinelibrary.PallyconWVMSDKFactory;
import com.pallycon.widevinelibrary.UnAuthorizedDeviceException;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FullScreenVideoActivity extends AppCompatActivity implements View.OnClickListener,
        DialogInterface.OnClickListener {

    String videoUrl;
    Long playbackPositionMs;
    Float playbackSpeed;
    PlayerView playerView;
    ProgressBar loader;
    View layoutRetry;
    View btnRetry;
    ImageView fullscreenButton;
    TextView playbackSpeedButton;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;
    private int qualitySelection = 0;
    TextView floatingText;
    private int highestVideoQuality;
    private boolean openInPortraitMode;
    private boolean liveVideo;
    private String playbackSpeedText = "1x";
    String drmToken = "";

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_full_screen2);
        unbinder = ButterKnife.bind(this);
        playerView = findViewById(R.id.player_view_2);
        loader = findViewById(R.id.loader);
        layoutRetry = findViewById(R.id.layout_retry);
        btnRetry = findViewById(R.id.retry_button);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        videoUrl = getIntent().getStringExtra(Constants.EXTRA_VIDEO_URL);
        playbackPositionMs = getIntent().getLongExtra(Constants.EXTRA_PLAYBACK_POSITION_MS, 0);
        qualitySelection = getIntent().getIntExtra(Constants.EXTRA_PLAYBACK_QUALITY, 0);
        playbackSpeed = getIntent().getFloatExtra(Constants.EXTRA_PLAYBACK_SPEED_PARAM, 1f);
        openInPortraitMode = getIntent().getBooleanExtra(Constants.EXTRA_PORTRAIT_MODE, false);
        liveVideo = getIntent().getBooleanExtra(Constants.EXTRA_LIVE_VIDEO, false);
        playbackSpeedText = getIntent().getStringExtra(Constants.EXTRA_PLAYBACK_SPEED_TEXT);
        drmToken = getIntent().getStringExtra("TOKEN") != null ? getIntent().getStringExtra("TOKEN") : "";

        if (savedInstanceState != null) {
            // The user rotated the screen
            playbackPositionMs = savedInstanceState.getLong(Constants.STATE_PLAYBACK_POSITION_MS);
        }

//        new Handler().postDelayed(this::initializePlayer, 300);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumePlayer();
    }

    private void resumePlayer() {
        String siteId = BuildConfig.SITE_ID;
        String siteKey = BuildConfig.SITE_KEY;
        PallyconWVMSDK WVMAgent = null;
        try {
            WVMAgent = PallyconWVMSDKFactory.getInstance(this);
            WVMAgent.init(this, null, siteId, siteKey);
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
            Log.e("TAG", "loadPlayer: "+Uri.parse(videoUrl) );
            Log.e("TAG", "loadPlayer: "+drmToken );
            // drmSessionManager = WVMAgent.createDrmSessionManager(drmSchemeUuid, drmLicenseUrl, uri, "2b186217-47e6-44a5-a000-b3088f1e7f2e", "9c600cb9-8f3a-4e35-9e93-94a62db30a69", "", false);
            drmSessionManager = WVMAgent.createDrmSessionManagerByToken(
                    drmSchemeUuid,
                    drmLicenseUrl,
                    Uri.parse(videoUrl),
                    SharedPreference.getInstance().getLoggedInUser().getId(),
                    "",
                    drmToken,
                    false);
        } catch (PallyconDrmException e) {
            e.printStackTrace();
        }
        if (trackSelector == null) {
            TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
            trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        }
        renderersFactory = new DefaultRenderersFactory(this, drmSessionManager);
        player = ExoPlayerFactory.newSimpleInstance(this,renderersFactory, trackSelector);
        player.prepare(ExoUtils.buildMediaSource(this, Uri.parse(videoUrl)));
        player.seekTo(playbackPositionMs);
        player.setPlaybackParameters(new PlaybackParameters(playbackSpeed));
        if (trackSelector != null && trackSelector.getCurrentMappedTrackInfo() != null) {
            DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(0, qualitySelection);
            trackSelector.setSelectionOverride(0, trackSelector.getCurrentMappedTrackInfo().getTrackGroups(0), override);
        }
        player.setPlayWhenReady(true);
        playerView.setPlayer(player);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady)
                    loader.setVisibility(View.GONE);
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

                if (Objects.requireNonNull(error.getMessage()).contains("Unable to connect to")) {
                    layoutRetry.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.VISIBLE);
                    playbackPositionMs = player.getCurrentPosition();
                }
            }
        });

        floatingText = findViewById(R.id.floatingText_video_detail);
        playerView.findViewById(R.id.exo_duration).setVisibility(View.VISIBLE);
        playerView.findViewById(R.id.exo_orientation_lock).setVisibility(View.VISIBLE);

        fullscreenButton = playerView.findViewById(R.id.exo_fullscreen);
        fullscreenButton.setOnClickListener(this);
        playbackSpeedButton = playerView.findViewById(R.id.exo_playback_speed);
        playbackSpeedButton.setOnClickListener(this);
        playbackSpeedButton.setText(playbackSpeedText);

        Helper.blink(this, playerView, floatingText);
        btnRetry.setOnClickListener(v -> {
            layoutRetry.setVisibility(View.GONE);
            resumePlayer();
        });
        playerView.findViewById(R.id.exo_orientation_lock).setVisibility(View.VISIBLE);
        playerView.findViewById(R.id.exo_orientation_lock).setOnClickListener(this);

        if (openInPortraitMode)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState,
                                    @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putLong(Constants.STATE_PLAYBACK_POSITION_MS, player.getCurrentPosition());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            openInPortraitMode = false;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ExoUtils.setFullScreen(this, playerView, true);
            if (fullscreenButton != null)
                fullscreenButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.fullscreen_exit));

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            openInPortraitMode = true;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ExoUtils.setFullScreen(this, playerView, false);
            if (fullscreenButton != null)
                fullscreenButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.fullscreen_icon));
        }

//        new Handler().postDelayed(this::initializePlayer, 100);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.exo_orientation_lock:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                break;
            case R.id.exo_playback_speed:
                String[] optionsArr = getResources().getStringArray(R.array.PlaybackSpeed);
                AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme)
                        .setTitle("Select playback speed")
                        .setItems(optionsArr, this)
                        .create();
                alertDialog.show();
                break;

            case R.id.exo_setting:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                break;

            case R.id.exo_fullscreen:
                Intent intent = new Intent();
                intent.putExtra(Constants.EXTRA_PLAYBACK_POSITION_MS, player != null ? player.getCurrentPosition() : 0);
                intent.putExtra(Constants.EXTRA_PLAYBACK_SPEED_PARAM, player != null ? player.getPlaybackParameters().speed : 0);
                intent.putExtra(Constants.EXTRA_PLAYBACK_QUALITY, qualitySelection);
                intent.putExtra(Constants.EXTRA_PLAYBACK_SPEED_TEXT, playbackSpeedButton.getText());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        playbackSpeedText = playbackSpeedButton.getText().toString();
        if (player != null)
            player.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();

        if (player != null)
            player.release();
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
            default:
        }

        if (player != null) {
            playbackSpeedButton.setText(getResources().getStringArray(R.array.PlaybackSpeedText)[which]);
            PlaybackParameters param = new PlaybackParameters(playbackSpeed);
            player.setPlaybackParameters(param);
        }
    }

    // TODO : must implement PallyconEventListener
    private PallyconEventListener pallyconEventListener = new PallyconEventListener() {
        @Override
        public void onDrmKeysLoaded(Map<String, String> licenseInfo) {
        }

        @Override
        public void onDrmSessionManagerError(Exception e) {
            Toast.makeText(FullScreenVideoActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDrmKeysRestored() {
        }

        @Override
        public void onDrmKeysRemoved() {
        }
    };

}