package com.emedicoz.app.pastpaperexplanation.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.emedicoz.app.pastpaperexplanation.fragment.ExamWiseFragment;
import com.emedicoz.app.pastpaperexplanation.fragment.PPEAboutFragment;


import java.util.ArrayList;

public class PPEViewPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    ArrayList<String> stringArrayList;

    public PPEViewPagerAdapter(FragmentManager fm, ArrayList<String> stringArrayList, Context context) {
        super(fm);
        this.stringArrayList = stringArrayList;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 2:
                return PPEAboutFragment.newInstance();
            default:
                return ExamWiseFragment.newInstance(position);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return stringArrayList.get(position);
    }

    @Override
    public int getCount() {
        return stringArrayList.size();
    }
}
