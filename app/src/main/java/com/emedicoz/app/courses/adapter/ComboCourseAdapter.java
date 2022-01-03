package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.Topics;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

/**
 * Created by mridul on 28/9/17.
 */

public class ComboCourseAdapter extends RecyclerView.Adapter<ComboCourseAdapter.CourseHolder> {

    SingleCourseData singleCourseData;
    private Context context;
    private Topics[] topics;

    public ComboCourseAdapter(Context context, String courseId, Topics[] topics) {
        this.context = context;
        this.topics = topics;
    }

    public ComboCourseAdapter(Context context, String courseId, Topics[] topics, SingleCourseData singleCourseData) {
        this.context = context;
        this.topics = topics;
        this.singleCourseData = singleCourseData;
    }

    @Override
    public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_course_hor, parent, false);
        return new CourseHolder(view);
    }

    @Override
    public void onBindViewHolder(final CourseHolder holder, final int position) {

        holder.setData(topics[position]);
    }

    @Override
    public int getItemCount() {
        return topics.length;
    }

    public class CourseHolder extends RecyclerView.ViewHolder {
        TextView courseTV;
        TextView priceTV;
        TextView learnerTV;
        TextView ratingTV;
        TextView validityTv;
        ImageView imageIV;
        RatingBar ratingRB;
        Button btnEnroll;
        LinearLayout parentLL;
        private ImageView courseTypeIV;

        public CourseHolder(View itemView) {
            super(itemView);

            courseTV = itemView.findViewById(R.id.nameTV);
            priceTV = itemView.findViewById(R.id.priceTV);
            learnerTV = itemView.findViewById(R.id.learnerTV);
            ratingTV = itemView.findViewById(R.id.ratingTV);
            ratingRB = itemView.findViewById(R.id.ratingRB);
            imageIV = itemView.findViewById(R.id.imageIV);
            parentLL = itemView.findViewById(R.id.parentLL);
            validityTv = itemView.findViewById(R.id.validityTv);
            courseTypeIV = itemView.findViewById(R.id.courseTypeIV);
            btnEnroll = itemView.findViewById(R.id.btnEnroll);
            btnEnroll.setVisibility(View.GONE);

            courseTypeIV.setVisibility(View.GONE);
            priceTV.setVisibility(View.GONE);
            ratingTV.setVisibility(View.GONE);
            ratingRB.setVisibility(View.GONE);
            learnerTV.setVisibility(View.GONE);
            if (singleCourseData.isIs_renew()) {
                Helper.setCourseButtonText(context, btnEnroll, Const.RENEW);
            } else if (singleCourseData.getIs_purchased().equals("1")) {
                Helper.setCourseButtonText(context, btnEnroll, Const.PURCHASED);
            } else {
                if (singleCourseData.getMrp().equals("0"))
                    Helper.setCourseButtonText(context, btnEnroll, Const.FREE);
                else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (singleCourseData.getFor_dams().equals("0"))
                            Helper.setCourseButtonText(context, btnEnroll, Const.FREE);
                        else
                            Helper.setCourseButtonText(context, btnEnroll, Const.PAID);
                    } else {
                        if (singleCourseData.getNon_dams().equals("0"))
                            Helper.setCourseButtonText(context, btnEnroll, Const.FREE);
                        else
                            Helper.setCourseButtonText(context, btnEnroll, Const.PAID);
                    }
                }
            }

            btnEnroll.setOnClickListener((View view) -> {

                if (singleCourseData.isIs_renew() ||
                        (singleCourseData.getIs_purchased().equals("0") && !singleCourseData.getMrp().equals("0"))) {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
//                        if (!singleCourseData.getFor_dams().equals("0"))
                            Helper.goToCourseInvoiceScreen(context, singleCourseData);
                    } else {
//                        if (!singleCourseData.getNon_dams().equals("0"))
                            Helper.goToCourseInvoiceScreen(context, singleCourseData);
                    }
                }

            });


        }

        View.OnClickListener onClickListener = (View v) -> {
            try {
                SingleCourseData course = (SingleCourseData) v.getTag();
                Intent courseList = new Intent(context, CourseActivity.class); // FRAG_TYPE, Const.SINGLE_COURSE from CourseListAdapter
                if (course.getCourse_type().equals("1")) {
                    if (!GenericUtils.isEmpty(course.getIs_live())) {
                        if (course.getIs_live().equalsIgnoreCase("1")) {
                            SharedPreference.getInstance().putString(Constants.Extras.ID, course.getId());
                            courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_STUDY);
                            courseList.putExtra(Const.IMAGE, course.getDesc_header_image());
                        } else {
                            if (Helper.isStringValid(course.getIs_combo()) && course.getIs_combo().equalsIgnoreCase("1"))
                                courseList.putExtra(Const.FRAG_TYPE, Const.COMBO_COURSE);
                            else
                                courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
                        }
                    } else {
                        if (Helper.isStringValid(course.getIs_combo()) && course.getIs_combo().equalsIgnoreCase("1"))
                            courseList.putExtra(Const.FRAG_TYPE, Const.COMBO_COURSE);
                        else
                            courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
                    }
                } else if (Helper.isStringValid(course.getCourse_type()) && (course.getCourse_type().equalsIgnoreCase("2")
                        || course.getCourse_type().equalsIgnoreCase("3"))) {
                    if (course.getId().equalsIgnoreCase(Constants.Extras.QUESTION_BANK_COURSE_ID)) {
                        courseList.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE);
                    } else {
                        courseList.putExtra(Const.FRAG_TYPE, Const.TEST_COURSE);
                    }
                }
                courseList.putExtra(Const.COURSES, course);
                context.startActivity(courseList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        private void setData(final Topics course) {
            courseTV.setText(course.getTitle());
            if (!TextUtils.isEmpty(course.getValidity()) && !course.getValidity().equals("0")) {
                validityTv.setVisibility(View.VISIBLE);
                validityTv.setText(String.format("Valid Till: %s", course.getValidity()));
            } else
                validityTv.setVisibility(View.GONE);

            Glide.with(context)
                    .load(course.getCover_image())
                    .into(imageIV);

            parentLL.setTag(singleCourseData);
            parentLL.setOnClickListener(onClickListener);

//            parentLL.setOnClickListener((View view) -> {
//                Intent courseList = new Intent(context, CourseActivity.class); // FRAG_TYPE, Const.SINGLE_COURSE from CourseListAdapter
//                courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
//                courseList.putExtra(Const.PARENT_ID, courseId);
//                courseList.putExtra(Const.COURSES, setTopicToCourse(course));
//                courseList.putExtra("hidekey", "hide");
//                context.startActivity(courseList);
//            });
        }

        private Course setTopicToCourse(Topics topics) {
            Course course = new Course();
            course.setId(topics.getTopic_id());
            course.setCourse_type(singleCourseData.getCourse_type());
            course.setCover_image(singleCourseData.getCover_image());
            course.setDesc_header_image(singleCourseData.getDesc_header_image());
            course.setIs_combo(singleCourseData.getIs_combo());
            course.setTitle(topics.getTitle());
            course.setValidity(singleCourseData.getValidity());
            return course;
        }
    }


}
