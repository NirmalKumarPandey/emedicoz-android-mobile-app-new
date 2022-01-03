package com.emedicoz.app.courses.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.emedicoz.app.R;
import com.emedicoz.app.combocourse.activity.ComboCourseActivity;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.adapter.AllCoursesAdapater;
import com.emedicoz.app.courses.adapter.CatAdapter;
import com.emedicoz.app.courses.adapter.CourseListAdapter;
import com.emedicoz.app.courses.adapter.TileAdapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.CourseBannerData;
import com.emedicoz.app.modelo.Registration;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.CourseCatTileData;
import com.emedicoz.app.modelo.courses.CourseCategory;
import com.emedicoz.app.modelo.courses.CoursesData;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.pastpaperexplanation.activity.PastPaperExplanationActivity;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.retrofit.apiinterfaces.LandingPageApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommonFragForList extends Fragment implements MyNetworkCall.MyNetworkCallBack {

    private static final String TAG = "CommonFragForList";
    public ArrayList<String> seeAllStatus = new ArrayList<>();
    public String fragType = "";
    public String errorMessage;
    public String apiType;
    public int firstVisibleItem;
    public int visibleItemCount;
    public int totalItemCount;
    public int previousTotalItemCount;
    public boolean isSearching = false;
    public boolean isRefreshing = true;
    public String lastCourseId;
    public String lastTestId;
    public String email;
    public String password;
    public String damsToken;
    public String damsPass;
    public Progress mProgress;
    int start = 0;
    ImageView bannerIV;
    SwipeRefreshLayout swipeRefreshLayout;
    Dialog dialog;
    LinearLayout topTestQbank;
    LinearLayout catLL;
    User user, user1;
    CardView cardQbank, cardTestSeries, cvPreviousPaper;
    RecyclerView tileRV, catRV;
    Registration registration;
    TileAdapter tileAdapter;
    LinearLayoutManager linearLayoutManager;
    Activity activity;
    CourseCategory courseCategory;
    TextView all;
    ArrayList<Course> courseArrayList;
    public ArrayList<Course> searchArrayList;
    public ArrayList<CoursesData> coursesDataArrayList;
    ArrayList<CourseCatTileData> courseCatTileDataArrayList;
    ImageView liveCourseIV;
    ArrayList<ResultTestSeries> resultTestSeriesArrayList;
    AllCoursesAdapater allCoursesAdapater;
    CourseListAdapter courseListAdapter;
    Course course;
    int isAlreadyConnected = 0;
    EditText mDAMSET;
    EditText mDAMSpasswordET;
    String categoryId;
    CourseBannerData courseBannerData;
    public ArrayList<CourseCategory> categoryArrayList;
    NestedScrollView nestedScrollView;
    private RecyclerView commonListRV;
    private boolean loading = true;
    private int visibleThreshold = 5;
    private TextView errorTV;
    private TextView damsVerifyTV;
    CardView cvBrowsePlan;
    EditText mDAMSPasswordET;
    ImageView ivClearSearch;
    LinearLayout llSearchView;
    CardView registrationCV;
    public EditText etSearch;
    MyNetworkCall myNetworkCall;

    public CommonFragForList() {
        // Required empty public constructor
    }

    public static CommonFragForList newInstance(String fragType, CourseCategory courseCategory) {
        CommonFragForList commonFragForList = new CommonFragForList();
        Bundle args = new Bundle();
        args.putSerializable(Const.CATEGORY, courseCategory);
        args.putString(Const.FRAG_TYPE, fragType);
        commonFragForList.setArguments(args);
        return commonFragForList;
    }

    public static CommonFragForList newInstance(String fragType, ArrayList<Course> arrayList) {
        CommonFragForList commonFragForList = new CommonFragForList();
        Bundle args = new Bundle();
        args.putSerializable(Const.COURSE_LIST, arrayList);
        args.putString(Const.FRAG_TYPE, fragType);
        commonFragForList.setArguments(args);
        return commonFragForList;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        myNetworkCall = new MyNetworkCall(this, activity);
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);

        courseArrayList = new ArrayList<>();
        searchArrayList = new ArrayList<>();
        coursesDataArrayList = new ArrayList<>();
        resultTestSeriesArrayList = new ArrayList<>();
        courseCatTileDataArrayList = new ArrayList<>();
        categoryArrayList = new ArrayList<>();

        if (getArguments() != null) {
            categoryId = getArguments().getString(Const.CATEGORY_ID);
            fragType = getArguments().getString(Const.FRAG_TYPE);
            courseCategory = (CourseCategory) getArguments().getSerializable(Const.CATEGORY);
            course = (Course) getArguments().getSerializable(Const.COURSES);
            ArrayList<Course> courseList = (ArrayList<Course>) getArguments().getSerializable(Const.COURSE_LIST);
            if (courseList != null && !courseList.isEmpty())
                courseArrayList = courseList;
        }
        Log.e(TAG, "onCreate: frag_type - " + fragType);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof BaseABNavActivity) {
            ((BaseABNavActivity) activity).manageToolbar(Constants.ScreenName.COURSES);
        }

        getDatas(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        topTestQbank = view.findViewById(R.id.topTestQbank);
        liveCourseIV = view.findViewById(R.id.liveCourseIV);
        cvPreviousPaper = view.findViewById(R.id.cvPreviousPaper);
        cvBrowsePlan = view.findViewById(R.id.cv_browse_plan);

        llSearchView = view.findViewById(R.id.ll_search_view);
        ivClearSearch = view.findViewById(R.id.iv_clear_search);
        etSearch = view.findViewById(R.id.et_search);

        registrationCV = view.findViewById(R.id.registration);

        bannerIV = view.findViewById(R.id.bannerIV);
        if (fragType.equalsIgnoreCase(Const.ALLCOURSES)) {
            llSearchView.setVisibility(View.VISIBLE);
            topTestQbank.setVisibility(View.GONE);
            registrationCV.setVisibility(View.VISIBLE);
            nestedScrollView = view.findViewById(R.id.nestedScrollView);
            tileRV = view.findViewById(R.id.tileRV);
            catRV = view.findViewById(R.id.catRV);
            catLL = view.findViewById(R.id.catLL);
            all = view.findViewById(R.id.all);
            catRV.setLayoutManager(new GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false));

            tileRV.setLayoutManager(new GridLayoutManager(activity, 2));
            tileRV.addItemDecoration(new EqualSpacingItemDecoration(25, EqualSpacingItemDecoration.GRID));
            tileAdapter = new TileAdapter(activity, courseCatTileDataArrayList);
            tileRV.setAdapter(tileAdapter);
            all.setOnClickListener(v -> {
                if (activity instanceof BaseABNavActivity) {
                    ((BaseABNavActivity) activity).courseSidePanel();
                }
            });

            cardQbank = view.findViewById(R.id.cardQbank);
            cardTestSeries = view.findViewById(R.id.cardTestSeries);

            liveCourseIV.setOnClickListener(view1 -> ((BaseABNavActivity) Objects.requireNonNull(getActivity())).liveClassesLL.performClick());

            cvBrowsePlan.setOnClickListener(view12 -> {
                Intent intent = new Intent(activity, ComboCourseActivity.class);
                startActivity(intent);
            });

            cvPreviousPaper.setOnClickListener(view12 -> {
                Intent intent = new Intent(activity, PastPaperExplanationActivity.class);
                startActivity(intent);
            });

            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    // TODO Auto-generated method stub
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    // TODO Auto-generated method stub
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        ivClearSearch.setVisibility(View.VISIBLE);
                    } else {
                        ivClearSearch.setVisibility(View.GONE);
                        if (s.length() == 0) {
                            ((BaseABNavActivity) activity).refreshFragmentList(CommonFragForList.this, 1);
                        }
                    }
                }
            });

            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (TextUtils.isEmpty(Helper.GetText(etSearch))) {
                        Toast.makeText(activity, getString(R.string.enter_search_query), Toast.LENGTH_SHORT).show();
                    } else {
                        etSearch.clearFocus();
                        ((BaseABNavActivity) activity).searchData(Helper.GetText(etSearch));
                    }
                    return true;
                }
                return false;
            });
