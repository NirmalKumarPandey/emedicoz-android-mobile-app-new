package com.emedicoz.app.dailychallenge;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.adapter.RankAdapter;
import com.emedicoz.app.customviews.CircleImageView;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.dailychallenge.model.DCScoreCardResponse;
import com.emedicoz.app.modelo.RankList;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.QuizApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint({"SimpleDateFormat", "DefaultLocale"})
public class DailyChallengeScoreCardFragment extends Fragment implements View.OnClickListener {
    public String frag_type = "", errorMessage;
    TextView firstName, firstScore, firstTotal, firstDetail, firstRight, firstWrong, firstSkipped, firstTime,
            secondName, secondScore, secondTotal, secondDetail, secondRight, secondWrong, secondSkipped, secondTime,
            thirdName, thirdScore, thirdTotal, thirdDetail, thirdRight, thirdWrong, thirdSkipped, thirdTime;
    TextView noData;
    Activity activity;
    RankList rankList;
    ArrayList<RankList> arrayList, newArrayList = new ArrayList<>();
    ResultTestSeries resultTestSeries;
    CircleImageView imgFirstRanker, imgSecondRanker, imgThirdRanker;
    ImageView imgFirstRankerText, imgSecondRankerText, imgThirdRankerText, first, second, third;
    String testSegmentId;
    NonScrollRecyclerView rankRV;
    CardView expandList;
    LinearLayout cardDetail1, cardDetail2, cardDetail3;
    LinearLayout noUserYet1, noUserYet2, noUserYet3;
    RelativeLayout layoutRank1, layoutRank2, layoutRank3;
    LinearLayout recycleLL;
    CardView headerTitle;
    ImageView dropdownFirst, dropdownSecond, dropdownThird;
    LinearLayout firstLL, secondLL, thirdLL;
    RelativeLayout firstRankerDetailRL, secondRankerDetailRL, thirdRankerDetailRL, timeTakenFirst, timeTakenSecond, timeTakenThird;
    private TextView testSeriesName, totalQues, totalTime;
    Progress mProgress;
    String testSeriesId;
    LinearLayout calender_date;
    ImageView previous_date, next_date;
    TextView date_Tv;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    LinearLayout rootLayout;
    private RankAdapter rankAdapter;

    public DailyChallengeScoreCardFragment() {
        // Required empty public constructor
    }

    public static DailyChallengeScoreCardFragment newInstance(String testSegmentId, String testSeriesId) {
        DailyChallengeScoreCardFragment leaderBoardFragment = new DailyChallengeScoreCardFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
        args.putString(Const.TEST_SERIES_ID, testSeriesId);
        leaderBoardFragment.setArguments(args);
        return leaderBoardFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        myCalendar = eMedicozApp.getInstance().getDcCalendar();

        if (getArguments() != null) {
            testSegmentId = Objects.requireNonNull(getArguments()).getString(Constants.ResultExtras.TEST_SEGMENT_ID);
            testSeriesId = Objects.requireNonNull(getArguments()).getString(Const.TEST_SERIES_ID);

        }
    }

    public void getPreviousDay(Date date) {

        myCalendar.add(Calendar.DAY_OF_MONTH, -1);
        date_Tv.setText(getFormattedDate());
        getScoreBoardOfDailyChallenge();
    }

