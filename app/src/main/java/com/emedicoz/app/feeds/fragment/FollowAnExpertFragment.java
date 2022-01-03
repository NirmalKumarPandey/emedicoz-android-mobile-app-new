package com.emedicoz.app.feeds.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.feeds.adapter.PeopleRVAdapter;
import com.emedicoz.app.modelo.People;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SinglecatVideoDataApiInterface;
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

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowAnExpertFragment extends Fragment {

    Activity activity;
    MasterFeedsHitResponse masterFeedsHitResponse;
    RecyclerView followexpertRV;
    RelativeLayout finishbtnRL;
    TextView subtitlefollowTV;

    ArrayList<People> peopleArrayList;
    PeopleRVAdapter peopleRVAdapter;
    LinearLayoutManager linearLayoutManager;
    int hitCounter = 0;
    Progress mprogress;

    public FollowAnExpertFragment() {
        // default constructor
    }

    public static FollowAnExpertFragment newInstance() {
        FollowAnExpertFragment fragment = new FollowAnExpertFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mprogress = new Progress(getActivity());
        mprogress.setCancelable(false);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_followanexpert, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        followexpertRV = view.findViewById(R.id.followexpertRV);
        subtitlefollowTV = view.findViewById(R.id.subtitlefollowTV);
        finishbtnRL = view.findViewById(R.id.finishbtnRL);
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        followexpertRV.setLayoutManager(linearLayoutManager);

        if (SharedPreference.getInstance().getMasterHitResponse() == null) {
            if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                networkCallForValidateDAMSUser();
            }
            networkCallForMasterFeed();
        } else {
            masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();
            if (masterFeedsHitResponse != null && masterFeedsHitResponse.getExpert_list() != null && masterFeedsHitResponse.getExpert_list().size() > 5) {
                initPeopleAdapter();
            } else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    networkCallForValidateDAMSUser();
                }
                networkCallForMasterFeed();
            }
        }

        finishbtnRL.setOnClickListener(view1 -> {
            if (masterFeedsHitResponse != null && masterFeedsHitResponse.getExpert_list() != null
                    && masterFeedsHitResponse.getExpert_list().size() >= Helper.getMinimumFollowerCount()) {
                if (((PostActivity) activity).followExpertCounter >= Helper.getMinimumFollowerCount()) {
                    SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, true);
                    Helper.GoToNextActivity(activity);
                } else {
                    Toast.makeText(activity, String.format("Choose Minimum %s experts for feeds", SharedPreference.getInstance().getMasterHitResponse()
                            .getMinimumFollowersPerStream()), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (masterFeedsHitResponse != null) {
                    if (((PostActivity) activity).followExpertCounter >= Objects.requireNonNull(masterFeedsHitResponse.getExpert_list()).size()) {
                        SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, true);
                        Helper.GoToNextActivity(activity);
                    } else {
                        Toast.makeText(activity, "Please follow atleast " + masterFeedsHitResponse.getExpert_list().size() + " experts", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void networkCallForMasterFeed() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mprogress.show();
        SinglecatVideoDataApiInterface apis = ApiClient.createService(SinglecatVideoDataApiInterface.class);
        Call<JsonObject> response = apis.getMasterFeedForUser(SharedPreference.getInstance().getLoggedInUser().getId());
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
                        hitCounter++;
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            masterFeedsHitResponse = gson.fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), MasterFeedsHitResponse.class);
                            if (masterFeedsHitResponse != null && masterFeedsHitResponse.getExpert_list() != null) {
                                SharedPreference.getInstance().setMasterHitData(masterFeedsHitResponse);
                                if (!masterFeedsHitResponse.getExpert_list().isEmpty())
                                    initPeopleAdapter();
                                else if (hitCounter < 3) {
                                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                                        networkCallForValidateDAMSUser();
                                    }
                                    networkCallForMasterFeed();
                                } else {
                                    Helper.GoToNextActivity(activity);
                                    SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, true);
                                }

                            } else if (hitCounter < 3) {
                                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                                    networkCallForValidateDAMSUser();
                                }
                                networkCallForMasterFeed();
                            } else {
                                SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, true);
                                Helper.GoToNextActivity(activity);
                            }

                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_MASTER_HIT);
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
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    private void errorCallBack(String jsonString, String apiType) {
        Toast.makeText(activity, jsonString, Toast.LENGTH_SHORT).show();
        if (API.API_GET_MASTER_HIT.equals(apiType)) {
            hitCounter++;
        }
    }

    private void initPeopleAdapter() {
        peopleArrayList = masterFeedsHitResponse.getExpert_list();
        if (!peopleArrayList.isEmpty()) {
            peopleRVAdapter = new PeopleRVAdapter(peopleArrayList, activity, Const.PEOPLE_LIST_FEEDS_COMMONS, Const.COMMON_EXPERT_PEOPLE_VIEWALL, 1);
            followexpertRV.setAdapter(peopleRVAdapter);
            followexpertRV.setVisibility(View.VISIBLE);

            if (peopleArrayList.size() > Helper.getMinimumFollowerCount())
                subtitlefollowTV.setText(String.format("Choose Minimum %s experts for feeds", Helper.getMinimumFollowerCount()));
            else
                subtitlefollowTV.setText(String.format("Choose Minimum %s experts for feeds", peopleArrayList.size()));
        } else {
            followexpertRV.setVisibility(View.GONE);
        }
    }

    private void networkCallForValidateDAMSUser() {
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.validateDAMSUser(SharedPreference.getInstance().getLoggedInUser().getDams_tokken());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            // to handle success condition
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            Helper.SignOutUser(activity);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(activity, t);
            }
        });
    }
}
