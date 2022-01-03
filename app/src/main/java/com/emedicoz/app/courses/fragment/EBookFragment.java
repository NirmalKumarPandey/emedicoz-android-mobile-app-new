package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.modelo.dvl.DVLTopic;
import com.emedicoz.app.modelo.dvl.DvlData;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.video.adapter.DVLParentAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
public class EBookFragment extends Fragment implements MyNetworkCall.MyNetworkCallBack {

    Activity activity;
    RecyclerView eBookRV;
    LinearLayoutManager linearLayoutManager;
    ArrayList<String> arrayList;
    DVLParentAdapter adapter;
    DvlData eBookData;
    HashMap<Integer, ArrayList<DVLTopic>> hashMap;
    String courseId;
    MyNetworkCall myNetworkCall;

    public static EBookFragment newInstance(String courseId) {
        EBookFragment fragment = new EBookFragment();
        Bundle args = new Bundle();
        args.putString(Const.COURSE_ID, courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            courseId = getArguments().getString(Const.COURSE_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (activity instanceof BaseABNavActivity)
            ((BaseABNavActivity) activity).toolbar.setBackgroundResource(R.color.dark_ebook);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_e_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        eBookRV = view.findViewById(R.id.eBookRV);
        linearLayoutManager = new LinearLayoutManager(activity);
        eBookRV.setLayoutManager(linearLayoutManager);
        arrayList = new ArrayList<>();
        hashMap = new HashMap<>();
        myNetworkCall.NetworkAPICall(API.API_GET_EBOOK_DATA, true);
    }

    private void initEBookAdapter(DvlData ebookData) {
        if (ebookData != null) {
            if (hashMap.size() > 0)
                hashMap.clear();
            ArrayList<String> decks = new ArrayList<>();
            if (!decks.isEmpty())
                decks.clear();
            for (int i = 0; i < ebookData.getCurriculam().getTopics().size(); i++) {
                if (ebookData.getCurriculam().getTopics().get(i).getDeck() != null &&
                        !decks.contains(ebookData.getCurriculam().getTopics().get(i).getDeck())) {
                    decks.add(ebookData.getCurriculam().getTopics().get(i).getDeck());
                }
            }

            Log.e("onResponse: ", "Deck_SIZE" + decks.size());
            for (int i = 0; i < decks.size(); i++) {
                ArrayList<DVLTopic> topics = new ArrayList<>();
                if (!topics.isEmpty())
                    topics.clear();
                for (int j = 0; j < ebookData.getCurriculam().getTopics().size(); j++) {
                    if (ebookData.getCurriculam().getTopics().get(j).getDeck() != null &&
                            decks.get(i).equalsIgnoreCase(ebookData.getCurriculam().getTopics().get(j).getDeck())) {
                        topics.add(ebookData.getCurriculam().getTopics().get(j));
                    }
                }
                hashMap.put(i, topics);
            }

            adapter = new DVLParentAdapter(activity, decks, hashMap, ebookData, Const.EBOOK_SECTION);
            eBookRV.setAdapter(adapter);
        }
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        params.put(Const.COURSE_ID, courseId);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        Gson gson = new Gson();
        JSONObject data = GenericUtils.getJsonObject(jsonObject);
        eBookData = gson.fromJson(data.toString(), DvlData.class);
        initEBookAdapter(eBookData);
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        Toast.makeText(activity,jsonString, Toast.LENGTH_SHORT).show();
    }
}
