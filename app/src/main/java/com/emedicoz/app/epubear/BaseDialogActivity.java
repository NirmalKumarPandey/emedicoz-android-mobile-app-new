package com.emedicoz.app.epubear;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

/**
 * Created by amykh on 06.09.2016.
 */
public abstract class BaseDialogActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(getOpenAnimationRes(), 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, getCloseAnimationRes());
    }

    protected int getOpenAnimationRes() {
        return android.R.anim.fade_in;
    }

    protected int getCloseAnimationRes() {
        return android.R.anim.fade_out;
    }

    public void onOutsideClick(View v) {
        setResult(RESULT_CANCELED);
        this.finish();
    }
}
