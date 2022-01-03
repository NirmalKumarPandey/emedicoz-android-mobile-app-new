package com.emedicoz.app.utilso;

import android.app.UiModeManager;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.emedicoz.app.BuildConfig;

public class UiModeManagerUtil {
    private static final String TAG = "UiModeManagerUtil";

    /**
     * Enhanced version of UiModeManager.getNightMode() which considers Car Mode disabled on
     * Android < M when deciding if Night Mode should be considered on.
     */
    public static int getNightMode(@NonNull UiModeManager uiModeManager) {
        switch (uiModeManager.getNightMode()) {
            case UiModeManager.MODE_NIGHT_YES:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ||
                        uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_CAR) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "nightMode = on");
                    return UiModeManager.MODE_NIGHT_YES;
                } else {
                    if (BuildConfig.DEBUG) Log.d(TAG, "nightMode = off (Android < M and Car Mode disabled)");
                    return UiModeManager.MODE_NIGHT_NO;
                }
            case UiModeManager.MODE_NIGHT_AUTO:
                if (BuildConfig.DEBUG) Log.d(TAG, "updateRadioGroup nightMode = auto");
                return UiModeManager.MODE_NIGHT_AUTO;
            case UiModeManager.MODE_NIGHT_NO:
            default:
                if (BuildConfig.DEBUG) Log.d(TAG, "nightMode = off");
                return UiModeManager.MODE_NIGHT_NO;
        }
    }

    /**
     * Enhanced version of UiModeManager.setNightMode() which considers Car Mode requirement on
     * Android < M when enabling Night Mode.
     */
    public static void setNightMode(@NonNull UiModeManager uiModeManager, int mode) {
        switch (mode) {
            case UiModeManager.MODE_NIGHT_YES:
                if (BuildConfig.DEBUG) Log.d(TAG, "set nightMode = on");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    uiModeManager.enableCarMode(0);
                uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
                break;
            case UiModeManager.MODE_NIGHT_AUTO:
                if (BuildConfig.DEBUG) Log.d(TAG, "set nightMode = auto");
                uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_AUTO);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    uiModeManager.disableCarMode(0);
                break;
            case UiModeManager.MODE_NIGHT_NO:
            default:
                if (BuildConfig.DEBUG) Log.d(TAG, "set nightMode = off");
                uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    uiModeManager.disableCarMode(0);
                break;
        }
    }

}
