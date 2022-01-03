package com.emedicoz.app.feeds.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.emedicoz.app.R;
import com.emedicoz.app.retrofit.Constants;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YouTubeVideoPlayer extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    public String VIDEO_ID = "";
    private YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_video_player);

        init();
        VIDEO_ID = getIntent().getStringExtra(Const.YOUTUBE_ID);
        // Log.e("ID_Video", VIDEO_ID);

        youTubePlayerView.initialize(Constants.getAPIKEY1() +
                Constants.getAPIKEY2() + Constants.getAPIKEY3() + Constants.getAPIKEY4(), this);
    }

    private void init() {

        youTubePlayerView = findViewById(R.id.youtube_player_view);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (null == player) return;

        // Start buffering
        if (!wasRestored) {
            try {
                player.loadVideo(VIDEO_ID);
                player.play();
            } catch (IllegalStateException e) {
                initialize();
            }
        }

        // Add listeners to YouTubePlayer instance
        player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onAdStarted() {
            }

            @Override
            public void onError(YouTubePlayer.ErrorReason arg0) {
            }

            @Override
            public void onLoaded(String arg0) {
            }

            @Override
            public void onLoading() {
            }

            @Override
            public void onVideoEnded() {
            }

            @Override
            public void onVideoStarted() {
            }
        });


        player.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onBuffering(boolean arg0) {
            }

            @Override
            public void onPaused() {
            }

            @Override
            public void onPlaying() {
            }

            @Override
            public void onSeekTo(int arg0) {
            }

            @Override
            public void onStopped() {
            }
        });
    }

    private void initialize() {
        youTubePlayerView.initialize(Constants.getAPIKEY1() +
                Constants.getAPIKEY2() + Constants.getAPIKEY3() + Constants.getAPIKEY4(), this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
        String errorMessage = error.toString();
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.d("errorMessage:", errorMessage);
    }
}
