package com.emedicoz.app.epubear.utils.launch_manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by amykh on 29.08.2016.
 */
public class BaseActivityLaunchManager {
    protected Context mContext;

    public BaseActivityLaunchManager(Context context) {
        mContext = context;
    }

    protected void startActivity(Class<?> clazz, Bundle extras) {
        Intent intent = new Intent(mContext, clazz);
        intent.replaceExtras(extras);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    protected void startActivityForResult(Activity activity, Class<?> cls, int requestCode) {
        Intent intent = new Intent(mContext, cls);
        activity.startActivityForResult(intent, requestCode);
    }
}
