package com.emedicoz.app.courses.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.adapter.DescriptionReviewAdapter;
import com.emedicoz.app.courses.adapter.FAQRecyclerAdapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.liveclass.courses.DescCourseReview;
import com.emedicoz.app.modelo.liveclass.courses.DescriptionFAQ;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.LandingPageApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// calling from SingleOverviewAdapter, SingleCourse for faq list.

public class FAQActivity extends AppCompatActivity {

    RecyclerView reviewRecyclerView;
    FAQRecyclerAdapter faqRecyclerAdapter;
    Activity activity;
    public Progress mProgress;
    TextView tvTitle, tvNoData;
    ImageView ivBack;
    int pageNumber = 1;
    String lastReviewId = "0";
    private DescriptionReviewAdapter reviewAdapter;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String courseId;
    Course course;
    List<DescCourseReview> reviewList;
    List<DescriptionFAQ> faqList = new ArrayList<>();
    private static final String TAG = "FAQActivity";
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.faq_activity);

        activity = this;

        initViews();
        getIntentData();
        bindControls();
    }

    private void initViews() {
        mProgress = new Progress(this);
        mProgress.setCancelable(false);

        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);
        tvNoData = findViewById(R.id.tvNoData);

        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        faqRecyclerAdapter = new FAQRecyclerAdapter(activity, faqList);
        reviewRecyclerView.setAdapter(faqRecyclerAdapter);
    }

    private void getIntentData() {
        if (getIntent().getSerializableExtra(Constants.Extras.FAQ) != null) {
            faqList = (List<DescriptionFAQ>) getIntent().getSerializableExtra(Constants.Extras.FAQ);
        }
        reviewList = (List<DescCourseReview>) getIntent().getSerializableExtra(Constants.Extras.REVIEW);
        courseId = getIntent().getStringExtra(Const.COURSE_ID);
        course = (Course) getIntent().getSerializableExtra(Const.COURSES);
        type = getIntent().getStringExtra(Constants.Extras.TYPE);
    }

    private void bindControls() {
        if (Const.FAQ.equals(type)) {
            tvTitle.setText("FAQ's");
            if (faqList.size() > 0) {
                faqRecyclerAdapter.updateList(faqList);
            } else {
                networkcallForFaqData();
            }
        } else {
            if (!GenericUtils.isListEmpty(reviewList))
                setReviewsAdapter();
            else
                fetchReviews();
            tvTitle.setText("Review");
        }

        ivBack.setOnClickListener((View v) -> onBackPressed());
    }

    private void fetchReviews() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        isLoading = true;

        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getCoursereviewList(SharedPreference.getInstance().getLoggedInUser().getId(),
                courseId, lastReviewId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                isLoading = false;
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();
                    JSONArray dataArray;
                    if (mProgress.isShowing())
                        mProgress.dismiss();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            dataArray = GenericUtils.getJsonArray(jsonResponse);
                            if (lastReviewId.equals("0")) reviewList = new ArrayList<>();
                            for (int i = 0; i < dataArray.length(); i++) {
                                DescCourseReview review = gson.fromJson(dataArray.opt(i).toString(), DescCourseReview.class);
                                reviewList.add(review);
                            }
                            lastReviewId = reviewList.get(reviewList.size() - 1).getId();
                            setReviewsAdapter();
                        } else {
                            isLastPage = true;
//                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgress.isShowing())
                    mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_COURSE_REVIEW_LIST, activity, 1, 1);
            }
        });

    }

    private void setReviewsAdapter() {
        if (reviewAdapter == null) {
            reviewAdapter = new DescriptionReviewAdapter(activity, reviewList, Constants.Extras.ALL_REVIEW);
            LinearLayoutManager layoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
            reviewRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(25, EqualSpacingItemDecoration.VERTICAL));
            reviewRecyclerView.setLayoutManager(layoutManager);
            reviewRecyclerView.setAdapter(reviewAdapter);
            reviewRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                            pageNumber++;
                            fetchReviews();
                        }
                    }
                }
            });
            reviewRecyclerView.setAdapter(reviewAdapter);
        } else {
            reviewAdapter.setList(reviewList);
            reviewAdapter.notifyDataSetChanged();
        }

    }

    public void networkcallForFaqData() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        Progress mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        mProgress.show();
        LandingPageApiInterface apiInterface = ApiClient.createService(LandingPageApiInterface.class);
        Call<JsonObject> response = apiInterface.getFaqPageData(SharedPreference.getInstance().getLoggedInUser().getId(),
                course.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JSONArray dataArray = null;
                    Gson gson = new Gson();

                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            dataArray = GenericUtils.getJsonArray(jsonResponse);
                            if (dataArray != null && dataArray.length() > 0) {
                                faqList.clear();
                                tvNoData.setVisibility(View.GONE);
                                reviewRecyclerView.setVisibility(View.VISIBLE);

                                for (int i = 0; i < dataArray.length(); i++) {
                                    faqList.add(gson.fromJson(dataArray.get(i).toString(), DescriptionFAQ.class));
                                }

                                faqRecyclerAdapter.updateList(faqList);
                            } else {
                                reviewRecyclerView.setVisibility(View.GONE);
                                tvNoData.setVisibility(View.VISIBLE);
                                tvNoData.setText(jsonResponse.optString(Constants.Extras.MESSAGE));
                            }
                        } else {
                            reviewRecyclerView.setVisibility(View.GONE);
                            tvNoData.setVisibility(View.VISIBLE);
                            tvNoData.setText(jsonResponse.optString(Constants.Extras.MESSAGE));
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    reviewRecyclerView.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText(activity.getString(R.string.something_went_wrong));
                    Helper.printError(TAG, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNav(API.API_GET_FAQ_DATA, activity, 1, 1);
            }
        });
    }
}
