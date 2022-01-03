package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.SingleChildViewCurriculum;
import com.emedicoz.app.modelo.courses.Curriculam;
import com.emedicoz.app.modelo.courses.SingleCourseData;

import java.util.ArrayList;

public class CurriculumRecyclerAdapter extends RecyclerView.Adapter<CurriculumRecyclerAdapter.ViewHolder1> {

    SingleCourseData singleCourseData;
    SingleChildViewCurriculum singleChildViewCurriculum;
    private Context context;
    private String topic_id = "";
    private int chapterPosition;
    private boolean lockedStatus;
    private ArrayList<Curriculam.File_meta> fileMetaTypeArrayList;
    private ArrayList<Curriculam> curriculamArrayList;
    private CurriculumFileRecyclerAdapter curriculumFileRecyclerAdapter;

    public CurriculumRecyclerAdapter(SingleCourseData singleCourseData, String topic_id, Context context, ArrayList<Curriculam.File_meta> fileMetaTypeArrayList) {
        this.singleCourseData = singleCourseData;
        this.context = context;
        this.topic_id = topic_id;
        this.fileMetaTypeArrayList = fileMetaTypeArrayList;
    }

    public CurriculumRecyclerAdapter(SingleCourseData singleCourseData, Activity activity, ArrayList<Curriculam> fileMetaArrayList, boolean lockedStatus) {
        this.chapterPosition = 0;
        this.singleCourseData = singleCourseData;
        this.context = activity;
        this.curriculamArrayList = fileMetaArrayList;
        this.lockedStatus = lockedStatus;
    }

    public CurriculumRecyclerAdapter(SingleCourseData singleCourseData, CurriculumFileRecyclerAdapter curriculumFileRecyclerAdapter, ArrayList<Curriculam> curriculamArrayList, Context context, int position, boolean lockedStatus) {
        this.chapterPosition = position;
        this.curriculamArrayList = curriculamArrayList;
        this.curriculumFileRecyclerAdapter = curriculumFileRecyclerAdapter;
        this.singleCourseData = singleCourseData;
        this.context = context;
        this.lockedStatus = lockedStatus;
    }

    @Override
    public ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_curriculum_sub_item_new, parent, false);
        return new ViewHolder1(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder1 holder, int position) {
        if (curriculamArrayList != null)
            fileMetaTypeArrayList = curriculamArrayList.get(chapterPosition).getFile_meta();
        if (fileMetaTypeArrayList.size() - 1 != position) {
            holder.singleChildViewCurriculum.setChildData(fileMetaTypeArrayList.get(position), chapterPosition,
                    position, position == fileMetaTypeArrayList.size() - 1,
                    lockedStatus);
        } else {
            holder.singleChildViewCurriculum.setChildData(fileMetaTypeArrayList.get(position), chapterPosition, position, position == fileMetaTypeArrayList.size() - 1, lockedStatus);
        }

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

    // SingleChildView for single feed
    public class ViewHolder1 extends RecyclerView.ViewHolder {

        SingleChildViewCurriculum singleChildViewCurriculum;

        public ViewHolder1(final View view) {
            super(view);
            singleChildViewCurriculum = new SingleChildViewCurriculum((Activity) context, CurriculumRecyclerAdapter.this, singleCourseData, curriculamArrayList);
            singleChildViewCurriculum.initViews(view);

//            singleChildViewCurriculum.setChildData(childText);
        }
    }
}
