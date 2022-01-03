package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.courses.Review;

import java.util.ArrayList;

/**
 * Created by Cbc-03 on 01/06/18.
 */

public class TrendingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private ArrayList<Review> trendingArrayList;

    public TrendingAdapter(ArrayList<Review> ListTitle, Context context) {
        this.trendingArrayList = ListTitle;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_tag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if (position == 0) {
            viewHolder.listTV.setText("Trending");
        } else {
            viewHolder.listTV.setText(trendingArrayList.get(position).getText());
        }
    }

    @Override
    public int getItemCount() {
        return trendingArrayList.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView listTV;
        ImageView icon;

        public ViewHolder(final View view) {
            super(view);
            listTV = view.findViewById(R.id.listTitle);
            icon = view.findViewById(R.id.icon);

            view.setOnClickListener(view1 -> {
                //onclick handle
            });
        }
    }
}

