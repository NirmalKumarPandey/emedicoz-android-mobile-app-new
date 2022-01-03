package com.emedicoz.app.utilso;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;

import androidx.multidex.MultiDexApplication;

import com.balsikandar.crashreporter.CrashReporter;
import com.emedicoz.app.R;
import com.emedicoz.app.modelo.PlayerState;
import com.emedicoz.app.modelo.PostFile;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nekolaboratory.EmulatorDetector;
import com.uxcam.UXCam;

import java.util.Calendar;

import fr.maxcom.libmedia.Licensing;

/**
 * Created by Cbc-03 on 09/13/16.
 */
public class eMedicozApp extends MultiDexApplication {

    protected static eMedicozApp appInstance;
    private static Context appContext;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    public String rank;
    public boolean savePlayerState;
    public Dialog dialog;
    FirebaseAnalytics firebaseAnalytics;
    public PlayerState playerState;
    private MediaPlayer podcastPlayer;
    boolean darkMode = false;
    public PostFile postFile;
    private Calendar myCalendar;
    public String searchedKeyword;
    public String questionQuery;
    public String filterType;
    public boolean refreshTestScreen = false;

    public static Context getAppContext() {
        return appContext;
    }

    public static eMedicozApp getInstance() {
        return appInstance;
    }

    private static void setInstance(eMedicozApp app) {
        appInstance = app;
    }

    public MediaPlayer getPodcastPlayer() {
        return podcastPlayer;
    }

    public void setPodcastPlayer(MediaPlayer podcastPlayer) {
        this.podcastPlayer = podcastPlayer;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        setInstance(this);
        sAnalytics = GoogleAnalytics.getInstance(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        EmulatorDetector.isEmulator(this);
        CrashReporter.initialize(this);
        // Libmedia requirement
        Licensing.allow(this);
        // optional
        Licensing.setDeveloperMode(true);
        darkMode = SharedPreference.getInstance().getBoolean(Const.DARK_MODE);
        if (darkMode)
            ThemeHelper.applyTheme("dark");
        else
            ThemeHelper.applyTheme("light");

        //add UX cam for monitoring
        UXCam.startWithKey(Constants.CAM_KEY);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }

    public Calendar getDcCalendar() {
        if (myCalendar == null)
            myCalendar = Calendar.getInstance();
        return myCalendar;
    }
}
