package com.emedicoz.app.feeds.adapter;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.emedicoz.app.bookmark.ChildBookMarkFrag;
import com.emedicoz.app.podcast.PodcastFragment;
import com.emedicoz.app.recordedCourses.fragment.CourseListingFragment;
import com.emedicoz.app.utilso.Constants;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    String qTypeDqb;
    List<String> listOfTabs;
    ArrayList<String> strings;

    public ViewPagerAdapter(FragmentManager manager, List<String> listOfTabs, String qTypeDqb, ArrayList<String> strings) {
        super(manager);
        this.listOfTabs = listOfTabs;
        this.qTypeDqb = qTypeDqb;
        this.strings = strings;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        if (listOfTabs.get(i).equals("PODCAST")) {
            fragment = PodcastFragment.newInstance(true);
        } else if (listOfTabs.get(i).equals("WISHLIST")) {
            fragment = CourseListingFragment.newInstance("WISHLIST", "0");
        } else {
            fragment = new ChildBookMarkFrag();
            Bundle args = new Bundle();
            args.putString(Constants.Extras.TAG_NAME, listOfTabs.get(i));
            if (qTypeDqb.equals(Constants.Type.TEST_SERIES) && listOfTabs.get(i).toUpperCase().equalsIgnoreCase(Constants.TestType.TEST))
                args.putString(Constants.Extras.BOOKMARK_NAME, Constants.TestType.QUIZ);
            else
                args.putString(Constants.Extras.BOOKMARK_NAME, listOfTabs.get(i).toUpperCase());
            args.putString(Constants.Extras.Q_TYPE_DQB, qTypeDqb);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Constants.BOOKMARK_NAME = strings.get(position);
        return (strings.get(position));
    }

    @Override
    public int getCount() {
        return listOfTabs.size();
    }

}