package com.emedicoz.app.flashcard.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.activity.FlashCardActivity;
import com.emedicoz.app.flashcard.adapter.VaultParentAdapter;
import com.emedicoz.app.flashcard.model.FlashCards;
import com.emedicoz.app.flashcard.model.MainDeck;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;

public class AllCardsFragment extends Fragment implements MyNetworkCall.MyNetworkCallBack, View.OnClickListener {

    Activity activity;
    private RecyclerView rvVault;
    ArrayList<MainDeck> mainDeckArrayList = new ArrayList<>();
    MainDeck mainDeck;
    MyNetworkCall myNetworkCall;
    VaultParentAdapter vaultParentAdapter;
    Button btnAllCards;
    FlashCards flashCards;
    private ArrayList<FlashCards> flashCardsArrayList = new ArrayList<>();
    private String TAG = "AllCardsFragment";

    public AllCardsFragment() {
        // Required empty public constructor
    }

    public static AllCardsFragment newInstance() {
        AllCardsFragment fragment = new AllCardsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_all_cards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated: ");
        initViews(view);
        bindControls();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        if (((FlashCardActivity) activity).type == 1 || ((FlashCardActivity) activity).type == 0) {
            if (((FlashCardActivity) activity).isFirstHit) {
                hitApi(activity);
            }
        } else {
            if (((FlashCardActivity) activity).isFirstHit) {
                hitApi(activity);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }

    private void initViews(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        rvVault = view.findViewById(R.id.rv_vault);
        btnAllCards = view.findViewById(R.id.btn_all_cards);
    }

    private void bindControls() {
        btnAllCards.setOnClickListener(this);
        rvVault.setLayoutManager(new LinearLayoutManager(activity));
        rvVault.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));
        vaultParentAdapter = new VaultParentAdapter(activity, mainDeckArrayList);
        rvVault.setAdapter(vaultParentAdapter);
    }

    public void hitApi(Activity mActivity) {
        activity = mActivity;
        if (myNetworkCall == null) return;
        myNetworkCall.NetworkAPICall(API.API_GET_FLASHCARD_ALL_CARDS, true);
        if (((FlashCardActivity) activity).type != 0) {
            btnAllCards.setVisibility(View.VISIBLE);
            if (((FlashCardActivity) activity).type == 1) {
                btnAllCards.setText(getString(R.string.read_all_bookmarked));
            } else {
                btnAllCards.setText(getString(R.string.view_suspended_cards));
            }
        } else {
            btnAllCards.setVisibility(View.GONE);
        }
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser() != null ? SharedPreference.getInstance().getLoggedInUser().getId() : "");
        params.put(Constants.Extras.TYPE, ((FlashCardActivity) activity).type + 1);
        params.put(Const.RANDOM, !TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.CARD_SEQUENCE_STATUS)) ? SharedPreference.getInstance().getString(Const.CARD_SEQUENCE_STATUS) : "0");
        Log.e(TAG, "getAPI: " + params);
        ((FlashCardActivity) activity).isFirstHit = false;
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        JSONArray jsonArray = jsonObject.optJSONArray(Const.DATA);
        Gson gson = new Gson();
        switch (apiType) {
            case API.API_GET_FLASHCARD_ALL_CARDS:
                if (Objects.requireNonNull(jsonArray).length() > 0) {
                    mainDeckArrayList.clear();
                    ((FlashCardActivity) activity).isFirstHit = false;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mainDeck = gson.fromJson(jsonArray.optJSONObject(i).toString(), MainDeck.class);
                        mainDeckArrayList.add(mainDeck);
                    }
                    Log.e(TAG, "SuccessCallBack: " + mainDeckArrayList.size());
                    vaultParentAdapter.notifyDataSetChanged();
                }
                break;
            case API.API_READ_CARD:
                if (Objects.requireNonNull(jsonArray).length() > 0) {
                    flashCardsArrayList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        flashCards = gson.fromJson(jsonArray.optJSONObject(i).toString(), FlashCards.class);
                        if (((FlashCardActivity) activity).type == 1) {
                            flashCards.setShow(true);// nimesh -> this line added
                        } else {
                            flashCards.setShow(false);// nimesh -> this line added
                        }
                        flashCardsArrayList.add(flashCards);
                    }

                    if (((FlashCardActivity) activity).type == 1) {
                        Helper.gotoViewFlashCardActivity(((FlashCardActivity) activity).type, activity, "", flashCardsArrayList, true);
                    } else {
                        Helper.gotoViewFlashCardActivity(((FlashCardActivity) activity).type, activity, "", flashCardsArrayList, false);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        Toast.makeText(activity, jsonString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        myNetworkCall.NetworkAPICall(API.API_READ_CARD, true);
    }
}
