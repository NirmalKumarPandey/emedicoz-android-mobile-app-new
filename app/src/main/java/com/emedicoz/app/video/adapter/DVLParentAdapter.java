package com.emedicoz.app.video.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Map;

public class DVLParentAdapter extends RecyclerView.Adapter<DVLParentAdapter.DVLParentViewHolder> {

    Activity activity;
    List<String> decks;
    Map<Integer, ArrayList<DVLTopic>> hashMap;
    DvlData dvlData;
    String type;

    public DVLParentAdapter(Activity activity, List<String> decks, Map<Integer, ArrayList<DVLTopic>> hashMap, DvlData dvlData, String type) {
        this.activity = activity;
        this.decks = decks;
        this.hashMap = hashMap;
        this.dvlData = dvlData;
        this.type = type;
    }

    @NonNull
    @Override
    public DVLParentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_row_dvl, viewGroup, false);
        return new DVLParentViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull DVLParentViewHolder holder, int i) {
        holder.titleTV.setText(decks.get(i));
        DVLChildAdapter adapter = new DVLChildAdapter(activity, hashMap.get(i), dvlData, type);
        holder.expListView.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return decks.size();
    }

    class DVLParentViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV;
        NonScrollRecyclerView expListView;
        LinearLayout mainCurriculumLL;

        public DVLParentViewHolder(@NonNull View view) {
            super(view);
            expListView = view.findViewById(R.id.curriculumExpListLL);
            titleTV = view.findViewById(R.id.curriculumTextTV);
            mainCurriculumLL = view.findViewById(R.id.mainCurriculumLL);
            expListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        }
    }
}
