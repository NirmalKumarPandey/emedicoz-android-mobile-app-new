package com.emedicoz.app.courses.fragment;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.courses.adapter.DQBProgressAdapter;
import com.emedicoz.app.courses.adapter.TestSeriesGraphAdapter;
import com.emedicoz.app.courses.adapter.TestSeriesProgressAdapter;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.TabData;
import com.emedicoz.app.modelo.courses.TestQuizPojo;
import com.emedicoz.app.modelo.courses.TestSeries;
import com.emedicoz.app.modelo.courses.Topics;
import com.emedicoz.app.pastpaperexplanation.activity.PastPaperExplanationActivity;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.GridSpacingItemDecoration;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.LinearLayoutManagerWithSmoothScroller;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudyFragment extends Fragment implements View.OnClickListener, MyNetworkCall.MyNetworkCallBack {

    Activity activity;
    TextView studyTV;
    TextView tvReadCount;
    TextView tvTotalCount;
    LinearLayout mainRL;
    LinearLayout coursePanelLL;
    ArrayList<TabData> tabList;
    ArrayList<View> viewArrayList;
    String fragType = "";
    int fragNum = 0;
    private CircularProgressIndicator circularProgress;
    private FrameLayout flCirculorProgress;
    LayoutTransition layoutTransition;

    View.OnClickListener onTileClicklistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            layoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING);
            ((ViewGroup) mainRL.getParent()).setLayoutTransition(layoutTransition);

            for (int i = 0; i < viewArrayList.size(); i++) {
                viewArrayList.get(i).findViewById(R.id.parentViewLL).setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                ((TextView) viewArrayList.get(i).findViewById(R.id.tilesTextTv)).setTextColor(ContextCompat.getColor(activity, R.color.black));
                if (view == viewArrayList.get(i)) {
                    TabData tab = (TabData) view.getTag();
                    // course_type 2 for TEST, 3 for DQB, 4 for CRS, 5 for EBOOK
                    System.out.println("tab.getCourseType()--------------------"+tab.getCourseType());
                    switch (tab.getCourseType()) {
                        case Constants.Type.STUDY_TYPE_TEST:
                            viewArrayList.get(i).findViewById(R.id.parentViewLL).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_tab_test));
                            ((TextView) viewArrayList.get(i).findViewById(R.id.tilesTextTv)).setTextColor(ContextCompat.getColor(activity, R.color.left_panel_header_text_color));
                            replaceFragment(NewTestFragment.newInstance(tab.getCourseId()));
                            setSectionBackground(Constants.StudyType.TEST);
                            flCirculorProgress.setVisibility(View.GONE);
                            studyTV.setVisibility(View.GONE);
                            testSeriesLL.setVisibility(View.VISIBLE);
                            break;
                        case Constants.Type.STUDY_TYPE_DQB:
                            viewArrayList.get(i).findViewById(R.id.parentViewLL).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_tab_qbank));
                            ((TextView) viewArrayList.get(i).findViewById(R.id.tilesTextTv)).setTextColor(ContextCompat.getColor(activity, R.color.left_panel_header_text_color));
                            replaceFragment(QuestionBankSubject.newInstance(tab.getCourseId()));
                            setSectionBackground(Constants.StudyType.DQB);
                            if (singleCourseData != null) {
                                studyTV.setVisibility(View.GONE);
                                flCirculorProgress.setVisibility(View.VISIBLE);
                            } else {
                                studyTV.setVisibility(View.VISIBLE);
                                flCirculorProgress.setVisibility(View.GONE);
                            }
                            testSeriesLL.setVisibility(View.GONE);
                            break;
                        case Constants.Type.STUDY_TYPE_CRS:
                            viewArrayList.get(i).findViewById(R.id.parentViewLL).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_tab_ebook));
                            ((TextView) viewArrayList.get(i).findViewById(R.id.tilesTextTv)).setTextColor(ContextCompat.getColor(activity, R.color.left_panel_header_text_color));
                            replaceFragment(CRSFragment.newInstance(tab.getCourseId()));
                            setSectionBackground(Constants.StudyType.CRS);
                            flCirculorProgress.setVisibility(View.GONE);
                            studyTV.setVisibility(View.VISIBLE);
                            testSeriesLL.setVisibility(View.GONE);
                            break;
                        case Constants.Type.STUDY_TYPE_EBOOK:
                            viewArrayList.get(i).findViewById(R.id.parentViewLL).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_tab_ebook));
                            ((TextView) viewArrayList.get(i).findViewById(R.id.tilesTextTv)).setTextColor(ContextCompat.getColor(activity, R.color.left_panel_header_text_color));
                            replaceFragment(EBookFragment.newInstance(tab.getCourseId()));
                            setSectionBackground(Constants.StudyType.EBOOK);
                            flCirculorProgress.setVisibility(View.GONE);
                            studyTV.setVisibility(View.VISIBLE);
                            testSeriesLL.setVisibility(View.GONE);
                            break;
                        case Constants.Type.PPE_KIT:
                            viewArrayList.get(i).findViewById(R.id.parentViewLL).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_tab_ebook));
                            ((TextView) viewArrayList.get(i).findViewById(R.id.tilesTextTv)).setTextColor(ContextCompat.getColor(activity, R.color.left_panel_header_text_color));
                            setSectionBackground(Constants.StudyType.PREVIOUS_PAPER);
                            flCirculorProgress.setVisibility(View.GONE);
                            studyTV.setVisibility(View.VISIBLE);
                            testSeriesLL.setVisibility(View.GONE);
                            Intent intent = new Intent(activity, PastPaperExplanationActivity.class);
                            startActivity(intent);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    };
    private RecyclerView recyclerView;
    int complete;
    int total;
    SingleCourseData singleCourseData;
    ArrayList<Topics> topicsArrayList = new ArrayList<>();
    ImageView leftClickIV;
    ImageView rightClickIV;
    RecyclerView testSeriesRV;
    NonScrollRecyclerView graphGTRV;
    NonScrollRecyclerView graphOtherRV;
    LinearLayoutManager horizontalLayoutManager;
    RelativeLayout backRL;
    RelativeLayout rlQbankBack;
    RelativeLayout testSeriesLL;
    LinearLayout testShowHideLL;
    LinearLayout graphGTLL;
    LinearLayout otherLL;
    ImageView tapIV;
    ScrollView scrollView;
    TextView tvTapHereToSeeYourProgress;
    TextView gtGivenTV;
    TextView bestScoreTV;
    TextView bestRankTV;
    TextView avgPerTV;
    TextView showMoreGTTV;
    TextView showMoreOtherTV;
    TextView showLessGTTV;
    TextView showLessOtherTV;
    TestSeriesGraphAdapter testSeriesGraphAdapter;
    ArrayList<TestSeries> grandTestsProgressArrayList = new ArrayList<>();
    ArrayList<TestSeries> grandTests = new ArrayList<>();
    ArrayList<TestSeries> otherTests = new ArrayList<>();
    ArrayList<TestSeries> allGrandTestList = new ArrayList<>();
    ArrayList<TestSeries> allOtherTestList = new ArrayList<>();
    MyNetworkCall myNetworkCall;
    private static final String TAG = "StudyFragment";
    Menu menu;

    public StudyFragment() {
        // Required empty public constructor
    }

    public static StudyFragment newInstance(String fragType) {
        StudyFragment studyFragment = new StudyFragment();
        Bundle args = new Bundle();
        args.putString(Const.FRAG_TYPE, fragType);
        studyFragment.setArguments(args);
        return studyFragment;
    }

    // this method is used to get the instance of fragment and set the arguments
    public static StudyFragment newInstance() {
        StudyFragment fragment = new StudyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            fragType = getArguments().getString(Const.FRAG_TYPE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof HomeActivity) {
            menu = ((HomeActivity) activity).getMyMenu();
            if (menu!=null){
                menu.findItem(R.id.app_bar_search).setVisible(false);
            }
            ((HomeActivity) activity).floatingActionButton.setVisibility(View.GONE);
            ((HomeActivity) activity).toolbarHeader.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_study, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        layoutTransition = new LayoutTransition();
        mainRL = view.findViewById(R.id.mainRL);
        //Load animation
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        ((ViewGroup) mainRL.getParent()).setLayoutTransition(layoutTransition);
        studyTV = view.findViewById(R.id.textViewStudy);
        coursePanelLL = view.findViewById(R.id.coursePanelLL);
        flCirculorProgress = view.findViewById(R.id.fl_circulor_progress);
        circularProgress = view.findViewById(R.id.circular_progress);
        graphGTLL = view.findViewById(R.id.graphGTLL);
        otherLL = view.findViewById(R.id.otherLL);

        rlQbankBack = view.findViewById(R.id.rlQbankBack);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 5, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(5, 25, true));

        tvReadCount = view.findViewById(R.id.tv_read_count);
        tvTotalCount = view.findViewById(R.id.tv_total_count);

        testSeriesLL = view.findViewById(R.id.testSeriesLL);
        tapIV = view.findViewById(R.id.tapIV);
        scrollView = view.findViewById(R.id.scrollView);
        testShowHideLL = view.findViewById(R.id.testShowHideLL);
        gtGivenTV = view.findViewById(R.id.gtGivenTV);
        bestScoreTV = view.findViewById(R.id.bestScoreTV);
        bestRankTV = view.findViewById(R.id.bestRankTV);
        avgPerTV = view.findViewById(R.id.avgPerTV);
        showMoreGTTV = view.findViewById(R.id.showMoreGTTV);
        showMoreOtherTV = view.findViewById(R.id.showMoreOtherTV);
        showLessGTTV = view.findViewById(R.id.showLessGTTV);
        showLessOtherTV = view.findViewById(R.id.showLessOtherTV);
        rightClickIV = view.findViewById(R.id.rightClickIV);
        leftClickIV = view.findViewById(R.id.leftClickIV);
        tvTapHereToSeeYourProgress = view.findViewById(R.id.tv_tap_here_to_see_your_progress);
        testSeriesRV = view.findViewById(R.id.testSeriesRV);
        backRL = view.findViewById(R.id.backRL);
        horizontalLayoutManager = new LinearLayoutManagerWithSmoothScroller(activity, LinearLayoutManager.HORIZONTAL, false);
        testSeriesRV.setLayoutManager(horizontalLayoutManager);
        graphGTRV = view.findViewById(R.id.graphGTRV);
        graphGTRV.setLayoutManager(new LinearLayoutManager(activity));
        graphOtherRV = view.findViewById(R.id.graphOtherRV);
        graphOtherRV.setLayoutManager(new LinearLayoutManager(activity));
        graphGTRV.setNestedScrollingEnabled(false);
        graphOtherRV.setNestedScrollingEnabled(false);
        bindControls();
    }

    private void bindControls() {
        tabList = new ArrayList<>();
        viewArrayList = new ArrayList<>();
        flCirculorProgress.setOnClickListener(this);
        rightClickIV.setOnClickListener(this);
        leftClickIV.setOnClickListener(this);
        tapIV.setOnClickListener(this);
        showMoreGTTV.setOnClickListener(this);
        showMoreOtherTV.setOnClickListener(this);
        showLessGTTV.setOnClickListener(this);
        showLessOtherTV.setOnClickListener(this);
        backRL.setOnClickListener(this);
        flCirculorProgress.setOnClickListener(this);
        rlQbankBack.setOnClickListener(this);

        if (activity instanceof BaseABNavActivity) {
            studyTV.setVisibility(View.GONE);
        } else {
            studyTV.setVisibility(View.VISIBLE);
        }
        networkCallForTabData("bindControls");
    }

    public CircularProgressIndicator.ProgressTextAdapter TIME_TEXT_ADAPTER = currentProgress -> "";

    public void setTestSeriesData(TestQuizPojo testQuizPojo) {

        Log.e("TAG", "setTestSeriesData: " + testQuizPojo.toString());
        clearTestArrayList();

        for (int i = 0; i < testQuizPojo.getMasters().length; i++) {
            if (testQuizPojo.getMasters()[i].getTest_type_title().equalsIgnoreCase(Constants.TestType.GRAND_TEST)) {
                gtGivenTV.setText(testQuizPojo.getMasters()[i].getAttempt());
                bestScoreTV.setText(testQuizPojo.getMasters()[i].getMax_mark());
                avgPerTV.setText(testQuizPojo.getMasters()[i].getAvg_percentage() + " %");
            }
        }

        for (int i = 0; i < testQuizPojo.getTest_series().length; i++) {
            TestSeries testSeries = testQuizPojo.getTest_series()[i];
            for (int j = 0; j < testQuizPojo.getMasters().length; j++) {
                String title = testQuizPojo.getMasters()[j].getTest_type_title();
                if (testSeries.getTopic_name().equalsIgnoreCase(title)) {
                    if (testSeries.getTopic_name().equalsIgnoreCase(Constants.TestType.GRAND_TEST)) {
                        grandTestsProgressArrayList.add(testSeries);
                    }
                    if (testSeries.getIs_paused().equals("0")) {
                        if (testSeries.getTopic_name().equalsIgnoreCase(Constants.TestType.GRAND_TEST))
                            grandTests.add(testSeries);
                        else
                            otherTests.add(testSeries);
                    }
                }
            }
        }

        testSeriesRV.setAdapter(new TestSeriesProgressAdapter(activity, grandTestsProgressArrayList));

        if (!grandTests.isEmpty()) {
            graphGTLL.setVisibility(View.VISIBLE);
            setGrandTest();
        } else {
            graphGTLL.setVisibility(View.GONE);
        }

        if (!otherTests.isEmpty()) {
            setOtherTest();
            otherLL.setVisibility(View.VISIBLE);
        } else {
            otherLL.setVisibility(View.GONE);
        }
    }

    private void clearTestArrayList() {
        if (!grandTestsProgressArrayList.isEmpty())
            grandTestsProgressArrayList.clear();
        if (!grandTests.isEmpty())
            grandTests.clear();
        if (!otherTests.isEmpty())
            otherTests.clear();
        if (!allGrandTestList.isEmpty())
            allGrandTestList.clear();
        if (!allOtherTestList.isEmpty())
            allOtherTestList.clear();
    }

    private void setGrandTest() {
        if (grandTests.size() <= 5) {
            showMoreGTTV.setVisibility(View.GONE);
            showLessGTTV.setVisibility(View.GONE);
            testSeriesGraphAdapter = new TestSeriesGraphAdapter(activity, grandTests);
        } else {
            showMoreGTTV.setVisibility(View.VISIBLE);
            showLessGTTV.setVisibility(View.GONE);
            for (int i = 0; i < 5; i++) {
                allGrandTestList.add(grandTests.get(i));
            }
            testSeriesGraphAdapter = new TestSeriesGraphAdapter(activity, allGrandTestList);
        }

        graphGTRV.setAdapter(testSeriesGraphAdapter);
    }

    private void setOtherTest() {
        if (otherTests.size() <= 5) {
            showMoreOtherTV.setVisibility(View.GONE);
            showLessOtherTV.setVisibility(View.GONE);
            testSeriesGraphAdapter = new TestSeriesGraphAdapter(activity, otherTests);
        } else {
            showMoreOtherTV.setVisibility(View.VISIBLE);
            showLessOtherTV.setVisibility(View.GONE);
            for (int i = 0; i < 5; i++) {
                allOtherTestList.add(otherTests.get(i));
            }
            testSeriesGraphAdapter = new TestSeriesGraphAdapter(activity, allOtherTestList);
        }
        graphOtherRV.setAdapter(testSeriesGraphAdapter);
    }

    public void setDQBProgress(SingleCourseData singleCourseData) {
        Log.e("StudyFragment ", "setDQBProgress: ");
        this.singleCourseData = singleCourseData;
        complete = 0;
        total = 0;
        topicsArrayList.clear();
        topicsArrayList.addAll(Arrays.asList(singleCourseData.getCurriculam().getTopics()));
        flCirculorProgress.setVisibility(View.VISIBLE);
        studyTV.setVisibility(View.GONE);
        for (int i = 0; i < topicsArrayList.size(); i++) {
            complete = complete + Integer.parseInt(!TextUtils.isEmpty(topicsArrayList.get(i).getCompleted()) ? topicsArrayList.get(i).getCompleted() : "0");
            total = total + Integer.parseInt(!TextUtils.isEmpty(topicsArrayList.get(i).getTotal()) ? topicsArrayList.get(i).getTotal() : "0");
        }
        Log.e("TAG", "bindControls: " + complete);
        Log.e("TAG", "bindControls: " + total);

        circularProgress.setProgress(complete, total);
        tvReadCount.setText(String.valueOf(complete));
        tvTotalCount.setText(String.valueOf(total));

        circularProgress.setProgressTextAdapter(TIME_TEXT_ADAPTER);
        recyclerView.setAdapter(new DQBProgressAdapter(activity, this, topicsArrayList, singleCourseData));
    }

    public void networkCallForTabData(String str) {
        Log.e("StudyFragment", "networkCallForTabData: " + str);
        myNetworkCall.NetworkAPICall(API.API_STUDY_TAB_DATA, true);
    }

    private void setTabData() {
        mainRL.setVisibility(View.VISIBLE);
        coursePanelLL.removeAllViews();
        viewArrayList.clear();
        coursePanelLL.setWeightSum(tabList.size());
        for (TabData tab : tabList) {
            coursePanelLL.setGravity(Gravity.CENTER);
            coursePanelLL.addView(addTiles(tab));
        }
    }

    private View addTiles(TabData tab) {
        LinearLayout view = (LinearLayout) View.inflate(activity, R.layout.single_item_study_tab, null);
        TextView tilesText = view.findViewById(R.id.tilesTextTv);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        layoutParams.setMargins(5, 5, 5, 5);

        tilesText.setText(tab.getTitle());
        view.setTag(tab);
        view.setLayoutParams(layoutParams);
        view.setGravity(Gravity.CENTER);
        view.setPadding(2, 2, 2, 10);
        viewArrayList.add(view);
        view.setOnClickListener(onTileClicklistener);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlQbankBack:
            case R.id.fl_circulor_progress:
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                    rlQbankBack.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    rlQbankBack.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.leftClickIV:
                if (horizontalLayoutManager.findFirstVisibleItemPosition() > 0) {
                    testSeriesRV.smoothScrollToPosition(horizontalLayoutManager.findFirstVisibleItemPosition() - 1);
                }
                break;
            case R.id.rightClickIV:
                testSeriesRV.smoothScrollToPosition(horizontalLayoutManager.findLastVisibleItemPosition() + 1);
                break;
            case R.id.tapIV:
                if (scrollView.getVisibility() == View.VISIBLE) {
                    scrollView.setVisibility(View.GONE);
                    tapIV.setImageResource(R.mipmap.tap);
                    backRL.setVisibility(View.GONE);
                    tvTapHereToSeeYourProgress.setVisibility(View.VISIBLE);
                    mainRL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_test));
                } else {
                    scrollView.setVisibility(View.VISIBLE);
                    tapIV.setImageResource(R.mipmap.tap_open);
                    backRL.setVisibility(View.VISIBLE);
                    tvTapHereToSeeYourProgress.setVisibility(View.GONE);
                    mainRL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_bottom_round_corner_fill_dark_green));
                }
                break;
            case R.id.showMoreGTTV:
                if (!allGrandTestList.isEmpty())
                    allGrandTestList.clear();
                allGrandTestList.addAll(grandTests);
                if (testSeriesGraphAdapter != null)
                    testSeriesGraphAdapter.notifyDataSetChanged();
                showMoreGTTV.setVisibility(View.GONE);
                showLessGTTV.setVisibility(View.VISIBLE);
                break;
            case R.id.showMoreOtherTV:
                if (!allOtherTestList.isEmpty())
                    allOtherTestList.clear();
                allOtherTestList.addAll(otherTests);
                if (testSeriesGraphAdapter != null)
                    testSeriesGraphAdapter.notifyDataSetChanged();
                showMoreOtherTV.setVisibility(View.GONE);
                showLessOtherTV.setVisibility(View.VISIBLE);
                break;
            case R.id.showLessGTTV:
                if (!allGrandTestList.isEmpty())
                    allGrandTestList.clear();
                if (!GenericUtils.isListEmpty(grandTests)) {
                    for (int i = 0; i < 5; i++)
                        allGrandTestList.add(grandTests.get(i));
                }
                if (testSeriesGraphAdapter != null)
                    testSeriesGraphAdapter.notifyDataSetChanged();
                showMoreGTTV.setVisibility(View.VISIBLE);
                showLessGTTV.setVisibility(View.GONE);
                break;
            case R.id.showLessOtherTV:
                if (!allOtherTestList.isEmpty())
                    allOtherTestList.clear();
                for (int i = 0; i < 5; i++) {
                    allOtherTestList.add(otherTests.get(i));
                }
                if (testSeriesGraphAdapter != null)
                    testSeriesGraphAdapter.notifyDataSetChanged();
                showMoreOtherTV.setVisibility(View.VISIBLE);
                showLessOtherTV.setVisibility(View.GONE);
                break;
            case R.id.backRL:
                if (scrollView.getVisibility() == View.VISIBLE)
                    scrollView.setVisibility(View.GONE);
                tapIV.setImageResource(R.mipmap.tap);
                backRL.setVisibility(View.GONE);
                mainRL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_test));
                break;
            default:
                break;
        }
    }

    private void setSectionBackground(String section) {
        switch (section) {
            case Constants.StudyType.DQB:
                studyTV.setText(Constants.StudyType.DQB);
                mainRL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_qbank));
                break;
            case Constants.StudyType.TEST:
                studyTV.setText(Constants.StudyType.TEST);
                mainRL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_test));
                break;
            case Constants.StudyType.CRS:
                studyTV.setText(Constants.StudyType.CRS);
                mainRL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_ebook));
                break;
            case Constants.StudyType.EBOOK:
                studyTV.setText(Constants.StudyType.EBOOK);
                mainRL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_study_ebook));
                break;
            case Constants.StudyType.PREVIOUS_PAPER:
                studyTV.setText(Constants.StudyType.PREVIOUS_PAPER);
                mainRL.setBackground(ContextCompat.getDrawable(activity, R.drawable.drawable_gradient_dvl));
                break;
            default:
                break;
        }
    }

    public void addFragment(Fragment fragment) {
        try {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_container, fragment)
                    .commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void replaceFragment(Fragment fragment) {
        try {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        tabList.clear();

        JSONArray data = GenericUtils.getJsonArray(jsonObject);
        if (jsonObject.optBoolean("status") && data.length() != 0) {
            for (int i = 0; i < data.length(); i++) {
                JSONObject dataObj = data.optJSONObject(i);
                TabData tabData = new Gson().fromJson(dataObj.toString(), TabData.class);
                if (!tabData.getCourseType().equals("1")) {
                    tabList.add(tabData);
                }
                if (fragType != null && fragType.equals(Constants.StudyType.CRS) && tabData.getCourseType().equals("4")) {
                    fragNum = i;
                }
                if (fragType != null && fragType.equals(Constants.StudyType.TESTS) && tabData.getCourseType().equals("2")) {
                    fragNum = i;
                }
                if (fragType != null && fragType.equals(Constants.StudyType.QBANKS) && tabData.getCourseType().equals("3")) {
                    fragNum = i;
                }

            }
            setTabData();

            new Handler().postDelayed(() -> {
                if (!GenericUtils.isListEmpty(viewArrayList)) {
                    viewArrayList.get(fragNum).findViewById(R.id.parentViewLL).performClick();
                }

            }, 100);

        } else
            Helper.showErrorLayoutForNav(API.API_STUDY_TAB_DATA, activity, 1, 0);
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        if (jsonString.equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message))) {
            if (activity instanceof BaseABNavActivity) {
                Helper.showErrorLayoutForNav(API.API_STUDY_TAB_DATA, activity, 1, 1);
            } else {
                Helper.showErrorLayoutForNoNav(API.API_STUDY_TAB_DATA, activity, 1, 1);
            }
        } else {
            if (activity instanceof BaseABNavActivity) {
                Helper.showErrorLayoutForNav(API.API_STUDY_TAB_DATA, activity, 1, 2);
            } else {
                Helper.showErrorLayoutForNoNav(API.API_STUDY_TAB_DATA, activity, 1, 2);
            }
        }
    }

}