package com.emedicoz.app.flashcard.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.activity.FlashCardActivity;
import com.emedicoz.app.flashcard.model.FlashCards;
import com.emedicoz.app.flashcard.model.SubDeck;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
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

public class VaultChildAdapter extends RecyclerView.Adapter<VaultChildAdapter.VaultChildHolder> {

    Context context;
    List<SubDeck> subDeckList;

    public VaultChildAdapter(Context context, List<SubDeck> subDeckList) {
        this.context = context;
        this.subDeckList = subDeckList;
    }

    @NonNull
    @Override
    public VaultChildHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.vault_child_adapter_item, viewGroup, false);
        return new VaultChildHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaultChildHolder vaultChildHolder, int i) {
        SubDeck subDeck = subDeckList.get(i);
        vaultChildHolder.title.setText(subDeck.getTitle());
        vaultChildHolder.completedCountTV.setText(subDeck.getTotalCard() + " Cards");
        if (((FlashCardActivity) context).type == 0) {
            vaultChildHolder.btnRead.setText(context.getString(R.string.read));
        } else if (((FlashCardActivity) context).type == 1) {
            vaultChildHolder.btnRead.setText(context.getString(R.string.read_bookmarked));
        } else if (((FlashCardActivity) context).type == 2) {
            vaultChildHolder.btnRead.setText(context.getString(R.string.read_suspended));
        }

        if (!TextUtils.isEmpty(subDeck.getImage())) {
            Glide.with(context).load(subDeck.getImage())
                    .apply(new RequestOptions().error(Helper.GetDrawable(subDeck.getTitle(), context, ColorGenerator.MATERIAL.getRandomColor())))
                    .into(vaultChildHolder.topicIV);
        } else
            vaultChildHolder.topicIV.setImageDrawable(Helper.GetDrawable(subDeck.getTitle(), context, ColorGenerator.MATERIAL.getRandomColor()));
    }

    @Override
    public int getItemCount() {
        return null != subDeckList ? subDeckList.size() : 0;
    }

    class VaultChildHolder extends RecyclerView.ViewHolder implements View.OnClickListener, MyNetworkCall.MyNetworkCallBack {

        ImageView topicIV;
        TextView title;
        TextView completedCountTV;
        MyNetworkCall myNetworkCall;
        int position;
        FlashCards flashCards;
        Button btnRead;
        ArrayList<FlashCards> flashCardsArrayList = new ArrayList<>();

        public VaultChildHolder(@NonNull View itemView) {
            super(itemView);

            btnRead = itemView.findViewById(R.id.btn_read);
            topicIV = itemView.findViewById(R.id.topicIV);
            title = itemView.findViewById(R.id.title);
            completedCountTV = itemView.findViewById(R.id.completedCountTV);
            btnRead.setOnClickListener(this);
            myNetworkCall = new MyNetworkCall(this, context);
        }

        @Override
        public void onClick(View view) {
            position = getAdapterPosition();
            if (position >= 0) {
                myNetworkCall.NetworkAPICall(API.API_READ_CARD, true);
            }
        }

        @Override
        public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
            Map<String, Object> params = new HashMap<>();
            params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser() != null ? SharedPreference.getInstance().getLoggedInUser().getId() : "");
            params.put(Constants.Extras.TYPE, ((FlashCardActivity) context).type + 1);
            params.put(Const.SUBDECK, subDeckList.get(position).getSdId());
            params.put(Const.RANDOM, !TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.CARD_SEQUENCE_STATUS)) ? SharedPreference.getInstance().getString(Const.CARD_SEQUENCE_STATUS) : "0");
            Log.e("VaultChildHolder", "getAPI: " + params);
            return service.postData(apiType, params);
        }

        @Override
        public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
            JSONArray jsonArray = jsonObject.optJSONArray(Const.DATA);
            Gson gson = new Gson();
            if (Objects.requireNonNull(jsonArray).length() > 0) {
                flashCardsArrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    flashCards = gson.fromJson(jsonArray.optJSONObject(i).toString(), FlashCards.class);
                    flashCardsArrayList.add(flashCards);
                }

                if (((FlashCardActivity) context).type == 0 || ((FlashCardActivity) context).type == 1) {
                    ((FlashCardActivity) context).isFirstHit = true;
                }

                if (((FlashCardActivity) context).type == 0 || ((FlashCardActivity) context).type == 1) {
                    for (int i = 0; i < flashCardsArrayList.size(); i++) {
                        flashCardsArrayList.get(i).setShow(true);
                    }
                    Helper.gotoViewFlashCardActivity(((FlashCardActivity) context).type, (Activity) context, subDeckList.get(position).getTitle(), flashCardsArrayList, true);
                } else {
                    Helper.gotoViewFlashCardActivity((Activity) context, subDeckList.get(position).getTitle(), flashCardsArrayList, false);
                }
            }
        }

        @Override
        public void errorCallBack(String jsonString, String apiType) {
            Helper.showToast(jsonString, context);
        }
    }
}
