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

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.NewTestQuizCourseAdapter;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
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

public class NewChildTestQuizCourseFrag extends Fragment implements View.OnClickListener {

    private static final String TAG = "NewChildTestQuizCourseF";
    String isPurchased;
    TextView noDataText;
    LinearLayout noDataLL;
    ImageView noDataImage;
    LinearLayout parentBottomLAY;
    SingleCourseData singleCourseData;
    private Activity activity;
    private NonScrollRecyclerView recyclerView;
    private ArrayList<TestSeries> testSeries;
    private String id;
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

    public static NewChildTestQuizCourseFrag getInstance(ArrayList<TestSeries> testSeries, String id, String tabname, String isPurchased, SingleCourseData singleCourseData) {
        NewChildTestQuizCourseFrag frag = new NewChildTestQuizCourseFrag();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.TEST_QUIZ, testSeries);
        Log.e(TAG, "Series_List : " + testSeries.toString());
        Log.e(TAG, "page : " + tabname);
        bundle.putString(Const.COURSE_ID, id);
        bundle.putString(Const.IS_PURCHASED, isPurchased);
        bundle.putString(Constants.Extras.PAGE, tabname);
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
        return Objects.requireNonNull(date).getTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_recycler_view, container, false);
        activity = getActivity();
        parentBottomLAY = view.findViewById(R.id.parentBottomLAY);
        activeParentLL = view.findViewById(R.id.activeParentLL);
        upcomingParentLL = view.findViewById(R.id.upcomingParentLL);
        missedParentLL = view.findViewById(R.id.missedParentLL);
        completedParentLL = view.findViewById(R.id.completedParentLL);

        parentBottomLAY.setVisibility(View.VISIBLE);
        activeParentLL.setTag(Constants.TestStatus.ACTIVE);
        upcomingParentLL.setTag(Constants.TestStatus.UPCOMING);
        missedParentLL.setTag(Constants.TestStatus.MISSED);
        completedParentLL.setTag(Constants.TestStatus.COMPLETED);
        activeTV = view.findViewById(R.id.activeTV);
        upcomingTV = view.findViewById(R.id.upcomingTV);
        missedTV = view.findViewById(R.id.missedTV);
        completedTV = view.findViewById(R.id.completedTV);

        activeParentLL.setOnClickListener(this);
        upcomingParentLL.setOnClickListener(this);
        missedParentLL.setOnClickListener(this);
        completedParentLL.setOnClickListener(this);

        noDataText = view.findViewById(R.id.noDataText);
        noDataImage = view.findViewById(R.id.noDataImage);
        noDataImage.setImageResource(R.mipmap.test_blank);
        noDataLL = view.findViewById(R.id.noDataLL);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        getBundleData();
        return view;
    }

    private void getBundleData() {
        if (getArguments() != null) {
            testSeries = (ArrayList<TestSeries>) getArguments().getSerializable(Const.TEST_QUIZ);
            id = getArguments().getString(Const.COURSE_ID);
            isPurchased = getArguments().getString(Const.IS_PURCHASED);
            singleCourseData = (SingleCourseData) getArguments().getSerializable(Const.COURSE_DESC);
            Log.e("Test_series_data", "GetBundleData()" + testSeries.toString());
            extractData();
            if (!GenericUtils.isListEmpty(activeTestList)) {
                changeTabColor(Constants.TestStatus.ACTIVE);
                setRecyclerAdapter(activeTestList);
            } else if (!GenericUtils.isListEmpty(upcomingList)) {
                changeTabColor(Constants.TestStatus.UPCOMING);
                setRecyclerAdapter(upcomingList);
            } else if (!GenericUtils.isListEmpty(missedList)) {
                changeTabColor(Constants.TestStatus.MISSED);
                setRecyclerAdapter(missedList);
            } else {
                changeTabColor(Constants.TestStatus.COMPLETED);
                setRecyclerAdapter(completedTestList);
            }

        }
    }

    private void setRecyclerAdapter(ArrayList<TestSeries> list) {
        NewTestQuizCourseAdapter adapter = new NewTestQuizCourseAdapter(activity, list, id, isPurchased, singleCourseData);
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
            if (!TextUtils.isEmpty(testSeries1.getIs_user_attemp()) && testSeries1.getIs_paused().equals("0")) {
                completedTestList.add(testSeries1);
            } else if ((currentDate >= getMilliFromDate(testSeries1.getTest_start_date()) &&
                    currentDate < getMilliFromDate(testSeries1.getTest_end_date())) || testSeries1.getIs_paused().equals("1")) {
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

        if (Constants.TestStatus.ACTIVE.equals(title)) {
            activeParentLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_tab_white_stroke_green));
            activeTV.setTextColor(ContextCompat.getColor(activity, R.color.blue));
        } else if (Constants.TestStatus.UPCOMING.equals(title)) {
            upcomingParentLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_tab_white_stroke_green));
            upcomingTV.setTextColor(ContextCompat.getColor(activity, R.color.blue));
        } else if (Constants.TestStatus.MISSED.equals(title)) {
            missedParentLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_tab_white_stroke_green));
            missedTV.setTextColor(ContextCompat.getColor(activity, R.color.blue));
        } else if (Constants.TestStatus.COMPLETED.equals(title)) {
            completedParentLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_tab_white_stroke_green));
            completedTV.setTextColor(ContextCompat.getColor(activity, R.color.blue));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activeParentLL:
                changeTabColor(Constants.TestStatus.ACTIVE);
                if (!GenericUtils.isListEmpty(activeTestList)) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noDataLL.setVisibility(View.GONE);
                    setRecyclerAdapter(activeTestList);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noDataLL.setVisibility(View.VISIBLE);
                    noDataText.setText(R.string.no_active_test_found);
                }
                break;
            case R.id.upcomingParentLL:
                changeTabColor(Constants.TestStatus.UPCOMING);
                if (!GenericUtils.isListEmpty(upcomingList)) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noDataLL.setVisibility(View.GONE);
                    setRecyclerAdapter(upcomingList);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noDataLL.setVisibility(View.VISIBLE);
                    noDataText.setText(R.string.no_upsoming_test_found);
                }
                break;
            case R.id.missedParentLL:
                changeTabColor(Constants.TestStatus.MISSED);
                if (!GenericUtils.isListEmpty(missedList)) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noDataLL.setVisibility(View.GONE);
                    setRecyclerAdapter(missedList);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noDataLL.setVisibility(View.VISIBLE);
                    noDataText.setText(R.string.no_missed_test_found);
                }
                break;
            case R.id.completedParentLL:
                changeTabColor(Constants.TestStatus.COMPLETED);

                if (!GenericUtils.isListEmpty(completedTestList)) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noDataLL.setVisibility(View.GONE);
                    setRecyclerAdapter(completedTestList);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noDataLL.setVisibility(View.VISIBLE);
                    noDataText.setText(R.string.no_completed_test_found);
                }
                break;
            default:
                break;
        }
    }
}
