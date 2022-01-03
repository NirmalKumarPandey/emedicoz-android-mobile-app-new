package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.Total;

import java.util.ArrayList;

public class SubItemStudyAdapter extends RecyclerView.Adapter<SubItemStudyAdapter.SubItemHolder> {
    Activity context;
    ArrayList<Total> listsArrayList;

    public SubItemStudyAdapter(Activity activity, ArrayList<Total> list) {
        this.context = activity;
        this.listsArrayList = list;
    }

    @NonNull
    @Override
    public SubItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exam_prep_subitem, null);
        return new SubItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubItemHolder holder, int position) {
        holder.subItemTV.setText(listsArrayList.get(position).getCount());

    }

    @Override
    public int getItemCount() {
        return listsArrayList.size();
    }

    public class SubItemHolder extends RecyclerView.ViewHolder {
        TextView subItemTV;

        public SubItemHolder(View itemView) {
            super(itemView);

            subItemTV = itemView.findViewById(R.id.row1_1TV);
        }
    }
}

