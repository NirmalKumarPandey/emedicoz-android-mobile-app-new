package com.emedicoz.app.courses.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.adapter.ReviewAdapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.Instructor;
import com.emedicoz.app.modelo.courses.Reviews;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.InstructorFragApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstructorFragment extends Fragment implements View.OnClickListener {
    Activity activity;
    String fragType = "";
    String textReview = "";
    RelativeLayout userReviewsLL;
    RatingBar reviewsRatingBr;
    AppCompatRatingBar userRatingBr;
    EditText addReviewTextET;
    CardView cvSingleCourseRating;
    LinearLayout courseLL;
    LinearLayout btnControlReview;
    LinearLayout mainParentLL;
    Instructor instructorData;
    RecyclerView reviewRecyclerView;
    ReviewAdapter reviewAdapter;
    List<Reviews> reviewsList = new ArrayList<>();
    TextView instructorstudentsTV, instructorcoursesTV, instructorTV, reviewTextTV, courseRatingCountTV, ratingTV, profileName,
            instructorratingTV, instructornameTV, instructorDesTV, aboutTv, coursesbyInstTV;
    Button instrProfBtn, postReviewIBtn, cancelReviewIBtn, seeReviewsBtn, seeAllCourseBtn;
    SingleCourseData singleCourseData;
    ImageView instrprofilepicIV, optionIV, profileImage, userProfilePicIVText, profilepicIVText;
    CardView aboutCard;
    Progress mprogress;


    View.OnClickListener onCourseClickListener = v -> {
        Course instructor = (Course) v.getTag();
        Intent courseList = new Intent(activity, CourseActivity.class); //FRAG_TYPE, Const.SINGLE_COURSE InstructorFragment
        courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
        courseList.putExtra(Const.COURSES, instructor);
        startActivity(courseList);
    };

    public InstructorFragment() {
        // Required empty public constructor
    }

    public static InstructorFragment newInstance(SingleCourseData singleCourseData) {
        InstructorFragment instructorFragment = new InstructorFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.COURSE_DESC, singleCourseData);
        instructorFragment.setArguments(bundle);
        return instructorFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instructor, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mprogress = new Progress(getActivity());
        mprogress.setCancelable(false);

        if (getArguments() != null) {
            fragType = getArguments().getString(Const.FRAG_TYPE);
            singleCourseData = (SingleCourseData) getArguments().getSerializable(Const.COURSE_DESC);
        }
        activity = getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        networkCallForInstructorData();
    }

    private LinearLayout initInstrCourseView(Course course) {
        TextView courseTV;
        TextView priceTV;
        TextView learnerTV;
        TextView ratingTV;
        final ImageView imageIV;
        RatingBar ratingRB;
        Button btnEnroll;
        LinearLayout verView = (LinearLayout) View.inflate(activity, R.layout.single_row_course_hor, null);
        courseTV = verView.findViewById(R.id.nameTV);
        priceTV = verView.findViewById(R.id.priceTV);
        learnerTV = verView.findViewById(R.id.learnerTV);
        ratingTV = verView.findViewById(R.id.ratingTV);
        ratingRB = verView.findViewById(R.id.ratingRB);
        imageIV = verView.findViewById(R.id.imageIV);
        btnEnroll = verView.findViewById(R.id.btnEnroll);
        btnEnroll.setVisibility(View.GONE);

        courseTV.setText(course.getTitle());

        if (course.getMrp().equals("0")) {
            priceTV.setText("Free");
        } else if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
            if (course.getFor_dams().equals(course.getMrp())) {
                priceTV.setText(String.format("%s %s", activity.getResources().getString(R.string.rs), course.getMrp()));
            } else {
                StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                priceTV.setText(String.format("%s %s %s", activity.getResources().getString(R.string.rs), course.getMrp(), course.getFor_dams()), TextView.BufferType.SPANNABLE);
                Spannable spannable = (Spannable) priceTV.getText();
                spannable.setSpan(strikeThroughSpan, 2, course.getMrp().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            if (course.getNon_dams().equals(course.getMrp())) {
                priceTV.setText(String.format("%s %s", activity.getResources().getString(R.string.rs), course.getMrp()));
            } else {
                StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                priceTV.setText(String.format("%s %s %s", activity.getResources().getString(R.string.rs), course.getMrp(), course.getNon_dams()), TextView.BufferType.SPANNABLE);
                Spannable spannable = (Spannable) priceTV.getText();
                spannable.setSpan(strikeThroughSpan, 2, course.getMrp().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        learnerTV.setText((course.getLearner() + " " + ((course.getLearner().equals("1") || (course.getLearner().equals("0")) ? Const.LEARNER : Const.LEARNERS))));

        ratingTV.setText(course.getRating());
        ratingRB.setRating(Float.parseFloat(course.getRating()));
        Glide.with(this).load(course.getCover_image()).apply(new RequestOptions().placeholder(R.mipmap.courses_blue).error(R.mipmap.courses_blue)).into(imageIV);
        verView.setTag(course);
        verView.setOnClickListener(onCourseClickListener);
        return verView;
    }

    private void initViews(View view) {
        instructornameTV = view.findViewById(R.id.instructornameTV);
        instructorcoursesTV = view.findViewById(R.id.coursescountTV);
        instructorDesTV = view.findViewById(R.id.instructorDesTV);
        instructorratingTV = view.findViewById(R.id.ratingcountTV);
        instructorstudentsTV = view.findViewById(R.id.studentsCountTV);
        instrProfBtn = view.findViewById(R.id.viewProfileBtn);
        courseLL = view.findViewById(R.id.courseInstrLL);
        aboutTv = view.findViewById(R.id.aboutTV);
        aboutCard = view.findViewById(R.id.aboutCard);
        courseRatingCountTV = view.findViewById(R.id.ratingcourseTV);
        ratingTV = view.findViewById(R.id.reviewratingTV);
        reviewsRatingBr = view.findViewById(R.id.reviewsratingRB);
        coursesbyInstTV = view.findViewById(R.id.courseByInstrTV);
        instrprofilepicIV = view.findViewById(R.id.profilepicIV);
        instructorTV = view.findViewById(R.id.instructorTV);
        userReviewsLL = view.findViewById(R.id.userReviewsLL);
        cvSingleCourseRating = view.findViewById(R.id.cvSingleCourseRating);
        addReviewTextET = view.findViewById(R.id.writereviewET);
        postReviewIBtn = view.findViewById(R.id.btn_submit);
        cancelReviewIBtn = view.findViewById(R.id.btn_cancel);
        userRatingBr = view.findViewById(R.id.ratingBar);
        reviewTextTV = view.findViewById(R.id.reviewTextTV);
        optionIV = view.findViewById(R.id.optionTV);
        profileImage = view.findViewById(R.id.userprofilepicIV);
        userProfilePicIVText = view.findViewById(R.id.userprofilepicIVText);
        profileName = view.findViewById(R.id.profileName);
        seeReviewsBtn = view.findViewById(R.id.seeAllReviewsBtn);
        mainParentLL = view.findViewById(R.id.mainParentLL);
        btnControlReview = view.findViewById(R.id.btnControlReview);
        profilepicIVText = view.findViewById(R.id.profilepicIVText);
        seeAllCourseBtn = view.findViewById(R.id.seeAllCourseBtn);
        reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);

        bindControls();
    }

    private void bindControls() {
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        reviewAdapter = new ReviewAdapter(activity, reviewsList);
        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(25, EqualSpacingItemDecoration.VERTICAL));

        if (instrProfBtn.getVisibility() == View.VISIBLE) instrProfBtn.setVisibility(View.GONE);
        instructorTV.setVisibility(View.GONE);

        optionIV.setOnClickListener(this);
        seeReviewsBtn.setOnClickListener(this);
        postReviewIBtn.setOnClickListener(this);
        cancelReviewIBtn.setOnClickListener(this);
        seeAllCourseBtn.setOnClickListener(this);
    }

    private void networkCallForInstructorData() {
        mprogress.show();
        InstructorFragApiInterface apiInterface = ApiClient.createService(InstructorFragApiInterface.class);
        Call<JsonObject> response = apiInterface.getinstructorData(SharedPreference.getInstance().getLoggedInUser().getId(),
                singleCourseData.getInstructor_data().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mprogress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            instructorData = gson.fromJson(data.toString(), Instructor.class);
                            setInstructorData();
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_INSTRUCTOR_DATA);
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
                Helper.showErrorLayoutForNoNav(API.API_GET_INSTRUCTOR_DATA, activity, 1, 1);
            }
        });
    }

    private void networkCallForAddInstReviewCourse() {
        mprogress.show();
        InstructorFragApiInterface apiInterface = ApiClient.createService(InstructorFragApiInterface.class);
        Call<JsonObject> response = apiInterface.getinstructorReviewCourse(SharedPreference.getInstance().getLoggedInUser().getId(),
                instructorData.getId(), String.valueOf(userRatingBr.getRating()), addReviewTextET.getText().toString());
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
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            SharedPreference.getInstance().putBoolean(Const.REVIEWS, true);
                            networkCallForInstructorData();
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_ADD_INSTRUCTOR_REVIEW_COURSE);
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
                Helper.showErrorLayoutForNoNav(API.API_ADD_INSTRUCTOR_REVIEW_COURSE, activity, 1, 1);
            }
        });
    }

    private void networkCallFordeleteUserInstReview() {
        mprogress.show();
        InstructorFragApiInterface apiInterface = ApiClient.createService(InstructorFragApiInterface.class);
        Call<JsonObject> response = apiInterface.deleteUserInstructorReviews(SharedPreference.getInstance().getLoggedInUser().getId(),
                instructorData.getId());
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
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            SharedPreference.getInstance().putBoolean(Const.REVIEWS, true);
                            networkCallForInstructorData();
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_DELETE_USER_INSTRUCTOR_REVIEWS);
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
                Helper.showErrorLayoutForNoNav(API.API_DELETE_USER_INSTRUCTOR_REVIEWS, activity, 1, 1);
            }
        });
    }

    private void networkCallForEditInstReview() {
        mprogress.show();
        InstructorFragApiInterface apiInterface = ApiClient.createService(InstructorFragApiInterface.class);
        Call<JsonObject> response = apiInterface.editUserInstructorReviews(SharedPreference.getInstance().getLoggedInUser().getId(),
                instructorData.getId(), String.valueOf(userRatingBr.getRating()), addReviewTextET.getText().toString());
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
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            SharedPreference.getInstance().putBoolean(Const.REVIEWS, true);
                            networkCallForInstructorData();
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_EDIT_USER_INSTRUCTOR_REVIEWS);
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
                Helper.showErrorLayoutForNoNav(API.API_EDIT_USER_INSTRUCTOR_REVIEWS, activity, 1, 1);
            }
        });
    }

    public void retryApis(String apiType) {
        switch (apiType) {
            case API.API_GET_INSTRUCTOR_DATA:
                networkCallForInstructorData();
                break;
            case API.API_ADD_INSTRUCTOR_REVIEW_COURSE:
                networkCallForAddInstReviewCourse();
                break;
            case API.API_DELETE_USER_INSTRUCTOR_REVIEWS:
                networkCallFordeleteUserInstReview();
                break;
            case API.API_EDIT_USER_INSTRUCTOR_REVIEWS:
                networkCallForEditInstReview();
                break;
            default:
                break;
        }
    }

    private void errorCallBack(String jsonString, String apiType) {
        if (jsonString.equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
            Helper.showErrorLayoutForNoNav(apiType, activity, 1, 1);

        if (jsonString.contains(getString(R.string.something_went_wrong)))
            Helper.showErrorLayoutForNoNav(apiType, activity, 1, 2);
    }

    private void setInstructorData() {

        if (mainParentLL.getVisibility() == View.GONE) mainParentLL.setVisibility(View.VISIBLE);
        instructorstudentsTV.setText(instructorData.getStudents());
        instructorcoursesTV.setText(instructorData.getCourses());
        instructorratingTV.setText(String.format("%s/5.0", instructorData.getRating()));
        instructornameTV.setText(instructorData.getName());

        //Capitalizing the first letter of User's Name
        profileName.setText(Helper.CapitalizeText(SharedPreference.getInstance().getLoggedInUser().getName()));

        // ** To set the Image on Profile ** //
        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getProfile_picture())) {
            profileImage.setVisibility(View.VISIBLE);
            userProfilePicIVText.setVisibility(View.GONE);

            Glide.with(this).load(SharedPreference.getInstance().getLoggedInUser().getProfile_picture()).into(profileImage);
        } else {
            Drawable dr = Helper.GetDrawable(SharedPreference.getInstance().getLoggedInUser().getName(), activity, SharedPreference.getInstance().getLoggedInUser().getId());
            if (dr != null) {
                profileImage.setVisibility(View.GONE);
                userProfilePicIVText.setVisibility(View.VISIBLE);
                userProfilePicIVText.setImageDrawable(dr);
            } else {
                profileImage.setVisibility(View.VISIBLE);
                userProfilePicIVText.setVisibility(View.GONE);
                profileImage.setImageResource(R.mipmap.default_pic);
            }
        }


        if (instructorData != null && !TextUtils.isEmpty(instructorData.getReview()))
            ratingTV.setText((instructorData.getReview() + " " + (instructorData.getReview().equals("0") || instructorData.getReview().equals("1") ? Const.REVIEW : Const.REVIEWS)));
        courseRatingCountTV.setText(Objects.requireNonNull(instructorData).getRating());
        reviewsRatingBr.setRating(Float.parseFloat(String.valueOf(instructorData.getRating())));

        if (courseLL.getChildCount() > 0) courseLL.removeAllViews();

        if (!instructorData.getCourse_list().isEmpty()) {
            int i = 0;   // to show only first three courses
            for (Course courses : instructorData.getCourse_list()) {
                if (i == 3) {
                    seeAllCourseBtn.setVisibility(View.VISIBLE);
                    break;
                } else {
                    courseLL.addView(initInstrCourseView(courses));
                    i++;
                }
            }
        }

        if (!TextUtils.isEmpty(instructorData.getAbout())) {
            aboutTv.setText(instructorData.getAbout());
            aboutCard.setVisibility(View.VISIBLE);
        }
        coursesbyInstTV.setText(String.format("%s by %s", Const.COURSES, instructorData.getName()));

        if (!TextUtils.isEmpty(instructorData.getProfile_pic())) {
            instrprofilepicIV.setVisibility(View.VISIBLE);
            profilepicIVText.setVisibility(View.GONE);

            Glide.with(this).load(instructorData.getProfile_pic()).apply(new RequestOptions().error(R.mipmap.default_pic).placeholder(R.mipmap.default_pic)).into(instrprofilepicIV);
        } else {
            Drawable dr = Helper.GetDrawable(instructorData.getName(), activity, instructorData.getId());
            if (dr != null) {
                instrprofilepicIV.setVisibility(View.GONE);
                profilepicIVText.setVisibility(View.VISIBLE);
                profilepicIVText.setImageDrawable(dr);
            } else {
                instrprofilepicIV.setVisibility(View.VISIBLE);
                profilepicIVText.setVisibility(View.GONE);
                instrprofilepicIV.setImageResource(R.mipmap.default_pic);
            }
        }

        if (singleCourseData.getIs_purchased().equals("1")) {
            userReviewsLL.setVisibility(View.VISIBLE);
        } else {
            userReviewsLL.setVisibility(View.GONE);
        }

        // to set Rating content
        setRatingContent();

        if (instructorData.getUser_given_review() != null) {
            addReviewTextET.setVisibility(View.GONE);
            btnControlReview.setVisibility(View.GONE);
            optionIV.setVisibility(View.VISIBLE);
            reviewTextTV.setText(instructorData.getUser_given_review().getText());
            reviewTextTV.setVisibility(View.VISIBLE);
            userRatingBr.setRating(Float.parseFloat(instructorData.getUser_given_review().getRating()));
            userRatingBr.setIsIndicator(true);
            postReviewIBtn.setTag("1");
        } else {
            addReviewTextET.setVisibility(View.VISIBLE);
            btnControlReview.setVisibility(View.VISIBLE);
            optionIV.setVisibility(View.INVISIBLE);
            reviewTextTV.setVisibility(View.GONE);
            postReviewIBtn.setText(getString(R.string.submit));
            addReviewTextET.setText("");
            userRatingBr.setRating(Float.parseFloat("0"));
            userRatingBr.setIsIndicator(false);
        }
    }

    private void setRatingContent() {
        if (instructorData.getReviews() != null) {
            reviewsList.clear();
            for (Reviews review : singleCourseData.getReviews()) {
                Reviews reviews = new Reviews();
                reviews.setCourse_fk_id(review.getCourse_fk_id());
                reviews.setCreation_time(review.getCreation_time());
                reviews.setId(review.getId());
                reviews.setName(review.getName());
                reviews.setProfile_picture(review.getProfile_picture());
                reviews.setRating(review.getRating());
                reviews.setText(review.getText());
                reviews.setUser_id(review.getUser_id());
                reviewsList.add(reviews);
            }
            reviewAdapter.notifyDataSetChanged();
        } else {
            cvSingleCourseRating.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                onSubmitReviewClick(postReviewIBtn.getTag());
                break;


            case R.id.btn_cancel:
                if (postReviewIBtn.getTag().equals("0")) {
                    addReviewTextET.setText("");
                    userRatingBr.setRating(0);
                    userRatingBr.setIsIndicator(false);
                } else {
                    addReviewTextET.setVisibility(View.GONE);
                    btnControlReview.setVisibility(View.GONE);
                    optionIV.setVisibility(View.VISIBLE);
                    reviewTextTV.setText(!TextUtils.isEmpty(singleCourseData.getReview().getText()) ? singleCourseData.getReview().getText() : "");
                    reviewTextTV.setVisibility(View.VISIBLE);
                    userRatingBr.setRating(Float.parseFloat(singleCourseData.getReview().getRating()));
                    userRatingBr.setIsIndicator(true);
                }

                break;

            case R.id.optionTV:
                showPopMenu(optionIV);
                break;

            case R.id.seeAllCourseBtn:
                Intent see = new Intent(activity, CourseActivity.class);//FRAG_TYPE, Const.INSTR_REVIEWS InstructorFragment
                see.putExtra(Const.FRAG_TYPE, Const.SEEALL_INSTRUCTOR_COURSE);
                see.putExtra(Const.COURSE_LIST, instructorData.getCourse_list());
                startActivity(see);
                break;
            case R.id.seeAllReviewsBtn:
                Intent reviews = new Intent(activity, CourseActivity.class);//FRAG_TYPE, Const.INSTR_REVIEWS InstructorFragment
                reviews.putExtra(Const.FRAG_TYPE, Const.INSTR_REVIEWS);
                reviews.putExtra(Const.COURSE_DESC, singleCourseData);
                startActivity(reviews);

                break;

            default:
                break;
        }
    }

    private void onSubmitReviewClick(Object tag) {
        if (/*!TextUtils.isEmpty(addReviewTextET.getText().toString()) &&*/ userRatingBr.getRating() != 0.0) {
            textReview = addReviewTextET.getText().toString();
            if (tag.equals("0"))
                networkCallForAddInstReviewCourse();
            else
                networkCallForEditInstReview();
        } else {
            Toast.makeText(activity, R.string.enter_text_post_review, Toast.LENGTH_SHORT).show();
        }
    }

    public void showPopMenu(final View view) {
        PopupMenu popup = new PopupMenu(activity, view);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.editIM:
                    editReviews();
                    return true;
                case R.id.deleteIM:
                    View v = Helper.newCustomDialog(activity, false, activity.getString(R.string.app_name), activity.getString(R.string.deleteReviews));

                    Button btnCancel;
                    Button btnSubmit;

                    btnCancel = v.findViewById(R.id.btn_cancel);
                    btnSubmit = v.findViewById(R.id.btn_submit);

                    btnCancel.setText(getString(R.string.cancel));
                    btnSubmit.setText(getString(R.string.delete));

                    btnCancel.setOnClickListener(v1 -> Helper.dismissDialog());

                    btnSubmit.setOnClickListener(v1 -> {
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
        networkCallFordeleteUserInstReview();
    }

    public void editReviews() {
        addReviewTextET.setVisibility(View.VISIBLE);
        btnControlReview.setVisibility(View.VISIBLE);
        optionIV.setVisibility(View.INVISIBLE);
        reviewTextTV.setVisibility(View.GONE);
        postReviewIBtn.setText(getString(R.string.submit));
        addReviewTextET.setText(instructorData.getUser_given_review().getText());
        userRatingBr.setRating(Float.parseFloat(instructorData.getUser_given_review().getRating()));
        userRatingBr.setIsIndicator(false);
    }
}
