package com.emedicoz.app.courses.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.DownloadAdapter;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadedDataListFragment extends Fragment {

    private final String tab;
    RecyclerView downloadRV;
    Activity activity;
    LinearLayout noDownloads;

    private static MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    private RelativeLayout layoutPlayer;
    private ImageView btnPlay;
    private TextView txvCurrentPos, txvDuration;
    private SeekBar seekBar;
    private ProgressBar loader;

    @Override
    public void onPause() {
        if (tab.equalsIgnoreCase(Constants.MY_DOWNLOAD_TABS.PODCAST))
            resetMediaPlayer();
        super.onPause();
    }

    public DownloadedDataListFragment() {
        // Required empty public constructor
        tab = "";
    }

    public DownloadedDataListFragment(String tab) {
        this.tab = tab;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_download_data_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        downloadRV = view.findViewById(R.id.downloadVideosRV);
        noDownloads = view.findViewById(R.id.noDownloadedVideos);
        downloadRV.setLayoutManager(new LinearLayoutManager(activity));

        layoutPlayer = view.findViewById(R.id.layout_player);
        btnPlay = view.findViewById(R.id.play);
        loader = view.findViewById(R.id.loader);
        seekBar = view.findViewById(R.id.seek_bar);
        txvCurrentPos = view.findViewById(R.id.txv_current_position);
        txvDuration = view.findViewById(R.id.txv_podcast_duration);

        ArrayList<offlineData> offlineDataList = getOfflineData(eMedicozDownloadManager.getAllOfflineData(activity));
        if (GenericUtils.isListEmpty((offlineDataList))) {
            noDownloads.setVisibility(View.VISIBLE);
            downloadRV.setVisibility(View.GONE);
        } else {
            downloadRV.setVisibility(View.VISIBLE);
            noDownloads.setVisibility(View.GONE);
            DownloadAdapter downloadAdapter = new DownloadAdapter(activity, getAllFiles(activity.getFilesDir()),
                    offlineDataList, tab, pod -> loadPlayerWithURL(pod));
            downloadRV.setAdapter(downloadAdapter);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekChange(seekBar);
            }
        });
        btnPlay.setOnClickListener(view1 -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlay.setImageResource(R.drawable.exo_play);
            } else {
                mediaPlayer.start();
                btnPlay.setImageResource(R.drawable.exo_pause);
            }

            startPlayProgressUpdater();
        });

        view.findViewById(R.id.ffwd).setOnClickListener(view1 -> {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 11000);
                startPlayProgressUpdater();
            }
        });

        view.findViewById(R.id.rew).setOnClickListener(view1 -> {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                startPlayProgressUpdater();
            }
        });

        view.findViewById(R.id.btn_cross).setOnClickListener(view1 -> {
            resetMediaPlayer();
            layoutPlayer.setVisibility(View.GONE);
        });

    }

    private void resetMediaPlayer() {
        MediaPlayer mediaPlayer = eMedicozApp.getInstance().getPodcastPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer = null;
            eMedicozApp.getInstance().setPodcastPlayer(mediaPlayer);
        }
    }

    public void startPlayProgressUpdater() {
        if (mediaPlayer == null) return;
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        txvCurrentPos.setText(getTimeString(mediaPlayer.getCurrentPosition()));

        if (mediaPlayer.isPlaying()) {
            Runnable notification = () -> startPlayProgressUpdater();
            handler.postDelayed(notification, 1000);
        } else {
            btnPlay.setImageResource(R.drawable.exo_play);
        }
    }

    // This is event handler thumb moving event
    private void seekChange(View v) {
        if (mediaPlayer.isPlaying()) {
            SeekBar sb = (SeekBar) v;
            mediaPlayer.seekTo(sb.getProgress());
            txvCurrentPos.setText(getTimeString(sb.getProgress()));
            startPlayProgressUpdater();
        }
    }

    @SuppressLint("DefaultLocale")
    private String getTimeString(long millis) {
        StringBuilder buf = new StringBuilder();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        if (hours != 0) {
            buf.append(String.format("%02d", hours))
                    .append(":")
                    .append(String.format("%02d", minutes))
                    .append(":")
                    .append(String.format("%02d", seconds));
        } else
            buf.append(String.format("%02d", minutes))
                    .append(":")
                    .append(String.format("%02d", seconds));

        return buf.toString();
    }

    public void loadPlayerWithURL(offlineData pod) {

        layoutPlayer.setVisibility(View.VISIBLE);
        mediaPlayer = eMedicozApp.getInstance().getPodcastPlayer();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            eMedicozApp.getInstance().setPodcastPlayer(mediaPlayer);
        }

        ((TextView) layoutPlayer.findViewById(R.id.txv_podcast_title)).setText(pod.getLink());
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(activity.getFilesDir() + "/" + pod.getLink());
            mediaPlayer.prepareAsync();
            loader.setVisibility(View.VISIBLE);
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();
                loader.setVisibility(View.GONE);
                btnPlay.setImageResource(R.drawable.exo_pause);

                seekBar.setMax(mediaPlayer.getDuration());
                txvDuration.setText(getTimeString(mediaPlayer.getDuration()));
                startPlayProgressUpdater();
