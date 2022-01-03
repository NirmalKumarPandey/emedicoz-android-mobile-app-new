package com.emedicoz.app.epubear;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.emedicoz.app.R;
import com.emedicoz.app.utilso.GenericUtils;


/**
 * Created by amykh on 17.05.2017.
 */

public class SettingActivity extends BaseDialogActivity {
    private SwitchCompat mUnpackSwitch;
    private CompoundButton.OnCheckedChangeListener mOnSwitchChange = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(UNPACK_DOCUMENT_PREF, isChecked);
            editor.commit();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        mPreferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
        boolean unpackOn = mPreferences.getBoolean(UNPACK_DOCUMENT_PREF, true);

        setContentView(R.layout.activity_settings);

        findView();
        setListeners();

        mUnpackSwitch.setChecked(unpackOn);
    }

    @Override
    protected void findView() {
        mUnpackSwitch = findViewById(R.id.unpack_switch);
    }

    @Override
    protected void setListeners() {
        mUnpackSwitch.setOnCheckedChangeListener(mOnSwitchChange);
    }
}
