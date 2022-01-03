package com.emedicoz.app.courses.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.courses.adapter.NavigationQuizAdapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.customviews.TestSeriesOptionWebView;
import com.emedicoz.app.modelo.courses.quiz.Questions;
import com.emedicoz.app.modelo.courses.quiz.QuizModel;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.QuizApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Quiz extends Fragment implements View.OnClickListener {
    private static final String TAG = "Quiz";
    public QuizModel quiz;
    Activity activity;
    String frag_type = "";
    ImageView resumeBtn;
    Questions questions;
    TextView resumeQuiz, quizTime, questCountTV, optionIconTV;
    TestSeriesOptionWebView questionTV, answerTV;
    Button previousBtn, nextBtn;
    RelativeLayout resumeQuizLL, finishQuizLL;
    LinearLayout answerLL, mcqItemLL, shareRankLL;
    int timeinMillis, timeinSeconds;
    ProgressBar quesTimeProgressBar;
    CountDownTimer mcountDown;
    ArrayList<View> viewArrayList;
    int currentQues = 0;
    ArrayList<String> userAnswered;
    JsonArray question_dump;
    JsonObject questionArray;
    CardView questionQuizCV;
    Progress mprogress;

    View.OnClickListener optionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < viewArrayList.size(); i++) {
                if (v == viewArrayList.get(i)) {
                    if (quiz.getBasic_info().getTest_type().equals("1")) {
                        LinearLayout view = (LinearLayout) viewArrayList.get(i).getTag(R.id.optionsAns);
                        if ((view.getChildAt(0)).getBackground().getConstantState()
                                .equals(ContextCompat.getDrawable(activity, R.drawable.circle_bg_true).getConstantState())) {
                            (view.getChildAt(0)).setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_bg));
                            questions.getUserAnswered().remove(String.valueOf(viewArrayList.indexOf(view) + 1));
                            Collections.sort(questions.getUserAnswered());
                            questions.setUser_answer(TextUtils.join(",", questions.getUserAnswered()));
                        } else {
                            (view.getChildAt(0)).setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_bg_true));
                            if (!questions.getUserAnswered().contains(String.valueOf(viewArrayList.indexOf(view) + 1))) {
                                questions.getUserAnswered().add(String.valueOf(viewArrayList.indexOf(view) + 1));
                            }
                            Collections.sort(questions.getUserAnswered());
                            questions.setUser_answer(TextUtils.join(",", questions.getUserAnswered()));
                        }
                    } else if (questions.getQuestion_type().equals("SC") || questions.getQuestion_type().equals("TF")) {
                        String options = (String) viewArrayList.get(i).getTag(R.id.questions);
                        LinearLayout view = (LinearLayout) viewArrayList.get(i).getTag(R.id.optionsAns);
                        (view.getChildAt(0)).setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_bg_true));
                        questions.setUser_answer(String.valueOf(viewArrayList.indexOf(view) + 1));
                        Log.e("Position of Index", String.valueOf(viewArrayList.indexOf(view) + 1));
                    } else {
                        LinearLayout view = (LinearLayout) viewArrayList.get(i).getTag(R.id.optionsAns);
                        if ((view.getChildAt(0)).getBackground().getConstantState()
                                .equals(ContextCompat.getDrawable(activity, R.drawable.circle_bg_true).getConstantState())) {
                            (view.getChildAt(0)).setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_bg));
                            questions.getUserAnswered().remove(String.valueOf(viewArrayList.indexOf(view) + 1));
                            Collections.sort(questions.getUserAnswered());
                            questions.setUser_answer(TextUtils.join(",", questions.getUserAnswered()));
                        } else {
                            (view.getChildAt(0)).setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_bg_true));
                            if (!questions.getUserAnswered().contains(String.valueOf(viewArrayList.indexOf(view) + 1))) {
                                questions.getUserAnswered().add(String.valueOf(viewArrayList.indexOf(view) + 1));
                            }
                            Collections.sort(questions.getUserAnswered());
                            questions.setUser_answer(TextUtils.join(",", questions.getUserAnswered()));
                        }
                    }
                } else {
                    if (!quiz.getBasic_info().getTest_type().equals("1") &&
                            (questions.getQuestion_type().equals("SC") || questions.getQuestion_type().equals("TF"))) {
                        String options = (String) viewArrayList.get(i).getTag(R.id.questions);
                        LinearLayout view = (LinearLayout) viewArrayList.get(i).getTag(R.id.optionsAns);
                        (view.getChildAt(0)).setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_bg));
                    }
                }
            }
        }
    };

    public Quiz() {
        // Required empty public constructor
    }

    public static Quiz newInstance(String frag_type, QuizModel quiz) {
        Quiz quizFragment = new Quiz();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.TESTSERIES, quiz);
        bundle.putSerializable(Const.FRAG_TYPE, frag_type);
        quizFragment.setArguments(bundle);
        return quizFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //        GenericUtils.manageScreenShotFeature(activity);
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    public void initalizeNavigationView() {
        ((QuizActivity) activity).controllerRV.setLayoutManager(new GridLayoutManager(activity, ((QuizActivity) activity).DEFAULT_SPAN_COUNT, LinearLayoutManager.VERTICAL, false));
        ((QuizActivity) activity).navigationQuizAdapter = new NavigationQuizAdapter(activity, quiz);
        ((QuizActivity) activity).controllerRV.setAdapter(((QuizActivity) activity).navigationQuizAdapter);

        ((QuizActivity) activity).attemptedIV.setImageDrawable(TextDrawable.builder()
                .buildRound("A", ContextCompat.getColor(activity, R.color.colorPrimary)));
        ((QuizActivity) activity).notAttemptedIV.setImageDrawable(TextDrawable.builder()
                .buildRound("NA", ContextCompat.getColor(activity, R.color.greayrefcode_dark)));
    }

    public void controlQuizQuestion(int currentQuesCount) {
        Log.e("Clicked position", String.valueOf(currentQuesCount));

        clearQuestionView();

        if (!TextUtils.isEmpty(quiz.getQuestion_bank().get(currentQues).getUser_answer())) {
            quiz.getQuestion_bank().get(currentQues).setAnswered(true);
            ((QuizActivity) activity).navigationQuizAdapter.notifyDataSetChanged();
        }

        if (quiz.getQuestion_bank().size() == (currentQuesCount + 1)) {
            currentQues = currentQuesCount;
            nextBtn.setText(getString(R.string.submit_rev));
            setQuestionData(quiz.getQuestion_bank().get(currentQues));
        } else {
            currentQues = currentQuesCount;
            nextBtn.setText(getString(R.string.next));
            setQuestionData(quiz.getQuestion_bank().get(currentQues));
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mprogress = new Progress(getActivity());
        mprogress.setCancelable(false);

        final int valueTime;
        initViews(view);

        if (((QuizActivity) activity).navigationQuizAdapter == null) {
            initalizeNavigationView();
        }
        if (Helper.getStorageInstance(activity).getRecordObject(quiz.getBasic_info().getId()) != null) {
            quiz = (QuizModel) Helper.getStorageInstance(activity).getRecordObject(quiz.getBasic_info().getId());
            setQuestionData(quiz.getQuestion_bank().get(currentQues));
            valueTime = Integer.parseInt(quiz.getBasic_info().getTime_in_mins()) * 1000;
        } else {
            setQuestionData(quiz.getQuestion_bank().get(currentQues));
            valueTime = Integer.parseInt(quiz.getBasic_info().getTime_in_mins()) * 60 * 1000;
        }


        quesTimeProgressBar.setMax(valueTime);
        quesTimeProgressBar.getProgressDrawable().setColorFilter(
                ContextCompat.getColor(activity, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
//        quesTimeProgressBar.setScaleY(2.25f);
        mcountDown = new CountDownTimer(valueTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                quesTimeProgressBar.setMax(valueTime);
                quesTimeProgressBar.setProgress(valueTime - (int) millisUntilFinished);
//                Log.e("Log_tag", String.valueOf(quesTimeProgressBar.getProgress()));

                String minute = String.valueOf(((millisUntilFinished / 1000) / 60));
                String sec = String.valueOf((millisUntilFinished / 100 / 1000));
                int seconds = (int) ((millisUntilFinished / 1000) % 60);
                int minutes = (int) ((millisUntilFinished / 1000) / 60);
                quizTime.setText(minutes + ":" + seconds + "");
                timeinMillis = (int) ((millisUntilFinished / 1000));
                timeinSeconds = (quesTimeProgressBar.getProgress() / 1000) % 60;
                Log.e("timeinMillis", String.valueOf(timeinSeconds));
            }

            @Override
            public void onFinish() {
                quizTime.setText("0" + ":" + "0" + "''");
                quesTimeProgressBar.setMax(valueTime * 1000);
                timeinMillis = ((valueTime / 1000) % 60);
                timeinSeconds = (quesTimeProgressBar.getProgress() / 1000) % 60;
                Log.e("timeinMillis", String.valueOf(timeinMillis));
                onTimerFinished();
            }
        }.start();
    }

    private void onTimerFinished() {
        question_dump = new JsonArray();
        for (Questions questions : quiz.getQuestion_bank()) {
            JsonObject question = new JsonObject();
            question.addProperty("question_id", questions.getId());
            question.addProperty("answer", (TextUtils.isEmpty(questions.getUser_answer()) ? "" : questions.getUser_answer()));
            question_dump.add(question);
        }
        mcountDown.cancel();
        networkCallForcompleteinfoTestSeries();//NetworkAPICall(API.API_COMPLETE_INFO_TESTSERIES, true);
    }

    private void initViews(View view) {
        questCountTV = view.findViewById(R.id.quesCount);
        resumeQuiz = view.findViewById(R.id.playTextTV);
        resumeBtn = view.findViewById(R.id.playBtn);
        quizTime = view.findViewById(R.id.time_slot);
        resumeQuizLL = view.findViewById(R.id.resumeQuizLL);
        finishQuizLL = view.findViewById(R.id.finishQuizLL);
        questionTV = view.findViewById(R.id.questionQuizTV);
        previousBtn = view.findViewById(R.id.prevQuizBT);
        nextBtn = view.findViewById(R.id.nextQuizBT);
        answerLL = view.findViewById(R.id.quizQuestionLL);
        quesTimeProgressBar = view.findViewById(R.id.timer_progress);
        questionQuizCV = view.findViewById(R.id.questionQuizCV);

        previousBtn.setVisibility(View.INVISIBLE);
        previousBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        finishQuizLL.setOnClickListener(this);
        resumeQuizLL.setOnClickListener(this);
    }

    private void setQuestionData(Questions questionText) {
        questions = quiz.getQuestion_bank().get(currentQues);
        if (viewArrayList.size() > 0) {
            viewArrayList.clear();
        }
        if (currentQues == 0) {
            previousBtn.setVisibility(View.INVISIBLE);
        } else {
            previousBtn.setVisibility(View.VISIBLE);
        }
        if (answerLL.getChildCount() > 0)
            answerLL.removeAllViews();

        questionQuizCV.removeAllViews();
        TestSeriesOptionWebView questionTV = null;
        questionTV = (TestSeriesOptionWebView) getLayoutInflater().inflate(R.layout.quiz_solution_description, null);
        questionTV.loadData(questionText.getQuestion(), "text/html", "utf-8");
        questionTV.setDisableWebViewTouchListener(true);
        questionQuizCV.addView(questionTV);
//        questionTV.loadData(questionText.getQuestion(), "text/html", "utf-8");
//        questionTV.setDisableWebViewTouchListener(true);
        questCountTV.setText((currentQues + 1) + "/" + quiz.getQuestion_bank().size());

        if (quiz.getBasic_info().getTest_type().equals("1") || questionText.getQuestion_type().equalsIgnoreCase("MC")) {
            if (!TextUtils.isEmpty(questionText.getOption_1()))
                answerLL.addView(initAnswerMCViews("1", questionText.getOption_1(), questions));
            if (!TextUtils.isEmpty(questionText.getOption_2()))
                answerLL.addView(initAnswerMCViews("2", questionText.getOption_2(), questions));
            if (!TextUtils.isEmpty(questionText.getOption_3()))
                answerLL.addView(initAnswerMCViews("3", questionText.getOption_3(), questions));
            if (!TextUtils.isEmpty(questionText.getOption_4()))
                answerLL.addView(initAnswerMCViews("4", questionText.getOption_4(), questions));
            if (!TextUtils.isEmpty(questionText.getOption_5()))
                answerLL.addView(initAnswerMCViews("5", questionText.getOption_5(), questions));
        } else if (questionText.getQuestion_type().equalsIgnoreCase("SC") || questionText.getQuestion_type().equalsIgnoreCase("TF")) {
            if (!TextUtils.isEmpty(questionText.getOption_1()))
                answerLL.addView(initAnswerSCViews("1", questionText.getOption_1(), questions));
            if (!TextUtils.isEmpty(questionText.getOption_2()))
                answerLL.addView(initAnswerSCViews("2", questionText.getOption_2(), questions));
            if (!TextUtils.isEmpty(questionText.getOption_3()))
                answerLL.addView(initAnswerSCViews("3", questionText.getOption_3(), questions));
            if (!TextUtils.isEmpty(questionText.getOption_4()))
                answerLL.addView(initAnswerSCViews("4", questionText.getOption_4(), questions));
            if (!TextUtils.isEmpty(questionText.getOption_5()))
                answerLL.addView(initAnswerSCViews("5", questionText.getOption_5(), questions));
        }
    }

    private LinearLayout initAnswerMCViews(String text, String questions, Questions questionsModel) {
        LinearLayout view = (LinearLayout) View.inflate(activity, R.layout.mcq_quiz, null);

        answerTV = view.findViewById(R.id.optionTextTV);
        optionIconTV = view.findViewById(R.id.optionIconTV);
        mcqItemLL = view.findViewById(R.id.mcqlayout_LL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(3, 3, 3, 3);
        mcqItemLL.setLayoutParams(lp);

        if (questionsModel.isAnswered()) {
            String[] answer = questionsModel.getUser_answer().split(",");
            for (int i = 0; i < answer.length; i++) {
                if (answer[i].equals(text)) {
//            answerTV.loadData(URLDecoder.decode(questions), "text/html", "utf-8");
                    answerTV.loadData(questions, "text/html", "utf-8");
                    answerTV.setDisableWebViewTouchListener(true);
                    optionIconTV.setText(text);
                    optionIconTV.setBackgroundResource(R.drawable.circle_bg_true);
                } else {
//                    answerTV.loadData(URLDecoder.decode(questions), "text/html", "utf-8");
                    answerTV.loadData(questions, "text/html", "utf-8");
                    answerTV.setDisableWebViewTouchListener(true);
                    optionIconTV.setText(text);
                }
            }
        } else {
//            answerTV.loadData(URLDecoder.decode(questions), "text/html", "utf-8");
            answerTV.loadData(questions, "text/html", "utf-8");
            answerTV.setDisableWebViewTouchListener(true);
            optionIconTV.setText(text);
        }

        mcqItemLL.setTag(R.id.questions, optionIconTV.getText().toString());
        mcqItemLL.setTag(R.id.optionsAns, mcqItemLL);
        mcqItemLL.setOnClickListener(optionClickListener);
        viewArrayList.add(mcqItemLL);
        return view;
    }

    private LinearLayout initAnswerSCViews(String text, String questions, Questions questionsModel) {

        LinearLayout view = (LinearLayout) View.inflate(activity, R.layout.mcq_quiz, null);

        answerTV = view.findViewById(R.id.optionTextTV);
        optionIconTV = view.findViewById(R.id.optionIconTV);
        mcqItemLL = view.findViewById(R.id.mcqlayout_LL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(3, 3, 3, 3);
        mcqItemLL.setLayoutParams(lp);

        if (questionsModel.isAnswered()) {
            if (questionsModel.getUser_answer().equals(text)) {
//            answerTV.loadData(URLDecoder.decode(questions), "text/html", "utf-8");
                answerTV.loadData(questions, "text/html", "utf-8");
                answerTV.setDisableWebViewTouchListener(true);
                optionIconTV.setText(text);
                optionIconTV.setBackgroundResource(R.drawable.circle_bg_true);
            } else {
//                answerTV.loadData(URLDecoder.decode(questions), "text/html", "utf-8");
                answerTV.loadData(questions, "text/html", "utf-8");
                answerTV.setDisableWebViewTouchListener(true);
                optionIconTV.setText(text);
            }
        } else {
//            answerTV.loadData(URLDecoder.decode(questions), "text/html", "utf-8");
            answerTV.loadData(questions, "text/html", "utf-8");
            answerTV.setDisableWebViewTouchListener(true);
            optionIconTV.setText(text);
        }

        mcqItemLL.setTag(R.id.questions, optionIconTV.getText().toString());
        mcqItemLL.setTag(R.id.optionsAns, mcqItemLL);
        mcqItemLL.setOnClickListener(optionClickListener);
        viewArrayList.add(mcqItemLL);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate of Quiz ");
        if (getArguments() != null) {
            frag_type = getArguments().getString(Const.FRAG_TYPE);
            quiz = (QuizModel) getArguments().getSerializable(Const.TESTSERIES);
        }
        activity = getActivity();
        viewArrayList = new ArrayList<>();
    }

    public void networkCallForcompleteinfoTestSeries() {
        mprogress.show();
        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Log.e("TEST_GIVEN", "SECOND:" + quiz.getBasic_info().getId() + "third" + timeinSeconds + "fourth" + question_dump.toString());
        Call<JsonObject> response = apiInterface.getInstructorData(SharedPreference.getInstance().getLoggedInUser().getId(),
                quiz.getBasic_info().getId(), String.valueOf(timeinSeconds), question_dump.toString());
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
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            Helper.showErrorLayoutForNoNav(API.API_COMPLETE_INFO_TESTSERIES, activity, 0, 0);

                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            ResultTestSeries resultTestSeries = gson.fromJson(data.toString(), ResultTestSeries.class);
                            Helper.getStorageInstance(activity).deleteRecord(quiz.getBasic_info().getId());
                            SharedPreference.getInstance().putString(Const.QUIZSTATUS, Const.FINISH);
                            mcountDown.cancel();
//                     TODO have to open the screen of result and all.
                            Intent resultScreen = new Intent(activity, QuizActivity.class);
                            resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                            resultScreen.putExtra(Const.TESTSERIES, quiz);
                            resultScreen.putExtra(Const.RESULT_SCREEN, resultTestSeries);
                            activity.startActivity(resultScreen);
                            activity.finish();
                        } else {
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
                                Helper.showErrorLayoutForNoNav(API.API_COMPLETE_INFO_TESTSERIES, activity, 1, 1);

                            if (jsonResponse.optString(Constants.Extras.MESSAGE).contains(activity.getResources().getString(R.string.something_went_wrong_string)))
                                Helper.showErrorLayoutForNoNav(API.API_COMPLETE_INFO_TESTSERIES, activity, 1, 2);

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
                Helper.showErrorLayoutForNoNav(API.API_COMPLETE_INFO_TESTSERIES, activity, 1, 1);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finishQuizLL:
                showFinishPopup();
                break;


            case R.id.resumeQuizLL:
                //TODO resume and pause functionality
                resumeBtn.setImageResource(R.mipmap.pause_blue);
                showResumePopup(true);

                break;

            case R.id.nextQuizBT:

                clearQuestionView();

                if (!TextUtils.isEmpty(quiz.getQuestion_bank().get(currentQues).getUser_answer())) {
                    quiz.getQuestion_bank().get(currentQues).setAnswered(true);
                    ((QuizActivity) activity).navigationQuizAdapter.notifyDataSetChanged();
                }

                if (quiz.getQuestion_bank().size() == (currentQues + 1)) {
                    question_dump = new JsonArray();
                    for (Questions questions : quiz.getQuestion_bank()) {
                        JsonObject question = new JsonObject();
                        question.addProperty("question_id", questions.getId());
                        question.addProperty("answer", (TextUtils.isEmpty(questions.getUser_answer()) ? "" : questions.getUser_answer()));
                        question_dump.add(question);
                    }
                    networkCallForcompleteinfoTestSeries();//NetworkAPICall(API.API_COMPLETE_INFO_TESTSERIES, true);
                } else if (quiz.getQuestion_bank().size() == (currentQues + 2)) {
                    currentQues++;
                    nextBtn.setText(getString(R.string.submit_rev));
                    setQuestionData(quiz.getQuestion_bank().get(currentQues));
                } else {
                    currentQues++;
                    setQuestionData(quiz.getQuestion_bank().get(currentQues));
                }
                break;

            case R.id.prevQuizBT:

                if (!TextUtils.isEmpty(quiz.getQuestion_bank().get(currentQues).getUser_answer())) {
                    quiz.getQuestion_bank().get(currentQues).setAnswered(true);
                    ((QuizActivity) activity).navigationQuizAdapter.notifyDataSetChanged();
                }
                if (currentQues != 0) {
                    currentQues--;
                }
                nextBtn.setText(getString(R.string.next));
                setQuestionData(quiz.getQuestion_bank().get(currentQues));
                break;
        }
    }

    private void clearQuestionView() {

    }

    private void showFinishPopup() {
        View v = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name), activity.getString(R.string.finishQuiz));

        Button btnCancel, btnSubmit;

        btnCancel = v.findViewById(R.id.btn_cancel);
        btnSubmit = v.findViewById(R.id.btn_submit);

        btnCancel.setText(getString(R.string.resume));
        btnSubmit.setText(getString(R.string.submit));
        btnSubmit.setAllCaps(false);

        btnCancel.setOnClickListener(v1 -> Helper.dismissDialog());

        btnSubmit.setOnClickListener(v1 -> {
            Helper.dismissDialog();
            question_dump = new JsonArray();
            for (Questions questions : quiz.getQuestion_bank()) {
                JsonObject question = new JsonObject();
                question.addProperty("question_id", questions.getId());
                question.addProperty("answer", (TextUtils.isEmpty(questions.getUser_answer()) ? "" : questions.getUser_answer()));
                question_dump.add(question);
            }
            mcountDown.cancel();
            networkCallForcompleteinfoTestSeries();//NetworkAPICall(API.API_COMPLETE_INFO_TESTSERIES, true);
        });
    }

    public void showResumePopup(boolean callingMethod) {
        if (callingMethod) {
            View v = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name), activity.getString(R.string.pauseQuiz));

            Button btnCancel, btnSubmit;

            btnCancel = v.findViewById(R.id.btn_cancel);
            btnSubmit = v.findViewById(R.id.btn_submit);

            btnCancel.setText(getString(R.string.cancel));
            btnSubmit.setText(getString(R.string.pause));

            btnCancel.setOnClickListener(v1 -> Helper.dismissDialog());

            btnSubmit.setOnClickListener(v1 -> {
                Helper.dismissDialog();
                quiz.getBasic_info().setTime_in_mins(String.valueOf(timeinMillis));
                quiz.setResume(true);
                quiz.setQuesCount(currentQues);
                Helper.getStorageInstance(activity).addRecordStore(quiz.getBasic_info().getId(), quiz);
                mcountDown.cancel();
                activity.finish();
            });
        } else {
            View v = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name), activity.getString(R.string.are_you_sure_you_want_to_quit_the_quiz));

            Button btnCancel, btnSubmit;
            btnCancel = v.findViewById(R.id.btn_cancel);
            btnSubmit = v.findViewById(R.id.btn_submit);

            btnCancel.setText(getString(R.string.no));
            btnSubmit.setText(getString(R.string.yes));

            btnCancel.setOnClickListener(v1 -> Helper.dismissDialog());

            btnSubmit.setOnClickListener(v1 -> {
                Helper.dismissDialog();
                quiz.getBasic_info().setTime_in_mins(String.valueOf(timeinMillis));
                quiz.setResume(true);
                quiz.setQuesCount(currentQues);
                Helper.getStorageInstance(activity).addRecordStore(quiz.getBasic_info().getId(), quiz);
                mcountDown.cancel();
                activity.finish();
            });
        }
    }
}
