package com.emedicoz.app.testmodule.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.testmodule.activity.ViewSolutionWithTabNew;
import com.emedicoz.app.testmodule.adapter.MyRecyclerAdapter;
import com.emedicoz.app.testmodule.adapter.MyRecyclerAdapterTwo;
import com.emedicoz.app.testmodule.callback.FragmentChangeListener;
import com.emedicoz.app.testmodule.callback.NumberPadOnClick;
import com.emedicoz.app.testmodule.model.ViewSolutionData;
import com.emedicoz.app.testmodule.model.ViewSolutionResult;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.LinearLayoutManagerWithSmoothScroller;
import com.emedicoz.app.utilso.SharedPreference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewSolutionWithTabFragment extends Fragment implements FragmentChangeListener, View.OnClickListener, NumberPadOnClick {

    private static final String TAG = "ViewSolutionWithTabFrag";
    public static ArrayList<ViewSolutionResult> resultArrayList;
    public int currentPage;
    public ViewSolutionData viewSolutionData;
    public MyRecyclerAdapter rvNumberPadAdapter;
    Activity activity;
    DrawerLayout drawerLayout;
    RelativeLayout parentRL;
    String testSegmentId = "", test_series_name = "";
    TextView testSeriesName;
    TextView tvQuestionnumber, noDataTV;
    int i = 0;
    TextView gridView, listView, tvCorrectCount, tvIncorrectCount, tvSkipCount;
    MyRecyclerAdapterTwo myRecyclerAdapterTwo;
    LinearLayoutManager linearLayoutManager;
    Progress progress;
    RelativeLayout btnNext, btnPrev;
    boolean custom = false;
    String tabName;
    private RecyclerView rvNumberpad, rvQuestion;
    private LinearLayout llDrawerRight;

    public ViewSolutionWithTabFragment() {
        // Required empty public constructor
    }

    public static ViewSolutionWithTabFragment newInstance(String testSegmentId, String testSeriesName, boolean custom, String tabName) {
        ViewSolutionWithTabFragment frag = new ViewSolutionWithTabFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
        bundle.putString(Constants.Extras.NAME, testSeriesName);
        bundle.putString(Constants.Extras.TAB_NAME, tabName);
        bundle.putBoolean(Constants.Extras.CUSTOM, custom);
        frag.setArguments(bundle);
        return frag;
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
        return inflater.inflate(R.layout.fragment_view_solution_with_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resultArrayList = new ArrayList<>();
        initView(view);
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, fragment.toString());
        fragmentTransaction.commit();
    }

    private void initView(View view) {

        rvNumberpad = view.findViewById(R.id.rvnumberpad);
        rvQuestion = view.findViewById(R.id.rl_questionpad);
        tvQuestionnumber = view.findViewById(R.id.tv_questionnumber);
        testSeriesName = view.findViewById(R.id.testSeriesName);
        btnNext = view.findViewById(R.id.btn_next);
        noDataTV = view.findViewById(R.id.noDataTV);
        gridView = view.findViewById(R.id.gridView);
        listView = view.findViewById(R.id.listView);
        parentRL = view.findViewById(R.id.parentRL);
        tvCorrectCount = view.findViewById(R.id.tv_correct_count);
        tvIncorrectCount = view.findViewById(R.id.tv_incorrect_count);
        tvSkipCount = view.findViewById(R.id.tv_skip_count);
        drawerLayout = view.findViewById(R.id.drawerLayout);
        llDrawerRight = view.findViewById(R.id.llDrawerRight);
        btnPrev = view.findViewById(R.id.btn_prev);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        gridView.setOnClickListener(this);
        listView.setOnClickListener(this);
        ((ViewSolutionWithTabNew) activity).index.setOnClickListener(v -> drawerLayout.openDrawer(llDrawerRight));
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
        linearLayoutManager = new LinearLayoutManagerWithSmoothScroller(activity, LinearLayoutManager.HORIZONTAL, false);
        rvNumberpad.setLayoutManager(linearLayoutManager);
        rvQuestion.setLayoutManager(new GridLayoutManager(activity, 6));
        getBundleData();
        getViewSolutionData();
    }

    private void getBundleData() {

        if (getArguments() != null) {
            test_series_name = getArguments().getString(Constants.Extras.NAME);
            testSegmentId = getArguments().getString(Constants.ResultExtras.TEST_SEGMENT_ID);
            custom = getArguments().getBoolean(Constants.Extras.CUSTOM);
            tabName = getArguments().getString(Constants.Extras.TAB_NAME);

            if (activity instanceof ViewSolutionWithTabNew)
                viewSolutionData = ((ViewSolutionWithTabNew) activity).viewSolutionData;


            Log.e(TAG, "getBundleData: " + tabName);

            if (viewSolutionData != null && activity instanceof ViewSolutionWithTabNew) {
                switch (tabName) {
                    case Constants.TestStatus.CORRECT:
                        resultArrayList = ((ViewSolutionWithTabNew) activity).viewSolutionResultCorrect;
                        break;
                    case Constants.TestStatus.INCORRECT:
                        resultArrayList = ((ViewSolutionWithTabNew) activity).viewSolutionResultIncorrect;
                        break;
                    case Constants.TestStatus.SKIPPED:
                        resultArrayList = ((ViewSolutionWithTabNew) activity).viewSolutionResultSkipped;
                        break;
                    case Constants.TestStatus.BOOKMARKED:
                        resultArrayList = ((ViewSolutionWithTabNew) activity).viewSolutionResultBookmarked;
                        break;
                    case Constants.TestStatus.ALL:
                    default:
                        resultArrayList = viewSolutionData.getResult();
                        break;
                }
            }
        }

    }

    private void getViewSolutionData() {
        if (resultArrayList == null || resultArrayList.isEmpty()) {
            parentRL.setVisibility(View.GONE);
            noDataTV.setVisibility(View.VISIBLE);
            tvCorrectCount.setText("0");
            tvIncorrectCount.setText("0");
            tvSkipCount.setText("0");
        } else {
            parentRL.setVisibility(View.VISIBLE);
            noDataTV.setVisibility(View.GONE);
            setViewSolutionData();
            tvQuestionnumber.setText(String.format("Question 1/%d", resultArrayList.size()));
        }
    }

    private void setViewSolutionData() {
        int correctCount = 0, incorrectCount = 0, skipCount = 0;
        for (int i = 0; i < resultArrayList.size(); i++) {
            if (resultArrayList.get(i).getUserAnswer().equals("")) {
                skipCount++;
            } else if (resultArrayList.get(i).getUserAnswer().equalsIgnoreCase(resultArrayList.get(i).getAnswer())) {
                correctCount++;
            } else {
                incorrectCount++;
            }
        }

        tvSkipCount.setText(String.valueOf(skipCount));
        tvIncorrectCount.setText(String.valueOf(incorrectCount));
        tvCorrectCount.setText(String.valueOf(correctCount));

        replaceFragment(ViewSolutionFragment.newInstance(i, tabName));
        for (int i = 0; i < resultArrayList.size(); i++) {
            if (i == 0) {
                resultArrayList.get(i).set_selected(true);
            } else {
                resultArrayList.get(i).set_selected(false);
            }
        }

        rvNumberPadAdapter = new MyRecyclerAdapter(resultArrayList, activity, R.layout.single_row_testpad_no, this, this);
        rvNumberpad.setAdapter(rvNumberPadAdapter);

        myRecyclerAdapterTwo = new MyRecyclerAdapterTwo(resultArrayList, activity, R.layout.single_row_testpad_no, this, this, "0");
        rvQuestion.setAdapter(myRecyclerAdapterTwo);
    }

    @Override
    public void sendOnclickInd(int index) {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        }
        replaceFragment(ViewSolutionFragment.newInstance(index, tabName));
        rvNumberPadAdapter.setSelectePosition(index);
        myRecyclerAdapterTwo.setSelectePosition(index);
        rvQuestion.scrollToPosition(index);
        rvNumberpad.scrollToPosition(index);
        tvQuestionnumber.setText(String.format("%d/%d Questions", index + 1, resultArrayList.size()));
        i = index;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (i < resultArrayList.size() - 1) {
                    i++;
                    replaceFragment(ViewSolutionFragment.newInstance(i, tabName));
                    tvQuestionnumber.setText(String.format("%d/%d Questions", i + 1, resultArrayList.size()));
                    rvNumberPadAdapter.setSelectePosition(i);
                    myRecyclerAdapterTwo.setSelectePosition(i);
                    rvNumberpad.scrollToPosition(i);
                    rvQuestion.scrollToPosition(i);
                }
                break;

            case R.id.btn_prev:
                if (i > 0) {
                    i--;
                    replaceFragment(ViewSolutionFragment.newInstance(i, tabName));

                    tvQuestionnumber.setText(String.format("%d/%d Questions", i + 1, resultArrayList.size()));
                    rvNumberPadAdapter.setSelectePosition(i);
                    myRecyclerAdapterTwo.setSelectePosition(i);
                    rvNumberpad.scrollToPosition(i);
                    rvQuestion.scrollToPosition(i);
                }
                break;
            case R.id.img_testback:
                activity.onBackPressed();
                break;

            case R.id.gridView:
                SharedPreference.getInstance().putString("VIEW", "0");
                GridLayoutManager manager = new GridLayoutManager(activity, 6);
                rvQuestion.setLayoutManager(manager);
                gridView.setTextColor(ContextCompat.getColor(activity, R.color.black));
                gridView.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_grid_list));
                listView.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                listView.setTextColor(ContextCompat.getColor(activity, R.color.white));
                myRecyclerAdapterTwo = new MyRecyclerAdapterTwo(resultArrayList, activity, R.layout.single_row_testpad_no, this, this, "0");
                rvQuestion.setAdapter(myRecyclerAdapterTwo);
                rvQuestion.scrollToPosition(i);
                break;
            case R.id.listView:
                SharedPreference.getInstance().putString("VIEW", "1");
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                rvQuestion.setLayoutManager(linearLayoutManager);
                listView.setTextColor(ContextCompat.getColor(activity, R.color.black));
                listView.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_grid_list));
                gridView.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                gridView.setTextColor(ContextCompat.getColor(activity, R.color.white));
                myRecyclerAdapterTwo = new MyRecyclerAdapterTwo(resultArrayList, activity, R.layout.single_row_testpad_no, this, this, "1");
                rvQuestion.setAdapter(myRecyclerAdapterTwo);
                rvQuestion.scrollToPosition(i);
                break;
        }
    }


}
