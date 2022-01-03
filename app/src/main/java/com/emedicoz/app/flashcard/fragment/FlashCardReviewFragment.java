package com.emedicoz.app.flashcard.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.CustomItemSelectedListener;
import com.emedicoz.app.flashcard.activity.FlashCardActivity;
import com.emedicoz.app.flashcard.adapter.DropDownAdapter;
import com.emedicoz.app.flashcard.adapter.ReviewParentAdapter;
import com.emedicoz.app.flashcard.model.FlashCards;
import com.emedicoz.app.flashcard.model.MainDeck;
import com.emedicoz.app.flashcard.model.SubDeck;
import com.emedicoz.app.flashcard.model.Topic;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;

public class FlashCardReviewFragment extends Fragment implements CustomItemSelectedListener, View.OnClickListener, MyNetworkCall.MyNetworkCallBack, AdapterView.OnItemSelectedListener, View.OnTouchListener {

    RecyclerView rvReviewParent;
    LinearLayoutManager linearLayoutManager;
    RelativeLayout rlDropdown;
    CardView cvDropdown;
    CardView cvConfirm;
    Spinner spinner;
    MyNetworkCall myNetworkCall;
    Button btnStartReview;
    Button btnAddNewCard;
    Button btnConfirm;
    TextView tvLeftCards;
    TextView tvStudiedCardCountAndTime;
    TextView tvSpinner;
    TextView tvConfirm;
    String[] count = {"1", "2", "3", "4", "5"};
    MainDeck mainDeck;
    ArrayList<MainDeck> mainDeckArrayList = new ArrayList<>();
    ArrayList<SubDeck> subDeckArrayList = new ArrayList<>();
    ArrayList<Topic> topicArrayList = new ArrayList<>();
    ReviewParentAdapter parentAdapter;
    FlashCards flashCards;
    ArrayList<FlashCards> flashCardsArrayList = new ArrayList<>();
    private Activity activity;
    private boolean isSpinnerTouched = false;
    private String mId;
    private String mType;
    private int leftCard = 0;
    int reviewedCard = 0;
    int cardReviewTime = 0;
    PopupWindow popupWindow;
    private static final String TAG = "FlashCardReviewFragment";
    DropDownAdapter otrsCategoryAdapter;
    private String type = "";
    String subjectId = "ALL";
    String subTopicId = "ALL";
    TextView tvSubject;
    TextView tvSubTopic;
    TextView tvNumberOfCard;
    AlertDialog addCardDialog;

    public FlashCardReviewFragment() {
        // Required empty public constructor
    }


    public static FlashCardReviewFragment newInstance() {
        FlashCardReviewFragment fragment = new FlashCardReviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flash_card_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((FlashCardActivity) activity).type = 0;
        ((FlashCardActivity) activity).isFirstHit = true;
        ((FlashCardActivity) activity).manageToolbar("Flashcards", true);
        initViews(view);
        bindControls();

    }

    private void initViews(View view) {
        btnAddNewCard = view.findViewById(R.id.btn_add_new_card);
        btnStartReview = view.findViewById(R.id.btn_start_review);
        rvReviewParent = view.findViewById(R.id.rv_review_parent);
        rlDropdown = view.findViewById(R.id.rl_dropdown);
        cvConfirm = view.findViewById(R.id.cv_confirm);
        cvDropdown = view.findViewById(R.id.cv_dropdown);
        spinner = view.findViewById(R.id.spinner);
        tvStudiedCardCountAndTime = view.findViewById(R.id.tv_studied_card_count_and_time);
        tvSpinner = view.findViewById(R.id.tv_spinner);
        tvConfirm = view.findViewById(R.id.tv_confirm);
        tvLeftCards = view.findViewById(R.id.tv_left_cards);
        myNetworkCall = new MyNetworkCall(this, activity);
    }

