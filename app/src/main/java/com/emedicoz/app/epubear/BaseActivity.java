package com.emedicoz.app.epubear;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.emedicoz.app.epubear.utils.launch_manager.ActivityLaunchManager;

/**
 * Created by amykh on 29.08.2016.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static final String PREFERENCES_KEY = "PREFERENCES_KEY";
    protected static final String NIGHT_MODE_PREF = "NIGHT_MODE_PREF";
    protected static final String CURRENT_PAGE_PREF = "CURRENT_PAGE_PREF";
    protected static final String CURRENT_FONT_SIZE_PREF = "CURRENT_FONT_SIZE_PREF";
    protected static final String UNPACK_DOCUMENT_PREF = "UNPACK_DOCUMENT_PREF";

    protected ActivityLaunchManager mActivityManager;
    protected SharedPreferences mPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityManager = new ActivityLaunchManager(this);
    }

    public ActivityLaunchManager getActivityManager() {
        return mActivityManager;
    }

    protected void replaceFragment(int contentFrame, BaseFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(contentFrame, fragment, fragment.getClass().getCanonicalName());
        transaction.commit();
        fm.executePendingTransactions();
    }

    protected void addFragment(int contentFrame, BaseFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(contentFrame, fragment, fragment.getClass().getCanonicalName());
        transaction.commit();
        fm.executePendingTransactions();
    }

    protected abstract void findView();

    protected abstract void setListeners();
}
