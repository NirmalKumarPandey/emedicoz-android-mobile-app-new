package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.R;
import com.emedicoz.app.pubnub.PubSubPojo;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.SharedPreference;

import java.util.ArrayList;

public class PubSubAdapter extends RecyclerView.Adapter<PubSubAdapter.PubSubViewHolder> {
    private static final String TAG = "PubSubAdapter";

    Activity activity;
    ArrayList<PubSubPojo> values;

    public PubSubAdapter(Activity activity, ArrayList<PubSubPojo> values) {
        this.activity = activity;
        this.values = values;
    }

    @NonNull
    @Override
    public PubSubViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_pubsub, viewGroup, false);
        return new PubSubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PubSubViewHolder holder, int i) {
        PubSubPojo dsMsg = values.get(i);
        Log.e(TAG, "onBindViewHolder: " + dsMsg.getMessage() + "," + dsMsg.getSender() + "," + dsMsg.getTimestamp() + "," + i + "," + values.size());
        setData(holder, i);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public void setData(final PubSubViewHolder holder, int listPosition) {
        if (values.get(listPosition).getId().equals(SharedPreference.getInstance().getLoggedInUser().getId()) && !values.get(listPosition).getType().equalsIgnoreCase("admin")) {

            Log.e(TAG, "setData is called " + listPosition);
            holder.llAdmin.setVisibility(View.GONE);
            holder.llUser.setVisibility(View.VISIBLE);

            String msg = values.get(listPosition).getSender() + "-" + values.get(
                    listPosition).getMessage();

       /*     Spannable spannable = new SpannableString(msg);
            spannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, dataSet.get(listPosition).getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, dataSet.get(listPosition).getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvUser.setText(spannable, TextView.BufferType.SPANNABLE);
*/
            holder.tvUser.setText(msg);
            if (!SharedPreference.getInstance().getLoggedInUser().getProfile_picture().equals("")) {
                Glide.with(activity)
                        .asBitmap()
                        .load(SharedPreference.getInstance().getLoggedInUser().getProfile_picture())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.default_profile_img))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                Bitmap scale = Bitmap.createScaledBitmap(result, 100, 100, true);
                                holder.ivUser.setImageBitmap(scale);
                            }
                        });
            } else {
                holder.ivUser.setImageResource(R.mipmap.default_profile_img);
            }
        } else {
            if (!GenericUtils.isEmpty(values.get(listPosition).getProfile_pic())) {
                Glide.with(activity)
                        .asBitmap()
                        .load(values.get(listPosition).getProfile_pic())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.default_profile_img))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                Bitmap scale = Bitmap.createScaledBitmap(result, 100, 100, true);
                                holder.ivAdmin.setImageBitmap(scale);
                            }
                        });
            } else {
                holder.ivAdmin.setImageResource(R.mipmap.default_profile_img);
            }
            holder.llUser.setVisibility(View.GONE);
            holder.llAdmin.setVisibility(View.VISIBLE);

            String msg = values.get(listPosition).getSender() + "-" + values.get(listPosition).getMessage();

/*            Spannable spannable = new SpannableString(msg);
            spannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, dataSet.get(listPosition).getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, dataSet.get(listPosition).getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvAdmin.setText(spannable, TextView.BufferType.SPANNABLE);*/
            holder.tvAdmin.setText(msg);
/*            holder.tv_username.setText(dataSet.get(listPosition).getName());
            holder.tv_time.setText(dataSet.get(listPosition).getDate());*/

        }
    }

    public class PubSubViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAdmin, ivUser;
        TextView tvAdmin, tvUser, tv_username1, tv_username, tv_time, tv_time1;
        LinearLayout llAdmin, llUser;

        public PubSubViewHolder(@NonNull View convertView) {
            super(convertView);

            tv_username1 = itemView.findViewById(R.id.tv_username1);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_time1 = itemView.findViewById(R.id.tv_time1);
            ivUser = itemView.findViewById(R.id.iv_user);
            ivAdmin = itemView.findViewById(R.id.iv_admin);
            tvUser = itemView.findViewById(R.id.tv_user);
            tvAdmin = itemView.findViewById(R.id.tv_admin);
            llUser = itemView.findViewById(R.id.ll_user);
            llAdmin = itemView.findViewById(R.id.ll_admin);
        }
    }
}
