package com.emedicoz.app.courses.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.common.BaseABNoNavActivity;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.modelo.custommodule.ModuleData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomModuleStartFragement extends Fragment {

    Activity activity;
    View view;
    TextView creationData;
    TextView tvSubject;
    TextView tvDificultyLevel;
    TextView tvExamMode;
    TextView noOfQuestions;
    TextView createAndDeleteModule;
    Button startQuiz;
    ModuleData moduleData;
    public boolean isModuleStart = false;
    Progress mProgress;

    public CustomModuleStartFragement() {
        // Required empty public constructor
    }

    public static CustomModuleStartFragement newInstance(ModuleData moduleData, boolean isModuleStart) {
        CustomModuleStartFragement fragment = new CustomModuleStartFragement();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.moduleData, moduleData);
        bundle.putBoolean(Const.IS_MODULE_START, isModuleStart);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_custom_module_start, container, false);
        Log.e("onCreateView: ", "onCreateView is called");
        initView();
        getBundleData();
        ((BaseABNoNavActivity) activity).nextButton.setVisibility(View.GONE);
        createAndDeleteModule.setOnClickListener(v -> deleteModule());

        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (moduleData.getTest_result() != null) {
                    if (moduleData.getTest_result().getState().equalsIgnoreCase("0") || SharedPreference.getInstance().getString("STATE").equals("0")) {

                        ResultTestSeries resultTestSeries = new ResultTestSeries();
                        resultTestSeries.setCorrectCount(moduleData.getTest_result().getCorrect_count());
                        resultTestSeries.setIncorrectCount(moduleData.getTest_result().getIncorrect_count());
                        resultTestSeries.setNonAttempt(moduleData.getTest_result().getNon_attempt());
                        resultTestSeries.setTestSeriesId(moduleData.getId());
                        resultTestSeries.setAccuracy(moduleData.getAccuracy());
                        Intent resultScreen = new Intent(activity, QuizActivity.class);
                        resultScreen.putExtra(Const.FRAG_TYPE, Const.QBANK_RESULT);
                        resultScreen.putExtra(Const.RESULT_SCREEN, resultTestSeries);
                        resultScreen.putExtra("CUSTOM", true);
                        activity.startActivity(resultScreen);

                    } else if (moduleData.getTest_result().getState().equalsIgnoreCase("1") || SharedPreference.getInstance().getString("STATE").equals("1")) {
                        if (moduleData.getMode().equals("1")) {
                            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST);
                        } else if (moduleData.getMode().equals("2")) {
                            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.Q_BANK);
                        }
                        Intent quizView = new Intent(activity, TestBaseActivity.class);
                        quizView.putExtra(Const.STATUS, false);
                        quizView.putExtra("CUSTOM", true);
                        quizView.putExtra(Const.TEST_SERIES_ID, moduleData.getId());
                        activity.startActivity(quizView);
                        // NetworkAPICall(API.API_GET_COMPLETE_INFO_CUSTOM_TEST_SERIES, true);
                    } else {
                        if (moduleData.getMode().equals("1")) {
                            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST);
                        } else if (moduleData.getMode().equals("2")) {
                            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.Q_BANK);
                        }
                        Intent quizView = new Intent(activity, TestBaseActivity.class);
                        quizView.putExtra(Const.STATUS, false);
                        quizView.putExtra("CUSTOM", true);
                        quizView.putExtra(Const.TEST_SERIES_ID, moduleData.getId());
                        activity.startActivity(quizView);
                    }
