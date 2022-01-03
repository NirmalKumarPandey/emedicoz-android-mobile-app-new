package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.courses.Curriculam;

import java.util.ArrayList;

public class DetailComboCourseAdapterTwo extends RecyclerView.Adapter<DetailComboCourseAdapterTwo.DetailComboCourseViewHolderTwo> {

    Activity activity;
    ArrayList<Curriculam.File_meta> file_meta;

    public DetailComboCourseAdapterTwo(Activity activity, ArrayList<Curriculam.File_meta> file_meta) {
        this.activity = activity;
        this.file_meta = file_meta;
    }

    @NonNull
    @Override
    public DetailComboCourseViewHolderTwo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_detail_two, viewGroup, false);
        return new DetailComboCourseViewHolderTwo(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailComboCourseViewHolderTwo holder, int i) {

        holder.detailComboTV.setText(file_meta.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return file_meta.size();
    }

    public class DetailComboCourseViewHolderTwo extends RecyclerView.ViewHolder {
        TextView detailComboTV;

        public DetailComboCourseViewHolderTwo(@NonNull View itemView) {
            super(itemView);
            detailComboTV = itemView.findViewById(R.id.detailComboTV);
        }
    }
}
