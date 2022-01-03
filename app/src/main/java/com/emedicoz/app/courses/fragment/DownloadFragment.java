package com.emedicoz.app.courses.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.courses.adapter.DownloadTabAdapter;
import com.emedicoz.app.utilso.Constants;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadFragment extends Fragment {

    Activity activity;
    private DownloadTabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public DownloadFragment() {
        // Required empty public constructor
    }

    public static DownloadFragment newInstance() {
        DownloadFragment downloadFragment = new DownloadFragment();
        return downloadFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_download, container, false);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (activity instanceof BaseABNavActivity) {
            BaseABNavActivity.postFAB.setVisibility(View.GONE);
        }
        viewPager = view.findViewById(R.id.viewPagerDownload);
        tabLayout = view.findViewById(R.id.tabLayoutDownload);

        adapter = new DownloadTabAdapter(getChildFragmentManager());
        adapter.addFragment(new DownloadedDataListFragment(Constants.MY_DOWNLOAD_TABS.VIDEO), Constants.MY_DOWNLOAD_TABS.VIDEO);
        adapter.addFragment(new DownloadedDataListFragment(Constants.MY_DOWNLOAD_TABS.EPUB), Constants.MY_DOWNLOAD_TABS.EPUB);
        adapter.addFragment(new DownloadedDataListFragment(Constants.MY_DOWNLOAD_TABS.PDF), Constants.MY_DOWNLOAD_TABS.PDF);
        adapter.addFragment(new DownloadedDataListFragment(Constants.MY_DOWNLOAD_TABS.PODCAST), Constants.MY_DOWNLOAD_TABS.PODCAST);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
