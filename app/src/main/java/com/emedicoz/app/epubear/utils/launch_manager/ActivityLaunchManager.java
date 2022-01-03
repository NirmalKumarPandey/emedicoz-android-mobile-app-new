package com.emedicoz.app.epubear.utils.launch_manager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.emedicoz.app.epubear.SettingActivity;
import com.emedicoz.app.epubear.reader.FontSizeActivity;
import com.emedicoz.app.epubear.reader.ReaderActivity;

import java.io.File;

/**
 * Created by amykh on 29.08.2016.
 */
public class ActivityLaunchManager extends BaseActivityLaunchManager {

    public ActivityLaunchManager(Context context) {
        super(context);
    }

    public void launchReaderActivity(File document) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ReaderActivity.KEY_CURRENT_FILE_EXTRA, document);
        startActivity(ReaderActivity.class, bundle);
    }

    public void launchFontSizeActivity(Activity activity) {
        startActivityForResult(activity, FontSizeActivity.class, 1);
    }

    public void launchSettingActivity() {
        startActivity(SettingActivity.class, null);
    }
}
