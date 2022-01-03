package com.emedicoz.app.video.exoplayer2;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import com.emedicoz.app.R;
import com.emedicoz.app.utilso.GenericUtils;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class ExoUtils {

    public static MediaSource buildMediaSource(Activity activity, Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(activity, Util.getUserAgent(activity, activity.getResources().getString(R.string.app_name)));
        if (uri.getLastPathSegment() != null && uri.getLastPathSegment().contains("m3u8")) {

            return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        } else if (uri.getLastPathSegment() != null &&
                (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4"))) {

            return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
//            ProgressiveMediaSource.Factory mediaSourceFactory = new ProgressiveMediaSource.Factory(dataSourceFactory);
//            return mediaSourceFactory.createMediaSource(uri);
        } else {
            return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        }
    }

    public static void setFullScreen(Activity activity, View playerView, boolean setFullScren) {
        if (setFullScren) {
            hideSystemBar(activity);
            playerView.getLayoutParams().height = (int) (GenericUtils.getScreenDimension(activity, true)[0]);
        } else {
            showSystemBar(activity);
            float screenWidth = GenericUtils.getScreenDimension(activity, true)[1];
            playerView.getLayoutParams().height = (int) (screenWidth * 0.5625);
        }
    }

    // show video on full screen
    private static void showSystemBar(Activity activity) {
        try {
            if (activity.getWindow() != null) {
                View decorView = activity.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*to hide the status bar*/
    private static void hideSystemBar(Activity activity) {
        try {
            if (activity.getWindow() != null) {
                View decorView = activity.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getQualityString(int value) {
        if (value < 360)
            return "Low";
        else if (value < 480)
            return "Medium";
        else
            return "High";
    }

    public static int findIndexOfHighestQuality(String[] videoQuality, String item) {

        for (int i = 0; i < videoQuality.length; i++)
            if (videoQuality[i] != null && videoQuality[i].contains(item))
                return i;
        return -1;
    }

    public static String[] removeNullFromArray(String[] videoQuality) {
        ArrayList<String> stringArrayList = new ArrayList<String>();

        for (String s : videoQuality)
            if (s != null)
                stringArrayList.add(s);

        return stringArrayList.toArray(new String[stringArrayList.size()]);
    }
}
