package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.CourseCatTileData;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

import java.util.List;

public class TileAdapter extends RecyclerView.Adapter<TileAdapter.TileViewHolder> {

    Activity activity;
    List<CourseCatTileData> tileDataArrayList;

    public TileAdapter(Activity activity, List<CourseCatTileData> tileDataArrayList) {
        this.activity = activity;
        this.tileDataArrayList = tileDataArrayList;
    }

    @NonNull
    @Override
    public TileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_cat_tile_item, viewGroup, false);
        return new TileViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull TileViewHolder holder, int position) {

        final CourseCatTileData data = tileDataArrayList.get(position);
        holder.tileName.setText(data.getTitle());
        holder.tileCardView.setCardBackgroundColor(Color.parseColor(!TextUtils.isEmpty(data.getColorCode()) ? data.getColorCode() : "#ffffff"));
        if (!data.getImage().equals("")) {
            Glide.with(activity).load(data.getImage())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.test_series_btn).error(R.mipmap.test_series_btn))
                    .into(holder.tileIcon);
        } else {
            holder.tileIcon.setImageResource(R.mipmap.test_series_btn);
        }
        holder.tileCardView.setOnClickListener((View view) -> {
            if (data.getCategorytype().equals("0")) {

                try {
                    Intent courseList = new Intent(activity, CourseActivity.class); // FRAG_TYPE, Const.SINGLE_COURSE from CourseListAdapter
                    if (Helper.isStringValid(data.getCourseDetail().getCourseType()) && (data.getCourseDetail().getCourseType().equalsIgnoreCase("2")
                            || data.getCourseDetail().getCourseType().equalsIgnoreCase("3"))) {
                        if (data.getCourse().equalsIgnoreCase(Constants.Extras.QUESTION_BANK_COURSE_ID)) {
                            courseList.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE);
                        } else {
                            courseList.putExtra(Const.FRAG_TYPE, Const.TEST_COURSE);
                        }
                    } else {
                        if (data.getCourseDetail().getIsLive() != null
                                && data.getCourseDetail().getIsLive().equalsIgnoreCase("1")) {
                            SharedPreference.getInstance().putString(Constants.Extras.ID, data.getCourse());
                            courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_STUDY);
                            courseList.putExtra(Const.IMAGE, data.getImage());
                        } else {
                            courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_STUDY);
                        }
                    }
                    courseList.putExtra(Const.COURSES, getCourseData(data));
                    activity.startActivity(courseList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Intent courseList = new Intent(activity, CourseActivity.class);//FRAG_TYPE, Const.SEEALL_COURSE AllCoursesAdapter
                courseList.putExtra(Const.FRAG_TYPE, Const.SEEALL_COURSE);
                courseList.putExtra(Const.CATEGORY_ID, data.getAppcategory());
                courseList.putExtra(Const.TITLE, data.getTitle());
                activity.startActivity(courseList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tileDataArrayList.size();
    }

    public Course getCourseData(CourseCatTileData data) {
        Course course = new Course();
        course.setId(data.getCourse());
        course.setIs_combo("0");
        course.setCourse_type(data.getCourseDetail().getCourseType());
        course.setTitle(data.getTitle());
        return course;
    }

    class TileViewHolder extends RecyclerView.ViewHolder {

        ImageView tileIcon;
        TextView tileName;
        CardView tileCardView;

        public TileViewHolder(@NonNull View itemView) {
            super(itemView);


            tileIcon = itemView.findViewById(R.id.tileIcon);
            tileName = itemView.findViewById(R.id.tileName);
            tileCardView = itemView.findViewById(R.id.tileCardView);
        }
    }
}
