package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.LiveCourseActivity;
import com.emedicoz.app.modelo.courses.Bookmark;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Bookmark> data;
    Activity activity;

    public BookmarkAdapter(Activity activity, List<Bookmark> data) {
        this.data = data;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_index_row, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        holder.timeTV.setText(data.get(position).getTime());
        holder.tittleTV.setText(data.get(position).getInfo());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder /*implements SingleFeedView.DeletePostCallback*/ {

        TextView timeTV;
        TextView tittleTV;
        ImageView deleteIV;

        public MyViewHolder(View itemView) {
            super(itemView);
            deleteIV = itemView.findViewById(R.id.del_iv);
            timeTV = itemView.findViewById(R.id.feed_time_tv);
            tittleTV = itemView.findViewById(R.id.feed_tittle);

            deleteIV.setOnClickListener(view -> ((LiveCourseActivity) activity).onDelete(data.get(getAdapterPosition())));

            itemView.setOnClickListener(view -> ((LiveCourseActivity) activity).onSeek(data.get(getAdapterPosition())));

        }
    }


}

