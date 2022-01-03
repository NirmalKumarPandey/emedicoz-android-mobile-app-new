package com.emedicoz.app.courses.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;


public class MyCourseFragmentAdapter extends FragmentStateAdapter {

    private  ArrayList<Fragment> fragmentList = new ArrayList<>();
    private  ArrayList<String> fragmentTitleList = new ArrayList<String>();

    public MyCourseFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return getItem(position);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }


    private Fragment getItem(int i){
        return fragmentList.get(i);
    }

    public String getItemTitle(int i) {
        return fragmentTitleList.get(i);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}

