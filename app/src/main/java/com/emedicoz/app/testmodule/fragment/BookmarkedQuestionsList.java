package com.emedicoz.app.testmodule.fragment;


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
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.bookmark.adapter.TestAdapternew;
import com.emedicoz.app.bookmark.model.TestModel;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.SharedPreference;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkedQuestionsList extends Fragment {


    RecyclerView recyclerView;
    Activity activity;
    List<TestModel> itemList2 = new ArrayList<>();
    Progress mProgress;
    String lastPostId = "";
    String subjectId = "";

    public BookmarkedQuestionsList() {
        // Required empty public constructor
    }

    public static BookmarkedQuestionsList newInstance(String subjectId) {
        BookmarkedQuestionsList fragment = new BookmarkedQuestionsList();
        Bundle args = new Bundle();
        args.putString(Constants.Extras.SUBJECT_ID, subjectId);
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_bookmarked_questions_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            subjectId = getArguments().getString(Constants.Extras.SUBJECT_ID);
        }
        recyclerView = view.findViewById(R.id.bookmarkedListRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        networkCallForTest();
    }

    private void networkCallForTest() {
        mProgress.show();
        ApiInterface api = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = api.allSubjectBookmark(SharedPreference.getInstance().getLoggedInUser().getId(), subjectId, lastPostId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    mProgress.dismiss();
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            JSONArray jsonArray = GenericUtils.getJsonArray(jsonResponse);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.optJSONObject(i);
                                TestModel test = new Gson().fromJson(data.toString(), TestModel.class);
                                itemList2.add(test);
                            }
                            TestAdapternew adapter = new TestAdapternew(activity, itemList2, subjectId, "", "", "");
                            recyclerView.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

            }
        });
    }
}
