package com.emedicoz.app.courses.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.bookmark.NewBookMarkActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.QuizApiInterface;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.testmodule.activity.VideoTestBaseActivity;
import com.emedicoz.app.testmodule.activity.ViewSolutionWithTabNew;
import com.emedicoz.app.testmodule.callback.NumberPadOnClick;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestQuizActionActivity extends AppCompatActivity implements View.OnClickListener, NumberPadOnClick {
    private static String testSeriesId;
    Button startBtnQuiz;
    Button continueBtn;
    Button reattemptTestBtn;
    Button reviewTestBtn;
    ImageButton backIV;
    TextView quizTitleTV;
    TextView quizQuestionNo;
    TextView quizRating;
    TextView pauseTimeText;
    TextView completeTimeText;
    TextView commonBookmarkTV;
    String quizTitle;
    String quizQues;
    String quizQuesRating;
    String quizType;
    String screenType;
    LinearLayout pauseLinearLayout;
    LinearLayout completedLinearLayout;
    LinearLayout commonBookmarkLL;
    LinearLayout startQuizLL;


    // Result
    ResultTestSeries resultTestSeries;
    String videoSegmentId = "";
    String testSegmentId = "";
    String bookmarkCount = "";
    TextView correctTV;
    TextView incorrectTV;
    TextView skippedTV;
    TestQuizActionActivity activity;
    boolean clicked = false;
    boolean solution = true;
    boolean custom = false;
    Progress mProgress;
    RatingBar ratingBar;
    LinearLayout quizRateLL;
    String status = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.fragment_test_quiz_action);

        activity = this;
        mProgress = new Progress(this);
        mProgress.setCancelable(false);
        if (getIntent() != null && getIntent().getExtras() != null) {
            testSeriesId = getIntent().getExtras().getString(Const.TEST_SERIES_ID);
            quizTitle = getIntent().getExtras().getString(Constants.Extras.TITLE_NAME);
            quizQues = getIntent().getExtras().getString(Constants.Extras.QUES_NUM);
            quizQuesRating = getIntent().getExtras().getString(Constants.Extras.RATING);
            quizType = getIntent().getExtras().getString(Constants.Extras.QUIZ_TYPE);
            screenType = getIntent().getExtras().getString(Constants.Extras.SCREEN_TYPE);
            testSegmentId = getIntent().getExtras().getString(Constants.ResultExtras.TEST_SEGMENT_ID);
            videoSegmentId = getIntent().getExtras().getString(Constants.ResultExtras.QUIZ_SEGMENT_ID);
            //Result Screen
            resultTestSeries = (ResultTestSeries) getIntent().getExtras().getSerializable(Const.RESULT_SCREEN);
            solution = getIntent().getExtras().getBoolean(Constants.Extras.SOLUTION, true);
            custom = getIntent().getExtras().getBoolean(Constants.Extras.CUSTOM, false);
            status = getIntent().getExtras().getString(Const.STATUS);
        }
        if (testSeriesId != null)
            Log.e("onCreate: ", testSeriesId);
        initViews();
        resultInitView();

    }

    public void initViews() {
        completedLinearLayout = findViewById(R.id.completed_linear_layout);
        pauseLinearLayout = findViewById(R.id.continue_linear_layout);
        quizTitleTV = findViewById(R.id.quiz_title_tv);
        quizQuestionNo = findViewById(R.id.question_num_tv);
        quizRating = findViewById(R.id.ques_rating_tv);
        reattemptTestBtn = findViewById(R.id.reattempt_test_btn);
        reviewTestBtn = findViewById(R.id.review_test_btn);
        pauseTimeText = findViewById(R.id.paused_time_text);
        completeTimeText = findViewById(R.id.complete_time_text);
        commonBookmarkLL = findViewById(R.id.commonBookmarkLL);
        if (SharedPreference.getInstance().getMasterHitResponse() != null &&
                SharedPreference.getInstance().getMasterHitResponse().getShow_bookmark() != null &&
                SharedPreference.getInstance().getMasterHitResponse().getShow_bookmark().equalsIgnoreCase("0")) {
            commonBookmarkLL.setVisibility(View.GONE);
        } else {
            commonBookmarkLL.setVisibility(View.VISIBLE);
        }

        commonBookmarkTV = findViewById(R.id.commonBookmarkTV);
        startQuizLL = findViewById(R.id.startQuizLL);
        continueBtn = findViewById(R.id.continue_action_quiz);
        startBtnQuiz = findViewById(R.id.start_action_quiz);
        ratingBar = findViewById(R.id.quizRatingBar1);
        backIV = findViewById(R.id.backIV);

        backIV.setOnClickListener(v -> onBackPressed());
        reattemptTestBtn.setOnClickListener(this);
        reviewTestBtn.setOnClickListener(this);
        startBtnQuiz.setOnClickListener(this);
        continueBtn.setOnClickListener(this);
        commonBookmarkLL.setOnClickListener(this);
        if (!GenericUtils.isEmpty(testSeriesId))
            networkCallForBookmarkCount();
        openScreenChange();
        quizTitleTV.setText(quizTitle);
        quizQuestionNo.setText(String.format("Question: %s", quizQues));
        quizRating.setText(String.format("Rating: %s", quizQuesRating));
    }

    private void networkCallForBookmarkCount() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getBookmarkCount(SharedPreference.getInstance().getLoggedInUser().getId(), testSeriesId);
        response.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            mProgress.dismiss();
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            Log.e("onResponse: ", "COUNT: " + data.optString(Constants.Extras.BOOKMARKED_COUNT));
                            bookmarkCount = "Bookmarks (" + data.optString(Constants.Extras.BOOKMARKED_COUNT) + ")";
                            Spannable spannable = new SpannableString(bookmarkCount);
                            spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(TestQuizActionActivity.this, R.color.blue)), 10, (12 + data.optString("bookmarked_count").length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            commonBookmarkTV.setText(spannable, TextView.BufferType.SPANNABLE);
                        } else {
                            mProgress.dismiss();
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
            }
        });
    }

    public void resultInitView() {
        correctTV = findViewById(R.id.quizCorrectTV);
        incorrectTV = findViewById(R.id.quizIncorrectTV);
        skippedTV = findViewById(R.id.quizSkippedTV);
        quizRateLL = findViewById(R.id.quizRateLL);

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (clicked) {
                ratingBar.setIsIndicator(true);
            } else {
                ratingBar.setIsIndicator(false);
                networkCallForRating(ratingBar.getRating());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /**
             * start_action_quiz case is for attempting a qbank from start(it's not previously done)..
             * if quizType is VIDEO type, it means it is a VideoBased qbank
             * else it is a normal qbank
             */
            case R.id.start_action_quiz:
                /**
                 * continue_action_quiz case is for attempting a qbank which is previously paused(attempted but not completed) by user..
                 * if quizType is VIDEO type, it means it is a VideoBased qbank
                 * else it is a normal qbank
                 */
            case R.id.continue_action_quiz:
                /**
                 * reattempt_test_btn case is for attempting a qbank which is previously completed and user again wants to attempt that ..
                 * if quizType is VIDEO type, it means it is a VideoBased qbank
                 * else it is a normal qbank
                 */
            case R.id.reattempt_test_btn:
                if (quizType != null) {
                    if (quizType.equalsIgnoreCase("VIDEO")) {
                        Intent quizView = new Intent(this, VideoTestBaseActivity.class); //video base start
                        quizView.putExtra(Const.STATUS, false);
                        quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                        quizView.putExtra(Constants.Extras.TEST_VIDEO_QUIZ_ACTION, "test_video_quiz_action");
                        quizView.putExtra(Constants.Extras.QUES_NUM, quizQues);
                        quizView.putExtra(Constants.Extras.RATING, quizQuesRating);
                        startActivity(quizView);
                        finish();
                    } else {
                        Intent quizView = new Intent(this, TestBaseActivity.class); //text based start
                        quizView.putExtra(Const.STATUS, false);
                        quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                        quizView.putExtra(Constants.Extras.TEST_QUIZ_ACTION, "test_quiz_action");
                        quizView.putExtra(Constants.Extras.QUES_NUM, quizQues);
                        quizView.putExtra(Constants.Extras.RATING, quizQuesRating);
                        startActivity(quizView);
                        finish();

                    }
                }
                break;
            case R.id.commonBookmarkLL:
                Intent intent = new Intent(this, NewBookMarkActivity.class);
                intent.putExtra(Constants.Extras.ID, "");
                if (resultTestSeries != null)
                    intent.putExtra(Constants.Extras.NAME, resultTestSeries.getTestSeriesName());
                else
                    intent.putExtra(Constants.Extras.NAME, Constants.TestType.Q_BANK);
                intent.putExtra(Constants.Extras.TYPE, "Bookmark");
                intent.putExtra(Constants.Extras.NAME_OF_TAB, Constants.TestType.QUIZ);
                intent.putExtra(Const.TESTSERIES_ID, testSeriesId);
                intent.putExtra(Constants.Extras.Q_TYPE_DQB, "");
                startActivity(intent);
                break;

            case R.id.review_test_btn:
                Intent intentSolution = new Intent(TestQuizActionActivity.this, ViewSolutionWithTabNew.class);
                intentSolution.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, videoSegmentId);
                if (resultTestSeries != null)
                    intentSolution.putExtra(Constants.Extras.NAME, resultTestSeries.getTestSeriesName());
                else
                    intentSolution.putExtra(Constants.Extras.NAME, Constants.TestType.Q_BANK);
                intentSolution.putExtra(Constants.Extras.CUSTOM, custom);
                startActivity(intentSolution);
                break;
            default:
        }
    }

    public void openScreenChange() {
        if (screenType == null) return;
        if (screenType.equalsIgnoreCase(Constants.ResultExtras.COMPLETE)) { // when text or video test is over
            getDataThroughApi();

            setBookmarkLayoutMargin();
            startQuizLL.setVisibility(View.GONE);
            completedLinearLayout.setVisibility(View.VISIBLE);
            pauseLinearLayout.setVisibility(View.GONE);

        } else if (screenType.equalsIgnoreCase("PAUSE")) { //when text or video test is pause or onbackpressed
            getPauseTestTime();
            startQuizLL.setVisibility(View.GONE);
            completedLinearLayout.setVisibility(View.GONE);
            pauseLinearLayout.setVisibility(View.VISIBLE);
            setBookmarkLayoutMargin();
        } else {                                                   // when text or video test start first time
            pauseLinearLayout.setVisibility(View.GONE);
            completedLinearLayout.setVisibility(View.GONE);
            startQuizLL.setVisibility(View.VISIBLE);
            setBookmarkLayoutMargin();
        }


    }

    public void setBookmarkLayoutMargin() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) commonBookmarkLL.getLayoutParams();
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (10 * scale + 0.5f);
        layoutParams.setMargins(pixels, 0, pixels, 0);
        commonBookmarkLL.setLayoutParams(layoutParams);
    }

    public void getDataThroughApi() {
        if (!solution) {
            if (!custom)
                networkCallForQuizResult();
        } else {
            if (resultTestSeries != null) {
                if (clicked)
                    ratingBar.setClickable(false);
                else
                    ratingBar.setClickable(true);

                if (resultTestSeries.getCorrectCount() != null)
                    correctTV.setText(resultTestSeries.getCorrectCount());
                if (resultTestSeries.getIncorrectCount() != null)
                    incorrectTV.setText(resultTestSeries.getIncorrectCount());
                if (resultTestSeries.getNonAttempt() != null)
                    skippedTV.setText(resultTestSeries.getNonAttempt());
                if (!GenericUtils.isEmpty(resultTestSeries.getCreationTime())) {
                    String correctDateFormat = getFormattedDate(Long.parseLong(resultTestSeries.getCreationTime()));
                    completeTimeText.setText(getResources().getString(R.string.you_completed_test_on)+ " " + correctDateFormat);
                    //SharedPreference.getInstance().saveQbankTestStatus(Const.QBANK_TEST_STATUS, "true");
                    //SharedPreference.getInstance().saveQbankAttempOneTime(Const.QBANK_ATTEMPT, "1");
                } else {
                    completeTimeText.setVisibility(View.GONE);
                }

            } else {
                networkCallForQuizResult();
            }
        }
    }

    private void networkCallForRating(final float rating) {           //api for rating

        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Call<JsonObject> response = apiInterface.rateQbank(SharedPreference.getInstance().getLoggedInUser().getId(), testSeriesId, String.valueOf(rating));
        response.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            mProgress.dismiss();

                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            Toast.makeText(TestQuizActionActivity.this, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();

                            ratingBar.setRating(ratingBar.getRating());
                            ratingBar.setIsIndicator(true);
                            Log.e("Rating", "onResponse: " + data.getString("avg_rating"));
                            clicked = true;
                        } else {
                            mProgress.dismiss();
                            Toast.makeText(TestQuizActionActivity.this, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
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
            }
        });

    }

    public void networkCallForQuizResult() {               //api for text or video test complete screen datetime

        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Call<JsonObject> response;
        if (resultTestSeries != null)
            response = apiInterface.getUserleaderBoardResult_v3(SharedPreference.getInstance().getLoggedInUser().getId(), resultTestSeries.getId());
        else
            response = apiInterface.getUserleaderBoardResult_v3(SharedPreference.getInstance().getLoggedInUser().getId(), testSegmentId);
        response.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {

                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {

                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            resultTestSeries = gson.fromJson(data.toString(), ResultTestSeries.class);
                            if (resultTestSeries != null) {
                                if (clicked)
                                    ratingBar.setClickable(false);
                                else
                                    ratingBar.setClickable(true);

                                if (resultTestSeries.getCorrectCount() != null)
                                    correctTV.setText(resultTestSeries.getCorrectCount());
                                if (resultTestSeries.getIncorrectCount() != null)
                                    incorrectTV.setText(resultTestSeries.getIncorrectCount());
                                if (resultTestSeries.getNonAttempt() != null)
                                    skippedTV.setText(resultTestSeries.getNonAttempt());

                                quizQues = resultTestSeries.getTotalQuestions();
                                quizQuesRating = resultTestSeries.getAvgRating();
                                quizQuestionNo.setText(String.format("Question: %s", quizQues));
                                quizRating.setText(String.format("Rating: %s", quizQuesRating));

                                if (!GenericUtils.isEmpty(resultTestSeries.getCreationTime())) {
                                    String correctDateFormat = getFormattedDate(Long.parseLong(resultTestSeries.getCreationTime()));
                                    completeTimeText.setText(getResources().getString(R.string.you_completed_test_on)+ " " + correctDateFormat);
                                    //SharedPreference.getInstance().saveQbankTestStatus(Const.QBANK_TEST_STATUS, "true");
                                    //SharedPreference.getInstance().saveQbankAttempOneTime(Const.QBANK_ATTEMPT, "1");
                                } else {
                                    completeTimeText.setVisibility(View.GONE);
                                }

                                reviewTestBtn.setOnClickListener(view -> {
                                    Intent intent = new Intent(TestQuizActionActivity.this, ViewSolutionWithTabNew.class);
                                    intent.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                                    intent.putExtra(Constants.Extras.NAME, resultTestSeries.getTestSeriesName());
                                    startActivity(intent);
                                });

                                SharedPreference.getInstance().putString(Constants.Extras.SOLUTION, "Yes");
                            }
                        } else {
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
            }
        });
    }

    public void getPauseTestTime() {              //api for text or video test pause datetime
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Call<JsonObject> response;
        if (resultTestSeries != null)
            response = apiInterface.getUserleaderBoardResult_v3(SharedPreference.getInstance().getLoggedInUser().getId(), resultTestSeries.getId());
        else
            response = apiInterface.getUserleaderBoardResult_v3(SharedPreference.getInstance().getLoggedInUser().getId(), testSegmentId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {

                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            resultTestSeries = gson.fromJson(data.toString(), ResultTestSeries.class);
                            if (resultTestSeries != null) {
                                if (!GenericUtils.isEmpty(resultTestSeries.getCreationTime())) {
                                    String correctDateFormat = getFormattedDate(Long.parseLong(resultTestSeries.getCreationTime()));
                                    pauseTimeText.setText(getResources().getString(R.string.you_pause_test_on) + " " + correctDateFormat);
                                    mProgress.dismiss();
                                }
                            } else {
                                pauseTimeText.setVisibility(View.GONE);
                            }
                        } else {
                            mProgress.dismiss();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                mProgress.dismiss();
            }
        });

    }

    @Override
    public void sendOnclickInd(int index) {

    }

    private String getFormattedDate(long creationTime) {
        Date d = new Date(creationTime);
        DateFormat f = new SimpleDateFormat("dd:MM:yyyy hh.mm aa");
        String dateString = f.format(d);
        Date date = null;
        try {
            date = f.parse(f.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] amPM = dateString.split("\\s+");

        String[] fullDate = String.valueOf(date).split("\\s+");
        return fullDate[0] + ", " + fullDate[1] + " " + fullDate[2] + ", " + fullDate[5] + " at " + amPM[1] + " " + amPM[2];
    }

}
