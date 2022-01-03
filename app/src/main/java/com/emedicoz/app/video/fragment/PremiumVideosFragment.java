package com.emedicoz.app.video.fragment;

import android.app.Activity;
import android.graphics.Color;
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
import com.emedicoz.app.utilso.Helper;
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

public class PremiumVideosFragment extends Fragment implements MyNetworkCall.MyNetworkCallBack {
    private static final String TAG = "PremiumVideosFragment";
    Activity activity;
    RecyclerView recyclerViewDVL;
    LinearLayoutManager linearLayoutManager;
    HashMap<Integer, ArrayList<DVLTopic>> hashMap;
    MyNetworkCall myNetworkCall;
    private ArrayList<String> decks = new ArrayList<>();

    public static PremiumVideosFragment newInstance() {

        Bundle args = new Bundle();

        PremiumVideosFragment fragment = new PremiumVideosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof BaseABNavActivity)
            ((BaseABNavActivity) activity).toolbar.setBackgroundColor(Color.parseColor("#fec80e"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_e_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hashMap = new HashMap<>();
        initView(view);
    }

    private void initView(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        recyclerViewDVL = view.findViewById(R.id.eBookRV);
        linearLayoutManager = new LinearLayoutManager(activity);
        recyclerViewDVL.setLayoutManager(linearLayoutManager);

        if (Helper.getStorageInstance(activity).getRecordObject(Const.DVL_DATA) != null) {
            DvlData dvlData = (DvlData) Helper.getStorageInstance(activity).getRecordObject(Const.DVL_DATA);
            initDVLAdapter(dvlData);
        } else {
            myNetworkCall.NetworkAPICall(API.API_DVL_DATA, true);
        }

    }
/*

    @Override
    public void onResume() {
        super.onResume();

        if (recyclerViewDVL != null && recyclerViewDVL.getAdapter() != null) {
            decks.clear();
            recyclerViewDVL.getAdapter().notifyDataSetChanged();
        }
        myNetworkCall.NetworkAPICall(API.API_DVL_DATA, true);
    }
*/

    public void initDVLAdapter(DvlData dvlData) {
        if (dvlData != null) {
            if (hashMap.size() > 0)
                hashMap.clear();
            decks = new ArrayList<>();
            if (!decks.isEmpty())
                decks.clear();
            for (int i = 0; i < dvlData.getCurriculam().getTopics().size(); i++) {
                if (dvlData.getCurriculam().getTopics().get(i).getDeck() != null &&
                        !decks.contains(dvlData.getCurriculam().getTopics().get(i).getDeck())) {
                    decks.add(dvlData.getCurriculam().getTopics().get(i).getDeck());
                }
            }

            for (int i = 0; i < decks.size(); i++) {
                ArrayList<DVLTopic> topics = new ArrayList<>();
                if (!topics.isEmpty())
                    topics.clear();
                for (int j = 0; j < dvlData.getCurriculam().getTopics().size(); j++) {
                    if (dvlData.getCurriculam().getTopics().get(j).getDeck() != null &&
                            decks.get(i).equalsIgnoreCase(dvlData.getCurriculam().getTopics().get(j).getDeck())) {
                        topics.add(dvlData.getCurriculam().getTopics().get(j));
                    }
                }
                hashMap.put(i, topics);
            }
            Helper.getStorageInstance(activity).addRecordStore(Const.DVL_DATA, dvlData);
            DVLParentAdapter adapter = new DVLParentAdapter(activity, decks, hashMap, dvlData, Const.DVL_SECTION);
            recyclerViewDVL.setAdapter(adapter);
        }
    }

    public void retryApis() {
        myNetworkCall.NetworkAPICall(API.API_DVL_DATA, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        Gson gson = new Gson();
        JSONObject data = GenericUtils.getJsonObject(jsonObject);
        DvlData dvlData = gson.fromJson(data.toString(), DvlData.class);

        initDVLAdapter(dvlData);

    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
       /* if (activity instanceof BaseABNavActivity) {
            Helper.showErrorLayoutForNav(API.API_DVL_DATA, activity, 1, 2);
        } else {
            Helper.showErrorLayoutForNoNav(API.API_DVL_DATA, activity, 1, 2);
        }*/
        Toast.makeText(activity, jsonString, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "errorCallBack: " + jsonString);
    }
}
