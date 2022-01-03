package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.SingleChildViewCurriculum;
import com.emedicoz.app.modelo.courses.Curriculam;
import com.emedicoz.app.modelo.courses.SingleCourseData;

import java.util.ArrayList;

public class NewCurriculumRecyclerAdapter extends RecyclerView.Adapter<NewCurriculumRecyclerAdapter.NewCurriculumViewHolder> {
    SingleCourseData singleCourseData;
    SingleChildViewCurriculum singleChildViewCurriculum;
    private Context context;
    private String topic_id = "";
    private int chapterPosition;
    private boolean lockedStatus;
    private ArrayList<Curriculam.File_meta> fileMetaTypeArrayList;
    private ArrayList<Curriculam> curriculamArrayList;
    private NewCurriculumFileRecyclerAdapter curriculumFileRecyclerAdapter;

    public NewCurriculumRecyclerAdapter(SingleCourseData singleCourseData, NewCurriculumFileRecyclerAdapter curriculumFileRecyclerAdapter, ArrayList<Curriculam> curriculamArrayList, Context context, int position, boolean lockedStatus) {
        this.chapterPosition = position;
        this.curriculamArrayList = curriculamArrayList;
        this.curriculumFileRecyclerAdapter = curriculumFileRecyclerAdapter;
        this.singleCourseData = singleCourseData;
        this.context = context;
        this.lockedStatus = lockedStatus;
    }

    @Override
    public NewCurriculumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_curriculum_sub_item_new, parent, false);
        return new NewCurriculumViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewCurriculumViewHolder holder, int position) {

        holder.setData(curriculamArrayList.get(position), position);
    }

    @Override
    public int getItemCount() {
        /*if (curriculamArrayList != null) {
            return curriculamArrayList.get(chapterPosition).getFile_meta().size();
        } else {
            return fileMetaTypeArrayList.size();
        }*/
        return curriculamArrayList.get(chapterPosition).getFile_meta().size();
    }

    public void onUpdateStatus(int chapterPosition) {
        curriculumFileRecyclerAdapter.notifyItemChanged(chapterPosition);
        Log.e("position Called", String.valueOf(chapterPosition));
    }

    public class NewCurriculumViewHolder extends RecyclerView.ViewHolder {

        ImageView itemTypeIV, downloadIV, deleteIV, seeResultIV;
        TextView fileTypeTV;

        public NewCurriculumViewHolder(final View view) {
            super(view);

            itemTypeIV = view.findViewById(R.id.itemTypeimageIV);
            fileTypeTV = view.findViewById(R.id.fileTypeTV);
        }

        public void setData(Curriculam curriculam, int position) {
            fileTypeTV.setText(curriculam.getFile_meta().get(position - 1).getTitle());
        }
    }
}