/*
            etSearch.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                if (keyboardShown(etSearch.getRootView())) {
                    etSearch.requestFocus();
                    ((BaseABNavActivity) activity).bottomPanelRL.setVisibility(View.GONE);
                    Log.d("keyboard", "keyboard UP");
                } else {
                    etSearch.clearFocus();
                    ((BaseABNavActivity) activity).bottomPanelRL.setVisibility(View.VISIBLE);
                    Log.d("keyboard", "keyboard Down");
                }
            });*/

            ivClearSearch.setOnClickListener(view12 -> {
                etSearch.setText("");
                ((BaseABNavActivity) activity).searchText = "";
                ((BaseABNavActivity) activity).refreshFragmentList(CommonFragForList.this, 1);
            });

            registrationCV.setOnClickListener(view12 -> {
                Intent intent = new Intent(activity, CourseActivity.class);
                intent.putExtra(Const.FRAG_TYPE, Const.REG_COURSE);
                startActivity(intent);
            });
        }

        if (activity instanceof BaseABNavActivity) {
            BaseABNavActivity.postFAB.setVisibility(View.GONE);
        }

        initViews(view);

        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        commonListRV.setLayoutManager(linearLayoutManager);

        if (!fragType.equals(Const.LEADERBOARD) && isSearching) {
            commonListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if (totalItemCount > 10) { // if the list is more than 10 then only pagination will work
                        if (loading && totalItemCount > previousTotalItemCount) {
                            loading = false;
                            previousTotalItemCount = totalItemCount;
                        }
                        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold) &&
                                !searchArrayList.isEmpty() && searchArrayList.size() > totalItemCount - 1 &&
                                isSearching)
                            lastCourseId = searchArrayList.get(totalItemCount - 1).getId();

                        if (isAlreadyConnected == 0 && !isSearching) {
                            getDatas(false);
                            isAlreadyConnected = 1;
                        }
                        loading = true;
                    }
                }
            });
        }

    }

    private void initViews(View view) {
        commonListRV = view.findViewById(R.id.courseListRV);
        errorTV = view.findViewById(R.id.errorTV);
        damsVerifyTV = view.findViewById(R.id.damsDialogTV);
        if (fragType.equals(Const.ALLCOURSES) && activity instanceof BaseABNavActivity) {
            swipeRefreshLayout = ((BaseABNavActivity) activity).swipeRefreshLayout;
            swipeRefreshLayout.setRefreshing(false);
            commonListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    int firstPos = linearLayoutManager.findFirstCompletelyVisibleItemPosition();

                    swipeRefreshLayout.setEnabled(firstPos > 0 && !swipeRefreshLayout.isRefreshing());
                }
            });
        }
    }

    private boolean keyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    public void manageLiveAndComboCourseVisibility() {
        // this method is written to show and hide liveCourse image and combo course plan image based on condition of Api response
        MasterFeedsHitResponse masterHitResponse = SharedPreference.getInstance().getMasterHitResponse();
        if (masterHitResponse != null) {

            if (masterHitResponse.getShow_live_course() != null && masterHitResponse.getShow_live_course().equalsIgnoreCase("0")) {
                if (liveCourseIV != null)
                    liveCourseIV.setVisibility(View.GONE);
            } else {
                if (liveCourseIV != null)
                    liveCourseIV.setVisibility(View.VISIBLE);
            }

            if (masterHitResponse.getDisplay_combo() != null && masterHitResponse.getDisplay_combo().equalsIgnoreCase("1")) {
                if (cvBrowsePlan != null)
                    cvBrowsePlan.setVisibility(View.VISIBLE);
            } else {
                if (cvBrowsePlan != null)
                    cvBrowsePlan.setVisibility(View.GONE);
            }
            if (masterHitResponse.getShow_ppe_course() != null && masterHitResponse.getShow_ppe_course().equalsIgnoreCase("1")) {
                if (cvPreviousPaper != null)
                    cvPreviousPaper.setVisibility(View.VISIBLE);
            } else {
                if (cvPreviousPaper != null)
                    cvPreviousPaper.setVisibility(View.GONE);
            }
        } else {
            if (liveCourseIV != null && cvBrowsePlan != null && cvPreviousPaper != null) {
                liveCourseIV.setVisibility(View.GONE);
                cvBrowsePlan.setVisibility(View.GONE);
                cvPreviousPaper.setVisibility(View.GONE);
            }
        }
    }

    public void getDatas(boolean show) {
        if (isSearching) {
            networkCallForSearchCourse();//NetworkAPICall(API.API_SEARCH_COURSE, show);
        } else if (fragType.equals(Const.ALLCOURSES)) {
            /*if (SharedPreference.getInstance().getBoolean(Const.CHANGE_STREAM))
                ((BaseABNavActivity) activity).callOnResume();
            else */
            networkCallForBanner();
            networkCallForCourseCategory();
            if (SharedPreference.getInstance().getBoolean(Const.IS_LANDING_DATA)) {
                SharedPreference.getInstance().putBoolean(Const.IS_LANDING_DATA, false);
                networkCallForLandingDataV3();
            } else {
                if (Helper.isConnected(activity) && activity instanceof BaseABNavActivity && ((BaseABNavActivity) activity).courseStatus
                        && Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_COURSE) == null) {
                    //NetworkAPICall(API.API_GET_LANDING_PAGE_DATA, show);
                    networkCallForLandingDataV3();

                } else {
                    if (isRefreshing) {
                        coursesDataArrayList = (ArrayList<CoursesData>) Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_COURSE);
                        categoryArrayList = (ArrayList<CourseCategory>) Helper.getStorageInstance(activity).getRecordObject("categories");
                        initLandingPageAdapter();

                        if (((BaseABNavActivity) activity).courseStatus)
                            networkCallForLandingDataV3();
/*
                        networkCallForLandingDataV3();
                        coursesDataArrayList = (ArrayList<CoursesData>) Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_COURSE);
                        initLandingPageAdapter();
*/
                        //NetworkAPICall(API.API_GET_LANDING_PAGE_DATA, false);

                    } else {
                        isRefreshing = true;
                        //NetworkAPICall(API.API_GET_LANDING_PAGE_DATA, true); // this one
                        networkCallForLandingDataV3();
                    }
                }
            }
        } else if (fragType.equals(Const.SEEALL_INSTRUCTOR_COURSE)) {
            initMyCourseAdapter();
        }
    }

    private void networkCallForCourseCategory() {
        myNetworkCall.NetworkAPICall(API.API_GET_COURSE_CATEGORY, true);
    }

    private void networkCallForBanner() {
        myNetworkCall.NetworkAPICall(API.API_GET_COURSE_BANNER, true);
    }

    public void networkCallForUpdateToken() {
        myNetworkCall.NetworkAPICall(API.API_UPDATE_DAMS_TOKEN, true);
    }

    public void networkCallForLandingDataV3() {
        mProgress.show();
        LandingPageApiInterface apiInterface = ApiClient.createService(LandingPageApiInterface.class);
        Call<JsonObject> response = apiInterface.getLandingPageDataV3(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    Gson gson = new Gson();
                    JSONArray dataArray;
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e("TAG", " onResponse: " + jsonResponse);
                        swipeRefreshLayout.setRefreshing(false);
                        mProgress.dismiss();
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            String pointsConversionRate = GenericUtils.getJsonObject(jsonResponse).optString("pointsConversionRate");
                            String gst = GenericUtils.getJsonObject(jsonResponse).optString("gst");

                            if (!categoryArrayList.isEmpty())
                                categoryArrayList.clear();

                            dataArray = GenericUtils.getJsonObject(jsonResponse).optJSONArray(Const.COURSE);
                            if (dataArray != null) {
                                coursesDataArrayList = new ArrayList<>();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    if (dataArray.optJSONObject(i).optJSONArray("course_list") != null &&
                                            dataArray.optJSONObject(i).optJSONArray("course_list").length() > 0)
                                        categoryArrayList.add(gson.fromJson(Objects.requireNonNull(dataArray.optJSONObject(i).optJSONObject(activity.getResources().getString(R.string.category_info))).toString(), CourseCategory.class));
                                    else if (GenericUtils.isListEmpty(categoryArrayList)) {
                                        Helper.showErrorLayoutForNav(null, activity, 0, 0);
                                        return;
                                    }

                                    if (Objects.requireNonNull(dataArray.optJSONObject(i).optJSONArray(activity.getResources().getString(R.string.course_list))).length() > 0) {
                                        CoursesData coursesData = new CoursesData();
                                        ArrayList<Course> courseArrayList = new ArrayList<>();
                                        JSONObject categoryInfo = dataArray.optJSONObject(i).optJSONObject(activity.getResources().getString(R.string.category_info));
                                        if (Objects.requireNonNull(categoryInfo).optString(activity.getResources().getString(R.string.app_view_type)).equals("1")) {
                                            coursesData.setViewItemType(1);
                                            coursesData.setCategory_info(gson.fromJson(categoryInfo.toString(), CourseCategory.class));

                                            JSONArray courseLists = dataArray.optJSONObject(i).optJSONArray(activity.getResources().getString(R.string.course_list));
                                            for (int j = 0; j < ((Objects.requireNonNull(courseLists).length() > 2) ? 3 : courseLists.length()); j++) {
                                                JSONObject courseObject = courseLists.getJSONObject(j);
                                                Course course = gson.fromJson(Objects.requireNonNull(courseObject).toString(), Course.class);
                                                course.setPoints_conversion_rate(pointsConversionRate);
                                                course.setGst(gst);
                                                if (courseObject.optString("mrp").equals("0")) {
                                                    course.setCalMrp("Free");
                                                } else if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                                                    course.setIs_dams(true);
                                                    if (courseObject.optString(activity.getResources().getString(R.string.for_dams)).equals("0")) {
                                                        course.setCalMrp("Free");
                                                    } else {
                                                        if (courseObject.optString("mrp").equals(courseObject.optString(activity.getResources().getString(R.string.for_dams)))) {
                                                            course.setCalMrp(String.format("%s %s", getCurrencySymbol(),
                                                                    Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp"))));
                                                        } else {
                                                            course.setCalMrp(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp")) + " " +
                                                                    Helper.calculatePriceBasedOnCurrency(courseObject.optString(activity.getResources().getString(R.string.for_dams))));
                                                        }
                                                    }
                                                } else {
                                                    course.setIs_dams(false);
                                                    if (courseObject.optString(activity.getResources().getString(R.string.non_dams)).equals("0")) {
                                                        course.setCalMrp("Free");
                                                    } else {
                                                        if (courseObject.optString("mrp").equals(courseObject.optString(activity.getResources().getString(R.string.non_dams)))) {
                                                            course.setCalMrp(String.format("%s %s", getCurrencySymbol(),
                                                                    Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp"))));
                                                        } else {
                                                            course.setCalMrp(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp")) + " " +
                                                                    Helper.calculatePriceBasedOnCurrency(courseObject.optString(activity.getResources().getString(R.string.non_dams))));
                                                        }
                                                    }
                                                }
                                                if (course.getCalMrp().equalsIgnoreCase("free") || course.isIs_purchased())
                                                    course.setIsFreeTrial(false);
                                                courseArrayList.add(course);
                                            }
                                        } else if (categoryInfo.optString(activity.getResources().getString(R.string.app_view_type)).equals("2")) {
                                            coursesData.setViewItemType(0);
                                            coursesData.setCategory_info(gson.fromJson(categoryInfo.toString(), CourseCategory.class));

                                            JSONArray courseLists = dataArray.optJSONObject(i).optJSONArray(activity.getResources().getString(R.string.course_list));
                                            for (int j = 0; j < Objects.requireNonNull(courseLists).length(); j++) {
                                                JSONObject courseObject = courseLists.getJSONObject(j);
                                                Course course = gson.fromJson(courseObject.toString(), Course.class);
                                                course.setPoints_conversion_rate(pointsConversionRate);
                                                course.setGst(gst);
                                                if (courseObject.optString("mrp").equals("0")) {
                                                    course.setCalMrp("Free");
                                                } else if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                                                    course.setIs_dams(true);
                                                    if (courseObject.optString(activity.getResources().getString(R.string.for_dams)).equals("0")) {
                                                        course.setCalMrp("Free");
                                                    } else {
                                                        if (courseObject.optString("mrp").equals(courseObject.optString(activity.getResources().getString(R.string.for_dams)))) {
                                                            course.setCalMrp(String.format("%s %s", getCurrencySymbol(),
                                                                    Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp"))));
                                                        } else {
                                                            course.setDiscounted(true);
                                                            course.setCalMrp(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp")) + " " +
                                                                    Helper.calculatePriceBasedOnCurrency(courseObject.optString(activity.getResources().getString(R.string.for_dams))));
                                                        }
                                                    }
                                                } else {
                                                    course.setIs_dams(false);
                                                    if (courseObject.optString(activity.getResources().getString(R.string.non_dams)).equals("0")) {
                                                        course.setCalMrp("Free");
                                                    } else {
                                                        if (courseObject.optString("mrp").equals(courseObject.optString(activity.getResources().getString(R.string.non_dams)))) {
                                                            course.setCalMrp(String.format("%s %s", getCurrencySymbol(),
                                                                    Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp"))));
                                                        } else {
                                                            course.setDiscounted(true);
                                                            course.setCalMrp(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp")) + " " +
                                                                    Helper.calculatePriceBasedOnCurrency(courseObject.optString(activity.getResources().getString(R.string.non_dams))));
                                                        }
                                                    }
                                                }
                                                if (course.getCalMrp().equalsIgnoreCase("free") || course.isIs_purchased())
                                                    course.setIsFreeTrial(false);
                                                courseArrayList.add(course);
                                            }
                                        }

                                        coursesData.setCourse_list(courseArrayList);
                                        coursesDataArrayList.add(coursesData);
                                        Helper.getStorageInstance(activity).addRecordStore(Const.OFFLINE_COURSE, coursesDataArrayList);
                                    }
                                }
                                Helper.getStorageInstance(activity).addRecordStore("categories", categoryArrayList);
                            }
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
//                            initLandingPageAdapter();

                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message))) {
                                if (fragType.equals(Const.ALLCOURSES))
                                    Helper.showErrorLayoutForNav(API.API_GET_LANDING_PAGE_DATA, activity, 1, 1);
                                else
                                    Helper.showErrorLayoutForNoNav(API.API_GET_LANDING_PAGE_DATA, activity, 1, 1);
                            }

                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equals(activity.getResources().getString(R.string.something_went_wrong))) {
                                if (fragType.equals(Const.ALLCOURSES))
                                    Helper.showErrorLayoutForNav(API.API_GET_LANDING_PAGE_DATA, activity, 1, 1);
                                else
                                    Helper.showErrorLayoutForNoNav(API.API_GET_LANDING_PAGE_DATA, activity, 1, 1);
                            }

                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }

                        //if (((BaseABNavActivity) activity).courseStatus)
                        initLandingPageAdapter();

                        ((BaseABNavActivity) activity).courseStatus = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        mProgress.dismiss();
                        Log.e(TAG, " networkCallForLandingDataV3 onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                initLandingPageAdapter();
                if (fragType.equals(Const.ALLCOURSES))
                    Helper.showErrorLayoutForNav(API.API_GET_LANDING_PAGE_DATA, activity, 1, 1);
                else
                    Helper.showErrorLayoutForNoNav(API.API_GET_LANDING_PAGE_DATA, activity, 1, 1);

            }
        });
    }

    public void networkCallForSearchCourse() { //API_SEARCH_COURSE
        mProgress.show();
        LandingPageApiInterface apiInterface = ApiClient.createService(LandingPageApiInterface.class);
        Call<JsonObject> response = apiInterface.getSearchCourseData(SharedPreference.getInstance().getLoggedInUser().getId(),
                SharedPreference.getInstance().getString(Constants.SharedPref.COURSE_SEARCHED_QUERY), lastCourseId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e(TAG, "onResponse: " + jsonResponse);

                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONArray data = GenericUtils.getJsonArray(jsonResponse);
                            if (TextUtils.isEmpty(lastCourseId)) {
                                searchArrayList = new ArrayList<>();
                            }
                            for (int i = 0; i < data.length(); i++) {
                                Course coursesData = gson.fromJson(data.opt(i).toString(), Course.class);
                                searchArrayList.add(coursesData);
                            }
                        } else {
                            errorMessage = jsonResponse.optString(Constants.Extras.MESSAGE);
                            searchArrayList.clear();

                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message))) {
                                if (fragType.equals(Const.ALLCOURSES))
                                    Helper.showErrorLayoutForNav(API.API_SEARCH_COURSE, activity, 1, 1);
                                else
                                    Helper.showErrorLayoutForNoNav(API.API_SEARCH_COURSE, activity, 1, 1);
                            }

                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equals(activity.getResources().getString(R.string.something_went_wrong))) {
                                if (fragType.equals(Const.ALLCOURSES))
                                    Helper.showErrorLayoutForNav(API.API_SEARCH_COURSE, activity, 1, 1);
                                else
                                    Helper.showErrorLayoutForNoNav(API.API_SEARCH_COURSE, activity, 1, 1);
                            }

                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                        initSearchCourseAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Log.e(TAG, " networkCallForSearchCourse onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                if (fragType.equals(Const.ALLCOURSES))
                    Helper.showErrorLayoutForNav(API.API_SEARCH_COURSE, activity, 1, 1);
                else
                    Helper.showErrorLayoutForNoNav(API.API_SEARCH_COURSE, activity, 1, 1);

            }
        });

    }

    public void networkCallForAllData(int mPosition, String categoryId) {
        LandingPageApiInterface apiInterface = ApiClient.createService(LandingPageApiInterface.class);
        Call<JsonObject> response = apiInterface.getAllCatData(SharedPreference.getInstance().getLoggedInUser().getId(), categoryId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                JSONArray dataArray;
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e("CommonfragForList", " NetworkCallForAllCatData onResponse: " + jsonResponse);
                        courseArrayList = new ArrayList<>();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            dataArray = data.optJSONArray(Const.COURSE_LIST);
                            for (int i = 0; i < Objects.requireNonNull(dataArray).length(); i++) {
                                Course coursesData = gson.fromJson(dataArray.optJSONObject(i).toString(), Course.class);
                                courseArrayList.add(coursesData);
                            }

                            for (int i = 0; i < coursesDataArrayList.get(mPosition).getCourse_list().size(); i++) {
                                for (int j = 0; j < courseArrayList.size(); j++) {
                                    Log.e(TAG, "NetworkCallForAllCatData12345 : " + coursesDataArrayList.get(mPosition).getCourse_list().get(i).getId() +
                                            " ," + courseArrayList.get(j).getId());
                                    if (coursesDataArrayList.get(mPosition).getCourse_list().get(i).getId().equals(courseArrayList.get(j).getId())) {
                                        courseArrayList.remove(j);
                                    }
                                }
                            }

                            allCoursesAdapater.seeAllCourse(courseArrayList);

                            if (!seeAllStatus.contains(categoryId)) {
                                seeAllStatus.add(categoryId);
                                Helper.getStorageInstance(activity).addRecordStore(Const.COURSESEEALL_IDS, seeAllStatus);
                            }

                            Helper.getStorageInstance(activity).addRecordStore(categoryId, courseArrayList);

                        } else {
                            allCoursesAdapater.seeAllCourse(courseArrayList);
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message))) {
                                if (fragType.equals(Const.ALLCOURSES))
                                    Helper.showErrorLayoutForNav(API.API_GET_ALL_CATEGORY_DATA, activity, 1, 1);
                                else
                                    Helper.showErrorLayoutForNoNav(API.API_GET_ALL_CATEGORY_DATA, activity, 1, 1);
                            }

                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equals(activity.getResources().getString(R.string.something_went_wrong))) {
                                if (fragType.equals(Const.ALLCOURSES))
                                    Helper.showErrorLayoutForNav(API.API_GET_ALL_CATEGORY_DATA, activity, 1, 1);
                                else
                                    Helper.showErrorLayoutForNoNav(API.API_GET_ALL_CATEGORY_DATA, activity, 1, 1);
                            }
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mProgress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                initMyCourseAdapter();
                if (fragType.equals(Const.ALLCOURSES))
                    Helper.showErrorLayoutForNav(API.API_GET_ALL_CATEGORY_DATA, activity, 1, 1);
                else
                    Helper.showErrorLayoutForNoNav(API.API_GET_ALL_CATEGORY_DATA, activity, 1, 1);

            }
        });
    }

    private void initCategoryAdapter() {
        catLL.setVisibility(View.VISIBLE);
        tileRV.setVisibility(View.VISIBLE);
        CatAdapter catAdapter = new CatAdapter(activity, categoryArrayList);
        catRV.setAdapter(catAdapter);
    }

    public void retryApi(String apiname) {
        switch (apiname) {
            case API.API_GET_LANDING_PAGE_DATA:
                networkCallForLandingDataV3();
                break;
            case API.API_UPDATE_DAMS_TOKEN:
                networkCallForUpdateToken();
                break;
            case API.API_SEARCH_COURSE:
                networkCallForSearchCourse();
                break;
            default:
        }
    }

    private void initLandingPageAdapter() {
        initCategoryAdapter();
        manageLiveAndComboCourseVisibility();

        if (coursesDataArrayList != null && !coursesDataArrayList.isEmpty()) {
            for (CoursesData coursesData : coursesDataArrayList) {
                Log.e(TAG, "initLandingPageAdapter: " + coursesData.getViewItemType());
            }
            allCoursesAdapater = new AllCoursesAdapater(activity, coursesDataArrayList, CommonFragForList.this);
            commonListRV.setNestedScrollingEnabled(false);
            commonListRV.setAdapter(allCoursesAdapater);
            ((BaseABNavActivity) activity).setSidePanelCourse(coursesDataArrayList);

            if (TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken()))
                damsVerifyTV.setVisibility(View.VISIBLE);
            damsVerifyTV.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.blink));

            damsVerifyTV.setOnClickListener(view -> getDAMSLoginDialog(activity));

            errorTV.setVisibility(View.GONE);
            commonListRV.setVisibility(View.VISIBLE);

        } else {
            ((BaseABNavActivity) activity).setErrorMessage(errorMessage);
            errorTV.setText(errorMessage);
            commonListRV.setVisibility(View.GONE);
            if (errorMessage == null || errorMessage.contains(activity.getResources().getString(R.string.something_went_wrong))) {
                if (fragType.equals(Const.ALLCOURSES))
                    Helper.showErrorLayoutForNav(null, activity, 1, 0);
                else
                    Helper.showErrorLayoutForNoNav(null, activity, 1, 2);
            }
        }
    }

    public void checkDAMSLoginValidation() {
        damsToken = Helper.GetText(mDAMSET);
        damsPass = Helper.GetText(mDAMSPasswordET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(damsToken))
            isDataValid = Helper.DataNotValid(mDAMSET);

        else if (TextUtils.isEmpty(damsPass))
            isDataValid = Helper.DataNotValid(mDAMSPasswordET);

        if (isDataValid) {
            user = User.newInstance();
            user.setDams_username(damsToken);
            user.setDams_password(damsPass);
            networkCallForUpdateToken();//
            // NetworkAPICall(API.API_UPDATE_DAMS_TOKEN, true);
        }
    }

    public void getDAMSLoginDialog(final Activity ctx) {

        // custom dialog
        dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_dams_login);
        dialog.setCancelable(false);
        // set the custom dialog components - text, image and button
        Button mDAMSLoginbtn = dialog.findViewById(R.id.loginBtn);
        Button mDAMScancelbtn = dialog.findViewById(R.id.cancelBtn);
        mDAMSET = dialog.findViewById(R.id.damstokenET);
        mDAMSPasswordET = dialog.findViewById(R.id.damspassET);
        // if button is clicked, close the custom dialog
        mDAMSLoginbtn.setOnClickListener(v -> checkDAMSLoginValidation());
        mDAMScancelbtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void initMyCourseAdapter() {
        if (!courseArrayList.isEmpty()) {
            courseListAdapter = new CourseListAdapter(activity, courseArrayList, fragType);
            commonListRV.setAdapter(courseListAdapter);
            errorTV.setVisibility(View.GONE);
            commonListRV.setVisibility(View.VISIBLE);
        } else {
            errorTV.setText(errorMessage);
            commonListRV.setVisibility(View.GONE);
            if (errorMessage != null && errorMessage.contains(activity.getResources().getString(R.string.something_went_wrong))) {
                if (fragType.equals(Const.ALLCOURSES))
                    Helper.showErrorLayoutForNav(null, activity, 1, 0);
                else
                    Helper.showErrorLayoutForNoNav(null, activity, 1, 2);
            }
        }
    }

    private void initSearchCourseAdapter() {
        if (TextUtils.isEmpty(lastCourseId)) {
            if (!searchArrayList.isEmpty()) {
                nestedScrollView.scrollTo(0, (int) commonListRV.getY() * 45 / 100);
                courseListAdapter = new CourseListAdapter(activity, searchArrayList, Const.SEEALL_COURSE);
                commonListRV.setAdapter(courseListAdapter);
                errorTV.setVisibility(View.GONE);
                commonListRV.setVisibility(View.VISIBLE);

                ((BaseABNavActivity) activity).setSearchCourseData(searchArrayList);
            } else {
                ((BaseABNavActivity) activity).setErrorMessage(errorMessage);
                errorTV.setText(errorMessage);
                errorTV.setVisibility(View.VISIBLE);
                commonListRV.setVisibility(View.GONE);

                nestedScrollView.scrollTo(0, (int) errorTV.getY());

                if (errorMessage == null || errorMessage.contains(activity.getResources().getString(R.string.something_went_wrong))) {
                    if (fragType.equals(Const.ALLCOURSES))
                        Helper.showErrorLayoutForNav(null, activity, 1, 0);
                    else
                        Helper.showErrorLayoutForNoNav(null, activity, 1, 2);
                }
            }
        } else {
            courseListAdapter.notifyDataSetChanged();
            ((BaseABNavActivity) activity).courseSearchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        switch (apiType) {
            case API.API_UPDATE_DAMS_TOKEN:
                params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
                params.put(Const.DAMS_USERNAME, user.getDams_username());
                params.put(Const.DAMS_PASSWORD, user.getDams_password());
                return service.postData(apiType, params);
            case API.API_GET_COURSE_CATEGORY:
                params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
                return service.postData(apiType, params);
            case API.API_GET_COURSE_BANNER:
                Map<String, Object> params1 = new HashMap<>();
                params1.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
                return service.postData(apiType, params1);
        }
        return null;
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        Gson gson = new Gson();
        switch (apiType) {
            case API.API_GET_COURSE_CATEGORY:
                JSONArray dataArray = GenericUtils.getJsonArray(jsonObject);
                if (dataArray.length() > 0) {
                    if (!courseCatTileDataArrayList.isEmpty())
                        courseCatTileDataArrayList.clear();

                    for (int i = 0; i < dataArray.length(); i++) {
                        CourseCatTileData tileData = null;
                        tileData = gson.fromJson(dataArray.get(i).toString(), CourseCatTileData.class);
                        courseCatTileDataArrayList.add(tileData);
                    }
                    tileAdapter.notifyDataSetChanged();
                } else {
                    errorCallBack("", apiType);
                }
                break;
            case API.API_GET_COURSE_BANNER:
                JSONObject datas = GenericUtils.getJsonObject(jsonObject);
                courseBannerData = gson.fromJson(datas.toString(), CourseBannerData.class);
                if (!GenericUtils.isEmpty(courseBannerData.getImageLink())) {
                    bannerIV.setVisibility(View.VISIBLE);
                    if (!activity.isFinishing()) {

                        bannerIV.setVisibility(View.VISIBLE);
                        if (isAdded())
                            Glide.with(activity)
                                    .load(courseBannerData.getImageLink())
                                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                                    .into(bannerIV);
                        bannerIV.setOnClickListener(v -> {
                            final Course courseD = new Course();
                            courseD.setIs_combo(courseBannerData.getIsCombo());
                            courseD.setCourse_type(courseBannerData.getCourseType());
                            courseD.setId(courseBannerData.getCourseId());
                            courseD.setIs_live(courseBannerData.getIs_live());
                            Intent courseList = new Intent(activity, CourseActivity.class);//FRAG_TYPE, Const.SINGLE_COURSE AllCoursesAdapter
                            courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_STUDY);
                            courseList.putExtra(Const.IMAGE, courseBannerData.getImageLink());
                            courseList.putExtra(Const.COURSES, courseD);
                            activity.startActivity(courseList);
                                        /*if (courseBannerData.getCourseType().equalsIgnoreCase("1")) {
                                            if (!GenericUtils.isEmpty(courseBannerData.getIs_live())) {
                                                if (courseBannerData.getIs_live().equalsIgnoreCase("1")) {
                                                    SharedPreference.getInstance().putString(Constants.Extras.ID, courseBannerData.getCourseId());
                                                    courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_STUDY);
                                                    courseList.putExtra(Const.IMAGE, courseBannerData.getImageLink());
                                                } else {
                                                    if (Helper.isStringValid(courseBannerData.getIsCombo()) && courseBannerData.getIsCombo().equalsIgnoreCase("1")) {
                                                        courseList.putExtra(Const.FRAG_TYPE, Const.COMBO_COURSE);
                                                    } else {
                                                        courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
                                                    }
                                                    courseList.putExtra(Const.COURSES, courseD);
                                                }
                                            } else {
                                                if (Helper.isStringValid(courseBannerData.getIsCombo()) && courseBannerData.getIsCombo().equalsIgnoreCase("1")) {
                                                    courseList.putExtra(Const.FRAG_TYPE, Const.COMBO_COURSE);
                                                } else {
                                                    courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
                                                }
                                                courseList.putExtra(Const.COURSES, courseD);
                                            }
                                        } else {
                                            if (courseBannerData.getCourseId().equalsIgnoreCase(Constants.Extras.QUESTION_BANK_COURSE_ID)) {
                                                courseList.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE);
                                            } else {
                                                courseList.putExtra(Const.FRAG_TYPE, Const.TEST_COURSE);
                                            }
                                            courseList.putExtra(Constants.Extras.START_DATE, courseBannerData.getFromDate());
                                            courseList.putExtra(Constants.Extras.END_DATE, courseBannerData.getToDate());
                                            courseList.putExtra(Const.COURSES, courseD);
                                             activity.startActivity(courseList);
                                        }*/
                        });
                    }
                }
                break;
            case API.API_UPDATE_DAMS_TOKEN:
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
                JSONObject jsonObject1 = GenericUtils.getJsonObject(jsonObject);
                user1 = gson.fromJson(jsonObject1.toString(), User.class);
                user = User.newInstance();
                user = User.copyInstance(user1);
                registration = (user.getUser_registration_info() == null ? new Registration() : user.getUser_registration_info());
                user.setUser_registration_info(registration);
                SharedPreference.getInstance().setLoggedInUser(user);
                damsVerifyTV.setVisibility(View.GONE);
                networkCallForLandingDataV3();
                break;
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        switch (apiType) {
            case API.API_UPDATE_DAMS_TOKEN:
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
                GenericUtils.showToast(activity, jsonString);
                break;
            case API.API_GET_COURSE_CATEGORY:
                if (fragType.equals(Const.ALLCOURSES))
                    Helper.showErrorLayoutForNav(API.API_GET_LANDING_PAGE_DATA, activity, 1, 1);
                else
                    Helper.showErrorLayoutForNoNav(API.API_GET_LANDING_PAGE_DATA, activity, 1, 1);
                break;
            case API.API_GET_COURSE_BANNER:
                bannerIV.setVisibility(View.GONE);
                break;
            default:
                GenericUtils.showToast(activity, jsonString);
                break;
        }
    }
}
