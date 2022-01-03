package com.emedicoz.app.courses.adapter;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.emedicoz.app.courses.fragment.NewMyCourseFragment;
import com.emedicoz.app.utilso.Constants;

import java.util.ArrayList;
import java.util.List;

public class MyCoursesPagerAdapter extends FragmentPagerAdapter {
    private List<String> list = new ArrayList<>();

    public MyCoursesPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int i) {
        NewMyCourseFragment fragment = new NewMyCourseFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Extras.NAME, list.get(i));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return (list.get(position));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void addFragment(List<String> list) {
        this.list = list;
    }
}
