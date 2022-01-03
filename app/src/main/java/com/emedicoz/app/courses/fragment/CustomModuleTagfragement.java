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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.common.BaseABNoNavActivity;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.adapter.CreateCustomModuleSubjectAdapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.custommodule.TagData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomModuleTagfragement extends Fragment implements View.OnClickListener {

    public Button allSubjectButton, chooseSubjectButton;
    Activity activity;
    View view;
    TextView tv;
    TagFlowLayout mFlowLayout;
    ArrayList<String> itemname = new ArrayList<>();
    TagAdapter<TagData> madapter;
    ArrayList<TagData> tagData = new ArrayList<>();
    HashMap<String, String> finalResponse = new HashMap<>();
    String tags = "";
    CreateCustomModuleSubjectAdapter createCustomModuleSubjectAdapter;
    private String[] mVals;
    Progress mprogress;

    public CustomModuleTagfragement() {
        // Required empty public constructor
    }

    public static CustomModuleTagfragement newInstance(HashMap<String, String> finalResponse) {
        CustomModuleTagfragement fragment = new CustomModuleTagfragement();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.finalResponse, finalResponse);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_custom_module_tagfragement, container, false);
        initView();
        getBundleData();
        ((BaseABNoNavActivity) activity).nextButton.setVisibility(View.VISIBLE);
        ((BaseABNoNavActivity) activity).nextButton.setOnClickListener(v -> {
            for (int i = 0; i < tagData.size(); i++) {
                if (tagData.get(i).isSelected()) {
                    tags = tags + "," + tagData.get(i).getId();
                }
            }
            Log.e("tag_response", tags);

            if (!tags.equals("")) {

                if (tags.startsWith(",")) {
                    tags = tags.substring(1);
                }
                if (tags.endsWith(",")) {
                    tags = tags.substring(0, tags.length() - 1);
                }

                finalResponse.put("tags", tags);

                Intent conceIntent1 = new Intent(activity, CourseActivity.class);
                conceIntent1.putExtra(Const.FRAG_TYPE, Const.SELECT_MODE);
                conceIntent1.putExtra(Const.finalResponse, finalResponse);
                activity.startActivity(conceIntent1);
            }
        });
        allSubjectButton.setSelected(true);

        allSubjectButton.setOnClickListener(this);
        chooseSubjectButton.setOnClickListener(this);
        getTagData();


        return view;
    }

    private void getBundleData() {
        if (getArguments() != null) {
            finalResponse = (HashMap<String, String>) getArguments().getSerializable(Const.finalResponse);
        }
    }

    private void getTagData() {

        mprogress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getTagList();
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e("Tag_LIST", jsonResponse.toString());
                        mprogress.dismiss();
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            if (tagData != null)
                                tagData.clear();

                            mVals = new String[jsonResponse.getJSONArray(Const.DATA).length()];
                            for (int i = 0; i < jsonResponse.getJSONArray(Const.DATA).length(); i++) {
                                tagData.add(gson.fromJson(String.valueOf(jsonResponse.getJSONArray(Const.DATA).getJSONObject(i)), TagData.class));
                                //mVals[i]=jsonResponse.getJSONArray(Const.DATA).getJSONObject(i).optString("text");
                            }
                            addtagAdapter(true);
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_REWARD_POINTS);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mprogress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_REWARD_POINTS, activity, 1, 1);
            }
        });

    }

    private void errorCallBack(String optString, String apiGetRewardPoints) {
        Toast.makeText(activity, optString, Toast.LENGTH_SHORT).show();
    }

    private void addtagAdapter(boolean b) {
        if (b) {
            final LayoutInflater mInflater = LayoutInflater.from(getActivity());
            mFlowLayout = view.findViewById(R.id.id_flowlayout);
            madapter = new TagAdapter<TagData>(tagData) {
                @Override
                public boolean setSelected(int position, TagData tagData) {
                    getItem(position).setSelected(true);
                    return true;
                }

                @Override
                public View getView(FlowLayout flowLayout, int i, TagData tagData) {
                    tv = (TextView) mInflater.inflate(R.layout.tv,
                            mFlowLayout, false);
                    tv.setText(tagData.getText());
                    tv.setSelected(true);
                    return tv;
                }

                @Override
                public void onSelected(int position, View view) {
                    super.onSelected(position, view);
                    getItem(position).setSelected(true);
                    ((TextView) view.findViewById(R.id.text)).setTextColor(ContextCompat.getColor(activity,R.color.white));
                }

                @Override
                public void unSelected(int position, View view) {
                    super.unSelected(position, view);
                    getItem(position).setSelected(false);
                    ((TextView) view.findViewById(R.id.text)).setTextColor(ContextCompat.getColor(activity,R.color.colorGray4));
                }
            };
            mFlowLayout.setAdapter(madapter);


            mFlowLayout.setOnTagClickListener((view1, position, parent) -> {
                    chooseSubjectButton.setSelected(true);
                    allSubjectButton.setSelected(false);
                    return true;
            });
        } else {
            final LayoutInflater mInflater = LayoutInflater.from(getActivity());
            mFlowLayout = view.findViewById(R.id.id_flowlayout);
            madapter = new TagAdapter<TagData>(tagData) {

                @Override
                public View getView(FlowLayout flowLayout, int i, TagData tagData) {
                    tv = (TextView) mInflater.inflate(R.layout.tv,
                            mFlowLayout, false);
                    tv.setText(tagData.getText());
                    tv.setSelected(true);
                    return tv;
                }

                @Override
                public boolean setSelected(int position, TagData tagData) {
                    getItem(position).setSelected(false);
                    return false;
                }

                @Override
                public void onSelected(int position, View view) {
                    super.onSelected(position, view);
                    getItem(position).setSelected(true);
                    ((TextView) view.findViewById(R.id.text)).setTextColor(ContextCompat.getColor(activity,R.color.white));
                }

                @Override
                public void unSelected(int position, View view) {
                    super.unSelected(position, view);
                    getItem(position).setSelected(false);
                    ((TextView) view.findViewById(R.id.text)).setTextColor(ContextCompat.getColor(activity,R.color.colorGray4));
                }
            };
            mFlowLayout.setAdapter(madapter);


            mFlowLayout.setOnTagClickListener((view1, position, parent) -> {
                    chooseSubjectButton.setSelected(true);
                    allSubjectButton.setSelected(false);
                    return true;
            });
        }
    }


    private void initView() {
        allSubjectButton = view.findViewById(R.id.all_subject_button);
        chooseSubjectButton = view.findViewById(R.id.choose_subject_button);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_subject_button:
                allSubjectButton.setSelected(true);
                chooseSubjectButton.setSelected(false);
                if (mVals != null)
                    addtagAdapter(true);

                break;
            case R.id.choose_subject_button:
                allSubjectButton.setSelected(false);
                chooseSubjectButton.setSelected(true);
                if (mVals != null)
                    addtagAdapter(false);

                break;
            default:
                break;

        }
    }
}
