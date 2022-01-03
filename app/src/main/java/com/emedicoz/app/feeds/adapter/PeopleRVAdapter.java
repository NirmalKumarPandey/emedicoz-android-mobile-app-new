package com.emedicoz.app.feeds.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.emedicoz.app.feeds.activity.FeedsActivity;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.modelo.People;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.PeopleFollowApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.ShadowContainerView;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleRVAdapter extends RecyclerView.Adapter<PeopleRVAdapter.ViewHolder> {

    List<People> peopleArrayList;
    Activity activity;
    int check;
    String type;
    String peopleType;
    int currentPosition;

    public PeopleRVAdapter(List<People> peopleArrayList, Activity activity, String type, String peopleType, int check) {
        this.peopleArrayList = peopleArrayList;
        this.activity = activity;
        this.type = type;
        this.peopleType = peopleType;
        this.check = check;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_row_people_you_know, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_row_people, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (type.equals(Const.PEOPLE_LIST_FEEDS)) return 1; // list of people on feeds
        else if (type.equals(Const.PEOPLE_LIST_FEEDS_COMMONS))
            return 2; // list of people in common fragment
        else return 0; // Like list on common fragment
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        People people = peopleArrayList.get(position);

        holder.setPeopleData(people, getItemViewType(position));

    }

    @Override
    public int getItemCount() {
        return peopleArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTV;
        TextView dateTV;
        TextView specialisationTV;
        TextView followersTV;
        ImageView imageIV;
        ImageView imageIVText;
        ShadowContainerView scvFollowBtn;
        TextView tvFollow;
        ImageView ivFollow;
        LinearLayout llFollow;

        public ViewHolder(final View view) {
            super(view);
            nameTV = view.findViewById(R.id.nameTV);
            dateTV = view.findViewById(R.id.dateTV);
            specialisationTV = view.findViewById(R.id.specialisationTV);
            followersTV = view.findViewById(R.id.followersTV);
            imageIV = view.findViewById(R.id.imageIV);
            imageIVText = view.findViewById(R.id.imageIVText);
            tvFollow = view.findViewById(R.id.tvFollow);
            ivFollow = view.findViewById(R.id.ivFollow);
            llFollow = view.findViewById(R.id.llFollow);
            scvFollowBtn = view.findViewById(R.id.scvFollowBtn);

            view.setOnClickListener(view1 -> {
                if (check != 1)
                    Helper.GoToProfileActivity(activity, peopleArrayList.get(getAdapterPosition()).getUser_id());
            });
        }

        private void networkCallForFollow() {
            PeopleFollowApiInterface apiInterface = ApiClient.createService(PeopleFollowApiInterface.class);
            Call<JsonObject> response = apiInterface.follow(peopleArrayList.get(currentPosition).getUser_id(),
                    SharedPreference.getInstance().getLoggedInUser().getId());
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                if (activity instanceof PostActivity)
                                    ((PostActivity) activity).followExpertCounter++;
                                initViewFollowUnfollow(1);
                            } else {
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Helper.showExceptionMsg(activity, t);
                }
            });
        }

        private void networkCallForUnFollow() {
            PeopleFollowApiInterface apiInterface = ApiClient.createService(PeopleFollowApiInterface.class);
            Call<JsonObject> response = apiInterface.unfollow(peopleArrayList.get(currentPosition).getUser_id(),
                    SharedPreference.getInstance().getLoggedInUser().getId());
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                if (activity instanceof PostActivity && ((PostActivity) activity).followExpertCounter > 0)
                                    ((PostActivity) activity).followExpertCounter--;

                                initViewFollowUnfollow(0);
                            } else {
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Helper.showExceptionMsg(activity, t);
                }
            });
        }

        public void initViewFollowUnfollow(int type) {
            if (type == 1) {
                changeFollowBackground(llFollow, ivFollow, tvFollow, type);
                peopleArrayList.get(currentPosition).setWatcher_following(true);
                if (!TextUtils.isEmpty(peopleType) && peopleType.equalsIgnoreCase(Const.COMMON_EXPERT_PEOPLE_VIEWALL))
                    FeedsActivity.changeFollowingExpert(peopleArrayList.get(currentPosition), 1);
                else if (!TextUtils.isEmpty(peopleType) && peopleType.equalsIgnoreCase(Const.COMMON_PEOPLE_VIEWALL))
                    FeedsActivity.changeFollowingPeople(peopleArrayList.get(currentPosition), 1);
            } else {
                changeFollowBackground(llFollow, ivFollow, tvFollow, type);
                peopleArrayList.get(currentPosition).setWatcher_following(false);
                if (!TextUtils.isEmpty(peopleType) && peopleType.equalsIgnoreCase(Const.COMMON_EXPERT_PEOPLE_VIEWALL))
                    FeedsActivity.changeFollowingExpert(peopleArrayList.get(currentPosition), 0);
                else if (!TextUtils.isEmpty(peopleType) && peopleType.equalsIgnoreCase(Const.COMMON_PEOPLE_VIEWALL))
                    FeedsActivity.changeFollowingPeople(peopleArrayList.get(currentPosition), 1);
            }
        }

        public void setPeopleData(final People people, int position) {
            people.setName(Helper.CapitalizeText(people.getName()));

            if (position != 0) {
                people.setUser_id(people.getId());
            }

            nameTV.setText(people.getName());
            setProfileImage(people);

            //this is to show the specialisation for the expert people only
            if (!TextUtils.isEmpty(peopleType) && peopleType.equalsIgnoreCase(Const.COMMON_EXPERT_PEOPLE_VIEWALL) && !TextUtils.isEmpty(people.getSpecification())) {
                specialisationTV.setVisibility(View.VISIBLE);
                specialisationTV.setText(people.getSpecification());
            } else {
                specialisationTV.setVisibility(View.GONE);
            }

            // this is to show the time in like lists
            setDate(people,position);

            //this is for the first time to show users the number of followers each expert has
            setFollowersCount(people);

            //follow button will be gone if it is the profile of the person who is seeing the list
            if (!TextUtils.isEmpty(people.getUser_id()) && people.getUser_id().equals(SharedPreference.getInstance().getLoggedInUser().getId()))
                scvFollowBtn.setVisibility(View.GONE);
            else scvFollowBtn.setVisibility(View.VISIBLE);

            //setting the UI if person who is seeing the list is following the people or not
            if (people.isWatcher_following()) {
                changeFollowBackground(llFollow, ivFollow, tvFollow, 1);
            } else {
                changeFollowBackground(llFollow, ivFollow, tvFollow, 0);
            }

            scvFollowBtn.setOnClickListener(view1 -> onFollowClick());
        }

        private void setDate(People people, int position) {
            if (position == 0) {
                if (!TextUtils.isEmpty(people.getTime())) {
                    dateTV.setVisibility(View.VISIBLE);
                    dateTV.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(people.getTime())).equals("0 minutes ago") ? "Just Now" :
                            DateUtils.getRelativeTimeSpanString(Long.parseLong(people.getTime())));

                } else dateTV.setVisibility(View.GONE);
            }
        }

        private void onFollowClick() {
            Log.e("setPeopleData: ", "from on click");
            currentPosition = getAdapterPosition();
            if (peopleArrayList.get(currentPosition).isWatcher_following()) {
                networkCallForUnFollow();
            } else {
                networkCallForFollow();
            }
        }

        private void setFollowersCount(People people) {
            if (check == 1) {
                if (!TextUtils.isEmpty(people.getFollowers_count())) {
                    followersTV.setVisibility(View.VISIBLE);
                    followersTV.setText(String.format("%s Followers", people.getFollowers_count()));
                }
            } else {
                if (peopleType != null && peopleType.equalsIgnoreCase(Const.COMMON_EXPERT_PEOPLE_VIEWALL)) {
                    followersTV.setVisibility(View.VISIBLE);
                    long count = Long.parseLong(people.getFollowers_count());
                    Log.e("setPeopleData: ", "followers: " + count);
                    String countString = "";
                    if (Math.abs(count / 1000000) > 1) {
                        countString = (count / 1000000) + "m";
                    } else if (Math.abs(count / 1000) > 1) {
                        countString = (count / 1000) + "k";
                    } else {
                        countString = String.valueOf(count);
                    }
                    followersTV.setText(String.format("%s Followers", countString));
                } else
                    followersTV.setVisibility(View.GONE);
            }

        }

        private void setProfileImage(People people) {
            if (!TextUtils.isEmpty(people.getProfile_picture())) {
                imageIV.setVisibility(View.VISIBLE);
                imageIVText.setVisibility(View.GONE);
                Glide.with(activity)
                        .load(people.getProfile_picture())
                        .apply(new RequestOptions().placeholder(R.mipmap.default_pic))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Drawable dr = Helper.GetDrawable(people.getName(), activity, people.getId());
                                if (dr != null) {
                                    imageIV.setVisibility(View.GONE);
                                    imageIVText.setVisibility(View.VISIBLE);
                                    imageIVText.setImageDrawable(dr);
                                } else {
                                    imageIV.setVisibility(View.VISIBLE);
                                    imageIVText.setVisibility(View.GONE);
                                    imageIV.setImageResource(R.mipmap.default_pic);
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(imageIV);
            } else {
                Drawable dr = Helper.GetDrawable(people.getName(), activity, people.getId());
                if (dr != null) {
                    imageIV.setVisibility(View.GONE);
                    imageIVText.setVisibility(View.VISIBLE);
                    imageIVText.setImageDrawable(dr);
                } else {
                    imageIV.setVisibility(View.VISIBLE);
                    imageIVText.setVisibility(View.GONE);
                    imageIV.setImageResource(R.mipmap.default_pic);
                }
            }
        }

        private void changeFollowBackground(LinearLayout llFollow, ImageView imageView, TextView textView, int type) {
            if (type == 1) {
                imageView.setBackgroundResource(R.mipmap.profile_followers);
                textView.setText(R.string.following);
                textView.setTextColor(ContextCompat.getColor(activity, R.color.white));
                llFollow.setBackgroundResource(R.drawable.bg_capsule_fill_blue);
            } else {
                imageView.setBackgroundResource(R.mipmap.follow_blue);
                textView.setText(R.string.follow);
                textView.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                llFollow.setBackgroundResource(R.drawable.reg_round_white_bg);
            }
        }
    }
}
