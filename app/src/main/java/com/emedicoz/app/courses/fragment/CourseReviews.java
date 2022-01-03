package com.emedicoz.app.courses.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.adapter.ReviewAdapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.customviews.ReviewDialog;
import com.emedicoz.app.modelo.courses.Reviews;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseReviews extends Fragment implements View.OnClickListener {
    public int firstVisibleItem, visibleItemCount, totalItemCount;
    public int previousTotalItemCount;
    Activity activity;
    String frag_type = "", errorMessage, apiType;
    RecyclerView reviewsRV;
    LinearLayout addReviewsLL;
    EditText addReviewTextET;
    ImageButton postReviewIBtn;
    SingleCourseData coursesData;
    ArrayList<Reviews> reviewsArrayList;
    Float rating;
    ReviewAdapter reviewAdapter;
    String lastReviewId = "0", lastId = "0";
    int isalreadyconnected = 0;
    Progress mprogress;
    private boolean loading = true;
    private int visibleThreshold = 5;
    private TextView errorTV;
    private LinearLayoutManager linearLayoutManager;

    public CourseReviews() {
        // Required empty public constructor
    }

    public static CourseReviews newInstance(String fragType, SingleCourseData singleCourseData) {
        CourseReviews courseReviews = new CourseReviews();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.COURSE_DESC, singleCourseData);
        bundle.putString(Const.FRAG_TYPE, fragType);
        courseReviews.setArguments(bundle);
        return courseReviews;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reviewsArrayList = new ArrayList<>();
        if (getArguments() != null) {
            frag_type = getArguments().getString(Const.FRAG_TYPE);
            coursesData = (SingleCourseData) getArguments().getSerializable(Const.COURSE_DESC);
        }
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mprogress = new Progress(getActivity());
        mprogress.setCancelable(false);
        initViews(view);
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        reviewsRV.setLayoutManager(linearLayoutManager);

        reviewsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (totalItemCount >= 10) { // if the list is more than 10 then only pagination will work
                    if (loading) {
                        if (totalItemCount > previousTotalItemCount) {
                            loading = false;
                            previousTotalItemCount = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {

                        int i = 0;
                        while (i < totalItemCount) {
                            if (frag_type.equals(Const.INSTR_REVIEWS)) {
                                lastReviewId = reviewsArrayList.get(totalItemCount - 1 - i).getId();
                            } else if (frag_type.equals(Const.REVIEWS)) {
                                lastReviewId = reviewsArrayList.get(totalItemCount - 1 - i).getId();
                            }
                            i = totalItemCount;
                        }
                        if (isalreadyconnected == 0) {
                            refreshData(false);
                            isalreadyconnected = 1;
                        }
                        loading = true;
                    }
                }
            }
        });
        refreshData(true);
    }

    private void initViews(View view) {
        reviewsRV = view.findViewById(R.id.reviewsListRV);
        addReviewsLL = view.findViewById(R.id.addReviewLL);
        addReviewTextET = view.findViewById(R.id.writereviewET);
        postReviewIBtn = view.findViewById(R.id.postreviewIBtn);
        errorTV = view.findViewById(R.id.errorTV);
    }

    private void refreshData(boolean bool) {
        if (frag_type.equals(Const.INSTR_REVIEWS)) {
            networkCallForgetInstructorReviewList();//NetworkAPICall(API.API_GET_INSTRUCTOR_REVIEW_LIST, bool);
        } else if (frag_type.equals(Const.REVIEWS)) {
            networkCallForgetCourseReviewList(bool);//NetworkAPICall(API.API_GET_COURSE_REVIEW_LIST, bool);
        }
    }

    private void networkCallForaddReviewCourse() {
        mprogress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.addReviewCourse(SharedPreference.getInstance().getLoggedInUser().getId(),
                coursesData.getId(), String.valueOf(rating), addReviewTextET.getText().toString());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    mprogress.dismiss();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        clearText();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            networkCallForgetInstructorReviewList();//NetworkAPICall(API.API_GET_INSTRUCTOR_REVIEW_LIST, true);
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_ADD_REVIEW_COURSE);
                            networkCallForgetInstructorReviewList();//NetworkAPICall(API.API_GET_INSTRUCTOR_REVIEW_LIST, true);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_ADD_REVIEW_COURSE, activity, 1, 1);
            }
        });
    }

    private void networkCallForgetInstructorReviewList() {
        mprogress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getInstructorReviewList(SharedPreference.getInstance().getLoggedInUser().getId(),
                coursesData.getInstructor_data().getId(), lastReviewId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    Gson gson = new Gson();
                    JSONArray dataArray;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        mprogress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            dataArray = GenericUtils.getJsonArray(jsonResponse);
                            if (TextUtils.isEmpty(lastReviewId))
                                reviewsArrayList = new ArrayList<>();
                            for (int i = 0; i < dataArray.length(); i++) {
                                Reviews review = gson.fromJson(dataArray.opt(i).toString(), Reviews.class);
                                reviewsArrayList.add(review);
                            }
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_INSTRUCTOR_REVIEW_LIST);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                        initCourseReviewsAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_INSTRUCTOR_REVIEW_LIST, activity, 1, 1);
            }
        });
    }

    private void networkCallForgetCourseReviewList(Boolean bool) {
        if (bool) {
            mprogress.show();
        }
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getCoursereviewList(SharedPreference.getInstance().getLoggedInUser().getId(),
                coursesData.getId(), lastId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();
                    JSONArray dataArray;
                    if (mprogress.isShowing())
                        mprogress.dismiss();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            dataArray = GenericUtils.getJsonArray(jsonResponse);
                            if (TextUtils.isEmpty(lastId)) reviewsArrayList = new ArrayList<>();
                            for (int i = 0; i < dataArray.length(); i++) {
                                Reviews review = gson.fromJson(dataArray.opt(i).toString(), Reviews.class);
                                reviewsArrayList.add(review);
                            }
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_COURSE_REVIEW_LIST);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                        initCourseReviewsAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mprogress.isShowing())
                    mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_COURSE_REVIEW_LIST, activity, 1, 1);
            }
        });
    }

    public void retryApis(String apiType) {
        switch (apiType) {
            case API.API_GET_COURSE_REVIEW_LIST:
                networkCallForgetCourseReviewList(true);
                break;
            case API.API_ADD_REVIEW_COURSE:
                networkCallForaddReviewCourse();
                break;
            case API.API_GET_INSTRUCTOR_REVIEW_LIST:
                networkCallForgetInstructorReviewList();
                break;
        }
    }

    private void errorCallBack(String jsonString, String apiType) {
        switch (apiType) {
            case API.API_GET_INSTRUCTOR_REVIEW_LIST:
            case API.API_GET_COURSE_REVIEW_LIST:
                if (TextUtils.isEmpty(lastReviewId) || lastReviewId.equals("0")) {
                    errorMessage = jsonString;
                    apiType = apiType;
                }
                initCourseReviewsAdapter();
                break;
        }

        if (jsonString.equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
            Helper.showErrorLayoutForNoNav(apiType, activity, 1, 1);
    }

    private void initCourseReviewsAdapter() {
        if (!reviewsArrayList.isEmpty()) {
            reviewAdapter = new ReviewAdapter(activity, reviewsArrayList);
            reviewsRV.setAdapter(reviewAdapter);
            reviewsRV.addItemDecoration(new EqualSpacingItemDecoration(25,EqualSpacingItemDecoration.VERTICAL));
            errorTV.setVisibility(View.GONE);
            reviewsRV.setVisibility(View.VISIBLE);
        } else {
            errorTV.setText(errorMessage);
            errorTV.setVisibility(View.VISIBLE);
            reviewsRV.setVisibility(View.GONE);
            if (errorMessage.contains(getString(R.string.something_went_wrong_string))) {
                Helper.showErrorLayoutForNoNav(apiType, activity, 1, 1);
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.postreviewIBtn:
//                if (!TextUtils.isEmpty(addReviewTextET.getText().toString())) {
                ReviewDialog reviewDialog = new ReviewDialog(activity, CourseReviews.this);
                reviewDialog.show();
//                } else {
//                    Toast.makeText(activity, R.string.enter_text_post_review, Toast.LENGTH_SHORT).show();
//                }

                break;
        }
    }

    public void addReviews(Float rating) {
        this.rating = rating;
        networkCallForaddReviewCourse();//NetworkAPICall(API.API_ADD_REVIEW_COURSE, true);
    }

    public void clearText(Reviews reviews) {
        addReviewTextET.setText(reviews.getText());
    }

    public void clearText() {
        addReviewTextET.setText("");
    }
}
