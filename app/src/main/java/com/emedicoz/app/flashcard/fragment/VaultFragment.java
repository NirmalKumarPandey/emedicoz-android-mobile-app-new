package com.emedicoz.app.flashcard.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.activity.FlashCardActivity;
import com.emedicoz.app.flashcard.adapter.AllCardsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class VaultFragment extends Fragment {

    private Activity activity;
    private TabLayout mTabLayout;
    ViewPager viewpager;
    List<Fragment> fragList = new ArrayList<>();
    public List<String> titleList = new ArrayList<>();
    AllCardsPagerAdapter adapter;
    private static final String TAG = "VaultFragment";

    public VaultFragment() {
        // Required empty public constructor
    }

    public static VaultFragment newInstance() {
        return new VaultFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_vault, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated: ");
        ((FlashCardActivity) activity).manageToolbar("VAULT", false);
        initViews(view);
        bindControls();
    }

    private void initViews(View view) {
        mTabLayout = view.findViewById(R.id.tab_layout);
        viewpager = view.findViewById(R.id.viewpager);

        titleList.clear();
        fragList.clear();

        titleList.add(getString(R.string.all_cards));
        titleList.add(getString(R.string.bookmarked));
        titleList.add(getString(R.string.suspended));

        fragList.add(AllCardsFragment.newInstance());
        fragList.add(AllCardsFragment.newInstance());
        fragList.add(AllCardsFragment.newInstance());
        for (String module : titleList) {
            mTabLayout.addTab(mTabLayout.newTab().setText(module));
        }

        mTabLayout.post(() -> {
                // don't forget to add Tab first before measuring..
                DisplayMetrics displayMetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int widthS = displayMetrics.widthPixels;
                mTabLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int widthT = mTabLayout.getMeasuredWidth();

                if (widthS > widthT) {
                    mTabLayout.setTabMode(TabLayout.MODE_FIXED);
                    mTabLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                }
        });
    }

    private void bindControls() {
        Log.e(TAG, "bindControls: " + fragList.size());
        adapter = new AllCardsPagerAdapter(getChildFragmentManager(), fragList, VaultFragment.this);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(((FlashCardActivity) activity).type);
        mTabLayout.setupWithViewPager(viewpager);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ((FlashCardActivity) activity).type = position;
                Log.e(TAG, "onPageSelected: " + position);
                ((AllCardsFragment) adapter.getItem(position)).hitApi(activity);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