    public void getNextDay(Date date) {
        if (!DateUtils.isToday(myCalendar.getTimeInMillis())) {
            myCalendar.add(Calendar.DAY_OF_MONTH, 1);
            date_Tv.setText(getFormattedDate());
            getScoreBoardOfDailyChallenge();
        } else {
            Toast.makeText(activity, "Can not select future dates", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrayList = new ArrayList<>();
        newArrayList = new ArrayList<>();
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        initViews(view);

    }

    @Override
    public void onResume() {
        super.onResume();

        setDate();
    }

    public void setDate() {
        date_Tv.setText(getFormattedDate());
        getScoreBoardOfDailyChallenge();

        dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            date_Tv.setText(getFormattedDate());
            getScoreBoardOfDailyChallenge();

        };

    }

    private String getFormattedDate() {
        return new SimpleDateFormat("dd MMM yyyy").format(myCalendar.getTime());
    }

    private String getFormattedDateForAPI() {
        return new SimpleDateFormat("yyyy-MM-dd").format(myCalendar.getTime());
    }

    private void initViews(View view) {

        rootLayout = view.findViewById(R.id.root_layout);
        calender_date = view.findViewById(R.id.calender_date);
        previous_date = view.findViewById(R.id.previous_date);
        previous_date.setOnClickListener(this);
        next_date = view.findViewById(R.id.next_date);
        next_date.setOnClickListener(this);
        date_Tv = view.findViewById(R.id.date_Tv);
        noData = view.findViewById(R.id.no_data);
        testSeriesName = view.findViewById(R.id.testSeriesName);
        totalQues = view.findViewById(R.id.totalQues);
        totalTime = view.findViewById(R.id.totalTime);
        rankRV = view.findViewById(R.id.rankRV);
        recycleLL = view.findViewById(R.id.recycleLL);
        headerTitle = view.findViewById(R.id.layout_title_header);
        expandList = view.findViewById(R.id.expandList);
        firstName = view.findViewById(R.id.firstRankerName);
        timeTakenFirst = view.findViewById(R.id.timeTakenFirst);
        firstScore = view.findViewById(R.id.firstRankerScore);
        firstTotal = view.findViewById(R.id.firstRankerTotal);
        firstDetail = view.findViewById(R.id.firstRankerDetail);
        firstRight = view.findViewById(R.id.firstRankerRight);
        firstWrong = view.findViewById(R.id.firstRankerWrong);
        firstSkipped = view.findViewById(R.id.firstRankerSkipped);
        firstTime = view.findViewById(R.id.firstRankerTime);
        imgFirstRanker = view.findViewById(R.id.imgFirstRanker);
        imgFirstRankerText = view.findViewById(R.id.imgFirstRankerText);
        firstRankerDetailRL = view.findViewById(R.id.firstRankerDetailRL);
        first = view.findViewById(R.id.first);
        second = view.findViewById(R.id.second);
        third = view.findViewById(R.id.third);

        secondName = view.findViewById(R.id.secondRankerName);
        secondScore = view.findViewById(R.id.secondRankerScore);
        timeTakenSecond = view.findViewById(R.id.timeTakenSecond);
        secondTotal = view.findViewById(R.id.secondRankerTotal);
        secondDetail = view.findViewById(R.id.secondRankerDetail);
        secondRight = view.findViewById(R.id.secondRankerRight);
        secondWrong = view.findViewById(R.id.secondRankerWrong);
        secondSkipped = view.findViewById(R.id.secondRankerSkipped);
        secondTime = view.findViewById(R.id.secondRankerTime);
        imgSecondRanker = view.findViewById(R.id.imgSecondRanker);
        imgSecondRankerText = view.findViewById(R.id.imgSecondRankerText);
        secondRankerDetailRL = view.findViewById(R.id.secondRankerDetailRL);

        thirdName = view.findViewById(R.id.thirdRankerName);
        timeTakenThird = view.findViewById(R.id.timeTakenThird);
        thirdScore = view.findViewById(R.id.thirdRankerScore);
        thirdTotal = view.findViewById(R.id.thirdRankerTotal);
        thirdDetail = view.findViewById(R.id.thirdRankerDetail);
        thirdRight = view.findViewById(R.id.thirdRankerRight);
        thirdWrong = view.findViewById(R.id.thirdRankerWrong);
        thirdSkipped = view.findViewById(R.id.thirdRankerSkipped);
        thirdTime = view.findViewById(R.id.thirdRankerTime);
        imgThirdRanker = view.findViewById(R.id.imgThirdRanker);
        imgThirdRankerText = view.findViewById(R.id.imgThirdRankerText);
        thirdRankerDetailRL = view.findViewById(R.id.thirdRankerDetailRL);

        cardDetail1 = view.findViewById(R.id.cardDetail1);
        cardDetail2 = view.findViewById(R.id.cardDetail2);
        cardDetail3 = view.findViewById(R.id.cardDetail3);
        noUserYet1 = view.findViewById(R.id.noUserYetFirst);
        noUserYet2 = view.findViewById(R.id.noUserYetSecond);
        noUserYet3 = view.findViewById(R.id.noUserYetThird);
        layoutRank1 = view.findViewById(R.id.layout_rank_1);
        layoutRank2 = view.findViewById(R.id.layout_rank_2);
        layoutRank3 = view.findViewById(R.id.layout_rank_3);

        dropdownFirst = view.findViewById(R.id.dropdownFirst);
        dropdownSecond = view.findViewById(R.id.dropdownSecond);
        dropdownThird = view.findViewById(R.id.dropdownThird);

        firstLL = view.findViewById(R.id.firstLL);
        secondLL = view.findViewById(R.id.secondLL);
        thirdLL = view.findViewById(R.id.thirdLL);
        bindControls();

        rankAdapter = new RankAdapter(activity, newArrayList, resultTestSeries);
        rankRV.setAdapter(rankAdapter);
        rankRV.setNestedScrollingEnabled(false);
        rankRV.setLayoutManager(new LinearLayoutManager(activity));

    }

    private void bindControls() {
        firstRankerDetailRL.setOnClickListener(this);
        secondRankerDetailRL.setOnClickListener(this);
        thirdRankerDetailRL.setOnClickListener(this);

        date_Tv.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(activity, dateSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dc_scorecard_fragment, container, false);
    }

    private void networkCallForRank(String testSeriesId) {
        mProgress.show();
        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Call<JsonObject> response = apiInterface.getRankList(SharedPreference.getInstance().getLoggedInUser().getId(), testSeriesId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();
                    JSONArray dataArray;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            ArrayList<RankList> topThreeList = new ArrayList<>();
                            dataArray = GenericUtils.getJsonArray(jsonResponse);
                            for (int i = 0; i < dataArray.length(); i++) {
                                rankList = gson.fromJson(dataArray.opt(i).toString(), RankList.class);
                                topThreeList.add(rankList);
                            }
                            setTopThreeDetail(topThreeList);
                            newArrayList.clear();
                            arrayList.clear();
                            if (dataArray.length() > 3) {
                                recycleLL.setVisibility(View.VISIBLE);
                                headerTitle.setVisibility(View.VISIBLE);
                                for (int i = 3; i < dataArray.length(); i++) {
                                    rankList = gson.fromJson(dataArray.opt(i).toString(), RankList.class);
                                    arrayList.add(rankList);
                                }
                                if (arrayList.size() > 6) {
                                    for (int i = 0; i < 7; i++) {
                                        newArrayList.add(arrayList.get(i));
                                    }
                                } else {
                                    newArrayList.addAll(arrayList);
                                }

                                rankAdapter.notifyDataSetChanged();
                                expandList.setVisibility(View.VISIBLE);
                                expandList.setOnClickListener((View view) -> {
                                    newArrayList.clear();
                                    newArrayList.addAll(arrayList);
                                    rankAdapter.notifyDataSetChanged();
                                    expandList.setVisibility(View.GONE);
                                });
                            } else {
                                recycleLL.setVisibility(View.GONE);
                                headerTitle.setVisibility(View.GONE);
                            }
                        } else {
                            setTopThreeDetail(null);
                            recycleLL.setVisibility(View.GONE);
                            headerTitle.setVisibility(View.GONE);
                            noData.setVisibility(View.VISIBLE);
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

    private void setTopThreeDetail(ArrayList<RankList> arrayList) {

        if (arrayList == null) {
            layoutRank1.setVisibility(View.GONE);
            layoutRank2.setVisibility(View.GONE);
            layoutRank3.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            return;
        }
        noData.setVisibility(View.GONE);
        if (resultTestSeries != null && !GenericUtils.isEmpty(resultTestSeries.getSetType())) {
            timeTakenFirst.setVisibility(View.VISIBLE);
            timeTakenSecond.setVisibility(View.VISIBLE);
            timeTakenThird.setVisibility(View.VISIBLE);
        } else {
            timeTakenFirst.setVisibility(View.GONE);
            timeTakenSecond.setVisibility(View.GONE);
            timeTakenThird.setVisibility(View.GONE);
        }

        if (arrayList.size() == 1) {
            noUserYet2.setVisibility(View.VISIBLE);
            noUserYet3.setVisibility(View.VISIBLE);
            cardDetail2.setVisibility(View.GONE);
            cardDetail3.setVisibility(View.GONE);

            layoutRank1.setVisibility(View.VISIBLE);
            layoutRank2.setVisibility(View.INVISIBLE);
            layoutRank3.setVisibility(View.INVISIBLE);

        } else if (arrayList.size() == 2) {
            noUserYet3.setVisibility(View.VISIBLE);
            cardDetail3.setVisibility(View.GONE);

            layoutRank1.setVisibility(View.VISIBLE);
            layoutRank2.setVisibility(View.VISIBLE);
            layoutRank3.setVisibility(View.INVISIBLE);
/*
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            param.setMargins(0, 10, 0, 0);
            layoutRank2.setLayoutParams(param);*/
        } else {
            noUserYet1.setVisibility(View.GONE);
            noUserYet2.setVisibility(View.GONE);
            noUserYet3.setVisibility(View.GONE);

            cardDetail1.setVisibility(View.VISIBLE);
            cardDetail2.setVisibility(View.VISIBLE);
            cardDetail3.setVisibility(View.VISIBLE);

            layoutRank1.setVisibility(View.VISIBLE);
            layoutRank2.setVisibility(View.VISIBLE);
            layoutRank3.setVisibility(View.VISIBLE);
        }

        if (arrayList.size() == 1) {
            int totalMarks = Integer.parseInt(resultTestSeries.getTestSeriesMarks());
            firstName.setText(arrayList.get(0).getName());
            firstScore.setText(arrayList.get(0).getMarks());
            firstRight.setText(arrayList.get(0).getCorrect_count());
            firstWrong.setText(arrayList.get(0).getIncorrect_count());
            firstSkipped.setText(arrayList.get(0).getNon_attempt());
            setTestTime(firstTime, arrayList.get(0).getTimeSpent());
            firstTotal.setText(String.format("out of %d", totalMarks));
            if (!TextUtils.isEmpty(arrayList.get(0).getProfilePicture())) {
                imgFirstRanker.setVisibility(View.VISIBLE);
                imgFirstRankerText.setVisibility(View.GONE);
                Glide.with(activity).load(arrayList.get(0).getProfilePicture())
                        .into(imgFirstRanker);
            } else {
                Drawable dr = Helper.GetDrawable(arrayList.get(0).getName(), activity,
                        arrayList.get(0).getUserId());
                if (dr != null) {
                    imgFirstRanker.setVisibility(View.GONE);
                    imgFirstRankerText.setVisibility(View.VISIBLE);
                    imgFirstRankerText.setImageDrawable(dr);
                } else {
                    imgFirstRanker.setVisibility(View.VISIBLE);
                    imgFirstRankerText.setVisibility(View.GONE);
                    imgFirstRanker.setImageResource(R.mipmap.default_pic);
                }
            }

        } else if (arrayList.size() == 2) {
            int totalMarks = Integer.parseInt(resultTestSeries.getTestSeriesMarks());
            firstName.setText(arrayList.get(0).getName());
            firstScore.setText(arrayList.get(0).getMarks());
            firstRight.setText(arrayList.get(0).getCorrect_count());
            firstWrong.setText(arrayList.get(0).getIncorrect_count());
            firstSkipped.setText(arrayList.get(0).getNon_attempt());
            setTestTime(firstTime, arrayList.get(0).getTimeSpent());
            firstTotal.setText(String.format("out of %d", totalMarks));

            if (!TextUtils.isEmpty(arrayList.get(0).getProfilePicture())) {
                imgFirstRanker.setVisibility(View.VISIBLE);
                imgFirstRankerText.setVisibility(View.GONE);
                Glide.with(activity).load(arrayList.get(0).getProfilePicture())
                        .into(imgFirstRanker);
            } else {
                Drawable dr = Helper.GetDrawable(arrayList.get(0).getName(), activity,
                        arrayList.get(0).getUserId());
                if (dr != null) {
                    imgFirstRanker.setVisibility(View.GONE);
                    imgFirstRankerText.setVisibility(View.VISIBLE);
                    imgFirstRankerText.setImageDrawable(dr);
                } else {
                    imgFirstRanker.setVisibility(View.VISIBLE);
                    imgFirstRankerText.setVisibility(View.GONE);
                    imgFirstRanker.setImageResource(R.mipmap.default_pic);
                }
            }


            secondName.setText(arrayList.get(1).getName());
            secondScore.setText(arrayList.get(1).getMarks());
            secondRight.setText(arrayList.get(1).getCorrect_count());
            secondWrong.setText(arrayList.get(1).getIncorrect_count());
            secondSkipped.setText(arrayList.get(1).getNon_attempt());
            setTestTime(secondTime, arrayList.get(1).getTimeSpent());
            secondTotal.setText(String.format("out of %d", totalMarks));

            if (!TextUtils.isEmpty(arrayList.get(1).getProfilePicture())) {
                imgSecondRanker.setVisibility(View.VISIBLE);
                imgSecondRankerText.setVisibility(View.GONE);
                Glide.with(activity).load(arrayList.get(1).getProfilePicture())
                        .into(imgSecondRanker);
            } else {
                Drawable dr = Helper.GetDrawable(arrayList.get(1).getName(), activity,
                        arrayList.get(1).getUserId());
                if (dr != null) {
                    imgSecondRanker.setVisibility(View.GONE);
                    imgSecondRankerText.setVisibility(View.VISIBLE);
                    imgSecondRankerText.setImageDrawable(dr);
                } else {
                    imgSecondRanker.setVisibility(View.VISIBLE);
                    imgSecondRankerText.setVisibility(View.GONE);
                    imgSecondRanker.setImageResource(R.mipmap.default_pic);
                }
            }

        } else {
            int totalMarks = Integer.parseInt(GenericUtils.getParsableString(resultTestSeries.getTestSeriesMarks()));
            firstName.setText(arrayList.get(0).getName());
            firstScore.setText(arrayList.get(0).getMarks());
            firstRight.setText(arrayList.get(0).getCorrect_count());
            firstWrong.setText(arrayList.get(0).getIncorrect_count());
            firstSkipped.setText(arrayList.get(0).getNon_attempt());
            setTestTime(firstTime, arrayList.get(0).getTimeSpent());
            firstTotal.setText(String.format("out of %d", totalMarks));

            secondName.setText(arrayList.get(1).getName());
            secondScore.setText(arrayList.get(1).getMarks());
            secondRight.setText(arrayList.get(1).getCorrect_count());
            secondWrong.setText(arrayList.get(1).getIncorrect_count());
            secondSkipped.setText(arrayList.get(1).getNon_attempt());
            setTestTime(secondTime, arrayList.get(1).getTimeSpent());
            secondTotal.setText(String.format("out of %d", totalMarks));

            thirdName.setText(arrayList.get(2).getName());
            thirdScore.setText(arrayList.get(2).getMarks());
            thirdRight.setText(arrayList.get(2).getCorrect_count());
            thirdWrong.setText(arrayList.get(2).getIncorrect_count());
            thirdSkipped.setText(arrayList.get(2).getNon_attempt());
            setTestTime(thirdTime, arrayList.get(2).getTimeSpent());
            thirdTotal.setText(String.format("out of %d", totalMarks));


            if (!TextUtils.isEmpty(arrayList.get(2).getProfilePicture())) {
                imgThirdRanker.setVisibility(View.VISIBLE);
                imgThirdRankerText.setVisibility(View.GONE);
                Glide.with(activity).load(arrayList.get(2).getProfilePicture())
                        .into(imgThirdRanker);
            } else {
                Drawable dr = Helper.GetDrawable(arrayList.get(2).getName(), activity,
                        arrayList.get(2).getUserId());
                if (dr != null) {
                    imgThirdRanker.setVisibility(View.GONE);
                    imgThirdRankerText.setVisibility(View.VISIBLE);
                    imgThirdRankerText.setImageDrawable(dr);
                } else {
                    imgThirdRanker.setVisibility(View.VISIBLE);
                    imgThirdRankerText.setVisibility(View.GONE);
                    imgThirdRanker.setImageResource(R.mipmap.default_pic);
                }
            }

            if (!TextUtils.isEmpty(arrayList.get(1).getProfilePicture())) {
                imgSecondRanker.setVisibility(View.VISIBLE);
                imgSecondRankerText.setVisibility(View.GONE);
                Glide.with(activity).load(arrayList.get(1).getProfilePicture())
                        .into(imgSecondRanker);
            } else {
                Drawable dr = Helper.GetDrawable(arrayList.get(1).getName(), activity,
                        arrayList.get(1).getUserId());
                if (dr != null) {
                    imgSecondRanker.setVisibility(View.GONE);
                    imgSecondRankerText.setVisibility(View.VISIBLE);
                    imgSecondRankerText.setImageDrawable(dr);
                } else {
                    imgSecondRanker.setVisibility(View.VISIBLE);
                    imgSecondRankerText.setVisibility(View.GONE);
                    imgSecondRanker.setImageResource(R.mipmap.default_pic);
                }
            }

            if (!TextUtils.isEmpty(arrayList.get(0).getProfilePicture())) {
                imgFirstRanker.setVisibility(View.VISIBLE);
                imgFirstRankerText.setVisibility(View.GONE);
                Glide.with(activity).load(arrayList.get(0).getProfilePicture())
                        .into(imgFirstRanker);
            } else {
                Drawable dr = Helper.GetDrawable(arrayList.get(0).getName(), activity,
                        arrayList.get(0).getUserId());
                if (dr != null) {
                    imgFirstRanker.setVisibility(View.GONE);
                    imgFirstRankerText.setVisibility(View.VISIBLE);
                    imgFirstRankerText.setImageDrawable(dr);
                } else {
                    imgFirstRanker.setVisibility(View.VISIBLE);
                    imgFirstRankerText.setVisibility(View.GONE);
                    imgFirstRanker.setImageResource(R.mipmap.default_pic);
                }
            }
        }

    }

    private void setTestTime(TextView textView, String timeSpent) {
        if (Integer.parseInt(timeSpent) < 60) {
            textView.setText(String.format("%s sec", timeSpent));

        } else {
            textView.setText(String.format("%d mins", Math.round(Integer.parseInt(timeSpent) / 60)));
        }
    }

    private void getScoreBoardOfDailyChallenge() {
        if (activity.isFinishing()) return;

        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getScoreBoardOfDailyChallenge(SharedPreference.getInstance().getLoggedInUser().getId(),
                getFormattedDateForAPI());
        Log.e("getScoreBoard: ", Const.USER_ID + "-" + SharedPreference.getInstance().getLoggedInUser().getId() + " " + Constants.Extras.DATE + "-" + getFormattedDateForAPI());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            DCScoreCardResponse dcScoreCardResponse = gson.fromJson(jsonObject, DCScoreCardResponse.class);
                            List<ResultTestSeries> resultTestSeriesList = dcScoreCardResponse.getData();
                            if (!GenericUtils.isListEmpty(resultTestSeriesList)) {
                                rootLayout.setVisibility(View.VISIBLE);
                                noData.setVisibility(View.GONE);
                                resultTestSeries = resultTestSeriesList.get(0);
                                testSeriesName.setText(resultTestSeries.getTestSeriesName());
                                totalQues.setText(String.format("Total questions: %s", resultTestSeries.getTotalQuestions()));
                                totalTime.setText(String.format("Total time: %s mins", resultTestSeries.getTotalTestSeriesTime()));

                                networkCallForRank(resultTestSeries.getTestSeriesId());
                                /* replaceFragment(DailyChallengeScoreCardFragment.newInstance(fragType, testSegmentId, resultTestSeries.get(0)));*/
                            } else {
//                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                                rootLayout.setVisibility(View.GONE);
                                noData.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            noData.setVisibility(View.VISIBLE);
                            rootLayout.setVisibility(View.GONE);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.firstRankerDetailRL:
                if (firstLL.getVisibility() == View.GONE) {
                    firstLL.setVisibility(View.VISIBLE);
                    dropdownFirst.setImageResource(R.mipmap.result_show_more_up);
                } else {
                    firstLL.setVisibility(View.GONE);
                    dropdownFirst.setImageResource(R.mipmap.result_show_more_down);
                }
                break;

            case R.id.secondRankerDetailRL:
                if (secondLL.getVisibility() == View.GONE) {
                    secondLL.setVisibility(View.VISIBLE);
                    dropdownSecond.setImageResource(R.mipmap.result_show_more_up);
                } else {
                    secondLL.setVisibility(View.GONE);
                    dropdownSecond.setImageResource(R.mipmap.result_show_more_down);
                }
                break;

            case R.id.thirdRankerDetailRL:
                if (thirdLL.getVisibility() == View.GONE) {
                    thirdLL.setVisibility(View.VISIBLE);
                    dropdownThird.setImageResource(R.mipmap.result_show_more_up);
                } else {
                    thirdLL.setVisibility(View.GONE);
                    dropdownThird.setImageResource(R.mipmap.result_show_more_down);
                }
                break;
            case R.id.previous_date:
                getPreviousDay(Calendar.getInstance().getTime());
                break;
            case R.id.next_date:
                getNextDay(Calendar.getInstance().getTime());
                break;
        }
    }
}
