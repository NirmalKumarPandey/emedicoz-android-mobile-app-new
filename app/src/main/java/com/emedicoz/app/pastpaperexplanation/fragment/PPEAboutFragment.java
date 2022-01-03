package com.emedicoz.app.pastpaperexplanation.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.activity.FAQActivity;
import com.emedicoz.app.courses.adapter.ReviewAdapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Reviews;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.pastpaperexplanation.activity.PastPaperExplanationActivity;
import com.emedicoz.app.pastpaperexplanation.model.PPEData;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PPEAboutFragment extends Fragment {

    Activity activity;
    LinearLayout instructorLL;
    TextView courseTitleTV;
    TextView totalEnrolledCountTV;
    TextView rateCountTV;
    RelativeLayout enrollRL;
    TextView enrollNowTV;
    TextView priceTV;
    TextView instructorNameTV;
    TextView instructorDesTV;
    TextView studentsCountTV;
    TextView coursesCountTV;
    TextView ratingCountTV;
    TextView faqTV;
    ImageView profilePicIV;
    ImageView profilePicIVText;
    Button viewProfileBtn;
    CardView reviewsCV;
    RelativeLayout userReviewsLL;
    LinearLayout btnControlReview;
    ImageView optionIV;
    TextView reviewratingTV;
    TextView ratingcourseTV;
    Button postReviewIBtn;
    Button cancelReviewIBtn;
    AppCompatRatingBar userRatingBr;
    EditText addReviewTextET;
    TextView reviewTextTV;
    RatingBar reviewsratingRB;
    WebView ppeDescriptionWV;
    PPEData ppeData;
    List<Reviews> reviewsArrayList = new ArrayList<>();
    SingleCourseData singleCourseData;
    ReviewAdapter reviewAdapter;
    RecyclerView reviewRecyclerView;
    Button seeAllReviewsBtn;

    TextView profileName, enrollNow;
    LinearLayout instructorStatsLL;
    private CardView cvEnroll;

    public static PPEAboutFragment newInstance() {
        Bundle args = new Bundle();
        PPEAboutFragment fragment = new PPEAboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ppe_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        cvEnroll = view.findViewById(R.id.cvEnroll);
        enrollNow = view.findViewById(R.id.enrollNow);
        courseTitleTV = view.findViewById(R.id.courseTitle);
        totalEnrolledCountTV = view.findViewById(R.id.totalEnrolledCount);
        rateCountTV = view.findViewById(R.id.rateCountTV);

        courseTitleTV = view.findViewById(R.id.courseTitle);
        totalEnrolledCountTV = view.findViewById(R.id.totalEnrolledCount);
        rateCountTV = view.findViewById(R.id.rateCountTV);
        priceTV = view.findViewById(R.id.priceTV);

        instructorStatsLL = view.findViewById(R.id.instructorStatsLL);
        instructorLL = view.findViewById(R.id.instructorLL);
        instructorNameTV = view.findViewById(R.id.instructornameTV);
        instructorDesTV = view.findViewById(R.id.instructorDesTV);
        studentsCountTV = view.findViewById(R.id.studentsCountTV);
        coursesCountTV = view.findViewById(R.id.coursescountTV);
        ratingCountTV = view.findViewById(R.id.ratingcountTV);
        faqTV = view.findViewById(R.id.faqTV);
        profilePicIV = view.findViewById(R.id.profilepicIV);
        profilePicIVText = view.findViewById(R.id.profilepicIVText);
        viewProfileBtn = view.findViewById(R.id.viewProfileBtn);
        viewProfileBtn.setVisibility(View.GONE);
        userReviewsLL = view.findViewById(R.id.userReviewsLL);
        userReviewsLL.setVisibility(View.VISIBLE);
        reviewTextTV = view.findViewById(R.id.reviewTextTV);
        reviewratingTV = view.findViewById(R.id.reviewratingTV);
        ratingcourseTV = view.findViewById(R.id.ratingcourseTV);
        reviewsratingRB = view.findViewById(R.id.reviewsratingRB);
        userRatingBr = view.findViewById(R.id.ratingBar);
        profileName = view.findViewById(R.id.profileName);
        seeAllReviewsBtn = view.findViewById(R.id.seeAllReviewsBtn);
        postReviewIBtn = view.findViewById(R.id.btn_submit);
        postReviewIBtn.setOnClickListener(onClickListener);
        cancelReviewIBtn = view.findViewById(R.id.btn_cancel);
        cancelReviewIBtn.setOnClickListener(onClickListener);
        btnControlReview = view.findViewById(R.id.btnControlReview);
        reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        optionIV = view.findViewById(R.id.optionTV);
        optionIV.setOnClickListener(onClickListener);
        addReviewTextET = view.findViewById(R.id.writereviewET);
        reviewsCV = view.findViewById(R.id.cvReviewRating);
        ppeDescriptionWV = view.findViewById(R.id.ppeDescriptionWV);
        ppeDescriptionWV.getSettings().setJavaScriptEnabled(true);

        bindControls();

        if (activity instanceof PastPaperExplanationActivity) {
            ppeData = ((PastPaperExplanationActivity) activity).ppeData;
        }
        setAboutInfo();
    }

    private void bindControls() {

        cvEnroll.setOnClickListener(v -> goToCourseInvoiceScreen());

        faqTV.setOnClickListener(v -> {
            Intent intent = new Intent(activity, FAQActivity.class);
            intent.putExtra(Constants.Extras.FAQ, (Serializable) ppeData.getFaq());
            intent.putExtra(Constants.Extras.TYPE, Const.FAQ);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        });

        seeAllReviewsBtn.setOnClickListener(v -> {
            if (!GenericUtils.isListEmpty(ppeData.getCourseReview())) {
                Intent intent = new Intent(activity, FAQActivity.class);
                intent.putExtra(Constants.Extras.REVIEW, (Serializable) ppeData.getCourseReview());
                intent.putExtra(Const.COURSE_ID, ppeData.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });

    }

    private void goToCourseInvoiceScreen() {
        Intent intent = new Intent(activity, CourseActivity.class);
        intent.putExtra(Const.FRAG_TYPE, Const.COURSE_INVOICE);
        intent.putExtra(Const.COURSE_DESC, getData(ppeData));
        intent.putExtra(Constants.Extras.TYPE, Const.COURSE_INVOICE);
        activity.startActivity(intent);
    }

    private void setAboutInfo() {
        if (ppeData != null) {
            networkCallForCourseInfoRaw();
            courseTitleTV.setText(ppeData.getTitle());
            ppeDescriptionWV.loadDataWithBaseURL(null, ppeData.getDescription(), "text/html", "UTF-8", null);
            if (ppeData.getLearner().equals("0") || ppeData.getLearner().equals("1"))
                totalEnrolledCountTV.setText(ppeData.getLearner() + " learner");
            else
                totalEnrolledCountTV.setText(ppeData.getLearner() + " learners");

            rateCountTV.setText(ppeData.getRating());

            setEnrollData();
            setReviewsData();
            setInstructorData();
        }
    }

    private void setEnrollData() {
        if (ppeData.getIsPurchased().equalsIgnoreCase("0")) {
            if (ppeData.getMrp().equalsIgnoreCase("0")) {
                cvEnroll.setVisibility(View.GONE);
                userReviewsLL.setVisibility(View.VISIBLE);

            } else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (!ppeData.getForDams().equalsIgnoreCase("0")) {
                        userReviewsLL.setVisibility(View.GONE);
                        cvEnroll.setVisibility(View.VISIBLE);
                        enrollNow.setText(activity.getString(R.string.enroll) + " (" + Helper.getCurrencySymbol() + " " + ppeData.getForDams() + ")");
                        enrollNow.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                        enrollNow.setOnClickListener(v -> Helper.goToCourseInvoiceScreen(activity, singleCourseData));
                    } else {
                        cvEnroll.setVisibility(View.GONE);
                        userReviewsLL.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (!ppeData.getNonDams().equalsIgnoreCase("0")) {
                        cvEnroll.setVisibility(View.VISIBLE);
                        userReviewsLL.setVisibility(View.GONE);
                        enrollNow.setText(activity.getString(R.string.enroll) + " (" + Helper.getCurrencySymbol() + " " + ppeData.getNonDams() + ")");
                        enrollNow.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                        enrollNow.setOnClickListener(v -> Helper.goToCourseInvoiceScreen(activity, singleCourseData));

                    } else {
                        cvEnroll.setVisibility(View.GONE);
                        userReviewsLL.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else if (ppeData.getIsPurchased().equalsIgnoreCase("1")) {
            userReviewsLL.setVisibility(View.VISIBLE);
            cvEnroll.setVisibility(View.GONE);
        }
    }

    private void setReviewsData() {
        reviewAdapter = new ReviewAdapter(activity, reviewsArrayList);
        reviewRecyclerView.setAdapter(reviewAdapter);
        if (!GenericUtils.isListEmpty(ppeData.getCourseReview())) {
            reviewsCV.setVisibility(View.VISIBLE);
        } else {
            reviewsCV.setVisibility(View.GONE);
        }
    }

    private void setInstructorData() {
        if (ppeData.getInstructorData() != null) {
            instructorLL.setVisibility(View.VISIBLE);
            instructorNameTV.setText(ppeData.getInstructorData().getName());
            instructorDesTV.setText(ppeData.getInstructorData().getEmail());
            studentsCountTV.setText(ppeData.getInstructorData().getStudents());
            coursesCountTV.setText(ppeData.getInstructorData().getCourses());
            ratingCountTV.setText(ppeData.getInstructorData().getRating());
            if (!TextUtils.isEmpty(ppeData.getInstructorData().getProfile_pic())) {
                profilePicIV.setVisibility(View.VISIBLE);
                profilePicIVText.setVisibility(View.GONE);
                Glide.with(activity)
                        .asBitmap()
                        .load(ppeData.getInstructorData().getProfile_pic())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.default_pic))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                profilePicIV.setImageBitmap(result);
                            }
                        });
            } else {
                Drawable dr = Helper.GetDrawable(ppeData.getInstructorData().getName(), activity, ppeData.getInstructorData().getId());
                if (dr != null) {
                    profilePicIV.setVisibility(View.GONE);
                    profilePicIVText.setVisibility(View.VISIBLE);
                    profilePicIVText.setImageDrawable(dr);
                } else {
                    profilePicIV.setVisibility(View.VISIBLE);
                    profilePicIVText.setVisibility(View.GONE);
                    profilePicIV.setImageResource(R.mipmap.default_pic);
                }
            }
        } else {
            instructorLL.setVisibility(View.GONE);
        }
    }

    public SingleCourseData getData(PPEData ppeData) {
        SingleCourseData singleCourseData = new SingleCourseData();
        singleCourseData.setCourse_type(ppeData.getCourseType());
        singleCourseData.setFor_dams(ppeData.getForDams());
        singleCourseData.setNon_dams(ppeData.getNonDams());
        singleCourseData.setMrp(ppeData.getMrp());
        singleCourseData.setId(ppeData.getId());
        singleCourseData.setCover_image(ppeData.getCoverImage());
        singleCourseData.setTitle(ppeData.getTitle());
        singleCourseData.setLearner(ppeData.getLearner());
        singleCourseData.setRating(ppeData.getRating());
        singleCourseData.setGst_include(ppeData.getGstInclude());
        singleCourseData.setGst(ppeData.getGst());
        singleCourseData.setPoints_conversion_rate(ppeData.getPointsConversionRate());
        singleCourseData.setIs_subscription(ppeData.getIsSubscription());
        singleCourseData.setIs_instalment(ppeData.getIsInstalment());
        singleCourseData.setInstallment(ppeData.getInstallment());

        if (ppeData.getChildCourses() != null)
            singleCourseData.setChild_courses(ppeData.getChildCourses());

        return singleCourseData;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_submit:
                    if (userRatingBr.getRating() == 0.0) {
                        GenericUtils.showToast(activity, activity.getString(R.string.tap_a_star_to_give_your_rating));
                    }/* else if (TextUtils.isEmpty(addReviewTextET.getText().toString().trim())) {
                        GenericUtils.showToast(activity, activity.getString(R.string.enter_text_post_review));
                    }*/ else {
                        networkCallForAddreviewCourse();
                        if (postReviewIBtn.getTag().equals("0")) {
                            networkCallForAddreviewCourse();//NetworkAPICall(API.API_ADD_REVIEW_COURSE, true);
                        } else {
                            networkCallForEditCourseReview();//NetworkAPICall(API.API_EDIT_USER_COURSE_REVIEWS, true);
                        }
                    }
                    break;
                case R.id.btn_cancel:
                    if (postReviewIBtn.getTag().equals("0")) {
                        addReviewTextET.setText("");
                        userRatingBr.setRating(0);
                        userRatingBr.setIsIndicator(false);
                    } else {
                        reviewTextTV.setVisibility(View.VISIBLE);
                        addReviewTextET.setVisibility(View.GONE);
                        btnControlReview.setVisibility(View.GONE);
                        optionIV.setVisibility(View.VISIBLE);
                        userRatingBr.setRating(Float.parseFloat(singleCourseData.getReview().getRating()));
                        userRatingBr.setIsIndicator(true);
                    }
                    break;
                case R.id.optionTV:
                    showPopMenu(optionIV);
                    break;
            }
        }
    };

    private void networkCallForAddreviewCourse() {
        Progress mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        mProgress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);

        Call<JsonObject> response = apiInterface.addReviewCourse(SharedPreference.getInstance().getLoggedInUser().getId(),
                ppeData.getId(), String.valueOf(userRatingBr.getRating()), addReviewTextET.getText().toString());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            networkCallForCourseInfoRaw();
                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                        Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
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

    private void networkCallForCourseInfoRaw() {
        if (activity.isFinishing()) return;

        Progress mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        mProgress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.getSingleCourseInfoRaw(SharedPreference.getInstance().getLoggedInUser().getId(),
                ppeData.getId(),
                ppeData.getCourseType(),
                ppeData.getIsCombo(),
                "courseId");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            singleCourseData = gson.fromJson(data.toString(), SingleCourseData.class);
                            Constants.COURSEID = singleCourseData.getId();
                            if (singleCourseData.getRating() != null && !singleCourseData.getRating().equalsIgnoreCase(""))
                                Constants.RATING = Float.parseFloat(singleCourseData.getRating());

                            profileName.setText(Helper.CapitalizeText(SharedPreference.getInstance().getLoggedInUser().getName()));
                            showRatingContent(singleCourseData);

                            if (singleCourseData.getReviews() != null) {
                                reviewsCV.setVisibility(View.VISIBLE);
                                setRatingList(singleCourseData);
                            } else {
                                reviewsCV.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
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

    private void networkCallForDeleteReviews() {
        Progress mprogress = new Progress(activity);
        mprogress.setCancelable(false);
        mprogress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.deleteUserCourseReview(SharedPreference.getInstance().getLoggedInUser().getId(),
                singleCourseData.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mprogress.dismiss();

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            networkCallForCourseInfoRaw();
                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                        Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_DELETE_USER_COURSE_REVIEWS, activity, 1, 1);
            }
        });
    }

    private void networkCallForEditCourseReview() {
        Progress mprogress = new Progress(activity);
        mprogress.setCancelable(false);
        mprogress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.editUserCourseReviews(SharedPreference.getInstance().getLoggedInUser().getId()
                , singleCourseData.getId(), String.valueOf(userRatingBr.getRating()), addReviewTextET.getText().toString());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mprogress.dismiss();

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            networkCallForCourseInfoRaw();
                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                        Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_EDIT_USER_COURSE_REVIEWS, activity, 1, 1);
            }
        });
    }

    private void showRatingContent(SingleCourseData singleCourseData) {
        if (singleCourseData.getReview() != null) {
            btnControlReview.setVisibility(View.GONE);
            optionIV.setVisibility(View.VISIBLE);
            reviewTextTV.setVisibility(View.VISIBLE);
            addReviewTextET.setVisibility(View.GONE);
            reviewTextTV.setText(singleCourseData.getReview().getText());
            userRatingBr.setRating(Float.parseFloat(singleCourseData.getReview().getRating()));
            userRatingBr.setIsIndicator(true);
            postReviewIBtn.setTag("1");
        } else {
            reviewTextTV.setVisibility(View.GONE);
            addReviewTextET.setVisibility(View.VISIBLE);
            btnControlReview.setVisibility(View.VISIBLE);
            optionIV.setVisibility(View.INVISIBLE);
            postReviewIBtn.setText(activity.getResources().getString(R.string.submit));
            userRatingBr.setRating(Float.parseFloat("0"));
            userRatingBr.setIsIndicator(false);
        }

        ratingcourseTV.setText(singleCourseData.getRating());
        reviewsratingRB.setRating(Float.parseFloat(singleCourseData.getRating()));
        rateCountTV.setText(singleCourseData.getRating());
        reviewratingTV.setText(String.format("%s Reviews", singleCourseData.getReview_count()));
    }

    private void setRatingList(SingleCourseData singleCourseData) {
        reviewsArrayList.clear();
        if (singleCourseData.getReviews() != null) {
            for (Reviews review : singleCourseData.getReviews()) {
                com.emedicoz.app.modelo.courses.Reviews descCourseReview = new com.emedicoz.app.modelo.courses.Reviews();
                descCourseReview.setCourse_fk_id(review.getCourse_fk_id());
                descCourseReview.setCreation_time(review.getCreation_time());
                descCourseReview.setId(review.getId());
                descCourseReview.setName(review.getName());
                descCourseReview.setProfile_picture(review.getProfile_picture());
                descCourseReview.setRating(review.getRating());
                descCourseReview.setText(review.getText());
                descCourseReview.setUser_id(review.getUser_id());

                reviewsArrayList.add(descCourseReview);
            }
        }
        if (Integer.parseInt(GenericUtils.getParsableString(singleCourseData.getReview_count())) < 3)
            seeAllReviewsBtn.setVisibility(View.GONE);
        else
            seeAllReviewsBtn.setVisibility(View.VISIBLE);

        reviewAdapter.notifyDataSetChanged();
    }

    public void showPopMenu(final View v) {
        PopupMenu popup = new PopupMenu(activity, v);

        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.editIM:
                    editReviews();
                    return true;
                case R.id.deleteIM:
                    View v1 = Helper.newCustomDialog(activity, false, activity.getString(R.string.app_name), activity.getString(R.string.deleteReviews));
                    Button btnCancel;
                    Button btnSubmit;

                    btnCancel = v1.findViewById(R.id.btn_cancel);
                    btnSubmit = v1.findViewById(R.id.btn_submit);
                    btnCancel.setText(activity.getResources().getString(R.string.cancel));
                    btnSubmit.setText(activity.getResources().getString(R.string.delete));

                    btnCancel.setOnClickListener((View view) -> Helper.dismissDialog());

                    btnSubmit.setOnClickListener((View view) -> {
                        Helper.dismissDialog();
                        deleteReviews();
                    });
                    return true;
                default:
                    return false;
            }
        });
        popup.inflate(R.menu.comment_menu);

        popup.show();
    }

    public void deleteReviews() {
        postReviewIBtn.setTag("0");
        networkCallForDeleteReviews();//NetworkAPICall(API.API_DELETE_USER_COURSE_REVIEWS, true);
    }

    public void editReviews() {
        reviewTextTV.setVisibility(View.GONE);
        addReviewTextET.setVisibility(View.VISIBLE);
        addReviewTextET.setText(singleCourseData.getReview().getText());
        btnControlReview.setVisibility(View.VISIBLE);
        optionIV.setVisibility(View.INVISIBLE);
        postReviewIBtn.setText(activity.getResources().getString(R.string.submit));
        userRatingBr.setRating(Float.parseFloat(singleCourseData.getReview().getRating()));
        userRatingBr.setIsIndicator(false);
    }

}
