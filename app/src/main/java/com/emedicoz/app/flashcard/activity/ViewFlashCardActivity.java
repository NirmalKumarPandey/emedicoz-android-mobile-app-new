package com.emedicoz.app.flashcard.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.CircularTextView;
import com.emedicoz.app.flashcard.adapter.AllCardParentAdapter;
import com.emedicoz.app.flashcard.adapter.DisplayFlashCardAdapter;
import com.emedicoz.app.flashcard.model.AllFlashCard;
import com.emedicoz.app.flashcard.model.FlashCards;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;

public class ViewFlashCardActivity extends AppCompatActivity implements MyNetworkCall.MyNetworkCallBack, View.OnClickListener {

    RecyclerView rvDisplay;
    RecyclerView rvAllCardNumberSheet;
    ArrayList<FlashCards> flashCardsArrayList;
    ArrayList<FlashCards> newFlashCardsArrayList;
    ArrayList<AllFlashCard> allFlashCardArrayList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    MyNetworkCall myNetworkCall;
    int mPosition;
    DisplayFlashCardAdapter displayFlashCardAdapter;
    private String mUpdateNextType;
    TextView tvTitle;
    TextView tvCardsCountDetails;
    ImageView ivBack;
    ImageView ivDrawer;
    ImageView ivMedal;
    DrawerLayout drawerLayout;
    LinearLayout backLL;
    Button btnBack;
    TextView tvTotalCount;
    TextView tvCongratulation;
    TextView tvCardComplicationText;
    int i = 1;
    int totalNoOfCard = 0;
    ProgressBar progressBar1;
    Button btnPrevious;
    Button btnNext;
    LinearLayout previousNextLL;
    private static final String TAG = "ViewFlashCardActivity";
    FrameLayout progressFL;
    private Handler customHandler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private int secs;
    boolean isAllCards;
    AllCardParentAdapter allCardParentAdapter;
    String title = "";
    private boolean isFirstItem = true;
    String currentId = "";
    String nextId = "";
    String lastItemId = "";
    String firstItemId = "";
    int moveToPosition = 0;
    int studiedCount = 0;
    CircularTextView indicator;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_view_flash_card);

        if (getIntent() != null) {
            type = getIntent().getIntExtra(Const.POSITION, 0);

            if (!TextUtils.isEmpty(getIntent().getStringExtra(Const.TITLE)))
                title = getIntent().getStringExtra(Const.TITLE);

            isAllCards = getIntent().getBooleanExtra(Const.ALL_CARDS, false);
            Log.e(TAG, "onCreate: isAllCards - " + isAllCards);
            Type type = new TypeToken<ArrayList<FlashCards>>() {
            }.getType();
            flashCardsArrayList = new Gson().fromJson(SharedPreference.getInstance().getString(Const.FLASH_CARD_LIST), type);
            if (flashCardsArrayList == null) flashCardsArrayList = new ArrayList<>();
            totalNoOfCard = flashCardsArrayList.size();

            Set<String> stringHashSet = Collections.synchronizedSet(new LinkedHashSet<>());

            for (FlashCards flashCards : flashCardsArrayList) {
                stringHashSet.add(flashCards.getTopic());
            }

            for (String topic : stringHashSet) {
                if (isFirstItem) {
                    isFirstItem = false;
                    newFlashCardsArrayList = new ArrayList<>();
                    for (int j = 0; j < flashCardsArrayList.size(); j++) {
                        if (j == 0) {
                            if (topic.equals(flashCardsArrayList.get(j).getTopic())) {
                                flashCardsArrayList.get(j).setNewPosition(true);
                                newFlashCardsArrayList.add(flashCardsArrayList.get(j));
                            }
                        } else {
                            if (topic.equals(flashCardsArrayList.get(j).getTopic())) {
                                newFlashCardsArrayList.add(flashCardsArrayList.get(j));
                            }
                        }
                    }
                } else {
                    newFlashCardsArrayList = new ArrayList<>();
                    for (int j = 0; j < flashCardsArrayList.size(); j++) {
                        if (topic.equals(flashCardsArrayList.get(j).getTopic())) {
                            newFlashCardsArrayList.add(flashCardsArrayList.get(j));
                        }
                    }
                }

                allFlashCardArrayList.add(new AllFlashCard(topic, newFlashCardsArrayList));
            }

            flashCardsArrayList.clear();
            for (int k = 0; k < allFlashCardArrayList.size(); k++) {
                flashCardsArrayList.addAll(allFlashCardArrayList.get(k).getFlashCardsArrayList());
            }

            AllFlashCard lastItemOfAllFlashCardArrayList = null;
            if (allFlashCardArrayList.size() > 1) {
                lastItemOfAllFlashCardArrayList = allFlashCardArrayList.get(allFlashCardArrayList.size() - 1);
                ArrayList<FlashCards> flashCardsArray = lastItemOfAllFlashCardArrayList.getFlashCardsArrayList();
                if (!GenericUtils.isListEmpty(flashCardsArray))
                    lastItemId = flashCardsArray.get(flashCardsArray.size() - 1).getId();
            }
            if (!GenericUtils.isListEmpty(allFlashCardArrayList))
                firstItemId = allFlashCardArrayList.get(0).getFlashCardsArrayList().get(0).getId();

            Log.e(TAG, "onCreate: firstItemId : " + firstItemId);
            Log.e(TAG, "onCreate: lastItemId : " + lastItemId);

        }

        initViews();
        bindControls();
    }

    private void initViews() {
        myNetworkCall = new MyNetworkCall(this, this);
        tvCardsCountDetails = findViewById(R.id.tv_cards_count_details);
        rvDisplay = findViewById(R.id.rv_display);
        rvAllCardNumberSheet = findViewById(R.id.rv_all_card_number_sheet);
        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);
        ivDrawer = findViewById(R.id.iv_drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        btnBack = findViewById(R.id.btn_back);
        backLL = findViewById(R.id.ll_back);
        ivMedal = findViewById(R.id.iv_madal);
        tvCongratulation = findViewById(R.id.tv_congratulation);
        tvCardComplicationText = findViewById(R.id.tv_card_complication_text);
        tvTotalCount = findViewById(R.id.tv_total_count);
        progressBar1 = findViewById(R.id.progressBar1);
        progressFL = findViewById(R.id.progress_fl);
        indicator = findViewById(R.id.indicator);

        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        previousNextLL = findViewById(R.id.ll_previous_next);

        tvTotalCount.setText(String.format("%d/%d", i, totalNoOfCard));
        if (!TextUtils.isEmpty(title))
            tvCardsCountDetails.setText(String.format("%d Cards in %s\nStudied - 1 Cards\nLeft - %d Cards", totalNoOfCard, title, totalNoOfCard - 1));
        else
            tvCardsCountDetails.setText(String.format("Total %d Cards %s\nStudied - 1 Cards\nLeft - %d Cards", totalNoOfCard, title, totalNoOfCard - 1));

        progressBar1.setMax(totalNoOfCard);
        progressBar1.setProgress(i);

        indicator.setSolidColor(Color.RED);
    }

    private void bindControls() {
        tvTitle.setText("Flash Card");
        ivDrawer.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        ivDrawer.setOnClickListener(this);

        rvAllCardNumberSheet.setLayoutManager(new LinearLayoutManager(this));
        allCardParentAdapter = new AllCardParentAdapter(this, allFlashCardArrayList);
        rvAllCardNumberSheet.setAdapter(allCardParentAdapter);
        rvAllCardNumberSheet.addItemDecoration(new EqualSpacingItemDecoration(15, EqualSpacingItemDecoration.VERTICAL));
        ViewCompat.setNestedScrollingEnabled(rvAllCardNumberSheet, false);

        linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        if (isAllCards) {
            progressFL.setVisibility(View.GONE);
            if (flashCardsArrayList.size() > 1) {
                previousNextLL.setVisibility(View.VISIBLE);
            } else {
                previousNextLL.setVisibility(View.GONE);
            }

            ivDrawer.setVisibility(View.VISIBLE);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
        } else {
            ivDrawer.setVisibility(View.GONE);
            previousNextLL.setVisibility(View.GONE);
            progressFL.setVisibility(View.VISIBLE);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
        }

        rvDisplay.setLayoutManager(linearLayoutManager);
        displayFlashCardAdapter = new DisplayFlashCardAdapter(this, flashCardsArrayList, isAllCards);
        rvDisplay.setAdapter(displayFlashCardAdapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvDisplay);
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            secs = (int) (updatedTime / 1000);
            Log.e(TAG, "run: " + secs);
            customHandler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        startTimer();
    }

    private void startTimer() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreference.getInstance().remove(Const.FLASH_CARD_LIST);
        Log.e(TAG, "onDestroy: ");
    }

    public void setBookmark(int position) {
        mPosition = position;
        myNetworkCall.NetworkAPICall(API.API_CARD_BOOKMARK, true);
    }

    public void setSuspend(int position) {
        mPosition = position;
        myNetworkCall.NetworkAPICall(API.API_CARD_SUSPEND, true);
    }

    public void setUpdate(int position, String updateNextType) {
        mPosition = position;
        mUpdateNextType = updateNextType;
        myNetworkCall.NetworkAPICall(API.API_UPDATE_FLASH_CARD, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser() != null ? SharedPreference.getInstance().getLoggedInUser().getId() : "");
        params.put(Const.CARD_ID, flashCardsArrayList.get(mPosition).getId());
        switch (apiType) {
            case API.API_UPDATE_FLASH_CARD:
                params.put(Const.NEXT_TIME, mUpdateNextType);
                params.put(Const.TIME_TAKEN, secs);
                break;
            case API.API_CARD_BOOKMARK:
                params.put(Const.BOOKMARK, flashCardsArrayList.get(mPosition).getIsBookmarked().equals("0") ? "1" : "0");
                break;
            case API.API_CARD_SUSPEND:
                params.put(Const.SUSPEND, "1");
                break;
        }
        Log.e("ViewFlashCardActivity", "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        Toast.makeText(this, jsonObject.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
        switch (apiType) {
            case API.API_CARD_BOOKMARK:
                if (type == 1) {
                    flashCardsArrayList.remove(mPosition);
                    allFlashCardArrayList.clear();

                    Log.e(TAG, "SuccessCallBack: item position - " + linearLayoutManager.findFirstVisibleItemPosition());
                    Log.e(TAG, "SuccessCallBack: list size - " + flashCardsArrayList.size());

                    if (flashCardsArrayList.size() <= 1) {
                        if (previousNextLL.getVisibility() == View.VISIBLE) {
                            previousNextLL.setVisibility(View.GONE);
                        }
                    } else {
                        if (previousNextLL.getVisibility() == View.GONE) {
                            previousNextLL.setVisibility(View.VISIBLE);
                        }
                        if (linearLayoutManager.findFirstVisibleItemPosition() == flashCardsArrayList.size() || linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                            btnPrevious.setEnabled(false);
                            btnNext.setEnabled(true);
                        } else if (linearLayoutManager.findFirstVisibleItemPosition() + 1 == flashCardsArrayList.size()) {
                            btnPrevious.setEnabled(true);
                            btnNext.setEnabled(false);
                        } else if (flashCardsArrayList.size() > 2) {
                            btnPrevious.setEnabled(true);
                            btnNext.setEnabled(true);
                        }
                    }

                    totalNoOfCard = 0;
                    totalNoOfCard = flashCardsArrayList.size();

                    studiedCount = 0;
                    for (int b = 0; b < flashCardsArrayList.size(); b++) {
                        if (flashCardsArrayList.get(b).isSelected() ||
                                flashCardsArrayList.get(b).isComplete() ||
                                flashCardsArrayList.get(b).isAlreadyComplete() ||
                                flashCardsArrayList.get(b).isNewPosition()) {
                            studiedCount = studiedCount + 1;
                        }
                    }

                    if (studiedCount < 1) {
                        if (totalNoOfCard != 0) {
                            studiedCount = 1;
                        }

                        for (int c = 0; c < flashCardsArrayList.size(); c++) {
                            if (c == 0) {
                                flashCardsArrayList.get(c).setNewPosition(true);
                            } else {
                                flashCardsArrayList.get(c).setNewPosition(false);
                            }
                            flashCardsArrayList.get(c).setSelected(false);
                            flashCardsArrayList.get(c).setComplete(false);
                            flashCardsArrayList.get(c).setAlreadyComplete(false);
                        }
                    } else {
                        if (linearLayoutManager.findFirstVisibleItemPosition() == flashCardsArrayList.size()) {
                            flashCardsArrayList.get(0).setNewPosition(false);
                            flashCardsArrayList.get(0).setSelected(false);
                            flashCardsArrayList.get(0).setComplete(false);
                            flashCardsArrayList.get(0).setAlreadyComplete(true);
                        } else if (linearLayoutManager.findFirstVisibleItemPosition() + 1 == flashCardsArrayList.size() || flashCardsArrayList.size() > 2) {
                            if (flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).isComplete()) {
                                flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setAlreadyComplete(true);
                                flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setComplete(false);
                                flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setSelected(false);
                                flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setNewPosition(false);
                            } else {
                                flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setNewPosition(true);
                                flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setAlreadyComplete(false);
                                flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setComplete(false);
                                flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setSelected(false);
                            }
                        }
                    }

                    Set<String> stringHashSet = Collections.synchronizedSet(new LinkedHashSet<String>());

                    for (FlashCards flashCards : flashCardsArrayList) {
                        stringHashSet.add(flashCards.getTopic());
                    }

                    for (String topic : stringHashSet) {
                        newFlashCardsArrayList = new ArrayList<>();
                        for (int j = 0; j < flashCardsArrayList.size(); j++) {
                            if (topic.equals(flashCardsArrayList.get(j).getTopic())) {
                                newFlashCardsArrayList.add(flashCardsArrayList.get(j));
                            }
                        }

                        allFlashCardArrayList.add(new AllFlashCard(topic, newFlashCardsArrayList));
                    }

                    flashCardsArrayList.clear();
                    for (int k = 0; k < allFlashCardArrayList.size(); k++) {
                        flashCardsArrayList.addAll(allFlashCardArrayList.get(k).getFlashCardsArrayList());
                    }

                    displayFlashCardAdapter.notifyDataSetChanged();
                    allCardParentAdapter.notifyDataSetChanged();

                    if (!TextUtils.isEmpty(title)) {
                        tvCardsCountDetails.setText(String.format("%d Cards in %s\nStudied - %d Cards\nLeft - %d Cards", totalNoOfCard, title, studiedCount, totalNoOfCard - studiedCount));
                    } else {
                        tvCardsCountDetails.setText(String.format("Total %d Cards %s\nStudied - %d Cards\nLeft - %d Cards", totalNoOfCard, title, studiedCount, totalNoOfCard - studiedCount));
                    }
                } else {
                    if (flashCardsArrayList.get(mPosition).getIsBookmarked().equals("0")) {
                        flashCardsArrayList.get(mPosition).setIsBookmarked("1");
                    } else {
                        flashCardsArrayList.get(mPosition).setIsBookmarked("0");
                    }
                    displayFlashCardAdapter.notifyItemChanged(mPosition);
                }
                break;
            case API.API_CARD_SUSPEND:
                flashCardsArrayList.remove(mPosition);
                allFlashCardArrayList.clear();
                if (isAllCards) {
                    if (!flashCardsArrayList.isEmpty()) {
                        ivDrawer.setVisibility(View.VISIBLE);
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                        if (flashCardsArrayList.size() <= 1) {
                            if (previousNextLL.getVisibility() == View.VISIBLE) {
                                previousNextLL.setVisibility(View.GONE);
                            }
                        } else {
                            if (previousNextLL.getVisibility() == View.GONE) {
                                previousNextLL.setVisibility(View.VISIBLE);
                            }
                            if (linearLayoutManager.findFirstVisibleItemPosition() == flashCardsArrayList.size() || linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                                btnPrevious.setEnabled(false);
                                btnNext.setEnabled(true);
                            } else if (linearLayoutManager.findFirstVisibleItemPosition() + 1 == flashCardsArrayList.size()) {
                                btnPrevious.setEnabled(true);
                                btnNext.setEnabled(false);
                            } else if (flashCardsArrayList.size() > 2) {
                                btnPrevious.setEnabled(true);
                                btnNext.setEnabled(true);
                            }
                        }

                        totalNoOfCard = 0;
                        totalNoOfCard = flashCardsArrayList.size();

                        studiedCount = 0;
                        for (int b = 0; b < flashCardsArrayList.size(); b++) {
                            if (flashCardsArrayList.get(b).isSelected() ||
                                    flashCardsArrayList.get(b).isComplete() ||
                                    flashCardsArrayList.get(b).isAlreadyComplete() ||
                                    flashCardsArrayList.get(b).isNewPosition()) {
                                studiedCount = studiedCount + 1;
                            }
                        }

                        if (studiedCount < 1) {
                            if (totalNoOfCard != 0) {
                                studiedCount = 1;
                            }

                            for (int c = 0; c < flashCardsArrayList.size(); c++) {
                                if (c == 0) {
                                    flashCardsArrayList.get(c).setNewPosition(true);
                                } else {
                                    flashCardsArrayList.get(c).setNewPosition(false);
                                }
                                flashCardsArrayList.get(c).setSelected(false);
                                flashCardsArrayList.get(c).setComplete(false);
                                flashCardsArrayList.get(c).setAlreadyComplete(false);
                            }
                        } else {
                            if (linearLayoutManager.findFirstVisibleItemPosition() == flashCardsArrayList.size()) {
                                flashCardsArrayList.get(0).setNewPosition(false);
                                flashCardsArrayList.get(0).setSelected(false);
                                flashCardsArrayList.get(0).setComplete(false);
                                flashCardsArrayList.get(0).setAlreadyComplete(true);
                            } else if (linearLayoutManager.findFirstVisibleItemPosition() + 1 == flashCardsArrayList.size() || flashCardsArrayList.size() > 2) {
                                if (flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).isComplete()) {
                                    flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setAlreadyComplete(true);
                                    flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setComplete(false);
                                    flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setSelected(false);
                                    flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setNewPosition(false);
                                } else {
                                    flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setNewPosition(true);
                                    flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setAlreadyComplete(false);
                                    flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setComplete(false);
                                    flashCardsArrayList.get(linearLayoutManager.findFirstVisibleItemPosition()).setSelected(false);
                                }
                            }
                        }

                        Set<String> stringHashSet = Collections.synchronizedSet(new LinkedHashSet<String>());

                        for (FlashCards flashCards : flashCardsArrayList) {
                            stringHashSet.add(flashCards.getTopic());
                        }

                        for (String topic : stringHashSet) {
                            newFlashCardsArrayList = new ArrayList<>();
                            for (int j = 0; j < flashCardsArrayList.size(); j++) {
                                if (topic.equals(flashCardsArrayList.get(j).getTopic())) {
                                    newFlashCardsArrayList.add(flashCardsArrayList.get(j));
                                }
                            }
                            allFlashCardArrayList.add(new AllFlashCard(topic, newFlashCardsArrayList));
                        }

                        flashCardsArrayList.clear();
                        for (int k = 0; k < allFlashCardArrayList.size(); k++) {
                            flashCardsArrayList.addAll(allFlashCardArrayList.get(k).getFlashCardsArrayList());
                        }

                        studiedCount = 0;
                        for (int b = 0; b < flashCardsArrayList.size(); b++) {
                            if (flashCardsArrayList.get(b).isSelected() ||
                                    flashCardsArrayList.get(b).isComplete() ||
                                    flashCardsArrayList.get(b).isAlreadyComplete() ||
                                    flashCardsArrayList.get(b).isNewPosition()) {
                                studiedCount = studiedCount + 1;
                            }
                        }

                        tvCardsCountDetails.setText(String.format("%d Cards in %s\nStudied - %d Cards\nLeft - %d Cards", totalNoOfCard, title, studiedCount, totalNoOfCard - studiedCount));

                        lastItemId = "";
                        firstItemId = "";

                        AllFlashCard lastItemOfAllFlashCardArrayList = null;
                        if (allFlashCardArrayList.size() > 1) {
                            lastItemOfAllFlashCardArrayList = allFlashCardArrayList.get(allFlashCardArrayList.size() - 1);
                            ArrayList<FlashCards> flashCardsArray = lastItemOfAllFlashCardArrayList.getFlashCardsArrayList();
                            if (!GenericUtils.isListEmpty(flashCardsArray))
                                lastItemId = flashCardsArray.get(flashCardsArray.size() - 1).getId();
                        }

                        if (!GenericUtils.isListEmpty(allFlashCardArrayList))
                            firstItemId = allFlashCardArrayList.get(0).getFlashCardsArrayList().get(0).getId();

                        displayFlashCardAdapter.notifyDataSetChanged();
                        allCardParentAdapter.notifyDataSetChanged();
                    } else {
                        ivDrawer.setVisibility(View.GONE);
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        ivMedal.setVisibility(View.INVISIBLE);
                        tvCongratulation.setVisibility(View.GONE);
                        tvCardComplicationText.setText("No card left");
                        previousNextLL.setVisibility(View.GONE);
                    }
                }

                notifiedAdapter(mPosition);
                break;
            case API.API_UPDATE_FLASH_CARD:
                if (i < totalNoOfCard) {
                    i = i + 1;
                    tvTotalCount.setText(String.format("%d/%d", i, totalNoOfCard));
                    progressBar1.setProgress(i);
                }

                customHandler.removeCallbacks(updateTimerThread);
                startTimer();

                Log.e(TAG, "SuccessCallBack: " + i);

                flashCardsArrayList.remove(mPosition);
                rvDisplay.getRecycledViewPool().clear();
                notifiedAdapter(mPosition);
                break;
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        Helper.showToast(jsonString, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_drawer:
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
                break;
            case R.id.btn_next:
                try {
                    rvDisplay.scrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() + 1);

                    if (linearLayoutManager.findFirstVisibleItemPosition() + 1 == flashCardsArrayList.size() - 1) {
                        btnNext.setEnabled(false);
                    } else {
                        if (!btnNext.isEnabled())
                            btnNext.setEnabled(true);
                    }

                    next(linearLayoutManager.findFirstVisibleItemPosition());
                } catch (IllegalStateException e) {
                    Log.e(TAG, "onClick: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + e.getMessage());
                }
                break;
            case R.id.btn_previous:
                try {
                    rvDisplay.scrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() - 1);

                    if (linearLayoutManager.findFirstVisibleItemPosition() == 1) {
                        btnPrevious.setEnabled(false);
                    } else {
                        if (!btnPrevious.isEnabled())
                            btnPrevious.setEnabled(true);
                    }
                    previous(linearLayoutManager.findFirstVisibleItemPosition());
                } catch (IllegalStateException e) {
                    Log.e(TAG, "onClick: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + e.getMessage());
                }
                break;
            case R.id.iv_back:
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }

    private void next(int mPosition) {
        if (!btnPrevious.isEnabled())
            btnPrevious.setEnabled(true);

        Log.e(TAG, "setComplete: mPosition : (mPosition + 1) : " + (mPosition + 1));
        currentId = flashCardsArrayList.get(mPosition).getId();
        nextId = flashCardsArrayList.get(mPosition + 1).getId();

        for (int i = 0; i < allFlashCardArrayList.size(); i++) {
            for (int j = 0; j < allFlashCardArrayList.get(i).getFlashCardsArrayList().size(); j++) {
                if (allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).getId().equals(nextId)) {
                    if (allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).isComplete()) {
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setComplete(false);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setAlreadyComplete(true);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setSelected(false);
                    } else if (allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).isSelected()) {
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setComplete(true);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setAlreadyComplete(false);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setSelected(false);
                    } else {
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setNewPosition(true);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setComplete(false);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setAlreadyComplete(false);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setSelected(false);
                    }
                } else {
                    if (currentId.equals(allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).getId())) {
                        if (allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).isSelected() || allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).isNewPosition() || allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).isAlreadyComplete()) {
                            allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setSelected(false);
                            allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setComplete(true);
                            allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setAlreadyComplete(false);
                            allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setNewPosition(false);
                        }
                    }
                }
            }
        }


        studiedCount = 0;
        for (int a = 0; a < allFlashCardArrayList.size(); a++) {
            for (int b = 0; b < allFlashCardArrayList.get(a).getFlashCardsArrayList().size(); b++) {
                if (allFlashCardArrayList.get(a).getFlashCardsArrayList().get(b).isSelected() ||
                        allFlashCardArrayList.get(a).getFlashCardsArrayList().get(b).isComplete() ||
                        allFlashCardArrayList.get(a).getFlashCardsArrayList().get(b).isAlreadyComplete() ||
                        allFlashCardArrayList.get(a).getFlashCardsArrayList().get(b).isNewPosition()) {
                    studiedCount = studiedCount + 1;
                    Log.e(TAG, "next: b : " + b);
                }
            }
        }

        tvCardsCountDetails.setText(String.format("%d Cards in %s\nStudied - %d Cards\nLeft - %d Cards", totalNoOfCard, title, studiedCount, totalNoOfCard - studiedCount));
        allCardParentAdapter.notifyDataSetChanged();
    }

    private void previous(int mPosition) {
        if (!btnNext.isEnabled())
            btnNext.setEnabled(true);

        Log.e(TAG, "removeComplete: " + mPosition);
        String id1 = flashCardsArrayList.get(mPosition).getId();
        String previousId = flashCardsArrayList.get(mPosition - 1).getId();

        for (int i = 0; i < allFlashCardArrayList.size(); i++) {
            for (int j = 0; j < allFlashCardArrayList.get(i).getFlashCardsArrayList().size(); j++) {
                if (allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).getId().equals(id1)) {
                    if (allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).isComplete() || allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).isNewPosition() || allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).isSelected() || allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).isAlreadyComplete()) {
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setComplete(true);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setAlreadyComplete(false);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setSelected(false);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setNewPosition(false);
                    }
                } else if (previousId.equals(allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).getId())) {
                    if (allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).isComplete()) {
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setAlreadyComplete(true);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setSelected(false);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setComplete(false);
                    } else {
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setComplete(false);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setAlreadyComplete(false);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setSelected(false);
                        allFlashCardArrayList.get(i).getFlashCardsArrayList().get(j).setNewPosition(true);
                    }
                }
            }
        }
        allCardParentAdapter.notifyDataSetChanged();
    }

    public void moveToPosition(String id) {
        for (int i = 0; i < flashCardsArrayList.size(); i++) {
            if (flashCardsArrayList.get(i).getId().equals(id)) {
                moveToPosition = i;
                Log.e(TAG, "moveToPosition: " + moveToPosition);
                rvDisplay.scrollToPosition(moveToPosition);
            }
        }

        if (moveToPosition == 0) {
            btnPrevious.setEnabled(false);
        } else {
            btnPrevious.setEnabled(true);
        }

        if (moveToPosition == flashCardsArrayList.size() - 1) {
            btnNext.setEnabled(false);
        } else {
            btnNext.setEnabled(true);
        }

        for (int j = 0; j < allFlashCardArrayList.size(); j++) {
            for (int k = 0; k < allFlashCardArrayList.get(j).getFlashCardsArrayList().size(); k++) {
                if (allFlashCardArrayList.get(j).getFlashCardsArrayList().get(k).getId().equals(id)) {
                    allFlashCardArrayList.get(j).getFlashCardsArrayList().get(k).setSelected(true);
                    allFlashCardArrayList.get(j).getFlashCardsArrayList().get(k).setComplete(false);
                    allFlashCardArrayList.get(j).getFlashCardsArrayList().get(k).setAlreadyComplete(false);
                } else {
                    if (allFlashCardArrayList.get(j).getFlashCardsArrayList().get(k).isComplete() || allFlashCardArrayList.get(j).getFlashCardsArrayList().get(k).isAlreadyComplete() || allFlashCardArrayList.get(j).getFlashCardsArrayList().get(k).isNewPosition() || allFlashCardArrayList.get(j).getFlashCardsArrayList().get(k).isSelected()) {
                        allFlashCardArrayList.get(j).getFlashCardsArrayList().get(k).setComplete(true);
                    } else {
                        allFlashCardArrayList.get(j).getFlashCardsArrayList().get(k).setComplete(false);
                    }

                    allFlashCardArrayList.get(j).getFlashCardsArrayList().get(k).setAlreadyComplete(false);
                    allFlashCardArrayList.get(j).getFlashCardsArrayList().get(k).setSelected(false);
                }
                allFlashCardArrayList.get(j).getFlashCardsArrayList().get(k).setNewPosition(false);
            }
        }

        studiedCount = 0;
        for (int a = 0; a < allFlashCardArrayList.size(); a++) {
            for (int b = 0; b < allFlashCardArrayList.get(a).getFlashCardsArrayList().size(); b++) {
                if (allFlashCardArrayList.get(a).getFlashCardsArrayList().get(b).isSelected() ||
                        allFlashCardArrayList.get(a).getFlashCardsArrayList().get(b).isComplete() ||
                        allFlashCardArrayList.get(a).getFlashCardsArrayList().get(b).isAlreadyComplete() ||
                        allFlashCardArrayList.get(a).getFlashCardsArrayList().get(b).isNewPosition()) {
                    Log.e(TAG, "moveToPosition: studiedCount : " + b);
                    studiedCount = studiedCount + 1;
                }
            }
        }

        tvCardsCountDetails.setText(String.format("%d Cards %s\nStudied - %d Cards\nLeft - %d Cards", totalNoOfCard, title, studiedCount, totalNoOfCard - studiedCount));
        allCardParentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else
            super.onBackPressed();
    }

    private void notifiedAdapter(int i) {
        if (!flashCardsArrayList.isEmpty()) {
            displayFlashCardAdapter.notifyItemRemoved(i);
        } else {
            rvDisplay.setVisibility(View.GONE);
            backLL.setVisibility(View.VISIBLE);
        }
    }
}
