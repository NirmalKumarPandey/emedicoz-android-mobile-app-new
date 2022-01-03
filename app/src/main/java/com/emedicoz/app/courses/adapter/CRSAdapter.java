package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.modelo.dvl.DVLTopic;
import com.emedicoz.app.modelo.dvl.DvlData;

import java.util.ArrayList;
import java.util.HashMap;

public class CRSAdapter extends RecyclerView.Adapter<CRSAdapter.CRSViewHolder> {
    Activity activity;
    ArrayList<String> decks;
    HashMap<Integer, ArrayList<DVLTopic>> hashMap;
    DvlData crsData;

    public CRSAdapter(Activity activity, ArrayList<String> decks, HashMap<Integer, ArrayList<DVLTopic>> hashMap, DvlData crsData) {
        this.activity = activity;
        this.decks = decks;
        this.hashMap = hashMap;
        this.crsData = crsData;
    }

    @NonNull
    @Override
    public CRSViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_crs_parent, viewGroup, false);
        return new CRSViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final CRSViewHolder holder, int i) {
        holder.categoryName.setText(decks.get(i));
        CRSChildAdapter adapter = new CRSChildAdapter(activity, hashMap.get(i), crsData);
        holder.expListView.setAdapter(adapter);
        holder.parentLL.setOnClickListener(v -> {
            if (holder.expListView.getVisibility() == View.GONE) {
                holder.expListView.setVisibility(View.VISIBLE);
                holder.imgArrow.setImageResource(R.mipmap.ic_crs_up_arrow);
            } else {
                holder.expListView.setVisibility(View.GONE);
                holder.imgArrow.setImageResource(R.mipmap.ic_crs_down_arrow);
            }
        });
    }

    @Override
    public int getItemCount() {
        return decks.size();
    }

    class CRSViewHolder extends RecyclerView.ViewHolder {

        TextView categoryName;
        NonScrollRecyclerView expListView;
        LinearLayout parentLL;
        ImageView imgArrow;

        public CRSViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            expListView = itemView.findViewById(R.id.curriculumExpListLL);
            parentLL = itemView.findViewById(R.id.parentLL);
            imgArrow = itemView.findViewById(R.id.imgArrow);
            expListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        }
    }
}
