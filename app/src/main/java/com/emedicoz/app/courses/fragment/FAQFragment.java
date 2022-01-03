package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.CommonListAdapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.FAQ;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.LandingPageApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FAQFragment extends Fragment {
    Activity activity;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    Course course;
    String fragType;
    List<FAQ> faqArrayList;
    CommonListAdapter commonListAdapter;

    public static FAQFragment newInstance(String fragType, Course course) {

        FAQFragment fragment = new FAQFragment();
        Bundle args = new Bundle();
        args.putString(Const.FRAG_TYPE, fragType);
        args.putSerializable(Const.COURSE, course);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            fragType = getArguments().getString(Const.FRAG_TYPE);
            course = (Course) getArguments().getSerializable(Const.COURSE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_e_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.eBookRV);
        linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        networkcallForFaqData();
    }

    public void networkcallForFaqData() {
        Progress mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        mProgress.show();
        LandingPageApiInterface apiInterface = ApiClient.createService(LandingPageApiInterface.class);
        Call<JsonObject> response = apiInterface.getFaqPageData(SharedPreference.getInstance().getLoggedInUser().getId(),
                course.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JSONArray dataArray = null;
                    Gson gson = new Gson();

                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            dataArray = GenericUtils.getJsonArray(jsonResponse);
                            faqArrayList = new ArrayList<>();
                            for (int i = 0; i < dataArray.length(); i++) {
                                FAQ faq = gson.fromJson(dataArray.get(i).toString(), FAQ.class);
                                faqArrayList.add(faq);
                            }
                            commonListAdapter = new CommonListAdapter(activity, fragType, faqArrayList);
                            recyclerView.setAdapter(commonListAdapter);
                        } else {
                            Helper.showErrorLayoutForNav(API.API_GET_FAQ_DATA, activity, 1, 1);
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
                Helper.showErrorLayoutForNav(API.API_GET_FAQ_DATA, activity, 1, 1);
            }
        });
    }
}
