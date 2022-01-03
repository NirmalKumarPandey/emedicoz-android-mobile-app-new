package com.emedicoz.app.epubear.reader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.emedicoz.app.R;
import com.emedicoz.app.epubear.BaseDialogActivity;
import com.emedicoz.app.utilso.GenericUtils;

/**
 * Created by amykh on 06.09.2016.
 */
public class FontSizeActivity extends BaseDialogActivity {

    public static final String FONT_SIZE_ACTION = "FONT_SIZE_ACTION";
    public static final int RESULT_CANCEL = 0;
    public static final int RESULT_INCREASE = 1;
    public static final int RESULT_DECREASE = 2;

    private ImageView mIncreaseButton;
    private ImageView mDecreaseButton;

    private Intent mIntent = new Intent("android.intent.action.FONT_SIZE");
    View.OnClickListener mOnIncreaseBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Intent intent = new Intent();
//            intent.putExtra(FONT_SIZE_ACTION, RESULT_INCREASE);
//            setResult(RESULT_OK, intent);
//            finish();

//            intent with intent filter for increase button
            mIntent.putExtra("msg", "plus");
            getBaseContext().sendBroadcast(mIntent);
        }
    };
    View.OnClickListener mOnDecreaseBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Intent intent = new Intent();
//            intent.putExtra(FONT_SIZE_ACTION, RESULT_DECREASE);
//            setResult(RESULT_OK, intent);
//            finish();
//            intent with intent filter for decreace button
            mIntent.putExtra("msg", "minus");
            getBaseContext().sendBroadcast(mIntent);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        mPreferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);

        setContentView(R.layout.activity_font_size);

        findView();
        setListeners();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void findView() {
        mIncreaseButton = findViewById(R.id.increase_button);
        mDecreaseButton = findViewById(R.id.decrease_button);
    }

    @Override
    protected void setListeners() {
        mIncreaseButton.setOnClickListener(mOnIncreaseBtn);
        mDecreaseButton.setOnClickListener(mOnDecreaseBtn);
    }
}
