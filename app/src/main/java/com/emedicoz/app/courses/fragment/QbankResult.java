package com.emedicoz.app.courses.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.QuizApiInterface;
import com.emedicoz.app.testmodule.activity.ViewSolutionWithTabNew;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class QbankResult extends Fragment {


    ProgressBar vProgressBar1;
    ProgressBar vProgressBar2;
    ProgressBar vProgressBar3;
    ResultTestSeries resultTestSeries;
    String testSegmentId = "";
    TextView accuracyTV;
    TextView correctTV;
    TextView incorrectTV;
    TextView skippedTV;
    Button reviewAnswer;
    Button closeBtn;
    Activity activity;
    boolean clicked = false;
    boolean solution = true;
    boolean custom = false;
    Progress mProgress;
    RatingBar ratingBar;
    TextView qbankAnalysisTV;
    TextView complete;
    LinearLayout rateLL;

    public QbankResult() {
        // Required empty public constructor
    }

    public static QbankResult newInstance(ResultTestSeries resultTestSeries, String totalQuestions, String testSegmentId, boolean solution, boolean custom) {
        QbankResult quizResultAwait = new QbankResult();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.RESULT_SCREEN, resultTestSeries);
        bundle.putString(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
        bundle.putString("TOTAL_QUESTIONS", totalQuestions);
        bundle.putBoolean("SOLUTION", solution);
        bundle.putBoolean("CUSTOM", custom);
        quizResultAwait.setArguments(bundle);
        return quizResultAwait;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qbank_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            resultTestSeries = (ResultTestSeries) getArguments().getSerializable(Const.RESULT_SCREEN);
            testSegmentId = getArguments().getString(Constants.ResultExtras.TEST_SEGMENT_ID);
            solution = getArguments().getBoolean(Constants.Extras.SOLUTION);
            custom = getArguments().getBoolean(Constants.Extras.CUSTOM);
        }
        vProgressBar1 = view.findViewById(R.id.vprogressbar1);
        vProgressBar2 = view.findViewById(R.id.vprogressbar2);
        vProgressBar3 = view.findViewById(R.id.vprogressbar3);
        accuracyTV = view.findViewById(R.id.accuracyTV);
        correctTV = view.findViewById(R.id.correctTV);
        incorrectTV = view.findViewById(R.id.incorrectTV);
        skippedTV = view.findViewById(R.id.skippedTV);
        reviewAnswer = view.findViewById(R.id.reviewAnswer);
        closeBtn = view.findViewById(R.id.closeBtn);
        ratingBar = view.findViewById(R.id.ratingBar1);
        qbankAnalysisTV = view.findViewById(R.id.qbankAnalysisTV);
        rateLL = view.findViewById(R.id.rateLL);
        complete = view.findViewById(R.id.complete);
        if (!solution) {
            reviewAnswer.setVisibility(View.GONE);
            qbankAnalysisTV.setVisibility(View.GONE);
            qbankAnalysisTV.setText("Daily Quiz Analysis");
            complete.setText("Daily Quiz Analysis");
            rateLL.setVisibility(View.GONE);
            if (!custom)
                networkCallForQuizResult();
        } else {
            if (resultTestSeries != null) {
                qbankAnalysisTV.setVisibility(View.VISIBLE);
                if (custom)
                    rateLL.setVisibility(View.GONE);
                else
                    rateLL.setVisibility(View.VISIBLE);
                qbankAnalysisTV.setText("QBank Analysis");
                complete.setText("QBank Analysis");
                reviewAnswer.setVisibility(View.VISIBLE);
                setData();
                reviewAnswer.setOnClickListener((View v) -> {

                    Intent intent = new Intent(activity, ViewSolutionWithTabNew.class);
                    intent.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                    intent.putExtra(Constants.Extras.NAME, resultTestSeries.getTestSeriesName());
                    intent.putExtra("CUSTOM", custom);
                    activity.startActivity(intent);
                });


            } else {
                networkCallForQuizResult();
            }
        }

    }

    private void networkCallForRating(final float rating) {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Log.e("STATUS", resultTestSeries.getTestSeriesId());
        Call<JsonObject> response = apiInterface.rateQbank(SharedPreference.getInstance().getLoggedInUser().getId(), resultTestSeries.getTestSeriesId(), String.valueOf(rating));
        response.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            mProgress.dismiss();

                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            ratingBar.setRating(ratingBar.getRating());
                            ratingBar.setIsIndicator(true);
                            Log.e("Rating", "onResponse: " + data.getString("avg_rating"));
                            clicked = true;
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

    public void networkCallForQuizResult() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
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
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            mProgress.dismiss();

                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            resultTestSeries = gson.fromJson(data.toString(), ResultTestSeries.class);
                            if (resultTestSeries != null) {
                                setData();
                                reviewAnswer.setOnClickListener((View view) -> {

                                    Intent intent = new Intent(activity, ViewSolutionWithTabNew.class);
                                    intent.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                                    intent.putExtra(Constants.Extras.NAME, resultTestSeries.getTestSeriesName());
                                    activity.startActivity(intent);

                                });

                                SharedPreference.getInstance().putString("SOLUTION", "Yes");
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

    private void setData() {
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

        if (!GenericUtils.isEmpty(resultTestSeries.getAccuracy())) {
            accuracyTV.setText(resultTestSeries.getAccuracy() + " %");
        }

        int attempt = Integer.parseInt(resultTestSeries.getCorrectCount()) + Integer.parseInt(resultTestSeries.getIncorrectCount());
        int total = attempt + Integer.parseInt(resultTestSeries.getNonAttempt());

        vProgressBar1.setMax(total);
        vProgressBar1.setProgress(Integer.parseInt(resultTestSeries.getCorrectCount()));

        vProgressBar2.setMax(total);
        vProgressBar2.setProgress(Integer.parseInt(resultTestSeries.getIncorrectCount()));

        vProgressBar3.setMax(total);
        vProgressBar3.setProgress(Integer.parseInt(resultTestSeries.getNonAttempt()));
        closeBtn.setOnClickListener((View view) -> activity.onBackPressed());

        ratingBar.setOnRatingBarChangeListener((RatingBar ratingBar, float v, boolean b) -> {
            if (clicked) {
                ratingBar.setIsIndicator(true);
            } else {
                ratingBar.setIsIndicator(false);
                networkCallForRating(ratingBar.getRating());
            }
        });
    }

}
