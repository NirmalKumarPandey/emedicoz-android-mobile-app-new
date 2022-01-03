package com.emedicoz.app.ui.podcast;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.podcast.Podcast;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.eMedicozApp;

import java.io.IOException;


public class PodCastMediaPlayerFragment extends Fragment implements OnBackPressed{

MediaPlayer mediaPlayer;
    private static final String PODCAST_KEY = "podcast_key";
    public static PodCastMediaPlayerFragment newInstance(Podcast mPodcast) {

        PodCastMediaPlayerFragment mPodCastMediaPlayerFragment = new PodCastMediaPlayerFragment();
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(PODCAST_KEY,mPodcast);
        mPodCastMediaPlayerFragment.setArguments(mBundle);
        return  mPodCastMediaPlayerFragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.podcastmediaplayer, container, false);


        Podcast mPodcast = (Podcast) getArguments().getSerializable(PODCAST_KEY);
        loadPlayerWithURL(mPodcast);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }


    public void loadPlayerWithURL(Podcast pod) {

        mediaPlayer = eMedicozApp.getInstance().getPodcastPlayer();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            eMedicozApp.getInstance().setPodcastPlayer(mediaPlayer);
        }

//        ((TextView) layoutPlayer.findViewById(R.id.txv_podcast_title)).setText(pod.getTitle());
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(GenericUtils.isEmpty(pod.getDownloadedUrl()) ? pod.getUrl() : pod.getDownloadedUrl());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();
                /*btnPlay.setImageResource(R.drawable.exo_pause);

                seekBar.setMax(mediaPlayer.getDuration());
                txvDuration.setText(getTimeString(mediaPlayer.getDuration()));
                startPlayProgressUpdater();*/

            });

//            mediaPlayer.setOnCompletionListener(mediaPlayer -> btnPlay.setImageResource(R.drawable.exo_play));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer = null;
        }

        //getActivity().getSupportFragmentManager().popBackStack();
    }
}