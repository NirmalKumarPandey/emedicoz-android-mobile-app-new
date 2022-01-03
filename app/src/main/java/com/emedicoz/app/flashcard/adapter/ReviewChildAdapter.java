package com.emedicoz.app.flashcard.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.CustomItemSelectedListener;
import com.emedicoz.app.flashcard.activity.FlashCardActivity;
import com.emedicoz.app.flashcard.fragment.FlashCardReviewFragment;
import com.emedicoz.app.flashcard.model.FlashCards;
import com.emedicoz.app.flashcard.model.SubDeck;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

public class ReviewChildAdapter extends RecyclerView.Adapter<ReviewChildAdapter.ReviewChildHolder> {

    private Context context;
    private List<SubDeck> subDeckList;
    private FlashCardReviewFragment flashCardReviewFragment;

    public ReviewChildAdapter(Context context, List<SubDeck> subDeckList, FlashCardReviewFragment flashCardReviewFragment) {
        this.context = context;
        this.subDeckList = subDeckList;
        this.flashCardReviewFragment = flashCardReviewFragment;
    }

    @NonNull
    @Override
    public ReviewChildHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.flashcard_review_child_adapter_item, viewGroup, false);
        return new ReviewChildHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewChildHolder reviewHolder, int i) {
        if (i < subDeckList.size() - 1) {
            reviewHolder.divider.setVisibility(View.VISIBLE);
        } else {
            reviewHolder.divider.setVisibility(View.GONE);
        }

        SubDeck subDeck = subDeckList.get(i);
        reviewHolder.tvTitle.setText(subDeck.getTitle());
        if (subDeck.getReviewToday().equals("0") || subDeck.getReviewToday().equals("1")) {
            reviewHolder.tvReviewCount.setText(String.format("%s Review Today", subDeck.getReviewToday()));
        } else {
            reviewHolder.tvReviewCount.setText(String.format("%s Reviews Today", subDeck.getReviewToday()));
        }
        if (Integer.parseInt(subDeck.getReviewToday()) > 0) {
            reviewHolder.btnStartReview.setEnabled(true);
        } else {
            reviewHolder.btnStartReview.setEnabled(false);
        }
        reviewHolder.tvStudiedCount.setText(String.format("%s/%s %s", subDeck.getReadCards(), subDeck.getTotalCard(), context.getString(R.string.studied)));
        if (!TextUtils.isEmpty(subDeck.getImage())) {
            Glide.with(context).load(subDeck.getImage()).into(reviewHolder.topicIV);
        } else {
            reviewHolder.topicIV.setImageDrawable(Helper.GetDrawable(subDeck.getTitle(), context, ColorGenerator.MATERIAL.getRandomColor()));
        }

        reviewHolder.tvCountLeft.setText(String.format("%d left", Integer.parseInt(subDeck.getTotalCard()) - Integer.parseInt(subDeck.getReadCards())));
    }

    @Override
    public int getItemCount() {
        return null != subDeckList ? subDeckList.size() : 0;
    }

    class ReviewChildHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnItemSelectedListener, MyNetworkCall.MyNetworkCallBack, CustomItemSelectedListener {

        TextView tvReviewCount;
        TextView tvTitle;
        TextView tvSubtopic;
        TextView tvAddNewCard;
        TextView tvStudiedCount;
        TextView tvCountLeft;
        View divider;
        ImageView topicIV;
        ImageView ivMore;
        Button btnStartReview;
        Button btnConfirm;
        Button btnViewAllCard;
        Button btnStudyBookmark;
        Spinner spin1;
        RelativeLayout rlDropdown1;
        RelativeLayout rlDropdown2;
        LinearLayout llRoot;
        MyNetworkCall myNetworkCall;
        private ArrayList<FlashCards> flashCardsArrayList = new ArrayList<>();
        private FlashCards flashCards;
        private BottomSheetDialog mBottomSheetDialog;
        int position;
        PopupWindow popupWindow;
        private String mId = "";
        private String mType = "";


        public ReviewChildHolder(@NonNull View itemView) {
            super(itemView);

            tvReviewCount = itemView.findViewById(R.id.tv_recview_count);
            tvStudiedCount = itemView.findViewById(R.id.tv_studied_count);
            tvTitle = itemView.findViewById(R.id.tv_title);
            divider = itemView.findViewById(R.id.divider);
            btnStartReview = itemView.findViewById(R.id.btn_start_review);
            topicIV = itemView.findViewById(R.id.topicIV);
            ivMore = itemView.findViewById(R.id.iv_more);
            tvCountLeft = itemView.findViewById(R.id.tv_count_left);
            myNetworkCall = new MyNetworkCall(this, context);
            ivMore.setOnClickListener(this);
            btnStartReview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            position = getAdapterPosition();
            if (position >= 0) {
                switch (view.getId()) {
                    case R.id.btn_start_review:
                        flashCardReviewFragment.startReview(Const.SUBDECK_ID, subDeckList.get(position).getSdId());
                        break;
                    case R.id.rl_dropdown1:
                        spin1.performClick();
                        break;
                    case R.id.rl_dropdown2:
                        showSubTopic(view);
                        break;
                    case R.id.iv_more:
                        showBottomSheet();
                        break;
                    case R.id.btn_confirm:
                        mBottomSheetDialog.dismiss();
                        myNetworkCall.NetworkAPICall(API.API_ADD_CARD_SUBDECK, true);
                        break;
                    case R.id.btn_view_all_card:
                        mType = "1";
                        myNetworkCall.NetworkAPICall(API.API_READ_CARD, true);
                        break;
                    case R.id.btn_study_bookmark:
                        mType = "2";
                        myNetworkCall.NetworkAPICall(API.API_READ_CARD, true);
                        break;
                    default:
                        break;
                }
            }
        }

        private void showSubTopic(View view) {
            View popUpView = ((FlashCardActivity) context).getLayoutInflater().inflate(R.layout.dropdown_recyclerview, null);
            if (subDeckList.get(position).getTopics().size() < 5) {
                popupWindow = new PopupWindow(popUpView, rlDropdown1.getWidth(), WindowManager.LayoutParams.WRAP_CONTENT, true);
            } else {
                popupWindow = new PopupWindow(popUpView, rlDropdown1.getWidth(), 350, true);
            }

            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            RecyclerView recyclerView = popUpView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            DropDownAdapter otrsCategoryAdapter = new DropDownAdapter(context, subDeckList.get(position).getTopics(), this);
            recyclerView.setAdapter(otrsCategoryAdapter);
            popupWindow.showAsDropDown(view);
        }

        private void showBottomSheet() {
            mBottomSheetDialog = new BottomSheetDialog(context);
            LayoutInflater mInflater = LayoutInflater.from(context);
            View sheetView = mInflater.inflate(R.layout.flashcard_review_child_adapter_dialog, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();

            TextView tvTitle;

            tvAddNewCard = sheetView.findViewById(R.id.tv_add_new_card);
            tvTitle = sheetView.findViewById(R.id.tv_title);
            spin1 = sheetView.findViewById(R.id.spin1);
            llRoot = sheetView.findViewById(R.id.ll_root);
            btnConfirm = sheetView.findViewById(R.id.btn_confirm);
            btnViewAllCard = sheetView.findViewById(R.id.btn_view_all_card);
            btnStudyBookmark = sheetView.findViewById(R.id.btn_study_bookmark);

            tvSubtopic = sheetView.findViewById(R.id.tv_subtopic);

            spin1.setOnItemSelectedListener(this);
            tvTitle.setText(subDeckList.get(position).getTitle());

            getSpinner(spin1);
            rlDropdown1 = sheetView.findViewById(R.id.rl_dropdown1);
            rlDropdown2 = sheetView.findViewById(R.id.rl_dropdown2);

            rlDropdown1.setOnClickListener(this);
            rlDropdown2.setOnClickListener(this);
            btnConfirm.setOnClickListener(this);
            btnViewAllCard.setOnClickListener(this);
            btnStudyBookmark.setOnClickListener(this);
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String selectedItem = adapterView.getItemAtPosition(i).toString();
            tvAddNewCard.setText(selectedItem);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

        @Override
        public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
            Map<String, Object> params = new HashMap<>();
            params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser() != null ? SharedPreference.getInstance().getLoggedInUser().getId() : "");
            switch (apiType) {
                case API.API_READ_CARD:
                    params.put(Constants.Extras.TYPE, mType);
                    params.put(Const.SUBDECK, subDeckList.get(position).getSdId());
                    params.put(Const.RANDOM, !TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.CARD_SEQUENCE_STATUS)) ? SharedPreference.getInstance().getString(Const.CARD_SEQUENCE_STATUS) : "0");
                    break;
//                case API.API_REVIEW_CARD_SUBDECK:
                case API.API_ADD_CARD_SUBDECK:
                    params.put(Const.NO_OF_CARD, Helper.GetText(tvAddNewCard));
                    params.put(Const.SUBDECK_ID, subDeckList.get(position).getSdId());
                    params.put(Const.SUBTOPIC, mId.isEmpty() ? "" : mId);
                    break;
            }

            Log.e("ReviewChildAdapter", "getAPI: " + params);
            return service.postData(apiType, params);
        }

        @Override
        public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
            JSONArray jsonArray = jsonObject.optJSONArray(Const.DATA);
            Gson gson = new Gson();
            if (API.API_ADD_CARD_SUBDECK.equals(apiType)) {
                Fragment fragment = ((FlashCardActivity) context).getSupportFragmentManager().findFragmentById(R.id.fl_container);
                if (fragment instanceof FlashCardReviewFragment)
                    ((FlashCardReviewFragment) fragment).hitApi();
            }
            if (Objects.requireNonNull(jsonArray).length() > 0) {
                flashCardsArrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    flashCards = gson.fromJson(jsonArray.optJSONObject(i).toString(), FlashCards.class);
                    flashCardsArrayList.add(flashCards);
                }
                mBottomSheetDialog.dismiss();
                Helper.gotoViewFlashCardActivity((Activity) context, "", flashCardsArrayList, false);
            }
        }

        @Override
        public void errorCallBack(String jsonString, String apiType) {
            Toast.makeText(context, jsonString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onItemSelected(String text, String id) {
            popupWindow.dismiss();
            mId = id;
            tvSubtopic.setText(text);
            Log.e("ReviewChildAdapter", "onItemSelected: id & text :- " + id + " & " + text);
        }

        private void getSpinner(Spinner spinner) {
            final String[] count = {"5", "10", "15", "20", "25"};
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(eMedicozApp.getAppContext(), R.layout.spinadapt, R.id.weekofday, count);
            spinner.setAdapter(adapter);
        }
    }

}


