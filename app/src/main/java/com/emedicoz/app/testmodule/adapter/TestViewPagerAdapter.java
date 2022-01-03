package com.emedicoz.app.testmodule.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class TestViewPagerAdapter extends FragmentStatePagerAdapter {

    public ArrayList<Fragment> mFragmentList = new ArrayList<>();
    public ArrayList<String> mFragmentTitleList = new ArrayList<>();
    SparseArray<Fragment> registeredFragments = new SparseArray<>();
    Context context;
    int num;
    FragmentManager fragmentManager;

    private Fragment mCurrentFragment;

    public TestViewPagerAdapter(FragmentManager fragmentManager, Activity context, ArrayList<Fragment> mFragmentList, int num) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.mFragmentList = mFragmentList;
        this.num = num;
    }


//    public Fragment getCurrentFragment() {
//        return mFragmentList.get();
//    }

//    @Override
//    public void setPrimaryItem(ViewGroup container, int position, Object object) {
//        super.setPrimaryItem(container, position, object);
//        mCurrentFragment = (Fragment) object;
//    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        try {
            return mFragmentTitleList.get(position);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public Fragment getItem(int position) {

        return mFragmentList.get(position);

    }


    @Override
    public int getCount() {
        return num;

    }
}