    private void bindControls() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 30, 0, 0);
        btnAddNewCard.setLayoutParams(params);

        spinner.setOnTouchListener(this);
        spinner.setOnItemSelectedListener(this);
        btnStartReview.setOnClickListener(this);
        rlDropdown.setOnClickListener(this);
        cvDropdown.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        btnAddNewCard.setOnClickListener(this);
        linearLayoutManager = new LinearLayoutManager(activity);
        rvReviewParent.setLayoutManager(linearLayoutManager);
        rvReviewParent.addItemDecoration(new EqualSpacingItemDecoration(25, EqualSpacingItemDecoration.VERTICAL));
        parentAdapter = new ReviewParentAdapter(activity, mainDeckArrayList, FlashCardReviewFragment.this);
        rvReviewParent.setAdapter(parentAdapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.spinadapt, R.id.weekofday, count);
        spinner.setAdapter(adapter);
    }

    public void hitApi() {
        myNetworkCall.NetworkAPICall(API.API_GET_FLASHCARD_DECK, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        hitApi();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm:
                if (leftCard > 0) {
                    AlertDialog.Builder builder
                            = new AlertDialog
                            .Builder(activity);
                    builder.setMessage(R.string.finish_before_select_card);
                    builder.setCancelable(false);

                    builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    showConfirmDialogue(Helper.GetText(tvSpinner));
                }
                break;
            case R.id.cv_dropdown:
            case R.id.rl_dropdown:
                if (leftCard == 0) {
                    spinner.performClick();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(R.string.finish_before_select_card);
                    builder.setCancelable(false);

                    builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                break;

            case R.id.btn_confirm:
                // add new card
                Log.e(TAG, "onClick: tv_subject = " + tvSubject.getText().toString());
                Log.e(TAG, "onClick: subject id = " + subjectId);
                Log.e(TAG, "onClick: tv_sub_topic = " + tvSubTopic.getText().toString());
                Log.e(TAG, "onClick: subtopic id = " + subTopicId);
                Log.e(TAG, "onClick: tv_number_of_card = " + tvNumberOfCard.getText().toString());
                addCardDialog.dismiss();
                if (subjectId.equals("ALL")) {
                    myNetworkCall.NetworkAPICall(API.API_ADD_NEW_CARD, true);
                } else {
                    myNetworkCall.NetworkAPICall(API.API_ADD_CARD_SUBDECK, true);
                }
                break;
            case R.id.btn_add_new_card:
                // if review left card is 0 then else statement is working
                if (leftCard > 0) {
                    Helper.newCustomDialog(activity,
                            activity.getString(R.string.app_name),
                            activity.getString(R.string.you_must_have_to_finish_previous_cards_before_selecting_new_card),
                            false,
                            activity.getString(R.string.close),
                            ContextCompat.getDrawable(activity,R.drawable.bg_round_corner_fill_red));
                } else {
                    showDialog();
                }
                break;
            case R.id.btn_start_review:
                // here user start review all the cards
                myNetworkCall.NetworkAPICall(API.API_GET_REVIEW_CARDS, true);
                break;
            case R.id.rl_subject_dropdown:
                // show drop down for subject selection at the time of add new card
                showDropDown(view, Const.SUBDECK);
                break;
            case R.id.rl_subtopic_dropdown:
                // show drop down for subtopic selection at the time of add new card
                if (!TextUtils.isEmpty(subjectId)) {
                    showDropDown(view, Const.TOPIC);
                } else {
                    GenericUtils.showToast(activity, "Please choose subject.");
                }
                break;
            case R.id.rl_number_of_card_dropdown:
                // show drop down for number selection at the time of add new card
                showDropDown(view, Const.NO_OF_CARD);
                break;
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.add_new_card_dialog_layout, viewGroup, false);
        builder.setView(dialogView);
        RelativeLayout rlSubjectDropdown;
        RelativeLayout rlSubtopicDropdown;
        RelativeLayout rlNumberOfCardDropdown;

        rlSubjectDropdown = dialogView.findViewById(R.id.rl_subject_dropdown);
        rlSubtopicDropdown = dialogView.findViewById(R.id.rl_subtopic_dropdown);
        rlNumberOfCardDropdown = dialogView.findViewById(R.id.rl_number_of_card_dropdown);

        tvSubject = dialogView.findViewById(R.id.tv_subject);
        tvSubTopic = dialogView.findViewById(R.id.tv_sub_topic);
        tvNumberOfCard = dialogView.findViewById(R.id.tv_number_of_card);

        btnConfirm = dialogView.findViewById(R.id.btn_confirm);

        rlSubjectDropdown.setOnClickListener(this);
        rlSubtopicDropdown.setOnClickListener(this);
        rlNumberOfCardDropdown.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);


        if (subjectId.equals(Const.ALL)) {
            tvSubject.setText(getString(R.string.all));
        } else {
            for (SubDeck subDeck : subDeckArrayList) {
                if (subjectId.equals(subDeck.getSdId())) {
                    tvSubject.setText(subDeck.getTitle());
                    break;
                }
            }
        }

        if (subTopicId.equals(Const.ALL)) {
            tvSubTopic.setText(getString(R.string.all));
        } else {
            for (Topic topic : topicArrayList) {
                if (subTopicId.equals(topic.getId())) {
                    tvSubTopic.setText(topic.getTitle());
                    break;
                }
            }
        }

        addCardDialog = builder.create();
        Objects.requireNonNull(addCardDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addCardDialog.show();
    }

    // this method work for subject, subtopic and number selection only
    private void showDropDown(View view, String mType) {
        View popUpView = ((FlashCardActivity) activity).getLayoutInflater().inflate(R.layout.dropdown_recyclerview, null);
        int size = 0;
        type = mType;
        switch (mType) {
            case Const.SUBDECK:
                size = addSubDeckData(subDeckArrayList).size();
                break;
            case Const.TOPIC:
                size = addTopicData(topicArrayList).size();
                break;
            case Const.NO_OF_CARD:
                size = 4;
                break;
        }

        if (size < 5) {
            popupWindow = new PopupWindow(popUpView, view.getWidth(), WindowManager.LayoutParams.WRAP_CONTENT, true);
        } else {
            popupWindow = new PopupWindow(popUpView, view.getWidth(), 450, true);
        }

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView recyclerView = popUpView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        if (mType.equals(Const.SUBDECK)) {
            otrsCategoryAdapter = new DropDownAdapter(activity, mType, addSubDeckData(subDeckArrayList), FlashCardReviewFragment.this);
        } else if (mType.equals(Const.TOPIC)) {
            otrsCategoryAdapter = new DropDownAdapter(activity, mType, addTopicData(topicArrayList), FlashCardReviewFragment.this);
        } else {
            otrsCategoryAdapter = new DropDownAdapter(activity, mType, addCardNumberData(), FlashCardReviewFragment.this);
        }

        recyclerView.setAdapter(otrsCategoryAdapter);
        popupWindow.showAtLocation((View) view.getParent(), Gravity.END, 40, 0);
  }

    private ArrayList<Topic> addSubDeckData(ArrayList<SubDeck> arrayList) {
        ArrayList<Topic> subDeckList = new ArrayList<>();
        subDeckList.add(new Topic(getString(R.string.all), getString(R.string.all)));
        for (int i = 0; i < arrayList.size(); i++) {
            subDeckList.add(new Topic(arrayList.get(i).getSdId(), arrayList.get(i).getTitle()));
        }
        return subDeckList;
    }

    private ArrayList<Topic> addTopicData(ArrayList<Topic> arrayList) {
        ArrayList<Topic> topicList = new ArrayList<>();
        topicList.add(new Topic(getString(R.string.all), getString(R.string.all)));
        for (int i = 0; i < arrayList.size(); i++) {
            if (subjectId.equals(getString(R.string.all))) {
                topicList.add(new Topic(arrayList.get(i).getId(), arrayList.get(i).getSdId(), arrayList.get(i).getTitle()));
            } else {
                if (subjectId.equals(arrayList.get(i).getSdId())) {
                    topicList.add(new Topic(arrayList.get(i).getId(), arrayList.get(i).getSdId(), arrayList.get(i).getTitle()));
                }
            }
        }
        return topicList;
    }

    private ArrayList<Topic> addCardNumberData() {
        ArrayList<Topic> topicList = new ArrayList<>();
        topicList.add(new Topic("", "1"));
        topicList.add(new Topic("", "3"));
        for (int i = 1; i < 7; i++) {
            topicList.add(new Topic("", "" + (i * 5)));
        }
        return topicList;
    }

    // this method set subject, subtopic and no. of card
    public void setDataForCard(Topic topic) {
        popupWindow.dismiss();
        switch (type) {
            case Const.SUBDECK:
                tvSubject.setText(topic.getTitle());
                subjectId = topic.getId();
                tvSubTopic.setText(getString(R.string.all));
                subTopicId = getString(R.string.all);
                break;
            case Const.TOPIC:
                tvSubTopic.setText(topic.getTitle());
                subTopicId = topic.getId();
                if (!TextUtils.isEmpty(topic.getSdId())) {
                    subjectId = topic.getSdId();
                    for (SubDeck subDeck : subDeckArrayList) {
                        if (subjectId != null && subjectId.equals(subDeck.getSdId())) {
                            tvSubject.setText(subDeck.getTitle());
                            break;
                        }
                    }
                }
                break;
            case Const.NO_OF_CARD:
                tvNumberOfCard.setText(topic.getTitle());
                break;
        }
        Log.e(TAG, "onItemSelected: type = " + type);
    }

    private void showDropDown(View view) {
        int n = 5;
        View popUpView = ((FlashCardActivity) activity).getLayoutInflater().inflate(R.layout.dropdown_recyclerview, null);
        if (addData().size() < 5) {
            popupWindow = new PopupWindow(popUpView, view.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        } else {
            popupWindow = new PopupWindow(popUpView, view.getWidth(), 450, true);
        }

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView recyclerView = popUpView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        DropDownAdapter otrsCategoryAdapter = new DropDownAdapter(activity, addData(), this);
        recyclerView.setAdapter(otrsCategoryAdapter);
        popupWindow.showAtLocation((View) view.getParent(), Gravity.END, 40, 0);
//        popupWindow.showAsDropDown(view);
    }

    private List<Topic> addData() {
        List<Topic> topicList = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            topicList.add(new Topic("" + (i + 1), (i + 1) + " Hello"));
        }
        return topicList;
    }

    @Override
    public void onItemSelected(String text, String id) {

    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser() != null ? SharedPreference.getInstance().getLoggedInUser().getId() : "");
        switch (apiType) {
            case API.API_GET_REVIEW_CARDS:
                params.put(Const.RANDOM, !TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.CARD_SEQUENCE_STATUS)) ? SharedPreference.getInstance().getString(Const.CARD_SEQUENCE_STATUS) : "0");
                break;
            case API.API_REVIEW_CARD_SUBDECK:
            case API.API_REVIEW_CARD_DECK:
                params.put(mType, mId);