//                pod.setIsPrepared(true);
//                adapter.notifyDataSetChanged();
            });

            mediaPlayer.setOnCompletionListener(mediaPlayer -> btnPlay.setImageResource(R.drawable.exo_play));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface MediaPrepared {
        void onMediaPrepared(offlineData pod);
    }

    private ArrayList<offlineData> getOfflineData(ArrayList<offlineData> offlineDataArrayList) {
        ArrayList<offlineData> offlineData = new ArrayList<>();
        if (offlineDataArrayList != null) {
            for (offlineData offlineData1 : offlineDataArrayList) {
                switch (tab) {
                    case Constants.MY_DOWNLOAD_TABS.VIDEO:
                        if ((offlineData1.getLink().endsWith(".mp4") || offlineData1.getLink().contains("out_put"))) {
                            offlineData.add(offlineData1);
                        }
                        break;
                    case Constants.MY_DOWNLOAD_TABS.EPUB:
                        if ((offlineData1.getLink().endsWith(".epub"))) {
                            offlineData.add(offlineData1);
                        }
                        break;
                    case Constants.MY_DOWNLOAD_TABS.PDF:
                        if ((offlineData1.getLink().endsWith(".pdf"))) {
                            offlineData.add(offlineData1);
                        }
                        break;
                    case Constants.MY_DOWNLOAD_TABS.PODCAST:
                        if ((offlineData1.getLink().endsWith(".mp3"))) {
                            offlineData.add(offlineData1);
                        }
                        break;
                }
            }
        }
        return offlineData;
    }


    private ArrayList<File> getAllFiles(File parentDir) {
        Log.e("getpath: ", String.valueOf(parentDir));
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files;
        files = parentDir.listFiles();
        Log.e("Files: ", Arrays.toString(files));
        if (files != null) {
            for (File file : files) {
                switch (tab) {
                    case Constants.MY_DOWNLOAD_TABS.VIDEO:
                        if (file.getName().endsWith(".mp4") || file.getName().contains("out_put")) {
                            if (!inFiles.contains(file))
                                inFiles.add(file);
                        }
                        break;
                    case Constants.MY_DOWNLOAD_TABS.EPUB:
                        if (file.getName().endsWith(".epub")) {
                            if (!inFiles.contains(file))
                                inFiles.add(file);
                        }
                        break;
                    case Constants.MY_DOWNLOAD_TABS.PDF:
                        if (file.getName().endsWith(".pdf")) {
                            if (!inFiles.contains(file))
                                inFiles.add(file);
                        }
                        break;
                    case Constants.MY_DOWNLOAD_TABS.PODCAST:
                        if (file.getName().endsWith(".mp3")) {
                            if (!inFiles.contains(file))
                                inFiles.add(file);
                        }
                        break;
                }
            }
            Log.e("getListFiles: ", String.valueOf(inFiles));
        }
        return inFiles;
    }
}