//                    Intent quizView = new Intent(activity, TestBaseActivity.class);
//                    quizView.putExtra(Const.STATUS, false);
//                    quizView.putExtra(Constants.Extras.CUSTOM, true);
//                    quizView.putExtra(Const.TEST_SERIES_ID, moduleData.getId());
//                    activity.startActivity(quizView);
                } else if (SharedPreference.getInstance().getString("STATE").equals("0")) {
                    ResultTestSeries resultTestSeries = SharedPreference.getInstance().getResultTestSeries();
                    Intent resultScreen = new Intent(activity, QuizActivity.class);
                    resultScreen.putExtra(Const.FRAG_TYPE, Const.QBANK_RESULT);
                    resultScreen.putExtra(Const.RESULT_SCREEN, resultTestSeries);
                    resultScreen.putExtra(Constants.Extras.CUSTOM, true);
                    activity.startActivity(resultScreen);
                } else {
                    if (SharedPreference.getInstance().getString("STATE").equals("1")) {
                        if (moduleData.getMode().equals("1")) {
                            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST);
                        } else if (moduleData.getMode().equals("2")) {
                            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.Q_BANK);
                        }
                        Intent quizView = new Intent(activity, TestBaseActivity.class);
                        quizView.putExtra(Const.STATUS, false);
                        quizView.putExtra("CUSTOM", true);
                        quizView.putExtra(Const.TEST_SERIES_ID, moduleData.getId());
                        activity.startActivity(quizView);
                    } else if (SharedPreference.getInstance().getString("STATE").equals("0")) {
                        ResultTestSeries resultTestSeries = SharedPreference.getInstance().getResultTestSeries();
                        Intent resultScreen = new Intent(activity, QuizActivity.class);
                        resultScreen.putExtra(Const.FRAG_TYPE, Const.QBANK_RESULT);
                        resultScreen.putExtra(Const.RESULT_SCREEN, resultTestSeries);
                        resultScreen.putExtra("CUSTOM", true);
                        activity.startActivity(resultScreen);
                    } else {
                        if (moduleData.getMode().equals("1")) {
                            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST);
                        } else if (moduleData.getMode().equals("2")) {
                            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.Q_BANK);
                        }
                        Intent quizView = new Intent(activity, TestBaseActivity.class);
                        quizView.putExtra(Const.STATUS, false);
                        quizView.putExtra(Constants.Extras.CUSTOM, true);
                        quizView.putExtra(Const.TEST_SERIES_ID, moduleData.getId());
                        activity.startActivity(quizView);
                    }
//                    Intent quizView = new Intent(activity, TestBaseActivity.class);
//                    quizView.putExtra(Const.STATUS, false);
//                    quizView.putExtra(Constants.Extras.CUSTOM, true);
//                    quizView.putExtra(Const.TEST_SERIES_ID, moduleData.getId());
//                    activity.startActivity(quizView);
                }
            }
        });
        setData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume: ", "onResume is called");
        if (SharedPreference.getInstance().getString("STATE").equals("0")) {
            startQuiz.setText("Review");
        } else if (SharedPreference.getInstance().getString("STATE").equals("1")) {
            startQuiz.setText("Resume");
        } else {
            startQuiz.setText("Start Now");
        }
    }

    private void deleteModule() {

        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.deleteModuleData(SharedPreference.getInstance().getLoggedInUser().getId(), moduleData.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        mProgress.dismiss();
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            SharedPreference.getInstance().putString("STATE", "");
                            Intent conceIntent = new Intent(activity, CourseActivity.class);
                            conceIntent.putExtra(Const.FRAG_TYPE, "CUSTOM_MODULE");
                            activity.startActivity(conceIntent);
                            activity.finish();

                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE));
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mProgress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_REWARD_POINTS, activity, 1, 1);
            }
        });
    }

    private void errorCallBack(String optString) {
        Toast.makeText(activity, optString, Toast.LENGTH_SHORT).show();
    }

    private void setData() {
        if (moduleData != null) {
            creationData.setText(Helper.gaetFormatedDate(Long.parseLong(moduleData.getCreation()) * 1000));
            tvSubject.setText("" + moduleData.getSubjects() + " Subject Select");
            tvDificultyLevel.setText("" + moduleData.getDifficulty());
            if (moduleData.getMode().equalsIgnoreCase("1"))
                tvExamMode.setText("Exam Mode");
            else
                tvExamMode.setText("Regular Mode");
            noOfQuestions.setText(moduleData.getNoOfQuestion() + " Questions");
        }
    }


    private void getBundleData() {
        if (getArguments() != null) {
            moduleData = (ModuleData) getArguments().getSerializable(Const.moduleData);
            if (getArguments().containsKey(Const.IS_MODULE_START))
                isModuleStart = getArguments().getBoolean(Const.IS_MODULE_START);
        }
    }

    private void initView() {
        creationData = view.findViewById(R.id.creation_data);
        tvSubject = view.findViewById(R.id.tv_subject);
        tvDificultyLevel = view.findViewById(R.id.tv_dificulty_level);
        tvExamMode = view.findViewById(R.id.tv_exammode);
        noOfQuestions = view.findViewById(R.id.noOfQuestions);
        startQuiz = view.findViewById(R.id.startQuiz);
        createAndDeleteModule = view.findViewById(R.id.createAndDeleteModule);
    }
}
