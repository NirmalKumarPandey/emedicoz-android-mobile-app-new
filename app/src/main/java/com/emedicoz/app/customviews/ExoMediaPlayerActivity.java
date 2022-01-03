/*
package com.emedicoz.app.CustomViews;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import com.emedicoz.app.R;

public class ExoMediaPlayerActivity extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_media_player);
    }

    private void setupVideoView() {
        // Make sure to use the correct VideoView import
        videoView = findViewById(R.id.videoView);

        videoView.setMediaController(new MediaController(ExoMediaPlayerActivity.this));
        Uri uri = Uri.parse("https://dams-apps-production.s3.ap-south-1.amazonaws.com/vod/932589Sumer-sir-presentation.m3u8");
        //video.setDataSource("rtsp://1.2.3.4/app/stream");
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();

            }
        });
        //For now we just picked an arbitrary item to play
        //videoView.setVideoURI(Uri.parse("https://dams-apps-production.s3.ap-south-1.amazonaws.com/vod/932589Sumer-sir-presentation.m3u8"));
    }
}
*/
