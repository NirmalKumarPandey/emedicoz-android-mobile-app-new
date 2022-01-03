package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.activity.FAQActivity;
import com.emedicoz.app.courses.activity.NewCurriculumActivity;
import com.emedicoz.app.courses.adapter.CurriculumFileRecyclerAdapter;
import com.emedicoz.app.courses.adapter.ReviewAdapter;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.CourseLockedManager;
import com.emedicoz.app.modelo.courses.Curriculam;
import com.emedicoz.app.modelo.courses.Reviews;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.retrofit.apiinterfaces.SingleCourseApiInterface;
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
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;

public class NewSingleCourse extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, MyNetworkCall.MyNetworkCallBack {

    private static final String TAG = "SingleCourse";
    public static ImageView bannerimageIV, profileImage, instrprofilepicIV, playIV, profilepicIVText, userProfilePicIVText;
    ImageView optionIV;
    boolean refreshStatus = false;
    Activity activity;
    String fragmentType;
    String fragType = "", textReview = "", des = "";
    Course course;
    int courseDescriptionLength = 300;
    SwipeRefreshLayout swipeSingleCourse;
    NestedScrollView scroll;
    RelativeLayout userReviewsLL;
    LinearLayout btnControlReview;
    RelativeLayout bottomPriceLL;
    EditText addReviewTextET;
    AppCompatRatingBar userRatingBr;
    Button postReviewIBtn, cancelReviewIBtn, seeAll, topBuyBtn;
    SingleCourseData singleCourseData;
    RecyclerView reviewRecyclerView, curriculamRecyclerView;
    ReviewAdapter reviewAdapter;
    List<Reviews> reviewsList = new ArrayList<>();
    CardView cvSingleCourseRating, cvEnroll;
    WebView descriptionWebView;
    MyNetworkCall myNetworkCall;
    RelativeLayout rlVideo;
    LinearLayout llAbout;
    TextView tvVideos, tvNotesTest, tvAbout;
    LinearLayout llPrice, curriculumMainLL, curriculumListLL, courseLL, relatedCourseLL, mainParentLL;
    TextView rateCountTV, coursenameTV, learnerTV, ratingTV, instructorstudentsTV, instructorcoursesTV,
            curriculumTextTV, reviewTextTV, profileName, courseCategoryTitle, errorTV, instructorratingTV, instructornameTV,
            instructorDesTV, courseRatingTV, courseRatingCountTV, coursePriceTV, enrollNow, courseDescriptionTV, validityTv;
    Button instrProfBtn, readMoreCourseBtn, seeReviewsBtn, seeAllCurriculum, seeAllFAQ;
    RatingBar courseratingBR, reviewsRatingBr;
    CurriculumFileRecyclerAdapter curriculumFileRecyclerAdapter;
    NonScrollRecyclerView curriculumExpListRV;
    ArrayList<Curriculam> curriculamArrayList;
    ArrayList<Curriculam> videoCurriculamArrayList = new ArrayList<>();
    ClickableSpan readmoreclick, readlessclick;
    Progress mprogress;
    String hidekey = "";
    private String courseId;

    View.OnClickListener onCourseClickListener = v -> {
        Course course = (Course) v.getTag();
        Intent courseList = new Intent(activity, CourseActivity.class);// FRAG_TYPE, Const.SINGLE_COURSE
        courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
        courseList.putExtra(Const.COURSES, course);
        activity.startActivity(courseList);
    };

    public static NewSingleCourse newInstance(Course course, String courseId, String hidekey) {
        NewSingleCourse singleCourse = new NewSingleCourse();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.COURSES, course);
        bundle.putString(Const.PARENT_ID, courseId);
        bundle.putString("hidekey", hidekey);
        singleCourse.setArguments(bundle);
        return singleCourse;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: SingleCourse");
        activity = getActivity();

