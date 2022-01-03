package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.FAQActivity;
import com.emedicoz.app.courses.fragment.SingleStudy;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Cards;
import com.emedicoz.app.modelo.courses.ExamPrepItem;
import com.emedicoz.app.modelo.courses.Reviews;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.liveclass.courses.DescCourseSchedule;
import com.emedicoz.app.modelo.liveclass.courses.DescriptionFAQ;
import com.emedicoz.app.modelo.liveclass.courses.DescriptionResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SingleCourseApiInterface;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleOverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SingleOverviewAdapter";
    private String selectedTab;
    public String contentType;
    Activity activity;
    DescriptionResponse descriptionResponse;
    ArrayList<Cards> tiles = new ArrayList<>();
    ExamPrepItem examPrepItem;
    onButtonClicked buttonClicked;
    ArrayList<View> viewArrayList = new ArrayList<>();
    private String imageFooter;
    ClickableSpan readMoreClick, readLessClick;
    List<Reviews> reviewsArrayList = new ArrayList<>();
    SingleStudy singleStudy;

    public SingleOverviewAdapter(Activity activity, DescriptionResponse descriptionResponse, String contentType, onButtonClicked buttonClicked, String selectedTab, SingleStudy singleStudy) {
        this.descriptionResponse = descriptionResponse;
        this.activity = activity;
        this.contentType = contentType;
        this.buttonClicked = buttonClicked;
        this.selectedTab = selectedTab;
        this.singleStudy = singleStudy;
    }

    public void udateData(ExamPrepItem examPrepItem) {
        this.examPrepItem = examPrepItem;
        this.viewArrayList.clear();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(activity).inflate(R.layout.exam_prep_header, null);
            return new SingleStudyHeaderHolder(view);
        } else {
            view = LayoutInflater.from(activity).inflate(R.layout.item_overview, null);
            return new StudyOverviewViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            ((SingleOverviewAdapter.SingleStudyHeaderHolder) holder).setData(descriptionResponse, selectedTab);
        } else if (getItemViewType(position) == 1) {
            ((SingleOverviewAdapter.StudyOverviewViewHolder) holder).setData(descriptionResponse);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 0;  // HEADER
        else if (contentType.equalsIgnoreCase("Book")) return 2;
        else return 1;  // ANY THING ELSE
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public void updateimageFooter(String imagLinkFooter) {
        imageFooter = imagLinkFooter;
        Log.e("updateimageFooter: ", imageFooter);
    }

    public class SingleStudyHeaderHolder extends RecyclerView.ViewHolder implements OnClickListener {
        LinearLayout coursePanelLL;
        TextView leftTextTV, rightTextTV, titleTV;
        TextView videoLayout, notesTestLayout, discriptionLayout, coursesLayout;
        ImageView courseIV;
        RelativeLayout imageRL;

        public SingleStudyHeaderHolder(View itemView) {
            super(itemView);

            coursePanelLL = itemView.findViewById(R.id.coursePanelLL);
            imageRL = itemView.findViewById(R.id.imageRL);
            leftTextTV = itemView.findViewById(R.id.videosCount);
            rightTextTV = itemView.findViewById(R.id.testCount);
            titleTV = itemView.findViewById(R.id.upperTitle);
            courseIV = itemView.findViewById(R.id.courseImageIcon);
            videoLayout = itemView.findViewById(R.id.live_class_videos);
            notesTestLayout = itemView.findViewById(R.id.notes_test);
            discriptionLayout = itemView.findViewById(R.id.discription_layout);
            coursesLayout = itemView.findViewById(R.id.coursesLayout);
        }

        public void setData(DescriptionResponse descriptionResponse, String selectedTab) {
            titleTV.setText(descriptionResponse.getData().getBasic().getTitle());
            updateTileUI(selectedTab);
            if (!TextUtils.isEmpty(imageFooter)) {
                Glide.with(activity)
                        .asBitmap()
                        .load(imageFooter)
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.courses_blue))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                courseIV.setImageBitmap(result);
                            }
                        });
            } else {
                courseIV.setImageResource(R.mipmap.courses_blue);
            }

            // coursePanelLL.removeAllViews();
            tiles = new ArrayList<>();
            Cards tile = new Cards();
            tile.setTile_name("Overview");
            tile.setType("overview");
            tiles.add(tile);
            tiles.addAll(descriptionResponse.getData().getTiles());

            for (Cards cards : tiles) {
                if (cards.getType().equalsIgnoreCase("video")) {
                    videoLayout.setVisibility(View.VISIBLE);
                    videoLayout.setOnClickListener(this);
                } else if (cards.getType().equalsIgnoreCase(Constants.TestType.TEST) || cards.getType().equalsIgnoreCase("pdf") || cards.getType().equalsIgnoreCase("epub")) {
                    notesTestLayout.setVisibility(View.VISIBLE);
                    notesTestLayout.setOnClickListener(this);
                } else if (cards.getType().equalsIgnoreCase("course")) {
                    coursesLayout.setVisibility(View.VISIBLE);
                    coursesLayout.setOnClickListener(this);
                } else if (cards.getType().equalsIgnoreCase("overview")) {
                    discriptionLayout.setVisibility(View.VISIBLE);
                    discriptionLayout.setOnClickListener(this);
                }
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.live_class_videos:
                    buttonClicked.onTitleClicked(Const.VIDEO);
                    break;
                case R.id.notes_test:
                    buttonClicked.onTitleClicked(Const.TEST_EPUB_PDF);
                    break;
                case R.id.coursesLayout:
                    buttonClicked.onTitleClicked(Const.COURSE);
                    break;
                case R.id.discription_layout:
                    buttonClicked.onTitleClicked("overview");
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + v.getId());
            }
        }

        private void updateTileUI(String selectedTab) {
            if (GenericUtils.isEmpty(selectedTab)) return;
            switch (selectedTab) {
                case Const.VIDEO:
                    videoLayout.setTextColor(ContextCompat.getColor(activity, R.color.black));
                    videoLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_grid_list));
                    notesTestLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    notesTestLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    discriptionLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    discriptionLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    coursesLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    coursesLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    break;
                case Const.TEST_EPUB_PDF:
                    notesTestLayout.setTextColor(ContextCompat.getColor(activity, R.color.black));
                    notesTestLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_grid_list));
                    videoLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    videoLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    discriptionLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    discriptionLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    coursesLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    coursesLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    break;
                case Const.COURSE:
                    coursesLayout.setTextColor(ContextCompat.getColor(activity, R.color.black));
                    coursesLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_grid_list));
                    videoLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    videoLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    discriptionLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    discriptionLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    notesTestLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    notesTestLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    break;
                case "overview":
                    discriptionLayout.setTextColor(ContextCompat.getColor(activity, R.color.black));
                    discriptionLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_grid_list));
                    videoLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    videoLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    notesTestLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    notesTestLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    coursesLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    coursesLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + selectedTab);
            }
        }
    }

    public class StudyOverviewViewHolder extends RecyclerView.ViewHolder {

        SingleCourseData singleCourseData;
        TextView courseTitle, countTV;
        ImageView profilePicIV, profilePicIVText;
        TextView tutorName, tutorEmail, reviewratingTV, ratingcourseTV;
        RatingBar reviewsratingRB;
        TextView courseDescriptionTV, tvCourseDescription;
        WebView courseDescriptionWV;
        TextView totalEnrolledCount, rateCountTV;
        Button seeAllFAQ;
        Button seeAllReviewsBtn;
        TextView enrollNow;
        TextView headerTxtCourse, profileName, reviewTextTV;
        RelativeLayout userReviewsLL;
        CardView cvReviewRating, cvEnroll;
        RecyclerView faqRecyclerView, reviewRecyclerView, courseScheduleRecyclerView;
        ImageView profileImage, userProfilePicIVText;
        AppCompatRatingBar userRatingBr;
        EditText addReviewTextET;
        Progress mprogress;
        LinearLayout btnControlReview;
        ImageView optionIV;
        Button postReviewIBtn, cancelReviewIBtn;
        ReviewAdapter reviewAdapter;
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_submit:
                        if (userRatingBr.getRating() == 0.0) {
                            GenericUtils.showToast(activity, activity.getString(R.string.tap_a_star_to_give_your_rating));
                        }/* else if (TextUtils.isEmpty(addReviewTextET.getText().toString().trim())) {
                            GenericUtils.showToast(activity, activity.getString(R.string.enter_text_post_review));
                        }*/ else {
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
                    default:
                        throw new IllegalStateException("Unexpected value: " + view.getId());
                }
            }
        };

        public StudyOverviewViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            //    courseDescription = itemView.findViewById(R.id.courseDescription);
            countTV = itemView.findViewById(R.id.countTV);
            profilePicIV = itemView.findViewById(R.id.profilepicIV);
            profilePicIVText = itemView.findViewById(R.id.profilepicIVText);
            tutorName = itemView.findViewById(R.id.tutorName);
            tutorEmail = itemView.findViewById(R.id.tutorEmail);
            faqRecyclerView = itemView.findViewById(R.id.faqRecyclerView);
            reviewRecyclerView = itemView.findViewById(R.id.reviewRecyclerView);
            reviewRecyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
            reviewratingTV = itemView.findViewById(R.id.reviewratingTV);
            ratingcourseTV = itemView.findViewById(R.id.ratingcourseTV);
            reviewsratingRB = itemView.findViewById(R.id.reviewsratingRB);
            totalEnrolledCount = itemView.findViewById(R.id.totalEnrolledCount);
            rateCountTV = itemView.findViewById(R.id.rateCountTV);
            courseDescriptionTV = itemView.findViewById(R.id.descriptionTV);
            courseDescriptionWV = itemView.findViewById(R.id.descriptionWebView);
            courseDescriptionWV.getSettings().setJavaScriptEnabled(true);

            tvCourseDescription = itemView.findViewById(R.id.tvCourseDescription);
            seeAllFAQ = itemView.findViewById(R.id.seeAllFAQ);
            seeAllReviewsBtn = itemView.findViewById(R.id.seeAllReviewsBtn);
            cvReviewRating = itemView.findViewById(R.id.cvReviewRating);
            courseScheduleRecyclerView = itemView.findViewById(R.id.courseScheduleRecyclerView);
            cvEnroll = itemView.findViewById(R.id.cvEnroll);
            enrollNow = itemView.findViewById(R.id.enrollNow);
            headerTxtCourse = itemView.findViewById(R.id.header_txt_course);
            userReviewsLL = itemView.findViewById(R.id.userReviewsLL);

            profileImage = itemView.findViewById(R.id.userprofilepicIV);
            userProfilePicIVText = itemView.findViewById(R.id.userprofilepicIVText);
            userRatingBr = itemView.findViewById(R.id.ratingBar);
            addReviewTextET = itemView.findViewById(R.id.writereviewET);
            reviewTextTV = itemView.findViewById(R.id.reviewTextTV);
            profileName = itemView.findViewById(R.id.profileName);
            postReviewIBtn = itemView.findViewById(R.id.btn_submit);
            cancelReviewIBtn = itemView.findViewById(R.id.btn_cancel);
            btnControlReview = itemView.findViewById(R.id.btnControlReview);
            optionIV = itemView.findViewById(R.id.optionTV);

            mprogress = new Progress(activity);
            mprogress.setCancelable(false);

            reviewAdapter = new ReviewAdapter(activity, reviewsArrayList);
            reviewRecyclerView.setAdapter(reviewAdapter);
            reviewRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(25, EqualSpacingItemDecoration.VERTICAL));

            optionIV.setOnClickListener(onClickListener);
            cancelReviewIBtn.setOnClickListener(onClickListener);
            postReviewIBtn.setOnClickListener(onClickListener);

            singleCourseData = getData(descriptionResponse);

            networkCallForCourseInfoRaw();

            if (descriptionResponse.getData().getBasic().getIs_renew().equals("1")) {
                launchPurchaseFlow(true);
            } else if (descriptionResponse.getData().getIsPurchased().equalsIgnoreCase("0")) {
                launchPurchaseFlow(false);

            } else if (descriptionResponse.getData().getIsPurchased().equalsIgnoreCase("1")) {
                userReviewsLL.setVisibility(View.VISIBLE);
                cvEnroll.setVisibility(View.GONE);
            }

            seeAllFAQ.setOnClickListener(v -> {
                Intent intent = new Intent(activity, FAQActivity.class);
                intent.putExtra(Constants.Extras.FAQ, (Serializable) descriptionResponse.getData().getFaq());
                intent.putExtra(Constants.Extras.TYPE, Const.FAQ);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            });

            seeAllReviewsBtn.setOnClickListener(v -> {
                Intent intent = new Intent(activity, FAQActivity.class);
                intent.putExtra(Constants.Extras.REVIEW, (Serializable) descriptionResponse.getData().getCourseReview());
                intent.putExtra(Const.COURSE_ID, descriptionResponse.getData().getBasic().getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            });

            setProfileImage();
            showRatingContent(singleCourseData);
        }

        private void launchPurchaseFlow(boolean isRenew) {
           /* if (descriptionResponse.getData().getBasic().getIs_renew().equals("1")) {
                cvEnroll.setVisibility(View.VISIBLE);
                userReviewsLL.setVisibility(View.VISIBLE);
                enrollNow.setText(R.string.renew);
                enrollNow.setBackgroundColor(ContextCompat.getColor(activity, R.color.rewards_color));
                enrollNow.setOnClickListener(v -> Helper.goToCourseInvoiceScreen(activity, singleCourseData));

            } else */
            if (descriptionResponse.getData().getBasic().getMrp().equalsIgnoreCase("0")) {
                cvEnroll.setVisibility(View.GONE);
                userReviewsLL.setVisibility(View.VISIBLE);

            } else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (!descriptionResponse.getData().getBasic().getForDams().equalsIgnoreCase("0")) {
                        userReviewsLL.setVisibility(View.GONE);
                        cvEnroll.setVisibility(View.VISIBLE);
                        if (isRenew) {
                            enrollNow.setBackgroundColor(ContextCompat.getColor(activity, R.color.rewards_color));
                            enrollNow.setText(String.format("%s (%s %s)", activity.getString(R.string.renew), Helper.getCurrencySymbol(),
                                    descriptionResponse.getData().getBasic().getForDams()));
                        } else {
                            enrollNow.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                            enrollNow.setText(String.format("%s (%s %s)", activity.getString(R.string.enroll), Helper.getCurrencySymbol(),
                                    descriptionResponse.getData().getBasic().getForDams()));
                        }
                        enrollNow.setOnClickListener(v -> Helper.goToCourseInvoiceScreen(activity, singleCourseData));
                    } else {
                        cvEnroll.setVisibility(View.GONE);
                        userReviewsLL.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (!descriptionResponse.getData().getBasic().getNonDams().equalsIgnoreCase("0")) {
                        cvEnroll.setVisibility(View.VISIBLE);
                        userReviewsLL.setVisibility(View.GONE);
                        if (isRenew) {
                            enrollNow.setBackgroundColor(ContextCompat.getColor(activity, R.color.rewards_color));
                            enrollNow.setText(String.format("%s (%s %s)", activity.getString(R.string.renew), Helper.getCurrencySymbol(),
                                    descriptionResponse.getData().getBasic().getNonDams()));
                        } else {
                            enrollNow.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                            enrollNow.setText(String.format("%s (%s %s)", activity.getString(R.string.enroll), Helper.getCurrencySymbol(),
                                    descriptionResponse.getData().getBasic().getNonDams()));
                        }
                        enrollNow.setOnClickListener(v -> Helper.goToCourseInvoiceScreen(activity, singleCourseData));

                    } else {
                        cvEnroll.setVisibility(View.GONE);
                        userReviewsLL.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        private void setProfileImage() {
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
                    Reviews descCourseReview = new Reviews();
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

        private void networkCallForCourseInfoRaw() {
            if (activity.isFinishing()) return;
            if (!Helper.isConnected(activity)) {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
            mprogress.show();
            SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
            Log.e(TAG, "course_type" + singleCourseData.getCourse_type() + "is_combo" + singleCourseData.getIs_combo() + "course_id" + singleCourseData.getId());
            Call<JsonObject> response = apiInterface.getSingleCourseInfoRaw(SharedPreference.getInstance().getLoggedInUser().getId(),
                    singleCourseData.getId(),
                    singleCourseData.getCourse_type(),
                    singleCourseData.getIs_combo(),
                    "courseId");
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    mprogress.dismiss();
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;
                        Gson gson = new Gson();

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                                JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                                singleCourseData = gson.fromJson(data.toString(), SingleCourseData.class);
                                Constants.COURSEID = singleCourseData.getId();
                                singleCourseData.setIs_subscription(descriptionResponse.getData().getBasic().getIs_subscription()); // nimesh
                                if (singleStudy.singleCourseData != null)
                                    singleStudy.singleCourseData.setInstallment(singleCourseData.getInstallment());
                                if (singleCourseData.getRating() != null && !singleCourseData.getRating().equalsIgnoreCase(""))
                                    Constants.RATING = Float.parseFloat(singleCourseData.getRating());

                                if (singleCourseData.getReviews() != null) {
                                    Log.e(TAG, "onResponse: getReviews size = " + singleCourseData.getReviews().length);
                                } else {
                                    Log.e(TAG, "onResponse: getReviews size = null");
                                }

                                if (singleCourseData.getRating() != null) {
                                    Log.e(TAG, "onResponse: getRating = " + singleCourseData.getRating());
                                } else {
                                    Log.e(TAG, "onResponse: getRating = null");
                                }

                                if (singleCourseData.getReview() != null) {
                                    Log.e(TAG, "onResponse: getReview_rating = " + singleCourseData.getReview().getRating());
                                    Log.e(TAG, "onResponse: getReview_text = " + singleCourseData.getReview().getText());
                                } else {
                                    Log.e(TAG, "onResponse: getReview_rating = null");
                                    Log.e(TAG, "onResponse: getReview_text = null");
                                }

                                //Capitalizing the first letter of User's Name
                                profileName.setText(Helper.CapitalizeText(SharedPreference.getInstance().getLoggedInUser().getName()));
                                showRatingContent(singleCourseData);

                                if (singleCourseData.getReviews() != null) {
                                    cvReviewRating.setVisibility(View.VISIBLE);
                                    setRatingList(singleCourseData);
                                } else {
                                    cvReviewRating.setVisibility(View.GONE);
                                }
                            } else {
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mprogress.dismiss();
                    Helper.showErrorLayoutForNoNav(API.API_SINGLE_COURSE_INFO_RAW, activity, 1, 1);
                }
            });
        }

        private void networkCallForAddreviewCourse() {
            if (!Helper.isConnected(activity)) {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
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
                                networkCallForCourseInfoRaw();
                            } else {
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mprogress.dismiss();
                    Helper.showErrorLayoutForNoNav(API.API_ADD_REVIEW_COURSE, activity, 1, 1);
                }
            });
        }

        private void networkCallForDeleteReviews() {
            if (!Helper.isConnected(activity)) {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
            mprogress.show();
            SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
            Call<JsonObject> response = apiInterface.deleteUserCourseReview(SharedPreference.getInstance().getLoggedInUser().getId(),
                    singleCourseData.getId());
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    mprogress.dismiss();
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());

                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                networkCallForCourseInfoRaw();
                            } else {
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mprogress.dismiss();
                    Helper.showErrorLayoutForNoNav(API.API_DELETE_USER_COURSE_REVIEWS, activity, 1, 1);
                }
            });
        }

        private void networkCallForEditCourseReview() {
            if (!Helper.isConnected(activity)) {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
            mprogress.show();
            SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
            Call<JsonObject> response = apiInterface.editUserCourseReviews(SharedPreference.getInstance().getLoggedInUser().getId()
                    , singleCourseData.getId(), String.valueOf(userRatingBr.getRating()), addReviewTextET.getText().toString());
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    mprogress.dismiss();
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());

                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                networkCallForCourseInfoRaw();
                            } else {
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mprogress.dismiss();
                    Helper.showErrorLayoutForNoNav(API.API_EDIT_USER_COURSE_REVIEWS, activity, 1, 1);
                }
            });
        }

        public void retryApis(String apiType) {
            switch (apiType) {
                case API.API_ADD_REVIEW_COURSE:
                    networkCallForAddreviewCourse();
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

        public void showPopMenu(final View v) {
            PopupMenu popup = new PopupMenu(activity, v);

            popup.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.editIM:
                        editReviews();
                        return true;
                    case R.id.deleteIM:
                        View v1 = Helper.newCustomDialog(activity, false, activity.getString(R.string.app_name), activity.getString(R.string.deleteReviews));
                        Button btnCancel, btnSubmit;

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

        public SingleCourseData getData(DescriptionResponse descriptionResponse) {
            SingleCourseData singleCourseData = new SingleCourseData();
            singleCourseData.setCourse_type(descriptionResponse.getData().getBasic().getCourseType());
            singleCourseData.setFor_dams(descriptionResponse.getData().getBasic().getForDams());
            singleCourseData.setNon_dams(descriptionResponse.getData().getBasic().getNonDams());
            singleCourseData.setMrp(descriptionResponse.getData().getBasic().getMrp());
            singleCourseData.setId(descriptionResponse.getData().getBasic().getId());
            singleCourseData.setCover_image(descriptionResponse.getData().getBasic().getCoverImage());
            singleCourseData.setTitle(descriptionResponse.getData().getBasic().getTitle());
            singleCourseData.setLearner(descriptionResponse.getData().getBasic().getLearner());
            singleCourseData.setRating(descriptionResponse.getData().getBasic().getRating());
            singleCourseData.setGst_include(descriptionResponse.getData().getBasic().getGstInclude());

            singleCourseData.setGst(descriptionResponse.getData().getBasic().getGst());
            singleCourseData.setPoints_conversion_rate(descriptionResponse.getData().getBasic().getPointsConversionRate());

            singleCourseData.setIs_subscription(descriptionResponse.getData().getBasic().getIs_subscription());
            singleCourseData.setIs_instalment(descriptionResponse.getData().getBasic().getIs_instalment());
            Log.e(TAG, "getData: " + descriptionResponse.getData().getBasic().getChild_courses());
            singleCourseData.setChild_courses(descriptionResponse.getData().getBasic().getChild_courses());

            return singleCourseData;
        }

        public void setData(DescriptionResponse descriptionResponse) {

            courseTitle.setText(Helper.CapitalizeText(descriptionResponse.getData().getBasic().getTitle()));

            List<DescCourseSchedule> descCourseSchedules = descriptionResponse.getData().getBasic().getCourseSchedule();
            if (GenericUtils.isListEmpty(descCourseSchedules)) {
                headerTxtCourse.setVisibility(View.GONE);
                courseDescriptionTV.setVisibility(View.GONE);
                tvCourseDescription.setVisibility(View.VISIBLE); // nimesh
                courseDescriptionWV.setVisibility(View.VISIBLE); // nimesh
                courseDescriptionWV.loadDataWithBaseURL(null, descriptionResponse.getData().getBasic().getDescription(), "text/html", "UTF-8", null);
            } else {
                headerTxtCourse.setVisibility(View.VISIBLE);
                courseDescriptionTV.setVisibility(View.VISIBLE);
                courseDescriptionWV.setVisibility(View.GONE); // nimesh
                tvCourseDescription.setVisibility(View.GONE); // nimesh

                String des = descriptionResponse.getData().getBasic().getDescription();
                Log.e(TAG, "setData: " + descriptionResponse.getData().getBasic().getDescription());
                if (des.length() > 1200) {
                    des = des.substring(0, 1200) + "...";
                    courseDescriptionTV.setText(String.format("%s %s", HtmlCompat.fromHtml(des, HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Read More</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                    String finalDes = des;
                    readMoreClick = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {
                            courseDescriptionTV.setText(String.format("%s %s", HtmlCompat.fromHtml(descriptionResponse.getData().getBasic().getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Read Less</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                            Helper.makeLinks(courseDescriptionTV, "Read Less", readLessClick);
                        }
                    };

                    readLessClick = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {
                            courseDescriptionTV.setText(String.format("%s %s", HtmlCompat.fromHtml(finalDes, HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Read More</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                            Helper.makeLinks(courseDescriptionTV, "Read More", readMoreClick);
                        }
                    };
                    Helper.makeLinks(courseDescriptionTV, "Read More", readMoreClick);
                } else {
                    courseDescriptionTV.setText(HtmlCompat.fromHtml(des, HtmlCompat.FROM_HTML_MODE_LEGACY));
                }

                CourseScheduleAdapter courseScheduleAdapter = new CourseScheduleAdapter(activity, descriptionResponse.getData().getBasic().getCourseSchedule());
                courseScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                courseScheduleRecyclerView.setAdapter(courseScheduleAdapter);
            }

            StringBuilder subjectCount = new StringBuilder();
            for (int i = 0; i < descriptionResponse.getData().getTiles().size(); i++) {
                subjectCount.append(descriptionResponse.getData().getTiles().get(i).getTotoal()).append(" ").append(descriptionResponse.getData().getTiles().get(i).getTile_name()).append(" | ");
            }
            List<DescriptionFAQ> faq = descriptionResponse.getData().getFaq();
            if (faq != null) {
                /*FAQRecyclerAdapter faqRecyclerAdapter = new FAQRecyclerAdapter(activity, descriptionResponse.getData().getFaq(), "DESCRIPTION");
                faqRecyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                faqRecyclerView.setAdapter(faqRecyclerAdapter);*/
            }

            subjectCount = new StringBuilder(subjectCount.toString().trim());
            if (subjectCount.toString().endsWith("|")) {
                subjectCount = new StringBuilder(subjectCount.substring(0, subjectCount.length() - 1));
            }

            totalEnrolledCount.setText(String.format("%s Enrolled", descriptionResponse.getData().getTotalEnrolled()));
            countTV.setText(subjectCount.toString().trim());
            if (descriptionResponse.getData().getInstructorData() != null) {
                tutorName.setText(descriptionResponse.getData().getInstructorData().getName());
                tutorEmail.setText(descriptionResponse.getData().getInstructorData().getEmail());
                if (!TextUtils.isEmpty(descriptionResponse.getData().getInstructorData().getProfilePic())) {
                    profilePicIV.setVisibility(View.VISIBLE);
                    profilePicIVText.setVisibility(View.GONE);
                    Glide.with(activity)
                            .asBitmap()
                            .load(descriptionResponse.getData().getInstructorData().getProfilePic())
                            .apply(new RequestOptions()
                                    .placeholder(R.mipmap.default_pic))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                    profilePicIV.setImageBitmap(result);
                                }
                            });
                } else {
                    Drawable dr = Helper.GetDrawable(descriptionResponse.getData().getInstructorData().getName(), activity, descriptionResponse.getData().getInstructorData().getId());
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
            }
        }

    }

    public class WebViewClientImpl extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("TAG", "shouldOverrideUrlLoading: " + url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e("TAG", "onReceivedError:  " + errorCode + "   failingUrl  " + failingUrl);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }
}
