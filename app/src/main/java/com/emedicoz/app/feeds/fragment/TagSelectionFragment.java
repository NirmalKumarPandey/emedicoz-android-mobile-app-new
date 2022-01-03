package com.emedicoz.app.feeds.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.CustomSearchListAdapter;
import com.emedicoz.app.modelo.Tags;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SinglecatVideoDataApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jikoobaruah on 10/02/16.
 */
public class TagSelectionFragment extends AbsSearchListViewFragment {

    ArrayList<Tags> tagsArrayList;
    MasterFeedsHitResponse masterFeedsHitResponse;
    private TagAdapter mAdapter;
    private ITagSelectionListener selectionListener;

    @Override
    public void onAttach(Activity activity) {
        tagsArrayList = new ArrayList<>();
        super.onAttach(activity);
        if (!(activity instanceof ITagSelectionListener)) {
            throw new IllegalArgumentException(activity.getLocalClassName() + " must implement ITagSelectionListener");
        }
        selectionListener = (ITagSelectionListener) activity;
        masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Choose Tags");

    }

    @Override
    protected void fetchList() {
        if (masterFeedsHitResponse != null && !GenericUtils.isListEmpty(masterFeedsHitResponse.getAll_tags())) {
            tagsArrayList.addAll(getUserTagAsPerProfile());
            mAdapter.addAllItems(tagsArrayList);
        } else
            networkCallForMasterHit();
    }

    @Override
    protected RecyclerView.Adapter getListAdapter() {
        mAdapter = new TagAdapter();
        return mAdapter;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_tag_selection;
    }

    private void networkCallForMasterHit() {
        SinglecatVideoDataApiInterface apis = ApiClient.createService(SinglecatVideoDataApiInterface.class);
        Call<JsonObject> response = apis.getMasterFeedForUser(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            masterFeedsHitResponse = gson.fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), MasterFeedsHitResponse.class);
                            SharedPreference.getInstance().setMasterHitData(masterFeedsHitResponse);

                            if (!GenericUtils.isListEmpty(masterFeedsHitResponse.getAll_tags())) {
                                tagsArrayList.addAll(getUserTagAsPerProfile());
                                mAdapter.addAllItems(tagsArrayList);
                            }
                        } else {
                            Toast.makeText(getActivity(), jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(getActivity(), jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(getActivity(), t);
            }
        });
    }

    public ArrayList<Tags> getUserTagAsPerProfile() {
        ArrayList<Tags> tagsArrayList = new ArrayList<>();
        for (Tags tag : masterFeedsHitResponse.getAll_tags()) {
            if (tag.getMaster_id().equals(SharedPreference.getInstance().getLoggedInUser().getUser_registration_info().getMaster_id())) {
                tagsArrayList.add(tag);
            }
        }
        return tagsArrayList;
    }

    public interface ITagSelectionListener {
        void onTagSelected(Tags tag);
    }

    private class TagAdapter extends CustomSearchListAdapter<Tags> {

        @Override
        protected List<Tags> getFilteredList(String query) {
            ArrayList<Tags> filteredList = new ArrayList<>();
            if (query == null || query.trim().length() == 0)
                filteredList = masterItems;
            else {
                String tagName;
                int size = masterItems.size();
                for (int i = 0; i < size; i++) {
                    tagName = masterItems.get(i).getText();
                    if (tagName.toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(masterItems.get(i));
                    }
                }
            }
            return filteredList;
        }

        @Override
        protected RecyclerView.ViewHolder createViewholder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_tag, parent, false);

            return new ViewHolder(v);
        }

        @Override
        protected void bindViewholder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder tagHolder = (ViewHolder) holder;
            tagHolder.setTag(getItem(position));
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView nameTV;
            private Tags tag;

            public ViewHolder(View itemView) {
                super(itemView);
                nameTV = itemView.findViewById(R.id.nameTV);

                itemView.setOnClickListener(v -> {
                    if (selectionListener != null && tag != null)
                        selectionListener.onTagSelected(tag);
                    TagSelectionFragment.this.dismiss();
                });
            }

            public void setTag(Tags tags) {
                this.tag = tags;

                if (query != null && query.trim().length() > 0) {
                    Spannable wordToSpan = new SpannableString(tags.getText());
                    int spanStartIndex = tags.getText().toLowerCase().indexOf(query.toLowerCase());
                    if (spanStartIndex >= 0) {
                        wordToSpan.setSpan(new ForegroundColorSpan(Color.BLUE), spanStartIndex, spanStartIndex + query.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        wordToSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), spanStartIndex, spanStartIndex + query.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    nameTV.setText(wordToSpan);
                } else {
                    nameTV.setText(tags.getText());
                }
            }
        }
    }
}
