package com.emedicoz.app.customviews;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.emedicoz.app.R;

/**
 * Created by Cbc-03 on 05/24/17.
 */

public class Progress extends Dialog {

    private static final String TAG = "Progress";
    Context context;

    public Progress(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        setContentView(R.layout.progress_layout);

        super.setCancelable(false);
    }

    @Override
    public void show() {
//        Log.e(TAG, "show: " + context);
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}


