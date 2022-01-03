package com.emedicoz.app.courses.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.TestQuizCourseAdapterOne;
import com.emedicoz.app.customviews.CustomLayoutManager;
import com.emedicoz.app.customviews.VerticalSpaceItemDecoration;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.TestSeries;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ChildTestQuizCourseFrag extends Fragment {

    private static final String TAG = "ChildTestQuizCourseFrag";
    RecyclerView recyclerView;
    int position;
    LinearLayout noDataLL;
    LinearLayoutManager linearLayoutManager;
    CustomLayoutManager customLayoutManager;
    LinearLayout parentBottomLAY;
    String isPurchased;
    ImageView noDataImage;
    TextView noDataText;
    ArrayList<String> topics;
    SingleCourseData singleCourseData;
    private Activity activity;
    private ArrayList<TestSeries> testSeries;
    private LinearLayout activeParentLL;
    private LinearLayout upcomingParentLL;
    private LinearLayout missedParentLL;
    private LinearLayout completedParentLL;
    private ArrayList<TestSeries> activeTestList = new ArrayList<>();
    private ArrayList<TestSeries> upcomingList = new ArrayList<>();
    private ArrayList<TestSeries> missedList = new ArrayList<>();
    private ArrayList<TestSeries> completedTestList = new ArrayList<>();
    TextView activeTV;
    TextView upcomingTV;
    TextView missedTV;
    TextView completedTV;

    public ChildTestQuizCourseFrag() {
        // default constructor
    }

    public static ChildTestQuizCourseFrag getInstance(ArrayList<TestSeries> testSeries, ArrayList<String> topicList, String id, String tabName, String isPurchased, SingleCourseData singleCourseData) {
        ChildTestQuizCourseFrag frag = new ChildTestQuizCourseFrag();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.TEST_QUIZ, testSeries);
        bundle.putSerializable(Const.TOPICS, topicList);
        bundle.putString(Const.COURSE_ID, id);
        bundle.putString(Const.IS_PURCHASED, isPurchased);
        bundle.putString(Constants.Extras.PAGE, tabName);
        bundle.putSerializable(Const.COURSE_DESC, singleCourseData);
        frag.setArguments(bundle);
        return frag;
    }

    public static long getMilliFromDate(String dateFormat) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            date = formatter.parse(dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Today is " + date );
        return Objects.requireNonNull(date).getTime();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ChildTestQuizCourseFrag");
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_recycler_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        parentBottomLAY = Objects.requireNonNull(getView()).findViewById(R.id.parentBottomLAY);
        activeParentLL = getView().findViewById(R.id.activeParentLL);
        upcomingParentLL = getView().findViewById(R.id.upcomingParentLL);
        missedParentLL = getView().findViewById(R.id.missedParentLL);
        completedParentLL = getView().findViewById(R.id.completedParentLL);
        activeParentLL.setTag(Constants.TestStatus.ACTIVE);
        upcomingParentLL.setTag(Constants.TestStatus.UPCOMING);
        missedParentLL.setTag(Constants.TestStatus.MISSED);
        completedParentLL.setTag(Constants.TestStatus.COMPLETED);
        activeTV = getView().findViewById(R.id.activeTV);
        upcomingTV = getView().findViewById(R.id.upcomingTV);
        missedTV = getView().findViewById(R.id.missedTV);
        completedTV = getView().findViewById(R.id.completedTV);

        noDataText = getView().findViewById(R.id.noDataText);
        noDataImage = getView().findViewById(R.id.noDataImage);
        noDataLL = getView().findViewById(R.id.noDataLL);
        recyclerView = getView().findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        customLayoutManager = new CustomLayoutManager(activity);
        recyclerView.setLayoutManager(customLayoutManager);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    position = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                    Log.e(TAG, "display position:" + position);
                }
            }
        });
        getBundleData();
        /*((TestQuizActivity) activity).index.setOnClickListener((View v) -> {
                Dialog settingsDialog = new Dialog(activity);
                Objects.requireNonNull(settingsDialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
                View iView = LayoutInflater.from(activity).inflate(R.layout.dialog_indexing, null);
                settingsDialog.setContentView(iView);
                RecyclerView indexingRV = iView.findViewById(R.id.indexingRv);
                indexingRV.setLayoutManager(new LinearLayoutManager(activity));
                indexingRV.setAdapter(new IndexingAdapter(activity, topics));
                WindowManager.LayoutParams wmlp = settingsDialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.TOP | Gravity.END;
                wmlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                wmlp.y = 100;
                settingsDialog.getWindow().setAttributes(wmlp);
                settingsDialog.show();
                indexingRV.addOnItemTouchListener(new CustomTouchListener(activity, (view, index) -> {

                        recyclerView.smoothScrollToPosition(index);
                        Toast.makeText(activity, topics.get(index) + " " + index + " Clicked", Toast.LENGTH_SHORT).show();
                }));
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Size Test Series", "GetBundleData()" + testSeries.size());
    }

    private void getBundleData() {
        if (getArguments() != null) {
            testSeries = (ArrayList<TestSeries>) getArguments().getSerializable(Const.TEST_QUIZ);
            topics = (ArrayList<String>) getArguments().getSerializable(Const.TOPICS);
            isPurchased = getArguments().getString(Const.IS_PURCHASED);
            singleCourseData = (SingleCourseData) getArguments().getSerializable(Const.COURSE_DESC);
            Log.e("Size Test Series", "GetBundleData()" + testSeries.size());
            if (!GenericUtils.isListEmpty(testSeries)) {
                recyclerView.setVisibility(View.VISIBLE);
                noDataLL.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                noDataLL.setVisibility(View.VISIBLE);
                noDataText.setText(activity.getResources().getString(R.string.no_quiz_found));
            }
            if (Objects.requireNonNull(getArguments().getString(Constants.Extras.PAGE)).equalsIgnoreCase(Constants.TestStatus.ALL)) {
                changeTabColor(Constants.TestStatus.ACTIVE);
                extractData();
                setRecyclerAdapter(testSeries, topics);
            } else if (Objects.requireNonNull(getArguments().getString(Constants.Extras.PAGE)).equalsIgnoreCase(Constants.TestStatus.COMPLETED)) {
                if (!testSeries.isEmpty()) {
                    setRecyclerAdapter(testSeries, topics);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noDataLL.setVisibility(View.VISIBLE);
                    noDataText.setText(activity.getResources().getString(R.string.not_completed_any_quiz));
                }
            } else if (Objects.requireNonNull(getArguments().getString(Constants.Extras.PAGE)).equalsIgnoreCase(Constants.TestStatus.UNATTEMPTED)) {
                if (!testSeries.isEmpty()) {
                    setRecyclerAdapter(testSeries, topics);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noDataLL.setVisibility(View.VISIBLE);
                    noDataText.setText(activity.getResources().getString(R.string.attempted_all_quiz));
                }
            } else if (Objects.requireNonNull(getArguments().getString(Constants.Extras.PAGE)).equalsIgnoreCase(Constants.TestStatus.PAUSED)) {
                if (!testSeries.isEmpty()) {
                    setRecyclerAdapter(testSeries, topics);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noDataLL.setVisibility(View.VISIBLE);
                    noDataText.setText(activity.getResources().getString(R.string.not_paused_any_quiz));
                }
            } else {
                changeTabColor(Constants.TestStatus.ACTIVE);
                extractData();
                setRecyclerAdapter(activeTestList, topics);
            }

        }
    }

    private void setRecyclerAdapter(ArrayList<TestSeries> list, ArrayList<String> topics) {
        TestQuizCourseAdapterOne adapter = new TestQuizCourseAdapterOne(activity, topics, list, isPurchased, singleCourseData);
        recyclerView.setAdapter(adapter);
    }

    private void extractData() {
        activeTestList.clear();
        upcomingList.clear();
        missedList.clear();
        completedTestList.clear();
        long currentDate = Calendar.getInstance().getTimeInMillis();


        for (int i = 0; i < testSeries.size(); i++) {
            TestSeries testSeries1 = testSeries.get(i);
            if (!TextUtils.isEmpty(testSeries1.getIs_user_attemp()) && !testSeries1.getIs_user_attemp().equals("0")) {
                completedTestList.add(testSeries1);
            } else if (currentDate > getMilliFromDate(testSeries1.getTest_start_date()) &&
                    currentDate < getMilliFromDate(testSeries1.getTest_end_date())) {
                activeTestList.add(testSeries1);
            } else if (currentDate < getMilliFromDate(testSeries1.getTest_start_date())) {
                upcomingList.add(testSeries1);
            } else if (getMilliFromDate(testSeries1.getTest_end_date()) < currentDate &&
                    TextUtils.isEmpty(testSeries1.getIs_user_attemp()) || testSeries1.getIs_user_attemp().equals("0")) {
                missedList.add(testSeries1);
            }
        }
    }

    public void changeTabColor(String title) {

        activeParentLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_tab_test));
        activeTV.setTextColor(ContextCompat.getColor(activity, R.color.left_panel_header_text_color));
        upcomingParentLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_tab_test));
        upcomingTV.setTextColor(ContextCompat.getColor(activity, R.color.left_panel_header_text_color));
        missedParentLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_tab_test));
        missedTV.setTextColor(ContextCompat.getColor(activity, R.color.left_panel_header_text_color));
        completedParentLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_tab_test));
        completedTV.setTextColor(ContextCompat.getColor(activity, R.color.left_panel_header_text_color));

        switch (title) {
            case Constants.TestStatus.ACTIVE:
                activeParentLL.setBackground(ContextCompat.getDrawable(activity,R.drawable.bg_tab_white_stroke_green));
                activeTV.setTextColor(ContextCompat.getColor(activity,R.color.blue));
                break;
            case Constants.TestStatus.UPCOMING:
                upcomingParentLL.setBackground(ContextCompat.getDrawable(activity,R.drawable.bg_tab_white_stroke_green));
                upcomingTV.setTextColor(ContextCompat.getColor(activity,R.color.blue));
                break;
            case Constants.TestStatus.MISSED:
                missedParentLL.setBackground(ContextCompat.getDrawable(activity,R.drawable.bg_tab_white_stroke_green));
                missedTV.setTextColor(ContextCompat.getColor(activity,R.color.blue));
                break;
            case Constants.TestStatus.COMPLETED:
                completedParentLL.setBackground(ContextCompat.getDrawable(activity,R.drawable.bg_tab_white_stroke_green));
                completedTV.setTextColor(ContextCompat.getColor(activity,R.color.colorPrimary));
                break;
            default:
                break;
        }
    }
}
