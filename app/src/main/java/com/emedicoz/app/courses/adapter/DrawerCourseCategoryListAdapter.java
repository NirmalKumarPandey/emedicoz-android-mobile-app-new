package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

import java.util.ArrayList;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;

public class DrawerCourseCategoryListAdapter extends RecyclerView.Adapter<DrawerCourseCategoryListAdapter.CourseListViewHolder> {

    Context context;
    String app_view_type;
    ArrayList<Course> course_list;

    public DrawerCourseCategoryListAdapter(Context context, String app_view_type, ArrayList<Course> course_list) {
        this.context = context;
        this.app_view_type = app_view_type;
        this.course_list = course_list;
    }

    @NonNull
    @Override
    public CourseListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        if (app_view_type.equals("1")) {
            view = LayoutInflater.from(context).inflate(R.layout.new_single_row_course_hor, viewGroup, false);
        } else if (app_view_type.equals("2")) {
            view = LayoutInflater.from(context).inflate(R.layout.new_single_row_course_ver, viewGroup, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.new_single_row_course_hor, viewGroup, false);
        }

        return new CourseListViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return course_list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull CourseListViewHolder courseListViewHolder, int i) {
        Course course = course_list.get(i);
        courseListViewHolder.courseTV.setText(course.getTitle());
        if (course.isIs_renew() == "1") {
            Helper.setCourseButtonText(context, courseListViewHolder.btnEnroll, Const.RENEW);
        } else if (course.isIs_purchased()) {
            Helper.setCourseButtonText(context, courseListViewHolder.btnEnroll, Const.PURCHASED);
        } else {
            if (course.getMrp().equals("0"))
                Helper.setCourseButtonText(context, courseListViewHolder.btnEnroll, Const.FREE);
            else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (course.getFor_dams().equals("0"))
                        Helper.setCourseButtonText(context, courseListViewHolder.btnEnroll, Const.FREE);
                    else
                        Helper.setCourseButtonText(context, courseListViewHolder.btnEnroll, Const.PAID);
                } else {
                    if (course.getNon_dams().equals("0"))
                        Helper.setCourseButtonText(context, courseListViewHolder.btnEnroll, Const.FREE);
                    else
                        Helper.setCourseButtonText(context, courseListViewHolder.btnEnroll, Const.PAID);
                }
            }
        }

        courseListViewHolder.btnEnroll.setOnClickListener((View view) -> {

            if (course.isIs_renew() == "1" || (!course.isIs_purchased() && !course.getMrp().equals("0"))) {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
//                    if (!course.getFor_dams().equals("0"))
                        Helper.goToCourseInvoiceScreen(context, Helper.getData(course));
                } else {
//                    if (!course.getNon_dams().equals("0"))
                        Helper.goToCourseInvoiceScreen(context, Helper.getData(course));
                }
            }

        });

        if (course.getMrp().equals("0")) {
            courseListViewHolder.priceTV.setText("Free");
        } else {
            if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                if (course.getFor_dams().equalsIgnoreCase("0")) {
                    courseListViewHolder.priceTV.setText("Free");
                } else {
                    if (course.getFor_dams().equals(course.getMrp())) {
                        courseListViewHolder.priceTV.setText(String.format("%s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(course.getMrp())));
                    } else {
                        StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();
                        courseListViewHolder.priceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(course.getMrp()) + " " +
                                Helper.calculatePriceBasedOnCurrency(course.getFor_dams()), TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) courseListViewHolder.priceTV.getText();
                        spannable.setSpan(STRIKE_THROUGH_SPAN, 2, Helper.calculatePriceBasedOnCurrency(course.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            } else {
                if (course.getNon_dams().equalsIgnoreCase("0")) {
                    courseListViewHolder.priceTV.setText("Free");
                } else {
                    if (course.getNon_dams().equals(course.getMrp())) {
                        courseListViewHolder.priceTV.setText(String.format("%s %s", getCurrencySymbol(),
                                Helper.calculatePriceBasedOnCurrency(course.getMrp())));
                    } else {
                        StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();
                        courseListViewHolder.priceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(course.getMrp()) + " " +
                                Helper.calculatePriceBasedOnCurrency(course.getNon_dams()), TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) courseListViewHolder.priceTV.getText();
                        spannable.setSpan(STRIKE_THROUGH_SPAN, 2, Helper.calculatePriceBasedOnCurrency(course.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }

            }
        }

        courseListViewHolder.learnerTV.setText((course.getLearner() + ((course.getLearner().equals("1") || (course.getLearner().equals("0")) ? Const.LEARNER : Const.LEARNERS))));
        courseListViewHolder.ratingTV.setText(course.getRating());
        courseListViewHolder.ratingRB.setRating(Float.parseFloat(course.getRating()));
        Glide.with(context)
                .asBitmap()
                .load(course.getCover_image())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.courses_blue))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                        courseListViewHolder.imageIV.setImageBitmap(result);
                    }
                });
        courseListViewHolder.itemView.setTag(course);
//        courseListViewHolder.itemView.setOnClickListener(onCourseClickListener);
        courseListViewHolder.itemView.setOnClickListener(v -> {
            Intent courseList = new Intent(context, CourseActivity.class);
            courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_STUDY);
            courseList.putExtra(Const.IMAGE, course.getDesc_header_image());
            courseList.putExtra(Const.COURSES, course);
            context.startActivity(courseList);
        });
    }

    class CourseListViewHolder extends RecyclerView.ViewHolder {

        TextView courseTV, priceTV, learnerTV, ratingTV;
        final ImageView imageIV;
        RatingBar ratingRB;
        Button btnEnroll;

        public CourseListViewHolder(@NonNull View itemView) {
            super(itemView);

            courseTV = itemView.findViewById(R.id.nameTV);
            priceTV = itemView.findViewById(R.id.priceTV);
            learnerTV = itemView.findViewById(R.id.learnerTV);
            ratingTV = itemView.findViewById(R.id.ratingTV);
            ratingRB = itemView.findViewById(R.id.ratingRB);
            imageIV = itemView.findViewById(R.id.imageIV);
            btnEnroll = itemView.findViewById(R.id.btnEnroll);
        }
    }
}
