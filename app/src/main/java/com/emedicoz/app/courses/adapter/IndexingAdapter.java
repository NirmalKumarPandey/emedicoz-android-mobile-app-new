package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.feeds.activity.FeedsActivity;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.response.registration.StreamResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.RegFragApis;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IndexingAdapter extends RecyclerView.Adapter<IndexingAdapter.IndexingViewHolder> {
    Activity activity;
    ArrayList<StreamResponse> topics;
    private Progress mProgress;

    public IndexingAdapter(Activity activity, ArrayList<StreamResponse> topics) {
        this.activity = activity;
        this.topics = topics;
    }

    @NonNull
    @Override
    public IndexingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_row_indexing, viewGroup, false);

        mProgress = new Progress(activity);
        mProgress.setCancelable(false);

        return new IndexingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IndexingViewHolder holder, int i) {

        holder.indexingTV.setText(topics.get(i).getText_name());
        holder.indexingTV.setTag(i);
        holder.indexingTV.setOnClickListener(v -> {
            int pos = (int) v.getTag();
            networkCallForUpdatingStream(topics.get(pos), topics.get(pos));
        });
        if (i == topics.size() - 1)
            holder.divider.setVisibility(View.GONE);
    }

    public void networkCallForUpdatingStream(StreamResponse streamResponse, StreamResponse topic) {
        mProgress.show();
        Call<JsonObject> response;
        RegFragApis apis = ApiClient.createService(RegFragApis.class);
        response = apis.saveStreamDataForUser(SharedPreference.getInstance().getLoggedInUser().getId(), streamResponse.getId());

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            SharedPreference pref = SharedPreference.getInstance();
                            User user = pref.getLoggedInUser();
                            user.getUser_registration_info().setMaster_id(streamResponse.getId());
                            pref.setLoggedInUser(user);

                            pref.putBoolean(Const.IS_STATE_CHANGE, true);
                            pref.putBoolean(Const.IS_LANDING_DATA, true);
                            pref.putBoolean(Const.IS_STREAM_CHANGE, true);
                            eMedicozApp.getInstance().postFile = null;
                            Helper.getStorageInstance(activity).deleteRecord(Const.OFFLINE_COURSE);

                            if (activity instanceof HomeActivity) {
                                ((HomeActivity) activity).streamTV.setText(topic.getText_name());
                                ((HomeActivity) activity).setModeratorSelectedStream(topic);
                                pref.putString(Const.SUBTITLE, "");
                                if (((HomeActivity) activity).streamPopUp != null) {
                                    ((HomeActivity) activity).streamPopUp.dismiss();
                                    ((HomeActivity) activity).navController.navigateUp();
                                }

                                Intent intent = new Intent(activity, HomeActivity.class);
                                intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Helper.showErrorLayoutForNoNav(API.API_STREAM_REGISTRATION, activity, 1, 2);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_STREAM_REGISTRATION, activity, 1, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public class IndexingViewHolder extends RecyclerView.ViewHolder {

        TextView indexingTV;
        View divider;

        public IndexingViewHolder(@NonNull View itemView) {
            super(itemView);
            indexingTV = itemView.findViewById(R.id.indexingTV);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
