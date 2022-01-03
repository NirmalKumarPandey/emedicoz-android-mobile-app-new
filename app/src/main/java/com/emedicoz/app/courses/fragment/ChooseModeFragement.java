package com.emedicoz.app.courses.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.common.BaseABNoNavActivity;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.custommodule.ModuleData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
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

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseModeFragement extends Fragment implements View.OnClickListener {

    Activity activity;
    View view;

    RadioButton radio1;
    RadioButton radio2;
    CardView questionTitle1;
    CardView questionTitle2;
    HashMap<String, String> finalResponse = new HashMap<>();
    String mode = "";
    Progress mProgress;

    public ChooseModeFragement() {
        // Required empty public constructor
    }

    public static ChooseModeFragement newInstance(HashMap<String, String> finalResponse) {
        ChooseModeFragement fragment = new ChooseModeFragement();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.finalResponse, finalResponse);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_choose_mode_fragement, container, false);
        initView();
        getBundleData();
        ((BaseABNoNavActivity) activity).nextButton.setVisibility(View.VISIBLE);
        ((BaseABNoNavActivity) activity).nextButton.setOnClickListener(view1 -> {

            if (!mode.equals("")) {
                setCustomModuleData();
            } else {
                Toast.makeText(activity, getResources().getString(R.string.choose_exam_mode), Toast.LENGTH_SHORT).show();
            }
        });

        questionTitle1.setOnClickListener(this);
        questionTitle2.setOnClickListener(this);
        return view;
    }

    private void setCustomModuleData() {
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response;
        response = apiInterface.setModuleData(SharedPreference.getInstance().getLoggedInUser().getId(),
                finalResponse.get(Constants.CustomModuleExtras.NUMBER_OF_QUESTION),
                finalResponse.get(Constants.CustomModuleExtras.DIFFICULTY_LEVEL),
                finalResponse.get(Constants.CustomModuleExtras.QUES_FROM),
                finalResponse.get(Constants.CustomModuleExtras.SUBJECT),
                finalResponse.get(Constants.CustomModuleExtras.TOPICS),
                mode,
                SharedPreference.getInstance().getString(Constants.CustomModuleExtras.IS_TAG).equals("1") ? finalResponse.get(Constants.CustomModuleExtras.TAGS) : "");

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e("Response", jsonResponse.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {

                            SharedPreference.getInstance().putString("STATE", "");
                            ModuleData moduleData = new Gson().fromJson(String.valueOf(GenericUtils.getJsonObject(jsonResponse)), ModuleData.class);

                            Intent courceIntent = new Intent(activity, CourseActivity.class);
                            courceIntent.putExtra(Const.FRAG_TYPE, Const.STARTMODULE);
                            courceIntent.putExtra(Const.IS_MODULE_START, true);
                            courceIntent.putExtra(Const.moduleData, moduleData);
                            activity.startActivity(courceIntent);
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE));
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Failier", call.toString());
                Log.e("Failier", t.toString());
                Helper.showErrorLayoutForNoNav(API.API_GET_REWARD_POINTS, activity, 1, 1);
            }
        });
    }

    private void errorCallBack(String optString) {
        Toast.makeText(activity, optString, Toast.LENGTH_SHORT).show();
    }

    private void getBundleData() {
        if (getArguments() != null) {
            finalResponse = (HashMap<String, String>) getArguments().getSerializable(Const.finalResponse);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.question_title1:
                radio1.setChecked(true);
                radio2.setChecked(false);
                mode = "1";
                break;
            case R.id.question_title2:
                radio1.setChecked(false);
                radio2.setChecked(true);
                mode = "2";
                break;
            default:
                break;
        }
    }

    private void initView() {


        questionTitle1 = view.findViewById(R.id.question_title1);
        questionTitle2 = view.findViewById(R.id.question_title2);
        radio1 = view.findViewById(R.id.radio1);
        radio2 = view.findViewById(R.id.radio2);
    }
}
