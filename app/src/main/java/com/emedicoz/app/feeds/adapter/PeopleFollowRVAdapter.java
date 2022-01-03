package com.emedicoz.app.feeds.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.emedicoz.app.R;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.response.FollowResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.PeopleFollowApiInterface;
import com.emedicoz.app.retrofit.apiinterfaces.ProfileApiInterface;
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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class PeopleFollowRVAdapter extends RecyclerView.Adapter<PeopleFollowRVAdapter.ViewHolder> {

    ArrayList<FollowResponse> peopleArrayList;
    Activity activity;
    int currentPosition;
    User user = new User();
    ViewHolder mainHolder;
    int i = 0, x = 0;

    public PeopleFollowRVAdapter(ArrayList<FollowResponse> peopleArrayList, Activity activity) {
        this.peopleArrayList = peopleArrayList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_people, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FollowResponse people = peopleArrayList.get(position);

        if (people.getViewable_user() != null) {
            people.getViewable_user().setName(Helper.CapitalizeFirstLetterText(people.getViewable_user().getName()));

            holder.nameTV.setText(people.getViewable_user().getName());
            if (!GenericUtils.isEmpty(people.getViewable_user().getProfile_picture())) {
                holder.imageIV.setVisibility(View.VISIBLE);
                holder.imageIVText.setVisibility(View.GONE);

                Glide.with(activity)
                        .load(people.getViewable_user().getProfile_picture())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.default_pic)
                                .error(R.mipmap.default_pic))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.imageIV.setVisibility(View.GONE);
                                holder.imageIVText.setVisibility(View.VISIBLE);
                                holder.imageIVText.setImageDrawable(Helper.GetDrawable(people.getViewable_user().getName(), activity, people.getViewable_user().getId()));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(holder.imageIV);

            } else {
                holder.imageIV.setVisibility(View.GONE);
                holder.imageIVText.setVisibility(View.VISIBLE);
                holder.imageIVText.setImageDrawable(Helper.GetDrawable(people.getViewable_user().getName(), activity, people.getViewable_user().getId()));
            }
            Objects.requireNonNull(holder.llFollow).setTag(holder);

            if (people.getViewable_user().getId().equals(SharedPreference.getInstance().getLoggedInUser().getId()))
                holder.llFollow.setVisibility(View.GONE);
            else {
                holder.llFollow.setVisibility(View.VISIBLE);
                if (people.isWatcher_following()) {
                    holder.llFollow = changeBackgroundColor(holder.llFollow, 1);
                } else {
                    holder.llFollow = changeBackgroundColor(holder.llFollow, 0);
                }
            }
        }
        holder.llFollow.setTag(holder);
        holder.llFollow.setOnClickListener(holder.onFollowClick);

    }

    public LinearLayout changeBackgroundColor(LinearLayout v, int type) {
        v.invalidate();
        TextView textView = v.findViewById(R.id.tvFollow);
        ImageView imageView = v.findViewById(R.id.ivFollow);
        if (type == 1) {
            textView.setText(R.string.following);
            imageView.setBackgroundResource(R.mipmap.profile_followers);
            v.setBackgroundResource(R.drawable.reg_round_blue_bg);
            textView.setTextColor(ContextCompat.getColor(activity, R.color.white));
        } else {
            imageView.setBackgroundResource(R.mipmap.follow_blue);
            v.setBackgroundResource(R.drawable.reg_round_white_bg);
            textView.setText(R.string.follow);
            textView.setTextColor(ContextCompat.getColor(activity, R.color.blue));
        }
        return v;
    }

    @Override
    public int getItemCount() {
        return peopleArrayList.size();
    }

    private void networkCallForFollow() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        PeopleFollowApiInterface apiInterface = ApiClient.createService(PeopleFollowApiInterface.class);
        Call<JsonObject> response = apiInterface.follow(peopleArrayList.get(currentPosition).getViewable_user().getId(),
                SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            initViewFollowUnfollow(1);
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        GenericUtils.showToast(activity, activity.getResources().getString(R.string.jsonparsing_error_message));
                        e.printStackTrace();
                    }
                } else
                    GenericUtils.showToast(activity, activity.getResources().getString(R.string.exception_api_error_message));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    private void networkCallForUnFollow() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        PeopleFollowApiInterface apiInterface = ApiClient.createService(PeopleFollowApiInterface.class);
        Call<JsonObject> response = apiInterface.unfollow(peopleArrayList.get(currentPosition).getViewable_user().getId(),
                SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            initViewFollowUnfollow(0);
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        GenericUtils.showToast(activity, activity.getResources().getString(R.string.jsonparsing_error_message));
                        e.printStackTrace();
                    }
                } else
                    GenericUtils.showToast(activity, activity.getResources().getString(R.string.exception_api_error_message));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    public void initViewFollowUnfollow(int type) {
        if (type == 1) {
            mainHolder.llFollow = changeBackgroundColor(mainHolder.llFollow, type);
            peopleArrayList.get(currentPosition).setWatcher_following(true);
            networkCallForGetUser();
        } else {
            mainHolder.llFollow = changeBackgroundColor(mainHolder.llFollow, type);
            peopleArrayList.get(currentPosition).setWatcher_following(false);
            networkCallForGetUser();
        }
    }

    private void networkCallForGetUser() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        ProfileApiInterface apiInterface = ApiClient.createService(ProfileApiInterface.class);
        Call<JsonObject> response = apiInterface.getUser("data_model/user/Registration/get_active_user/"
                + SharedPreference.getInstance().getLoggedInUser().getId() + Const.IS_WATCHER + SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            user = gson.fromJson(data.toString(), User.class);
                            SharedPreference.getInstance().setLoggedInUser(user);

                        } else {
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View.OnClickListener onFollowClick;
        TextView nameTV;
        ImageView imageIV;
        ImageView imageIVText;
        LinearLayout llFollow;

        public ViewHolder(final View view) {
            super(view);

            llFollow = view.findViewById(R.id.llFollow);

            nameTV = view.findViewById(R.id.nameTV);

            imageIV = view.findViewById(R.id.imageIV);
            imageIVText = view.findViewById(R.id.imageIVText);
            view.setOnClickListener((View v) -> Helper.GoToProfileActivity(activity, peopleArrayList.get(getAdapterPosition()).getViewable_user().getId()));

            onFollowClick = (View v) -> {
                mainHolder = (ViewHolder) v.getTag();
                currentPosition = getAdapterPosition();
                if (peopleArrayList.get(currentPosition).isWatcher_following()) {
                    networkCallForUnFollow();//networkCall.NetworkAPICall(API.API_UNFOLLOW, true);
                } else {
                    networkCallForFollow();//networkCall.NetworkAPICall(API.API_FOLLOW, true);
                }
            };
        }
    }
}
