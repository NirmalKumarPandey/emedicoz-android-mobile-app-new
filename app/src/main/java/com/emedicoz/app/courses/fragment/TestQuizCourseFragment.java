package com.emedicoz.app.courses.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.activity.NewCurriculumActivity;
import com.emedicoz.app.courses.activity.TestQuizActivity;
import com.emedicoz.app.courses.adapter.CurriculumTopicAdapter;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.CourseLockedManager;
import com.emedicoz.app.modelo.courses.Curriculam;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SingleCourseApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;

public class TestQuizCourseFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "TestQuizCourseFragment";
    boolean refreshStatus = false;
    Activity activity;
    String fragType = "";
    String des = "";
    Course course;
    int courseDescriptionLength = 300;
    SwipeRefreshLayout swipeSingleCourse;
    NestedScrollView scroll;
    RelativeLayout bottomPriceLL;
    Button seeAll;
    Button topBuyBtn;
    String startDate = "";
    String endDate = "";
    SingleCourseData singleCourseData;
    LinearLayout curriculumListLL;
    LinearLayout courseLL;
    LinearLayout relatedCourseLL;
    LinearLayout mainParentLL;
    TextView bannerNameTV;
    TextView courseNameTV;
    TextView learnerTV;
    TextView courseDescriptionTV;
    TextView courseCategoryTitle;
    TextView errorTV;
    TextView courseRatingTV;
    TextView coursePriceTV;
    TextView curriculumTextTV;
    ImageView bannerImageIV;
    Button readMoreCourseBtn;
    Button buyBtn;
    Button seeAllCurriculum;
    Button faqBtn;
    NonScrollRecyclerView curriculumExpListRV;
    ArrayList<Curriculam> curriculamArrayList;
    ClickableSpan readMoreClick;
    ClickableSpan readLessClick;
    Progress mProgress;
    RatingBar courseRatingBR;


    View.OnClickListener onCourseClickListener = v -> {
        Course course = (Course) v.getTag();
        Intent courseList = new Intent(activity, CourseActivity.class);// FRAG_TYPE, Const.SINGLE_COURSE
        courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
        courseList.putExtra(Const.COURSES, course);
        activity.startActivity(courseList);
    };


    public TestQuizCourseFragment() {
        // Required empty public constructor
    }

    public static TestQuizCourseFragment newInstance(Course course) {
        TestQuizCourseFragment fragment = new TestQuizCourseFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.COURSES, course);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static TestQuizCourseFragment newInstance(Course course, String startDate, String endDate) {
        TestQuizCourseFragment fragment = new TestQuizCourseFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.COURSES, course);
        bundle.putString(Constants.Extras.START_DATE, startDate);
        bundle.putString(Constants.Extras.END_DATE, endDate);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: onCreate() TestQuizCourse");
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        curriculamArrayList = new ArrayList<>();
        if (getArguments() != null) {
            fragType = getArguments().getString(Const.FRAG_TYPE);
            course = (Course) getArguments().getSerializable(Const.COURSES);
            startDate = getArguments().getString(Constants.Extras.START_DATE);
            endDate = getArguments().getString(Constants.Extras.END_DATE);
        }
        activity = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SharedPreference.getInstance().getBoolean(Const.IS_PAYMENT_DONE)) {
            networkCallForCourseInfoRaw();
            SharedPreference.getInstance().putBoolean(Const.IS_PAYMENT_DONE, false);
        } else if (SharedPreference.getInstance().getString(Const.QUIZSTATUS).equals(Const.FINISH)) {
            networkCallForCourseInfoRaw();
            SharedPreference.getInstance().putString(Const.QUIZSTATUS, "");
        } else if (SharedPreference.getInstance().getBoolean(Const.REVIEWS)) {
            networkCallForCourseInfoRaw();
            SharedPreference.getInstance().putBoolean(Const.REVIEWS, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        networkCallForCourseInfoRaw();
    }

    private void initViews(View view) {
        scroll = view.findViewById(R.id.scroll);
        swipeSingleCourse = view.findViewById(R.id.swipeSingleCourse);
        bannerNameTV = view.findViewById(R.id.bannernameTV);
        learnerTV = view.findViewById(R.id.learnerTV);
        courseNameTV = view.findViewById(R.id.nameTV);
        courseDescriptionTV = view.findViewById(R.id.descriptionTV);
        coursePriceTV = view.findViewById(R.id.priceTV);
        courseRatingTV = view.findViewById(R.id.ratingTV);
        courseRatingTV.setVisibility(View.GONE);
        bannerImageIV = view.findViewById(R.id.bannerimageIV);
        readMoreCourseBtn = view.findViewById(R.id.readMoreBtn);
        buyBtn = view.findViewById(R.id.buyBtn);
        topBuyBtn = view.findViewById(R.id.topBuyBtn);
        faqBtn = view.findViewById(R.id.faqbtn);
        faqBtn.setVisibility(View.GONE);
        courseRatingBR = view.findViewById(R.id.ratingRB);
        courseRatingBR.setVisibility(View.GONE);
        bottomPriceLL = view.findViewById(R.id.bottomPriceLL);
        curriculumListLL = view.findViewById(R.id.curriculumListLL);
        seeAllCurriculum = view.findViewById(R.id.seeAllCurriculumBtn);
        seeAllCurriculum.setVisibility(View.GONE);
        curriculumTextTV = view.findViewById(R.id.curriculumTextTV);
        courseCategoryTitle = view.findViewById(R.id.tv1);
        seeAll = view.findViewById(R.id.courseCatseeAllBtn);
        courseLL = view.findViewById(R.id.coursesoptionLL);
        relatedCourseLL = view.findViewById(R.id.relatedCourseLL);
        mainParentLL = view.findViewById(R.id.mainParentLL);
        errorTV = view.findViewById(R.id.errorTV);
        curriculumExpListRV = view.findViewById(R.id.curriculumExpListLL);
        curriculumExpListRV.setVisibility(View.VISIBLE);

        curriculumExpListRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        seeAll.setVisibility(View.GONE);

        readMoreCourseBtn.setOnClickListener(this);
        faqBtn.setOnClickListener(this);
        seeAllCurriculum.setOnClickListener(this);
        swipeSingleCourse.setOnRefreshListener(this);
        swipeSingleCourse.setColorSchemeResources(R.color.blue, R.color.colorAccent, R.color.colorPrimaryDark);
        ViewCompat.setNestedScrollingEnabled(scroll, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_quiz_course, container, false);
    }

    private void networkCallForCourseInfoRaw() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Log.e(TAG, SharedPreference.getInstance().getLoggedInUser().getId() + " " + course.getId() + " " + course.getCourse_type() + " " + course.getIs_combo());
        Call<JsonObject> response = apiInterface.getSingleCourseInfoRaw(SharedPreference.getInstance().getLoggedInUser().getId(),
                course.getId(), course.getCourse_type(), course.getIs_combo(), "0");//369 in place of course.getId()
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            if (errorTV.getVisibility() == View.VISIBLE)
                                errorTV.setVisibility(View.GONE);
                            if (swipeSingleCourse.isRefreshing())
                                swipeSingleCourse.setRefreshing(false);
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            singleCourseData = gson.fromJson(data.toString(), SingleCourseData.class);
                            setAllDatatoView();
                            refreshStatus = false;
                            if (activity != null)
                                ((CourseActivity) activity).setToolbarTitle(!TextUtils.isEmpty(course.getTitle()) ? course.getTitle() : singleCourseData.getTitle());
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_SINGLE_COURSE_INFO_RAW);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_SINGLE_COURSE_INFO_RAW, activity, 1, 1);
            }
        });
    }

    private void networkCallForAddReviewCourse() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        /*userRatingBr.getRating()*/
        Call<JsonObject> response = apiInterface.addReviewCourse(SharedPreference.getInstance().getLoggedInUser().getId(),
                singleCourseData.getId(), "", /*addReviewTextET.getText().toString()*/"");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            networkCallForCourseInfoRaw();
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_ADD_REVIEW_COURSE);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_ADD_REVIEW_COURSE, activity, 1, 1);
            }
        });
    }

    private void networkCallForDeleteReviews() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.deleteUserCourseReview(SharedPreference.getInstance().getLoggedInUser().getId(),
                singleCourseData.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            networkCallForCourseInfoRaw();
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_DELETE_USER_COURSE_REVIEWS);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_DELETE_USER_COURSE_REVIEWS, activity, 1, 1);
            }
        });
    }

    private void networkCallForEditCourseReview() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        /*userRatingBr.getRating()*/
        Call<JsonObject> response = apiInterface.editUserCourseReviews(SharedPreference.getInstance().getLoggedInUser().getId()
                , singleCourseData.getId(), "", /*addReviewTextET.getText().toString()*/"");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        mProgress.dismiss();

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            networkCallForCourseInfoRaw();
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_EDIT_USER_COURSE_REVIEWS);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_EDIT_USER_COURSE_REVIEWS, activity, 1, 1);
            }
        });
    }

    private void networkCallFreeCourseTrans() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.makeFreeCourseTransaction(SharedPreference.getInstance().getLoggedInUser().getId(),
                singleCourseData.getId(), "0", "0", "0", "0", "0");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            SharedPreference.getInstance().putBoolean(Const.IS_PAYMENT_DONE, true);
                            Intent mycourse = new Intent(activity, CourseActivity.class); // FRAG_TYPE, Const.MYCOURSES SingleCourse
                            mycourse.putExtra(Const.FRAG_TYPE, Const.MYCOURSES);
                            startActivity(mycourse);
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_MAKE_FREE_COURSE_TRANSACTION);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_MAKE_FREE_COURSE_TRANSACTION, activity, 1, 1);
            }
        });
    }

    public void retryApis(String apiType) {
        switch (apiType) {
            case API.API_MAKE_FREE_COURSE_TRANSACTION:
                networkCallFreeCourseTrans();
                break;
            case API.API_SINGLE_COURSE_INFO_RAW:
                networkCallForCourseInfoRaw();
                break;
            case API.API_ADD_REVIEW_COURSE:
                networkCallForAddReviewCourse();
                break;
            case API.API_DELETE_USER_COURSE_REVIEWS:
                networkCallForDeleteReviews();
                break;
            case API.API_EDIT_USER_COURSE_REVIEWS:
                networkCallForEditCourseReview();
                break;
            default:
                break;
        }
    }

    private void errorCallBack(String jsonString, String apiType) {
        if (API.API_SINGLE_COURSE_INFO_RAW.equals(apiType)) {
            if (swipeSingleCourse.isRefreshing()) {
                swipeSingleCourse.setRefreshing(false);
                refreshStatus = false;
            }


            if (Helper.getStorageInstance(getActivity()).getRecordObject(String.valueOf(course.getId())) != null) {
                singleCourseData = (SingleCourseData) Helper.getStorageInstance(getActivity()).getRecordObject(String.valueOf(course.getId()));
                setAllDatatoView();

            } else {
                mainParentLL.setVisibility(View.GONE);
                errorTV.setVisibility(View.VISIBLE);
                errorTV.setText(jsonString);

                if (jsonString.equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
                    Helper.showErrorLayoutForNoNav(apiType, activity, 1, 1);

                if (jsonString.contains(activity.getResources().getString(R.string.something_went_wrong)))
                    Helper.showErrorLayoutForNoNav(apiType, activity, 1, 2);
            }
        }
    }

    private void setAllDatatoView() {
        if (mainParentLL.getVisibility() == View.GONE)
            mainParentLL.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(singleCourseData.getCover_video())) {
            bannerImageIV.setOnClickListener(this);
        }

        if (singleCourseData.getCourse_type().equalsIgnoreCase("2")) {
            curriculumTextTV.setText("Test");
        } else if (singleCourseData.getCourse_type().equalsIgnoreCase("3")) {
            curriculumTextTV.setText("Question Bank");
        }
        bannerNameTV.setText(singleCourseData.getTitle().length() > 0 ? Helper.CapitalizeText(singleCourseData.getTitle().trim()) : "");
        courseNameTV.setText(singleCourseData.getTitle().length() > 0 ? Helper.CapitalizeText(singleCourseData.getTitle().trim()) : "");
        learnerTV.setText((singleCourseData.getLearner() + " "
                + ((singleCourseData.getLearner().equals("1") || (singleCourseData.getLearner().equals("0")) ? Const.LEARNER : Const.LEARNERS))));
        courseRatingTV.setText((singleCourseData.getReview_count() + " "
                + ((singleCourseData.getReview_count().equals("1") || (singleCourseData.getReview_count().equals("0")) ? Const.REVIEW : Const.REVIEWS))));

        if (singleCourseData.getMrp().equals("0")) {
            coursePriceTV.setText(activity.getResources().getString(R.string.free));
            topBuyBtn.setVisibility(View.GONE);
            buyBtn.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
            if (singleCourseData.getFor_dams().equals(singleCourseData.getMrp())) {
                coursePriceTV.setText(String.format("%s %s", getCurrencySymbol(),
                        Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp())));
            } else {
                StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                coursePriceTV.setText(String.format("%s %s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp()), Helper.calculatePriceBasedOnCurrency(singleCourseData.getFor_dams())), TextView.BufferType.SPANNABLE);
                Spannable spannable = (Spannable) coursePriceTV.getText();
                spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (singleCourseData.getFor_dams().equalsIgnoreCase("0")) {
                coursePriceTV.setText(activity.getResources().getString(R.string.free));
                topBuyBtn.setVisibility(View.GONE);
                buyBtn.setVisibility(View.GONE);
            } else {
                topBuyBtn.setVisibility(View.VISIBLE);
                buyBtn.setVisibility(View.VISIBLE);
                topBuyBtn.setText(getString(R.string.enroll));
                topBuyBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                buyBtn.setText(getString(R.string.enroll));
                buyBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));

            }
        } else {
            if (singleCourseData.getNon_dams().equals(singleCourseData.getMrp())) {
                coursePriceTV.setText(String.format("%s %s", getCurrencySymbol(),
                        Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp())));
            } else {
                StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                coursePriceTV.setText(String.format("%s %s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp()), Helper.calculatePriceBasedOnCurrency(singleCourseData.getNon_dams())), TextView.BufferType.SPANNABLE);
                Spannable spannable = (Spannable) coursePriceTV.getText();
                spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (singleCourseData.getNon_dams().equalsIgnoreCase("0")) {
                coursePriceTV.setText(activity.getResources().getString(R.string.free));
                topBuyBtn.setVisibility(View.GONE);
                buyBtn.setVisibility(View.GONE);
            } else {
                topBuyBtn.setVisibility(View.VISIBLE);
                buyBtn.setVisibility(View.VISIBLE);
                topBuyBtn.setText(getString(R.string.enroll));
                topBuyBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                buyBtn.setText(getString(R.string.enroll));
                buyBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
            }
        }

        setDescriptionData();
        courseRatingBR.setRating(Float.parseFloat(singleCourseData.getRating()));

        if (this != null && this.isVisible() && singleCourseData.getDesc_header_image() != null) {
            Glide.with(getActivity().getApplicationContext()).load(singleCourseData.getDesc_header_image())
                    .error(Glide.with(bannerImageIV).load(R.mipmap.helpbanner)).into(bannerImageIV);
        } else {
            bannerImageIV.setImageResource(R.mipmap.helpbanner);
        }

        setEnrollData();
        /**
         * Method written to render related courses data
         */
        setRelatedCourses();

        if (curriculumListLL.getChildCount() > 0)
            curriculumListLL.removeAllViews();
        if (singleCourseData.getIs_purchased().equals("1") && singleCourseData.getIs_locked().equals("1"))
            CourseLockedManager.addCourseLockStatus(course.getId(), String.valueOf(0), activity);

        if (!curriculamArrayList.isEmpty()) curriculamArrayList.clear();

        CurriculumTopicAdapter adapter = new CurriculumTopicAdapter(activity,
                singleCourseData.getCurriculam().getTopics(), singleCourseData.getId(), singleCourseData.getTitle(), singleCourseData.getIs_purchased(), singleCourseData.getCourse_type(), singleCourseData, startDate, endDate);
        curriculumExpListRV.setAdapter(adapter);

    }

    private void setDescriptionData() {
        des = singleCourseData.getDescription();
        if (des.length() > courseDescriptionLength) {
            des = des.substring(0, courseDescriptionLength) + "...";
            courseDescriptionTV.setText(String.format("%s %s", HtmlCompat.fromHtml(des, HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Read More</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
            readMoreClick = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    courseDescriptionTV.setText(String.format("%s %s", HtmlCompat.fromHtml(singleCourseData.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Read Less</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                    Helper.makeLinks(courseDescriptionTV, "Read Less", readLessClick);
                }
            };

            readLessClick = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    courseDescriptionTV.setText(String.format("%s %s", HtmlCompat.fromHtml(des, HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Read More</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                    Helper.makeLinks(courseDescriptionTV, "Read More", readMoreClick);
                }
            };
            Helper.makeLinks(courseDescriptionTV, "Read More", readMoreClick);
        } else {
            courseDescriptionTV.setText(HtmlCompat.fromHtml(des, HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
    }

    private void setEnrollData() {
        if (singleCourseData.getIs_purchased().equals("1")) {
            bottomPriceLL.setVisibility(View.VISIBLE);
            buyBtn.setText(getString(R.string.enrolled));
            buyBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.green_new));
            buyBtn.setOnClickListener(null);
            topBuyBtn.setVisibility(View.GONE);
            coursePriceTV.setText(activity.getResources().getString(R.string.already_enrolled));
        } else {
            if (singleCourseData.getMrp().equals("0"))
                buyBtn.setVisibility(View.GONE);
            else if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                if (singleCourseData.getFor_dams().equals("0")) {
                    buyBtn.setVisibility(View.GONE);
                } else {
                    buyBtn.setVisibility(View.VISIBLE);
                    buyBtn.setOnClickListener(this);
                    topBuyBtn.setOnClickListener(this);
                }
            } else {
                if (singleCourseData.getNon_dams().equals("0")) {
                    buyBtn.setVisibility(View.GONE);
                } else {
                    buyBtn.setVisibility(View.VISIBLE);
                    buyBtn.setOnClickListener(this);
                    topBuyBtn.setOnClickListener(this);
                }
            }
            topBuyBtn.setVisibility(View.GONE);
            bottomPriceLL.setVisibility(View.VISIBLE);
        }
    }

    private void setRelatedCourses() {
        if (singleCourseData.getRelated_course() != null) {
            relatedCourseLL.setVisibility(View.GONE);
            int i = 0;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(5, 0, 5, 0);
            relatedCourseLL.setLayoutParams(lp);
            courseCategoryTitle.setText(getString(R.string.related_course));
            courseCategoryTitle.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            if (courseLL.getChildCount() > 0)
                courseLL.removeAllViews();
            for (Course courses : singleCourseData.getRelated_course()) {
                if (i == 3) break;
                else {
                    courseLL.setOrientation(LinearLayout.VERTICAL);
                    courseLL.addView(initCourseHorView(courses));
                    i++;
                }
            }
        } else {
            relatedCourseLL.setVisibility(View.GONE);
            courseCategoryTitle.setVisibility(View.GONE);
        }
    }

    public LinearLayout initCourseHorView(Course course) {
        TextView courseTV;
        TextView priceTV;
        TextView learnerTV;
        TextView ratingTV;
        final ImageView imageIV;
        RatingBar ratingRB;
        Button btnEnroll;

        LinearLayout view = (LinearLayout) View.inflate(activity, R.layout.single_row_course_hor, null);
        courseTV = view.findViewById(R.id.nameTV);
        priceTV = view.findViewById(R.id.priceTV);
        learnerTV = view.findViewById(R.id.learnerTV);
        ratingTV = view.findViewById(R.id.ratingTV);
        ratingRB = view.findViewById(R.id.ratingRB);
        imageIV = view.findViewById(R.id.imageIV);
        btnEnroll = view.findViewById(R.id.btnEnroll);
        btnEnroll.setVisibility(View.GONE);

        courseTV.setText(course.getTitle());
        if (course.getMrp().equals("0"))
            priceTV.setText(activity.getResources().getString(R.string.free));
        else if (TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
            if (course.getFor_dams().equals(course.getMrp())) {
                priceTV.setText(String.format("%s %s", getCurrencySymbol(),
                        Helper.calculatePriceBasedOnCurrency(course.getMrp())));
            } else {
                StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                priceTV.setText(String.format("%s %s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(course.getMrp()), Helper.calculatePriceBasedOnCurrency(course.getFor_dams())), TextView.BufferType.SPANNABLE);
                Spannable spannable = (Spannable) priceTV.getText();
                spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(course.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            if (course.getNon_dams().equals(course.getMrp())) {
                priceTV.setText(String.format("%s %s", getCurrencySymbol(),
                        Helper.calculatePriceBasedOnCurrency(course.getMrp())));
            } else {
                StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                priceTV.setText(String.format("%s %s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(course.getMrp()), Helper.calculatePriceBasedOnCurrency(course.getNon_dams())), TextView.BufferType.SPANNABLE);
                Spannable spannable = (Spannable) priceTV.getText();
                spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(course.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        learnerTV.setText((course.getLearner() + " " + ((course.getLearner().equals("1")) ? Const.LEARNER : Const.LEARNERS)));
        ratingTV.setText(course.getRating());
        ratingRB.setRating(Float.parseFloat(course.getRating()));

        Glide.with(this).load(course.getCover_image()).into(imageIV);
        view.setTag(course);
        view.setOnClickListener(onCourseClickListener);
        return view;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.faqbtn:
                Intent faq = new Intent(activity, CourseActivity.class);//FRAG_TYPE, Const.FAQ SingleCourse
                faq.putExtra(Const.FRAG_TYPE, Const.FAQ);
                faq.putExtra(Const.COURSES, course);
                startActivity(faq);

                break;

            case R.id.bannerimageIV:
                Helper.GoToVideoActivity(activity, singleCourseData.getCover_video(), Const.VIDEO_STREAM, "", "");
                break;

            case R.id.btn_submit:
                break;


            case R.id.btn_cancel:
                break;

            case R.id.optionTV:
                //showPopMenu(optionIV);
                break;

            case R.id.seeAllReviewsBtn:
                Intent reviews = new Intent(activity, CourseActivity.class);//FRAG_TYPE, Const.REVIEWS SingleCourse
                reviews.putExtra(Const.FRAG_TYPE, Const.REVIEWS);
                reviews.putExtra(Const.COURSE_DESC, singleCourseData);
                startActivity(reviews);

                break;

            case R.id.readMoreBtn:
                Intent courseDes = new Intent(activity, CourseActivity.class);//FRAG_TYPE, Const.COURSE_DES SingleCourse
                courseDes.putExtra(Const.FRAG_TYPE, Const.COURSE_DESC);
                courseDes.putExtra(Const.COURSE_DESC, singleCourseData);
                startActivity(courseDes);
                break;

            case R.id.viewProfileBtn:
                Intent instr = new Intent(activity, CourseActivity.class);//FRAG_TYPE, Const.INSTR SingleCourse
                instr.putExtra(Const.FRAG_TYPE, Const.INSTR);
                instr.putExtra(Const.COURSE_DESC, singleCourseData);
                startActivity(instr);
                break;

            case R.id.seeAllCurriculumBtn:

                if (course.getCourse_type().equalsIgnoreCase("2") || course.getCourse_type().equalsIgnoreCase("3")) {
                    Intent intent = new Intent(activity, TestQuizActivity.class);
                    intent.putExtra(Const.COURSE_ID, course.getId());
                    intent.putExtra(Const.TITLE, course.getTitle());
                    intent.putExtra("is_purchased", singleCourseData.getIs_purchased());
                    intent.putExtra("titlefrag", course.getTitle());
                    activity.startActivity(intent);
                } else {
                    Intent seeAllcurriculum = new Intent(activity, NewCurriculumActivity.class);//FRAG_TYPE, Const.CURRICULUM SingleCourse
                    seeAllcurriculum.putExtra(Const.COURSE_DESC, singleCourseData);
                    startActivity(seeAllcurriculum);
                }
                break;

            case R.id.buyBtn:
            case R.id.topBuyBtn:
                Intent courseInvoice = new Intent(activity, CourseActivity.class); // FRAG_TYPE, Const.COURSE_INVOICE CourseInvoice
                courseInvoice.putExtra(Const.FRAG_TYPE, Const.COURSE_INVOICE);
                courseInvoice.putExtra(Const.COURSE_DESC, singleCourseData);
                courseInvoice.putExtra(Constants.Extras.TYPE, Const.COURSE_INVOICE);
                startActivity(courseInvoice);
                break;
            default:
                break;

        }
    }

    @Override
    public void onRefresh() {
        refreshStatus = true;
        networkCallForCourseInfoRaw();
    }
}
