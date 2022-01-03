package com.emedicoz.app.dailychallenge.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.bookmark.NewBookMarkActivity;
import com.emedicoz.app.dailychallenge.model.SubjectBookmark;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;

import java.util.List;


public class DCBookmarkChildAdapter extends RecyclerView.Adapter<DCBookmarkChildAdapter.DCChildViewHolder> {
    Activity activity;
    List<SubjectBookmark> subjectBookmarkList;

    public DCBookmarkChildAdapter(Activity activity, List<SubjectBookmark> subjectBookmarkList) {
        this.activity = activity;
        this.subjectBookmarkList = subjectBookmarkList;
    }

    @NonNull
    @Override
    public DCChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_row_dc_bookmark_child, viewGroup, false);
        return new DCChildViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DCChildViewHolder dcChildViewHolder, final int i) {

        dcChildViewHolder.childBookmarkTitle.setText(subjectBookmarkList.get(i).getSubjectName());
        dcChildViewHolder.childTotalBookmarkCount.setText(String.format("(%d)", subjectBookmarkList.get(i).getQuestions().size()));

        dcChildViewHolder.cardView.setOnClickListener((View view) -> {
            if (!subjectBookmarkList.get(i).getSubjectId().isEmpty()) {
                Intent intent = new Intent(activity, NewBookMarkActivity.class);
                intent.putExtra(Constants.Extras.ID, subjectBookmarkList.get(i).getSubjectId());
                intent.putExtra(Constants.Extras.NAME, subjectBookmarkList.get(i).getSubjectName());
                intent.putExtra(Constants.Extras.TYPE, "Bookmark");
                intent.putExtra(Constants.Extras.NAME_OF_TAB, Constants.TestType.DAILY_CHALLENGE);
                intent.putExtra(Const.TESTSERIES_ID, "");
                intent.putExtra(Constants.Extras.Q_TYPE_DQB, "");
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, "No Bookmarks Found !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectBookmarkList == null ? 0 : subjectBookmarkList.size();
    }

    public static class DCChildViewHolder extends RecyclerView.ViewHolder {
        TextView childBookmarkTitle;
        TextView childTotalBookmarkCount;
        ImageView frontRightIV;
        CardView cardView;

        public DCChildViewHolder(@NonNull View itemView) {
            super(itemView);
            childBookmarkTitle = itemView.findViewById(R.id.title);
            frontRightIV = itemView.findViewById(R.id.frontRightIV);
            childTotalBookmarkCount = itemView.findViewById(R.id.completedCountTV);
            cardView = itemView.findViewById(R.id.parentCV);
        }
    }
}