        fragmentType = "1";
        SharedPreference.getInstance().putString("FRAGMENT_TYPE", fragmentType);
        mprogress = new Progress(activity);
        mprogress.setCancelable(false);
        curriculamArrayList = new ArrayList<>();
        if (getArguments() != null) {
            fragType = getArguments().getString(Const.FRAG_TYPE);
            course = (Course) getArguments().getSerializable(Const.COURSES);
            courseId = getArguments().getString(Const.PARENT_ID);
            hidekey = getArguments().getString("hidekey");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_course_new, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        bindControls();
    }

    private void initViews(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        rlVideo = view.findViewById(R.id.rlVideo);
        llAbout = view.findViewById(R.id.llAbout);
        tvVideos = view.findViewById(R.id.tvVideos);
        tvNotesTest = view.findViewById(R.id.tvNotesTest);
        tvAbout = view.findViewById(R.id.tvAbout);

        learnerTV = view.findViewById(R.id.totalEnrolledCount);
        coursenameTV = view.findViewById(R.id.courseTitle);
        rateCountTV = view.findViewById(R.id.rateCountTV);
        descriptionWebView = view.findViewById(R.id.descriptionWebView);

        curriculamRecyclerView = view.findViewById(R.id.curriculamRecyclerView);

        scroll = view.findViewById(R.id.scroll);
        swipeSingleCourse = view.findViewById(R.id.swipeSingleCourse);
        playIV = view.findViewById(R.id.playIV);
        ratingTV = view.findViewById(R.id.reviewratingTV);
        instructornameTV = view.findViewById(R.id.instructornameTV);
        instructorcoursesTV = view.findViewById(R.id.coursescountTV);
        instructorDesTV = view.findViewById(R.id.instructorDesTV);
        instructorratingTV = view.findViewById(R.id.ratingcountTV);
        instructorstudentsTV = view.findViewById(R.id.studentsCountTV);
        courseDescriptionTV = view.findViewById(R.id.descriptionTV);
        coursePriceTV = view.findViewById(R.id.priceTV);
        enrollNow = view.findViewById(R.id.enrollNow);
        llPrice = view.findViewById(R.id.llPrice);
        courseRatingCountTV = view.findViewById(R.id.ratingcourseTV);
        courseRatingTV = view.findViewById(R.id.ratingTV);
        bannerimageIV = view.findViewById(R.id.bannerimageIV);
        instrProfBtn = view.findViewById(R.id.viewProfileBtn);
        readMoreCourseBtn = view.findViewById(R.id.readMoreBtn);
        seeReviewsBtn = view.findViewById(R.id.seeAllReviewsBtn);
        topBuyBtn = view.findViewById(R.id.topBuyBtn);
        cvEnroll = view.findViewById(R.id.cvEnroll);
        seeAllFAQ = view.findViewById(R.id.seeAllFAQ);
        addReviewTextET = view.findViewById(R.id.writereviewET);
        postReviewIBtn = view.findViewById(R.id.btn_submit);
        cancelReviewIBtn = view.findViewById(R.id.btn_cancel);
        userRatingBr = view.findViewById(R.id.ratingBar);
        userReviewsLL = view.findViewById(R.id.userReviewsLL);
        reviewTextTV = view.findViewById(R.id.reviewTextTV);
        optionIV = view.findViewById(R.id.optionTV);
        profileImage = view.findViewById(R.id.userprofilepicIV);
        userProfilePicIVText = view.findViewById(R.id.userprofilepicIVText);
        profileName = view.findViewById(R.id.profileName);
        instrprofilepicIV = view.findViewById(R.id.profilepicIV);
        profilepicIVText = view.findViewById(R.id.profilepicIVText);
        reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
        btnControlReview = view.findViewById(R.id.btnControlReview);
        bottomPriceLL = view.findViewById(R.id.bottomPriceLL);
        courseratingBR = view.findViewById(R.id.ratingRB);
        reviewsRatingBr = view.findViewById(R.id.reviewsratingRB);
        curriculumListLL = view.findViewById(R.id.curriculumListLL);
        cvSingleCourseRating = view.findViewById(R.id.cvSingleCourseRating);
        curriculumMainLL = view.findViewById(R.id.curriculumLay);
        seeAllCurriculum = view.findViewById(R.id.seeAllCurriculumBtn);
        curriculumTextTV = view.findViewById(R.id.curriculumTextTV);
        courseCategoryTitle = view.findViewById(R.id.tv1);
        seeAll = view.findViewById(R.id.courseCatseeAllBtn);
        courseLL = view.findViewById(R.id.coursesoptionLL);
        relatedCourseLL = view.findViewById(R.id.relatedCourseLL);
        mainParentLL = view.findViewById(R.id.mainParentLL);
        errorTV = view.findViewById(R.id.errorTV);
        validityTv = view.findViewById(R.id.validityTv);
        curriculumExpListRV = view.findViewById(R.id.curriculumExpListLL);
    }