//                params.put(Const.TYPE, 1);
                params.put(Const.RANDOM, !TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.CARD_SEQUENCE_STATUS)) ? SharedPreference.getInstance().getString(Const.CARD_SEQUENCE_STATUS) : "0");
                break;
            case API.API_ADD_NEW_CARD:
                params.put(Const.NO_OF_CARD, Helper.GetText(tvNumberOfCard));
                params.put(Const.SUBDECK_ID, "ALL");
                params.put(Constants.Extras.TOPIC_ID, subTopicId.isEmpty() ? "" : subTopicId);
                break;
            case API.API_GET_FLASHCARD_DECK:
                break;

            case API.API_ADD_CARD_SUBDECK:
                params.put(Const.NO_OF_CARD, Helper.GetText(tvNumberOfCard));
                params.put(Const.SUBDECK_ID, subjectId.isEmpty() ? "" : subjectId);
                params.put(Const.SUBTOPIC, subTopicId.isEmpty() ? "" : subTopicId);
                break;
        }
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        JSONArray Jsonarray = jsonObject.optJSONArray(Const.DATA);
        Gson gson = new Gson();
        switch (apiType) {
            case API.API_ADD_CARD_SUBDECK:
            case API.API_ADD_NEW_CARD:
                hitApi();
                btnStartReview.setEnabled(true);
                break;
            case API.API_GET_REVIEW_CARDS:
            case API.API_REVIEW_CARD_DECK:
            case API.API_REVIEW_CARD_SUBDECK:
                if (Objects.requireNonNull(Jsonarray).length() > 0) {
                    flashCardsArrayList.clear();
                    for (int i = 0; i < Jsonarray.length(); i++) {
                        flashCards = gson.fromJson(Jsonarray.optJSONObject(i).toString(), FlashCards.class);
                        flashCardsArrayList.add(flashCards);
                    }
                    Helper.gotoViewFlashCardActivity(activity, "", flashCardsArrayList, false);
                }

                break;
            case API.API_GET_FLASHCARD_DECK:
                if (Jsonarray != null && Jsonarray.length() > 0) {
                    mainDeckArrayList.clear();
                    leftCard = 0;
                    reviewedCard = 0;
                    cardReviewTime = 0;
                    for (int i = 0; i < Jsonarray.length(); i++) {
                        mainDeck = gson.fromJson(Jsonarray.optJSONObject(i).toString(), MainDeck.class);
                        mainDeckArrayList.add(mainDeck);
                        try {
                            leftCard = leftCard + Integer.parseInt(Jsonarray.optJSONObject(i).optString(Const.LEFT_CARDS));
                            reviewedCard = reviewedCard + Integer.parseInt(Jsonarray.optJSONObject(i).optString(Const.REVIEWED_CARD));
                            cardReviewTime += Integer.parseInt(Jsonarray.optJSONObject(i).optString(Const.REVIEWED_TODAY_TIME));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    for (int i = 0; i < mainDeckArrayList.size(); i++) {
                        for (int j = 0; j < mainDeckArrayList.get(i).getSubdeck().size(); j++) {
                            for (int k = 0; k < mainDeckArrayList.get(i).getSubdeck().get(j).getTopics().size(); k++) {
                                mainDeckArrayList.get(i).getSubdeck().get(j).getTopics().get(k).setSdId(mainDeckArrayList.get(i).getSubdeck().get(j).getSdId());
                            }
                        }
                    }

                    subDeckArrayList.clear();
                    topicArrayList.clear();

                    for (MainDeck mainDeck : mainDeckArrayList) {
                        for (SubDeck subDeck : mainDeck.getSubdeck()) {
                            subDeckArrayList.add(subDeck);
                            topicArrayList.addAll(subDeck.getTopics());
                        }
                    }

                    Log.e(TAG, "SuccessCallBack: " + subDeckArrayList.size());
                    Log.e(TAG, "SuccessCallBack: " + topicArrayList.size());

                    if (leftCard > 0) {
                        btnStartReview.setEnabled(true);
                    } else {
                        btnStartReview.setEnabled(false);
                    }

                    tvLeftCards.setText(String.format("Reviews Left Todays - %d", leftCard));

                    if (cardReviewTime > 0) {
                        String minute = Helper.combinationFormatter(cardReviewTime);
                        tvStudiedCardCountAndTime.setText(HtmlCompat.fromHtml("Studied <font color='#2f90d0'><u>" + reviewedCard + "</u></font> Cards in <font color='#2f90d0'><u>" + minute + "</u></font> Today", HtmlCompat.FROM_HTML_MODE_LEGACY));
                    } else {
                        tvStudiedCardCountAndTime.setText(HtmlCompat.fromHtml("Studied <font color='#2f90d0'><u>" + reviewedCard + "</u></font> Cards Today", HtmlCompat.FROM_HTML_MODE_LEGACY));
                    }

                    parentAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        Toast.makeText(activity, jsonString, Toast.LENGTH_SHORT).show();
    }

    // start review of deck and sub deck
    public void startReview(String type, String id) {
        mId = id;
        mType = type;
        if (mType.equals(Const.DECK_ID)) {
            myNetworkCall.NetworkAPICall(API.API_REVIEW_CARD_DECK, true);
        } else {
            myNetworkCall.NetworkAPICall(API.API_REVIEW_CARD_SUBDECK, true);
        }

        Log.e("TAG", "startReview: id -  " + id);
        Log.e("TAG", "startReview: type -  " + mType);
//        myNetworkCall.NetworkAPICall(API.API_READ_CARD, true);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//        if (!isSpinnerTouched) return;
        Log.e("TAG", "onItemSelected: " + position);
        String selectedItem = adapterView.getItemAtPosition(position).toString();
        tvSpinner.setText(selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        isSpinnerTouched = true;
        return false;
    }

    private void showConfirmDialogue(String cardNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("You have selected " + cardNo + " new card from each subject")
                .setCancelable(false)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    dialog.cancel();
                    myNetworkCall.NetworkAPICall(API.API_ADD_NEW_CARD, true);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());

        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setTotalCardAndTime(String totalReadCard, int totalTime) {
        String minute = Helper.combinationFormatter(totalTime);

        if (!totalReadCard.equals("") && !minute.equals("")) {
            Log.e("setTotalCardAndTime: ", minute + " " + totalReadCard);
            tvStudiedCardCountAndTime.setText(HtmlCompat.fromHtml("Studied <font color='#2f90d0'>" + totalReadCard + "</font> Cards in <font color='#2f90d0'>" + minute + "</font> Today", HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
    }
}
