package com.emedicoz.app.flashcard.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.adapter.SubjectCardAdapter;
import com.emedicoz.app.flashcard.model.myprogress.SsubjectWiseCard.Data;
import com.emedicoz.app.flashcard.model.myprogress.SsubjectWiseCard.Deck;
import com.emedicoz.app.flashcard.model.myprogress.SsubjectWiseCard.Subdeck;
import com.emedicoz.app.flashcard.model.myprogress.SubjectWiseCard;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;

public class SubjectWiseCardActivity extends AppCompatActivity implements View.OnClickListener, MyNetworkCall.MyNetworkCallBack {

    ImageView ivDrawer;
    ImageView ivBack;
    RecyclerView recyclerView;
    MyNetworkCall myNetworkCall;
    private static final String TAG = "SubjectWiseCardActivity";
    private TextView tvTitle;
    TextView tvTotalCardCount;
    TextView tvCardStudiedCount;
    ArrayList<SubjectWiseCard> subjectWiseCardArrayList = new ArrayList<>();
    SubjectCardAdapter subjectCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_subject_wise_card);

        initViews();
        hitApi();
    }

    private void initViews() {
        myNetworkCall = new MyNetworkCall(this, this);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvTotalCardCount = findViewById(R.id.tv_total_card_count);
        tvCardStudiedCount = findViewById(R.id.tv_card_studied_count);
        ivDrawer = findViewById(R.id.iv_drawer);
        recyclerView = findViewById(R.id.recyclerView);

        bindViews();
    }

    private void bindViews() {
        ivBack.setOnClickListener(this);
        tvTitle.setText(R.string.my_progress);
        ivDrawer.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subjectCardAdapter = new SubjectCardAdapter(this, subjectWiseCardArrayList);
        recyclerView.setAdapter(subjectCardAdapter);
        recyclerView.addItemDecoration(new EqualSpacingItemDecoration(10, EqualSpacingItemDecoration.VERTICAL));
    }

    private void hitApi() {
        myNetworkCall.NetworkAPICall(API.API_GET_SUBJECT_WISE_CARD_PROGRESS, true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser() != null ? SharedPreference.getInstance().getLoggedInUser().getId() : "");
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {

        Data data = new Gson().fromJson(Objects.requireNonNull(jsonObject.optJSONObject(Const.DATA)).toString(), Data.class);

        String totalCard;
        String readCard;

        List<Deck> deckList = data.getDecks();
        if (!GenericUtils.isListEmpty(deckList)) {
            totalCard = data.getTotalCard().toString();
            readCard = data.getReadCard().toString();

            tvTotalCardCount.setText(String.format("Total Cards %s", totalCard));
            tvCardStudiedCount.setText(String.format("Cards Studied %s", readCard));
            subjectWiseCardArrayList.clear();
            for (int i = 0; i < deckList.size(); i++) {
                subjectWiseCardArrayList.add(new SubjectWiseCard(0, deckList.get(i).getTitle()));
                for (Subdeck subdeck : deckList.get(i).getSubdeck()) {
                    subjectWiseCardArrayList.add(new SubjectWiseCard(1, subdeck));
                }
            }
            subjectCardAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {

    }
}
