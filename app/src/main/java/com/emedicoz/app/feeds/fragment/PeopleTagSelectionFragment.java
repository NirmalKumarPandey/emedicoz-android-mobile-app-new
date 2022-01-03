package com.emedicoz.app.feeds.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.R;
import com.emedicoz.app.customviews.CustomSearchListAdapter;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.modelo.People;
import com.emedicoz.app.response.FollowResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.FollowingListApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PeopleTagSelectionFragment extends AbsSearchListViewFragment {

    ArrayList<People> followResponseArrayList;
    ArrayList<People> alreadyTaggedPeople;
    private PeopleTagAdapter mAdapter;
    private IPeopleSelectionListener selectionListener;
    Activity activity;

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        followResponseArrayList = new ArrayList<>();
        super.onAttach(activity);
        if (!(activity instanceof IPeopleSelectionListener)) {
            throw new IllegalArgumentException(activity.getLocalClassName() + " must implement ITagSelectionListener");
        }
        selectionListener = (IPeopleSelectionListener) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        followResponseArrayList = ((PostActivity) activity).followResponseArrayList;
        if (getArguments() != null) {
            alreadyTaggedPeople = (ArrayList<People>) getArguments().getSerializable(Const.ALREADY_TAGGED_PEOPLE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Choose People To Tag");
    }

    @Override
    protected void fetchList() {
        if (!followResponseArrayList.isEmpty()) {
            mAdapter.addAllItems(getCustomFileterList());
        } else
            networkCallForFollowingList();
    }

    public ArrayList<People> getCustomFileterList() {
        if (alreadyTaggedPeople == null) {
            return followResponseArrayList;
        } else {
            ArrayList<People> tempArray = new ArrayList<>(followResponseArrayList);
            for (People alreadyppl : alreadyTaggedPeople) {

                for (People res : followResponseArrayList) {
                    if (alreadyppl.getId().equals(res.getId())) {
                        tempArray.remove(res);
                    }
                }
            }
            followResponseArrayList = tempArray;

        }
        return followResponseArrayList;
    }

    @Override
    protected RecyclerView.Adapter getListAdapter() {
        mAdapter = new PeopleTagAdapter();
        return mAdapter;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_tag_selection;
    }

    private void networkCallForFollowingList() {
        FollowingListApiInterface apiInterface = ApiClient.createService(FollowingListApiInterface.class);
        Call<JsonObject> response = apiInterface.followinglist(SharedPreference.getInstance().getLoggedInUser().getId(),
                SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    Gson gson = new Gson();
                    JSONArray dataArray;
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            followResponseArrayList = new ArrayList<>();
                            dataArray = GenericUtils.getJsonArray(jsonResponse);
                            if (dataArray.length() > 0) {
                                int i = 0;
                                while (i < dataArray.length()) {
                                    JSONObject singledatarow = dataArray.optJSONObject(i);
                                    FollowResponse response1 = gson.fromJson(singledatarow.toString(), FollowResponse.class);
                                    People people = new People();
                                    people.setName(response1.getViewable_user().getName());
                                    people.setProfile_picture(response1.getViewable_user().getProfile_picture());
                                    people.setId(response1.getUser_id());
                                    followResponseArrayList.add(people);
                                    i++;
                                }
                                ((PostActivity) activity).followResponseArrayList = followResponseArrayList;
                                mAdapter.addAllItems(getCustomFileterList());
                            }
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    public interface IPeopleSelectionListener {
        void onPeopleTagSelected(People response);
    }

    private class PeopleTagAdapter extends CustomSearchListAdapter<People> {

        @Override
        protected List<People> getFilteredList(String query) {
            ArrayList<People> filteredList = new ArrayList<>();
            if (query == null || query.trim().length() == 0)
                filteredList = masterItems;
            else {
                String peopleName;
                int size = masterItems.size();
                for (int i = 0; i < size; i++) {
                    peopleName = masterItems.get(i).getName();
                    if (peopleName.toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(masterItems.get(i));
                    }
                }
            }
            return filteredList;
        }

        @Override
        protected RecyclerView.ViewHolder createViewholder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_people_tag, parent, false);

            return new ViewHolder(v);
        }

        @Override
        protected void bindViewholder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder tagHolder = (ViewHolder) holder;
            tagHolder.setUsers(getItem(position));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView nameTV;

            ImageView imageIV;
            ImageView imageIVText;
            People people;

            public ViewHolder(final View view) {
                super(view);

                nameTV = view.findViewById(R.id.nameTV);

                imageIV = view.findViewById(R.id.imageIV);
                imageIVText = view.findViewById(R.id.imageIVText);
                view.setOnClickListener(v -> {
                    if (selectionListener != null && people != null)
                        selectionListener.onPeopleTagSelected(people);
                    PeopleTagSelectionFragment.this.dismiss();
                });
            }

            public void setUsers(People people) {
                this.people = people;
                people.setName(Helper.CapitalizeText(people.getName()));

                if (query != null && query.trim().length() > 0) {
                    Spannable wordtoSpan = new SpannableString(people.getName());
                    int spanStartIndex = people.getName().toLowerCase().indexOf(query.toLowerCase());
                    if (spanStartIndex >= 0) {
                        wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), spanStartIndex, spanStartIndex + query.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        wordtoSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), spanStartIndex, spanStartIndex + query.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    nameTV.setText(wordtoSpan);
                } else {
                    nameTV.setText(people.getName());
                }

                if (!TextUtils.isEmpty(people.getProfile_picture())) {
                    imageIV.setVisibility(View.VISIBLE);
                    imageIVText.setVisibility(View.GONE);

                    Glide.with(activity)
                            .asBitmap()
                            .load(people.getProfile_picture())
                            .apply(new RequestOptions()
                                    .placeholder(R.mipmap.default_pic).error(R.mipmap.default_pic))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                    imageIV.setImageBitmap(result);
                                }
                            });
                } else {
                    imageIV.setVisibility(View.GONE);
                    imageIVText.setVisibility(View.VISIBLE);
                    imageIVText.setImageDrawable(Helper.GetDrawable(people.getName(), getContext(), people.getId()));
                }
            }
        }
    }
}
