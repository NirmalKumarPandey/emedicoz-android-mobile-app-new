package com.emedicoz.app.flashcard.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.emedicoz.app.flashcard.fragment.VaultFragment;

import java.util.List;

public class AllCardsPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> numOfFragment;
    Fragment fragment;

    public AllCardsPagerAdapter(FragmentManager fm, List<Fragment> numOfFragment, Fragment fragment) {
        super(fm);
        this.numOfFragment = numOfFragment;
        this.fragment = fragment;
    }

    @Override
    public Fragment getItem(int position) {
        return numOfFragment.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ((VaultFragment)fragment).titleList.get(position);
    }

    @Override
    public int getCount() {
        return numOfFragment.size();
    }


}