    private void bindControls() {
        curriculumExpListRV.setVisibility(View.VISIBLE);
        curriculumExpListRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        curriculamRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        seeAll.setVisibility(View.GONE);

        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        reviewAdapter = new ReviewAdapter(activity, reviewsList);
        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(25, EqualSpacingItemDecoration.VERTICAL));

        setOnClickListeners(seeReviewsBtn, readMoreCourseBtn, instrProfBtn, seeAllFAQ, optionIV, postReviewIBtn, cancelReviewIBtn, seeAllCurriculum, cvEnroll, tvVideos, tvNotesTest, tvAbout);

        swipeSingleCourse.setOnRefreshListener(this);
        swipeSingleCourse.setColorSchemeResources(R.color.blue, R.color.colorAccent, R.color.colorPrimaryDark);
        ViewCompat.setNestedScrollingEnabled(scroll, false);
    }

    private void setOnClickListeners(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    private void getAllCurriculumData() {
        myNetworkCall.NetworkAPICall(API.API_GET_FILE_LIST_CURRICULUM, true);
    }

    private void setAllDatatoView() {
        if (mainParentLL.getVisibility() == View.GONE)
            mainParentLL.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(singleCourseData.getCover_video())) {
            playIV.setVisibility(View.VISIBLE);
            bannerimageIV.setOnClickListener(this);
        } else {
            playIV.setVisibility(View.GONE);
        }

        coursenameTV.setText(singleCourseData.getTitle().length() > 0 ? Helper.CapitalizeText(singleCourseData.getTitle().trim()) : "");
        learnerTV.setText((singleCourseData.getLearner() + " "
                + ((singleCourseData.getLearner().equals("1") || (singleCourseData.getLearner().equals("0")) ? Const.LEARNER : Const.LEARNERS))));

        rateCountTV.setText(singleCourseData.getRating());

        instructorstudentsTV.setText(singleCourseData.getInstructor_data().getStudents());
        instructorcoursesTV.setText(singleCourseData.getInstructor_data().getCourses());
        instructorratingTV.setText(singleCourseData.getInstructor_data().getRating() + "/5.0");
        instructornameTV.setText(Helper.CapitalizeText(singleCourseData.getInstructor_data().getName()));
        courseRatingTV.setText((singleCourseData.getReview_count() + " "
                + ((singleCourseData.getReview_count().equals("1") || (singleCourseData.getReview_count().equals("0")) ? Const.REVIEW : Const.REVIEWS))));
        ratingTV.setText((singleCourseData.getReview_count() + " "
                + ((singleCourseData.getReview_count().equals("1") || (singleCourseData.getReview_count().equals("0")) ? Const.REVIEW : Const.REVIEWS))));

        if (singleCourseData.getMrp().equals("0")) {
            cvEnroll.setCardBackgroundColor(activity.getResources().getColor(R.color.red));
            llPrice.setVisibility(View.VISIBLE);
            enrollNow.setVisibility(View.GONE);
            coursePriceTV.setText(activity.getResources().getString(R.string.free));
            topBuyBtn.setVisibility(View.GONE);
        } else {
            cvEnroll.setCardBackgroundColor(activity.getResources().getColor(R.color.red));
            llPrice.setVisibility(View.VISIBLE);
            enrollNow.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                if (singleCourseData.getFor_dams().equals(singleCourseData.getMrp())) {
                    coursePriceTV.setText(String.format("%s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp())));
                } else {
                    StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                    coursePriceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp()) + " " +
                            Helper.calculatePriceBasedOnCurrency(singleCourseData.getFor_dams()), TextView.BufferType.SPANNABLE);
                    Spannable spannable = (Spannable) coursePriceTV.getText();
                    spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (singleCourseData.getFor_dams().equalsIgnoreCase("0")) {
                    coursePriceTV.setText(activity.getResources().getString(R.string.free));
                    topBuyBtn.setVisibility(View.GONE);
                } else {
                    topBuyBtn.setVisibility(View.VISIBLE);
                    topBuyBtn.setText(getString(R.string.enroll));
                    topBuyBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                }
            } else {
                if (singleCourseData.getNon_dams().equals(singleCourseData.getMrp())) {
                    coursePriceTV.setText(String.format("%s %s", getCurrencySymbol(),
                            Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp())));
                } else {
                    StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                    coursePriceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp()) + " " +
                            Helper.calculatePriceBasedOnCurrency(singleCourseData.getNon_dams()), TextView.BufferType.SPANNABLE);
                    Spannable spannable = (Spannable) coursePriceTV.getText();
                    spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (singleCourseData.getNon_dams().equalsIgnoreCase("0")) {
                    coursePriceTV.setText(activity.getResources().getString(R.string.free));
                    topBuyBtn.setVisibility(View.GONE);
                } else {
                    topBuyBtn.setVisibility(View.VISIBLE);
                    topBuyBtn.setText(getString(R.string.enroll));
                    topBuyBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                }
            }
        }

        descriptionWebView.loadDataWithBaseURL(null, singleCourseData.getDescription(), "text/html", "UTF-8", null);

        courseratingBR.setRating(Float.parseFloat(singleCourseData.getRating()));
        reviewsRatingBr.setRating(Float.parseFloat(singleCourseData.getRating()));
        courseRatingCountTV.setText(singleCourseData.getRating());

        //Capitalizing the first letter of User's Name
        profileName.setText(Helper.CapitalizeText(SharedPreference.getInstance().getLoggedInUser().getName()));

        // ** To set the Image on Profile ** //
        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getProfile_picture())) {
            profileImage.setVisibility(View.VISIBLE);
            userProfilePicIVText.setVisibility(View.GONE);
            Glide.with(activity)
                    .asBitmap()
                    .load(SharedPreference.getInstance().getLoggedInUser().getProfile_picture())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.default_pic))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                            profileImage.setImageBitmap(result);
                        }
                    });
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


        if (singleCourseData.getDesc_header_image() != null) {
            if (!singleCourseData.getDesc_header_image().equalsIgnoreCase("")) {
                if (!activity.isFinishing())
                    Glide.with(activity.getApplicationContext()).load(singleCourseData.getDesc_header_image()).into(bannerimageIV);
            } else {
                bannerimageIV.setImageResource(R.mipmap.courses_blue);
            }
        }

        // ** To set the Image on Instructor ** //
        if (!TextUtils.isEmpty(singleCourseData.getInstructor_data().getProfile_pic())) {
            instrprofilepicIV.setVisibility(View.VISIBLE);
            profilepicIVText.setVisibility(View.GONE);
            Glide.with(activity)
                    .asBitmap()
                    .load(singleCourseData.getInstructor_data().getProfile_pic())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.default_pic))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                            instrprofilepicIV.setImageBitmap(result);
                        }
                    });
        } else {
            Drawable dr = Helper.GetDrawable(singleCourseData.getInstructor_data().getName(), activity, singleCourseData.getInstructor_data().getId());
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


        if (!TextUtils.isEmpty(singleCourseData.getValidity()) && !singleCourseData.getValidity().equals("0") && singleCourseData.getIs_purchased().equals("1")) {
            validityTv.setVisibility(View.VISIBLE);
            validityTv.setText("Valid Till: " + singleCourseData.getValidity());
        } else {
            validityTv.setVisibility(View.GONE);
        }

        if (singleCourseData.getIs_purchased().equals("1")) {
            userReviewsLL.setVisibility(View.VISIBLE);
            topBuyBtn.setVisibility(View.GONE);
            cvEnroll.setCardBackgroundColor(activity.getResources().getColor(R.color.green_new));
            llPrice.setVisibility(View.GONE);
            enrollNow.setVisibility(View.VISIBLE);
        } else {
            if (singleCourseData.getMrp().equals("0")) {
                topBuyBtn.setVisibility(View.GONE);
            } else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (singleCourseData.getFor_dams().equals("0")) {
                        userReviewsLL.setVisibility(View.VISIBLE);
                        topBuyBtn.setVisibility(View.GONE);
                    } else {
                        topBuyBtn.setVisibility(View.VISIBLE);
                        userReviewsLL.setVisibility(View.GONE);
                        topBuyBtn.setOnClickListener(this);
                    }
                } else {
                    if (singleCourseData.getNon_dams().equals("0")) {
                        userReviewsLL.setVisibility(View.VISIBLE);
                        topBuyBtn.setVisibility(View.GONE);
                    } else {
                        topBuyBtn.setVisibility(View.VISIBLE);
                        userReviewsLL.setVisibility(View.GONE);
                        topBuyBtn.setOnClickListener(this);
                    }
                }
            }
        }

        if (hidekey.equalsIgnoreCase("hide")) {
            cvEnroll.setVisibility(View.GONE);
            topBuyBtn.setVisibility(View.GONE);
            coursePriceTV.setVisibility(View.GONE);
            bottomPriceLL.setVisibility(View.GONE);
        }

        if (singleCourseData.getRelated_course() != null) {
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

        // to set Rating content
        if (singleCourseData.getReviews() != null) {
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

        if (singleCourseData.getReview() != null) {
            addReviewTextET.setVisibility(View.GONE);
            btnControlReview.setVisibility(View.GONE);
            optionIV.setVisibility(View.VISIBLE);
            reviewTextTV.setText(singleCourseData.getReview().getText());
            reviewTextTV.setVisibility(View.VISIBLE);
            userRatingBr.setRating(Float.parseFloat(singleCourseData.getReview().getRating()));
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

    @Override
    public void onResume() {
        super.onResume();
        SharedPreference.getInstance().putString("FRAGMENT_TYPE", fragmentType);

        bannerimageIV.setVisibility(View.VISIBLE);
        if (SharedPreference.getInstance().getBoolean(Const.IS_PAYMENT_DONE)) {
            networkCallForCourseInfoRaw();//NetworkAPICall(API.API_SINGLE_COURSE_INFO_RAW, true);
            SharedPreference.getInstance().putBoolean(Const.IS_PAYMENT_DONE, false);
        } else if (SharedPreference.getInstance().getString(Const.QUIZSTATUS).equals(Const.FINISH)) {
            networkCallForCourseInfoRaw();//NetworkAPICall(API.API_SINGLE_COURSE_INFO_RAW, true);
            SharedPreference.getInstance().putString(Const.QUIZSTATUS, "");
        } else if (SharedPreference.getInstance().getBoolean(Const.REVIEWS)) {
            networkCallForCourseInfoRaw();//NetworkAPICall(API.API_SINGLE_COURSE_INFO_RAW, true);
            SharedPreference.getInstance().putBoolean(Const.REVIEWS, false);
        } else {
            networkCallForCourseInfoRaw();
        }
    }

    private void networkCallForCourseInfoRaw() {
        myNetworkCall.NetworkAPICall(API.API_GET_SINGLE_COURSE_INFO_RAW_V2, true);
        /*if (activity.isFinishing()) return;

        mprogress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Log.e(TAG, "course_type" + course.getCourse_type() + "is_combo" + course.getIs_combo() + "course_id" + course.getId() + "parent_id" + courseId);
        Call<JsonObject> response = apiInterface.getSingleCourseInfoRaw(SharedPreference.getInstance().getLoggedInUser().getId(),
                course.getId(), course.getCourse_type(), course.getIs_combo(), courseId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mprogress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_SINGLE_COURSE_INFO_RAW);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Helper.printError(TAG, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_SINGLE_COURSE_INFO_RAW, activity, 1, 1);
            }
        });*/
    }

    private void networkCallForAddReviewCourse() {
        mprogress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);

        Call<JsonObject> response = apiInterface.addReviewCourse(SharedPreference.getInstance().getLoggedInUser().getId(),
                singleCourseData.getId(), String.valueOf(userRatingBr.getRating()), addReviewTextET.getText().toString());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mprogress.dismiss();
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
                } else {
                    try {
                        Log.e(TAG, "onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
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

    private void networkCallForDeleteReviews() {
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
                mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_DELETE_USER_COURSE_REVIEWS, activity, 1, 1);
            }
        });
    }

    private void networkCallForEditCourseReview() {
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
                mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_EDIT_USER_COURSE_REVIEWS, activity, 1, 1);
            }
        });
    }

    private void networkCallFreeCourseTrans() {
        mprogress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        // for free course transaction except user_id and course_id everything will be given as 0 in api url
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
                        mprogress.dismiss();

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
                mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_MAKE_FREE_COURSE_TRANSACTION, activity, 1, 1);
            }
        });
    }

    public void retryApis(String apiType) {
        if (API.API_MAKE_FREE_COURSE_TRANSACTION.equals(apiType)) {
            networkCallFreeCourseTrans();
        } else if (API.API_SINGLE_COURSE_INFO_RAW.equals(apiType)) {
            networkCallForCourseInfoRaw();
        } else if (API.API_ADD_REVIEW_COURSE.equals(apiType)) {
            networkCallForAddReviewCourse();
        } else if (API.API_DELETE_USER_COURSE_REVIEWS.equals(apiType)) {
            networkCallForDeleteReviews();
        } else if (API.API_EDIT_USER_COURSE_REVIEWS.equals(apiType)) {
            networkCallForEditCourseReview();
        }
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        if (API.API_GET_SINGLE_COURSE_INFO_RAW_V2.equals(apiType)) {
            params.put(Const.COURSE_ID, course.getId());
            params.put(Const.COURSE_TYPE, course.getCourse_type());
            params.put(Const.IS_COMBO, course.getIs_combo());
            params.put(Const.PARENT_ID, courseId);
        } else if (API.API_GET_FILE_LIST_CURRICULUM.equals(apiType)) {
            params.put(Const.COURSE_ID, singleCourseData.getId());
        }
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        Gson gson = new Gson();
        if (API.API_GET_SINGLE_COURSE_INFO_RAW_V2.equals(apiType)) {
            if (errorTV.getVisibility() == View.VISIBLE)
                errorTV.setVisibility(View.GONE);
            if (swipeSingleCourse.isRefreshing())
                swipeSingleCourse.setRefreshing(false);
            JSONObject data = GenericUtils.getJsonObject(jsonObject);
            singleCourseData = gson.fromJson(data.toString(), SingleCourseData.class);
            getAllCurriculumData();
            Constants.COURSEID = singleCourseData.getId();
            if (singleCourseData.getRating() != null && !singleCourseData.getRating().equalsIgnoreCase(""))
                Constants.RATING = Float.parseFloat(singleCourseData.getRating());
            if (isAdded())

                setAllDatatoView();
            refreshStatus = false;

            if (activity != null)
                ((CourseActivity) activity).setToolbarTitle(!TextUtils.isEmpty(course.getTitle()) ? course.getTitle() : singleCourseData.getTitle());
        } else if (API.API_GET_FILE_LIST_CURRICULUM.equals(apiType)) {
            JSONArray jsonArray = jsonObject.optJSONArray(Const.DATA);
            if (jsonArray != null && jsonArray.length() > 0) {
//  setting the curriculum to the view if it is coming from back
                if (!singleCourseData.getCurriculam().getTitle().equals("")) {
                    if (curriculumListLL.getChildCount() > 0)
                        curriculumListLL.removeAllViews();
                    if (singleCourseData.getIs_purchased().equals("1") && singleCourseData.getIs_locked().equals("1"))
                        CourseLockedManager.addCourseLockStatus(course.getId(), String.valueOf(0), activity);

                    if (!curriculamArrayList.isEmpty()) curriculamArrayList.clear();

                    // setting the data to recyclerView

            /*for (int i = 0; i < singleCourseData.getCurriculam().getFile_meta().size(); i++) {
                if (singleCourseData.getCurriculam().getFile_meta().get(i).getFile().equals("video")) {
                    curriculamArrayList.add(singleCourseData.getCurriculam());
                }
            }*/

                    llAbout.setVisibility(View.GONE);
                    rlVideo.setVisibility(View.VISIBLE);

                    curriculamArrayList.add(singleCourseData.getCurriculam());
                    curriculumFileRecyclerAdapter = new CurriculumFileRecyclerAdapter(singleCourseData, activity, curriculamArrayList);
                    curriculamRecyclerView.setAdapter(curriculumFileRecyclerAdapter);

//            curriculumExpListRV.setAdapter(curriculumFileRecyclerAdapter);
                } else {
                    curriculumMainLL.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        if (API.API_SINGLE_COURSE_INFO_RAW.equals(apiType)) {
            if (swipeSingleCourse.isRefreshing()) {
                swipeSingleCourse.setRefreshing(false);
                refreshStatus = false;
            }

            if (Helper.getStorageInstance(activity).getRecordObject(String.valueOf(course.getId())) != null) {
                singleCourseData = (SingleCourseData) Helper.getStorageInstance(activity).getRecordObject(String.valueOf(course.getId()));
                if (isAdded())
                    setAllDatatoView();

            } else {
                mainParentLL.setVisibility(View.GONE);
                errorTV.setVisibility(View.VISIBLE);
                errorTV.setText(jsonString);

                if (jsonString.equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
                    Helper.showErrorLayoutForNoNav(apiType, activity, 1, 1);

                if (jsonString.contains(getString(R.string.something_went_wrong_string)))
                    Helper.showErrorLayoutForNoNav(apiType, activity, 1, 2);
            }
        } else {
            GenericUtils.showToast(activity, jsonString);
        }
    }


    public void deleteReviews() {
        postReviewIBtn.setTag("0");
        networkCallForDeleteReviews();//NetworkAPICall(API.API_DELETE_USER_COURSE_REVIEWS, true);
    }

    public void editReviews() {
        addReviewTextET.setVisibility(View.VISIBLE);
        btnControlReview.setVisibility(View.VISIBLE);
        optionIV.setVisibility(View.INVISIBLE);
        reviewTextTV.setVisibility(View.GONE);
        postReviewIBtn.setText(getString(R.string.submit));
        addReviewTextET.setText(singleCourseData.getReview().getText());
        userRatingBr.setRating(Float.parseFloat(singleCourseData.getReview().getRating()));
        userRatingBr.setIsIndicator(false);
    }

    public LinearLayout initCourseHorView(Course course) {
        TextView courseTV, priceTV, learnerTV, ratingTV;
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
        if (course.getMrp().equals("0")) {
            priceTV.setText(activity.getResources().getString(R.string.free));
        } else {
            if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
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
        }

        learnerTV.setText((course.getLearner() + " " + ((course.getLearner().equals("1")) ? Const.LEARNER : Const.LEARNERS)));
        ratingTV.setText(course.getRating());
        ratingRB.setRating(Float.parseFloat(course.getRating()));
        Glide.with(activity)
                .asBitmap()
                .load(course.getCover_image())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.courses_blue))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                        imageIV.setImageBitmap(result);
                    }
                });
        view.setTag(course);
        view.setOnClickListener(onCourseClickListener);
        return view;

    }

    public void showPopMenu(final View v) {
        PopupMenu popup = new PopupMenu(activity, v);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.editIM:
                    editReviews();
                    return true;

                case R.id.deleteIM:
                    View v1 = Helper.newCustomDialog(activity, false, activity.getString(R.string.app_name), activity.getString(R.string.deleteReviews));
                    Button btnCancel, btnSubmit;

                    btnCancel = v1.findViewById(R.id.btn_cancel);
                    btnSubmit = v1.findViewById(R.id.btn_submit);

                    btnCancel.setText(getString(R.string.cancel));
                    btnSubmit.setText(getString(R.string.delete));

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
        Menu menu = popup.getMenu();

        popup.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvVideos:
                llAbout.setVisibility(View.GONE);
                rlVideo.setVisibility(View.VISIBLE);
                setTabBackground(tvVideos, tvNotesTest, tvAbout);
                break;
            case R.id.tvNotesTest:
                setTabBackground(tvNotesTest, tvVideos, tvAbout);
                break;
            case R.id.tvAbout:
                llAbout.setVisibility(View.VISIBLE);
                rlVideo.setVisibility(View.GONE);
                setTabBackground(tvAbout, tvNotesTest, tvVideos);
                break;
            case R.id.seeAllFAQ:
                Intent faqIntent = new Intent(activity, FAQActivity.class);
                faqIntent.putExtra(Const.COURSES, course);
                faqIntent.putExtra(Constants.Extras.TYPE, Const.FAQ);
                activity.startActivity(faqIntent);
                break;
            case R.id.bannerimageIV:
                Helper.GoToVideoActivity(activity, singleCourseData.getCover_video(), Const.VIDEO_STREAM, "", "");
                break;
            case R.id.btn_submit:
                if (userRatingBr.getRating() == 0.0) {
                    GenericUtils.showToast(activity, activity.getString(R.string.tap_a_star_to_give_your_rating));
                }/* else if (TextUtils.isEmpty(addReviewTextET.getText().toString().trim())) {
                    GenericUtils.showToast(activity, activity.getString(R.string.enter_text_post_review));
                } */ else {
                    if (postReviewIBtn.getTag().equals("0")) {
                        networkCallForAddReviewCourse();//NetworkAPICall(API.API_ADD_REVIEW_COURSE, true);
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
                    addReviewTextET.setVisibility(View.GONE);
                    btnControlReview.setVisibility(View.GONE);
                    optionIV.setVisibility(View.VISIBLE);
                    reviewTextTV.setText(singleCourseData.getReview().getText());
                    reviewTextTV.setVisibility(View.VISIBLE);
                    userRatingBr.setRating(Float.parseFloat(singleCourseData.getReview().getRating()));
                    userRatingBr.setIsIndicator(true);
                }
                break;

            case R.id.optionTV:
                showPopMenu(optionIV);
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
                Intent seeAllcurriculum = new Intent(activity, NewCurriculumActivity.class);//FRAG_TYPE, Const.CURRICULUM SingleCourse
                seeAllcurriculum.putExtra(Const.COURSE_DESC, singleCourseData);
                startActivity(seeAllcurriculum);

                break;

            case R.id.cvEnroll:
            case R.id.buyBtn:
            case R.id.topBuyBtn:
                if (singleCourseData.getIs_purchased().equals("0"))
                    Helper.goToCourseInvoiceScreen(activity, singleCourseData);
                break;

        }
    }

    private void setTabBackground(TextView textView1, TextView textView2, TextView textView3) {
        textView1.setTextColor(activity.getResources().getColor(R.color.black));
        textView2.setTextColor(activity.getResources().getColor(R.color.white));
        textView3.setTextColor(activity.getResources().getColor(R.color.white));
        textView1.setBackground(activity.getResources().getDrawable(R.drawable.bg_extra_round_corner_fill_white));
        textView2.setBackground(activity.getResources().getDrawable(R.drawable.bg_btn));
        textView3.setBackground(activity.getResources().getDrawable(R.drawable.bg_btn));
    }

    @Override
    public void onRefresh() {
        refreshStatus = true;
        networkCallForCourseInfoRaw();//NetworkAPICall(API.API_SINGLE_COURSE_INFO_RAW, true);
    }

}
