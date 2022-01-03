package com.emedicoz.app.feeds.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.response.FollowResponse;
import com.emedicoz.app.utilso.Helper;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO: Replace the implementation with code for your data type.
 */
public class PeopleTagRVAdapter extends RecyclerView.Adapter<PeopleTagRVAdapter.ViewHolder> {

    protected String query;
    protected ArrayList<FollowResponse> masterItems = new ArrayList<>();
    Context context;
    Activity activity;
    Bitmap bitmap;
    private ArrayList<FollowResponse> displayItems = new ArrayList<>();


    public PeopleTagRVAdapter(ArrayList<FollowResponse> peopleArrayList, Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        addAllItems(peopleArrayList);
    }

    public void animateTo(List<FollowResponse> newList) {
        applyAndAnimateRemovals(newList);
        applyAndAnimateAdditions(newList);
        applyAndAnimateMovedItems(newList);
        for (int i = 0; i < displayItems.size(); i++) {
            notifyItemChanged(i);
        }
    }

    private void applyAndAnimateRemovals(List<FollowResponse> newList) {
        for (int i = displayItems.size() - 1; i >= 0; i--) {
            final FollowResponse t = displayItems.get(i);
            if (!newList.contains(t)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<FollowResponse> newList) {
        for (int i = 0, count = newList.size(); i < count; i++) {
            final FollowResponse t = newList.get(i);
            if (!displayItems.contains(t)) {
                addItem(i, t);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<FollowResponse> newList) {
        for (int toPosition = newList.size() - 1; toPosition >= 0; toPosition--) {
            final FollowResponse t = newList.get(toPosition);
            final int fromPosition = displayItems.indexOf(t);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public FollowResponse removeItem(int position) {
        final FollowResponse t = displayItems.remove(position);
        notifyItemRemoved(position);
        return t;
    }

    public void addAllItems(ArrayList<FollowResponse> models) {
        int i = displayItems.size();
        masterItems.addAll(models);
        displayItems.addAll(models);
        notifyItemRangeInserted(i, models.size());
    }

    public void addItem(int position, FollowResponse model) {
        displayItems.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final FollowResponse t = displayItems.remove(fromPosition);
        displayItems.add(toPosition, t);
        notifyItemMoved(fromPosition, toPosition);
    }

    protected List<FollowResponse> getFilteredList(String query) {
        ArrayList<FollowResponse> filteredList = new ArrayList<>();
        if (query == null || query.trim().length() == 0)
            filteredList = masterItems;
        else {
            String userName;
            int size = masterItems.size();
            for (int i = 0; i < size; i++) {
                userName = masterItems.get(i).getViewable_user().getName();
                if (userName.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(masterItems.get(i));
                }
            }
        }
        return filteredList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_people_tag, parent, false);
        return new ViewHolder(view);
    }

    protected FollowResponse getItem(int position) {
        return displayItems.get(position);
    }

    public final void filter(String query) {
        this.query = query;
        animateTo(getFilteredList(query));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setUsers(getItem(position));
    }

    @Override
    public int getItemCount() {
        return displayItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView NameTV;

        ImageView ImageIV;
        ImageView ImageIVText;
        FollowResponse response;

        public ViewHolder(final View view) {
            super(view);

            NameTV = view.findViewById(R.id.nameTV);

            ImageIV = view.findViewById(R.id.imageIV);
            ImageIVText = view.findViewById(R.id.imageIVText);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {/*
                    Fragment fragment = ((PostActivity) activity).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (fragment instanceof NewPostFragment)*/
                }//                        ((NewPostFragment) fragment).ChangeString(query, displayItems.get(getAdapterPosition()));

            });
        }

        public void setUsers(FollowResponse response) {
            this.response = response;
            response.getViewable_user().setName(Helper.CapitalizeText(response.getViewable_user().getName()));

            if (query != null && query.trim().length() > 0) {
                Spannable wordtoSpan = new SpannableString(response.getViewable_user().getName());
                int spanStartIndex = response.getViewable_user().getName().toLowerCase().indexOf(query.toLowerCase());
                if (spanStartIndex >= 0) {
                    wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), spanStartIndex, spanStartIndex + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    wordtoSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), spanStartIndex, spanStartIndex + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                NameTV.setText(wordtoSpan);
            } else {
                NameTV.setText(response.getViewable_user().getName());
            }

            if (!TextUtils.isEmpty(response.getViewable_user().getProfile_picture())) {
                ImageIV.setVisibility(View.VISIBLE);
                ImageIVText.setVisibility(View.GONE);
                Glide.with(activity).load(response.getViewable_user().getProfile_picture()).into(ImageIV);

            } else {
                ImageIV.setVisibility(View.GONE);
                ImageIVText.setVisibility(View.VISIBLE);
                ImageIVText.setImageDrawable(Helper.GetDrawable(response.getViewable_user().getName(), context, response.getId()));
            }
        }
    }
}
