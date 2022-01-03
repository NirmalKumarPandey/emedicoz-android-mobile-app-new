package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.modelo.courses.CourseCategory;
import com.emedicoz.app.utilso.Const;

import java.util.List;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.CatViewHolder> {

    Activity activity;
    List<CourseCategory> tileDataArrayList;

    public CatAdapter(Activity activity, List<CourseCategory> tileDataArrayList) {
        this.activity = activity;
        this.tileDataArrayList = tileDataArrayList;
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_category, viewGroup, false);
        return new CatViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, final int position) {
        holder.tileName.setText(tileDataArrayList.get(position).getName());
        holder.parentLL.setOnClickListener((View view) -> {
            Intent courseList = new Intent(activity, CourseActivity.class);//FRAG_TYPE, Const.SEEALL_COURSE AllCoursesAdapter
            courseList.putExtra(Const.FRAG_TYPE, Const.SEEALL_COURSE);
            courseList.putExtra(Const.COURSE_CATEGORY, tileDataArrayList.get(position));
            activity.startActivity(courseList);
        });

    }

    @Override
    public int getItemCount() {
        return tileDataArrayList.size();
    }

    class CatViewHolder extends RecyclerView.ViewHolder {

        TextView tileName;
        RelativeLayout parentLL;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);

            tileName = itemView.findViewById(R.id.categoryName);
            parentLL = itemView.findViewById(R.id.parentLL);
        }
    }

}
