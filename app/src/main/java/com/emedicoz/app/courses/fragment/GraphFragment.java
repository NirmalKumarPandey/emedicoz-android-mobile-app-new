package com.emedicoz.app.courses.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.ProgressBarAdapter;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.modelo.SubjectWiseResultData;
import com.emedicoz.app.modelo.SubjectWiseResultPart;
import com.emedicoz.app.modelo.SubjectWiseResultSubject;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment implements View.OnClickListener, MyNetworkCall.MyNetworkCallBack {


    private static final int MAX_X_VALUE = 19;
    private static final int GROUPS = 3;
    private static final String GROUP_1_LABEL = "Group 1";
    private static final String GROUP_2_LABEL = "Group 2";
    private static final String GROUP_3_LABEL = "Group 3";
    private static final float BAR_SPACE = 0.05f;
    private static final float BAR_WIDTH = 0.2f;
    String type = "0";
    Activity activity;
    String testSegmentId;
    TextView myScore, toppersScore, averageScore, totalScoreOne, totalScoreTwo, totalScoreThree,
            allTV, partATV, partBTV, partCTV, myResultTV, compareWithTopperTV;
    ResultTestSeries resultTestSeries;
    ProgressBarAdapter progressBarAdapter;
    ArrayList<String> partList;
    ArrayList<SubjectWiseResultPart> partAList, partBList, partCList;
    ArrayList<SubjectWiseResultSubject> partASubjectList, partBSubjectList, partCSubjectList;
    SubjectWiseResultData subjectData;
    ArrayList<SubjectWiseResultSubject> subjectWiseResults;
    NonScrollRecyclerView subjectRV;
    LinearLayout subjectLL, percentageLL, compareLL;
    CardView subCard, cardPart;
    private HorizontalBarChart chart;
    MyNetworkCall myNetworkCall;
    private static final String TAG = "GraphFragment";

    public static GraphFragment newInstance(String status, ResultTestSeries resultTestSeries) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putSerializable(Const.RESULT_TEST_SERIES, resultTestSeries);
        args.putString(Constants.ResultExtras.TEST_SEGMENT_ID, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            testSegmentId = getArguments().getString(Constants.ResultExtras.TEST_SEGMENT_ID);
            resultTestSeries = (ResultTestSeries) getArguments().getSerializable(Const.RESULT_TEST_SERIES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_analysis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = "0";
        initView(view);
    }

    private void initView(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        chart = view.findViewById(R.id.chart);
        myScore = view.findViewById(R.id.myScoreTV);
        toppersScore = view.findViewById(R.id.toppersScoreTV);
        averageScore = view.findViewById(R.id.averageScoreTV);
        totalScoreOne = view.findViewById(R.id.totalScoreTVOne);
        totalScoreTwo = view.findViewById(R.id.totalScoreTVTwo);
        totalScoreThree = view.findViewById(R.id.totalScoreTVThree);
        allTV = view.findViewById(R.id.allTV);
        percentageLL = view.findViewById(R.id.percentageLL);
        compareLL = view.findViewById(R.id.compareLL);
        subCard = view.findViewById(R.id.subCard);
        cardPart = view.findViewById(R.id.cardPart);
        partATV = view.findViewById(R.id.partATV);
        partBTV = view.findViewById(R.id.partBTV);
        partCTV = view.findViewById(R.id.partCTV);
        subjectRV = view.findViewById(R.id.subjectRV);
        subjectLL = view.findViewById(R.id.subjectLL);
        myResultTV = view.findViewById(R.id.myResultTV);
        compareWithTopperTV = view.findViewById(R.id.compareWithTopperTV);
        subjectRV.setLayoutManager(new LinearLayoutManager(activity));
        subjectRV.setNestedScrollingEnabled(false);
        subjectWiseResults = new ArrayList<>();
        partList = new ArrayList<>();
        partAList = new ArrayList<>();
        partBList = new ArrayList<>();
        partCList = new ArrayList<>();
        partASubjectList = new ArrayList<>();
        partBSubjectList = new ArrayList<>();
        partCSubjectList = new ArrayList<>();
        bindControls();
    }

    private void bindControls() {

        allTV.setOnClickListener(this);
        partATV.setOnClickListener(this);
        partBTV.setOnClickListener(this);
        partCTV.setOnClickListener(this);
        myResultTV.setOnClickListener(this);
        compareWithTopperTV.setOnClickListener(this);
        subCard.setVisibility(View.GONE);
        percentageLL.setVisibility(View.VISIBLE);
        compareLL.setVisibility(View.VISIBLE);
        myScore.setText(resultTestSeries.getMarks());
        averageScore.setText(resultTestSeries.getAverageMarks().getAvg_marks());
        toppersScore.setText(resultTestSeries.getTopperScore());
        totalScoreOne.setText(Const.OUT_OF + resultTestSeries.getTestSeriesMarks());
        totalScoreTwo.setText(Const.OUT_OF + resultTestSeries.getTestSeriesMarks());
        totalScoreThree.setText(Const.OUT_OF + resultTestSeries.getTestSeriesMarks());
        netoworkCallSubjectResult();
    }

    private void setBarChartData() {

        BarData data = createChartData();
        configureChartAppearance();
        prepareChartData(data);
    }

    private void configureChartAppearance() {
        chart.setPinchZoom(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        chart.getDescription().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);

        chart.getAxisRight().setEnabled(false);

        chart.getXAxis().setAxisMinimum(0);
        chart.getXAxis().setAxisMaximum(MAX_X_VALUE);
    }

    private BarData createChartData() {

        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();
        ArrayList<BarEntry> values3 = new ArrayList<>();

        for (int i = 0; i < MAX_X_VALUE; i++) {
            values1.add(new BarEntry(i, 30f));
            values2.add(new BarEntry(i, 20f));
            values3.add(new BarEntry(i, 10f));
        }

        BarDataSet set1 = new BarDataSet(values1, GROUP_1_LABEL);
        BarDataSet set2 = new BarDataSet(values2, GROUP_2_LABEL);
        BarDataSet set3 = new BarDataSet(values3, GROUP_3_LABEL);

        set1.setColor(ColorTemplate.MATERIAL_COLORS[0]);
        set2.setColor(ColorTemplate.MATERIAL_COLORS[1]);
        set3.setColor(ColorTemplate.MATERIAL_COLORS[2]);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);

        return new BarData(dataSets);
    }

    private void prepareChartData(BarData data) {
        chart.setData(data);

        chart.getBarData().setBarWidth(BAR_WIDTH);

        float groupSpace = 1f - ((BAR_SPACE + BAR_WIDTH) * GROUPS);
        chart.groupBars(0, groupSpace, BAR_SPACE);

        chart.invalidate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.allTV:
                if (type.equals("0")) {
                    progressBarAdapter = new ProgressBarAdapter(activity, subjectWiseResults, "0");
                } else {
                    progressBarAdapter = new ProgressBarAdapter(activity, subjectWiseResults, "1");
                }
                setActiveView(0);
                break;
            case R.id.partATV:
                if (type.equals("0")) {
                    progressBarAdapter = new ProgressBarAdapter(activity, partASubjectList, "0");

                } else {
                    progressBarAdapter = new ProgressBarAdapter(activity, partASubjectList, "1");

                }
                setActiveView(1);
                break;
            case R.id.partBTV:
                if (type.equals("0")) {
                    progressBarAdapter = new ProgressBarAdapter(activity, partBSubjectList, "0");

                } else {
                    progressBarAdapter = new ProgressBarAdapter(activity, partBSubjectList, "1");

                }
                setActiveView(2);
                break;
            case R.id.partCTV:
                if (type.equals("0")) {
                    progressBarAdapter = new ProgressBarAdapter(activity, partCSubjectList, "0");

                } else {
                    progressBarAdapter = new ProgressBarAdapter(activity, partCSubjectList, "1");

                }
                setActiveView(3);
                break;
            case R.id.myResultTV:
                type = "0";
                setActiveView(0);
                myResultTV.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                myResultTV.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_grid_list));
                compareWithTopperTV.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                compareWithTopperTV.setTextColor(ContextCompat.getColor(activity, R.color.white));
                progressBarAdapter = new ProgressBarAdapter(activity, subjectWiseResults, "0");
                subjectRV.setAdapter(progressBarAdapter);
                break;
            case R.id.compareWithTopperTV:
                type = "1";
                setActiveView(0);
                compareWithTopperTV.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                compareWithTopperTV.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_grid_list));
                myResultTV.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                myResultTV.setTextColor(ContextCompat.getColor(activity, R.color.white));
                progressBarAdapter = new ProgressBarAdapter(activity, subjectWiseResults, "1");
                subjectRV.setAdapter(progressBarAdapter);
                break;
        }
        subjectRV.setAdapter(progressBarAdapter);
    }

    private void setActiveView(int type) {
        allTV.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
        allTV.setTextColor(ContextCompat.getColor(activity, R.color.black));
        partATV.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
        partATV.setTextColor(ContextCompat.getColor(activity, R.color.black));
        partBTV.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
        partBTV.setTextColor(ContextCompat.getColor(activity, R.color.black));
        partCTV.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
        partCTV.setTextColor(ContextCompat.getColor(activity, R.color.black));
        if (type == 0) {
            allTV.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_btn));
            allTV.setTextColor(ContextCompat.getColor(activity, R.color.white));
        } else if (type == 1) {
            partATV.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_btn));
            partATV.setTextColor(ContextCompat.getColor(activity, R.color.white));
        } else if (type == 2) {
            partBTV.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_btn));
            partBTV.setTextColor(ContextCompat.getColor(activity, R.color.white));
        } else if (type == 3) {
            partCTV.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_btn));
            partCTV.setTextColor(ContextCompat.getColor(activity, R.color.white));
        }
    }

    private void extractParts() {
        for (int i = 0; i < subjectData.getPart().size(); i++) {
            if (!partList.contains(subjectData.getPart().get(i).getPartName())) {
                partList.add(subjectData.getPart().get(i).getPartName());
            }
        }
        Collections.sort(partList);
    }

    private void setPartData() {
        for (int i = 0; i < subjectData.getPart().size(); i++) {
            String partName = subjectData.getPart().get(i).getPartName();
            if (partName.equalsIgnoreCase(partList.get(0))) {
                partAList.add(subjectData.getPart().get(i));
            } else if (partName.equalsIgnoreCase(partList.get(1))) {
                partBList.add(subjectData.getPart().get(i));
            } else if (partName.equalsIgnoreCase(partList.get(2))) {
                partCList.add(subjectData.getPart().get(i));
            }
        }
    }

    private void setPartWiseSubject() {

        for (int i = 0; i < partAList.size(); i++) {
            for (int j = 0; j < subjectData.getSubject().size(); j++) {
                if (partAList.get(i).getSectionId().equals(subjectData.getSubject().get(j).getSubjectId())) {
                    partASubjectList.add(subjectData.getSubject().get(j));
                }
            }
        }

        for (int i = 0; i < partBList.size(); i++) {
            for (int j = 0; j < subjectData.getSubject().size(); j++) {
                if (partBList.get(i).getSectionId().equals(subjectData.getSubject().get(j).getSubjectId())) {
                    partBSubjectList.add(subjectData.getSubject().get(j));
                }
            }
        }

        for (int i = 0; i < partCList.size(); i++) {
            for (int j = 0; j < subjectData.getSubject().size(); j++) {
                if (partCList.get(i).getSectionId().equals(subjectData.getSubject().get(j).getSubjectId())) {
                    partCSubjectList.add(subjectData.getSubject().get(j));
                }
            }
        }
    }

    public void netoworkCallSubjectResult() {
        myNetworkCall.NetworkAPICall(API.API_GET_SUBJECT_ANALYSIS, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        params.put("result_id", testSegmentId);
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        Helper.showErrorLayoutForNoNav(API.API_GET_SUBJECT_ANALYSIS, activity, 0, 0);
        JSONObject data = GenericUtils.getJsonObject(jsonObject);
        subjectData = new Gson().fromJson(data.toString(), SubjectWiseResultData.class);

        if (subjectData.getPart() != null && !subjectData.getPart().isEmpty()) {
            cardPart.setVisibility(View.VISIBLE);
        } else {
            cardPart.setVisibility(View.GONE);
        }

        subjectWiseResults.addAll(subjectData.getSubject());
        if (!subjectWiseResults.isEmpty()) {
            progressBarAdapter = new ProgressBarAdapter(activity, subjectWiseResults, "0");
            subjectRV.setAdapter(progressBarAdapter);
        }

        extractParts();
        setPartData();
        setBarChartData();
        setPartWiseSubject();
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        if (jsonString.equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message))) {
            Helper.showErrorLayoutForNoNav(API.API_GET_SUBJECT_ANALYSIS, activity, 1, 1);
        } else {
            Helper.showErrorLayoutForNoNav(API.API_GET_SUBJECT_ANALYSIS, activity, 1, 2);
        }
    }
}
