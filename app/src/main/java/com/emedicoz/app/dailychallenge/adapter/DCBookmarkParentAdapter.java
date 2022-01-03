package com.emedicoz.app.dailychallenge.adapter;

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
import com.emedicoz.app.dailychallenge.model.DCBookmarkData;

import java.util.List;

public class DCBookmarkParentAdapter extends RecyclerView.Adapter<DCBookmarkParentAdapter.DCParentViewHolder> {
    Activity activity;
    List<DCBookmarkData> dcBookmarkDataList;

    public DCBookmarkParentAdapter(Activity activity, List<DCBookmarkData> dcBookmarkDataList) {
        this.activity = activity;
        this.dcBookmarkDataList = dcBookmarkDataList;
    }

    @NonNull
    @Override
    public DCParentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_row_dc_bookmark_parent, viewGroup, false);
        return new DCParentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DCParentViewHolder dcParentViewHolder, int i) {
        dcParentViewHolder.curriculumTextTV.setText(dcBookmarkDataList.get(i).getTitle());
        DCBookmarkChildAdapter adapter = new DCBookmarkChildAdapter(activity, dcBookmarkDataList.get(i).getSubject());
        dcParentViewHolder.curriculumExpListLL.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return dcBookmarkDataList == null ? 0 : dcBookmarkDataList.size();
    }

    public class DCParentViewHolder extends RecyclerView.ViewHolder {
        TextView curriculumTextTV;
        LinearLayout mainCurriculumLL;
        NonScrollRecyclerView curriculumExpListLL;

        public DCParentViewHolder(@NonNull View itemView) {
            super(itemView);
            curriculumTextTV = itemView.findViewById(R.id.curriculumTextTV);
            mainCurriculumLL = itemView.findViewById(R.id.mainCurriculumLL);
            curriculumExpListLL = itemView.findViewById(R.id.curriculumExpListLL);
            curriculumExpListLL.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        }
    }
}
