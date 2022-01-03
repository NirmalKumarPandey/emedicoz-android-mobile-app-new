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
import android.widget.LinearLayout;
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
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

import java.util.List;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;

public class CourseSearchAdapter extends RecyclerView.Adapter<CourseSearchAdapter.SearchCourseViewHolder> {

    Context context;
    List<Course> coursesArrayList;
    View.OnClickListener itemclickListener = (View v) -> {
        try {
            Course course = (Course) v.getTag();
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

    public CourseSearchAdapter(Context context, List<Course> coursesArrayList) {
        this.context = context;
        this.coursesArrayList = coursesArrayList;
    }

    @NonNull
    @Override
    public SearchCourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_single_row_course_hor, viewGroup, false);
        return new SearchCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchCourseViewHolder holder1, int position) {
        holder1.courseTV.setText(coursesArrayList.get(position).getTitle());

        if (!TextUtils.isEmpty(coursesArrayList.get(position).getValidity()) && !coursesArrayList.get(position).getValidity().equals("0")) {
            holder1.validityTv.setVisibility(View.VISIBLE);
            holder1.validityTv.setText("Valid Till: " + coursesArrayList.get(position).getValidity());
        } else
            holder1.validityTv.setVisibility(View.GONE);

        if (coursesArrayList.get(position).getMrp().equals("0")) {
            holder1.priceTV.setText(context.getResources().getString(R.string.free));
        } else {
            if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                if (coursesArrayList.get(position).getFor_dams().equals("0")) {
                    holder1.priceTV.setText(context.getResources().getString(R.string.free));
                } else {
                    if (coursesArrayList.get(position).getFor_dams().equals(coursesArrayList.get(position).getMrp())) {
                        holder1.priceTV.setText(String.format("%s %s", getCurrencySymbol(),
                                Helper.calculatePriceBasedOnCurrency(coursesArrayList.get(position).getMrp())));
                    } else {
                        StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                        holder1.priceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(coursesArrayList.get(position).getMrp()) + " " +
                                Helper.calculatePriceBasedOnCurrency(coursesArrayList.get(position).getFor_dams()), TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) holder1.priceTV.getText();
                        spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(coursesArrayList.get(position).getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            } else {
                if (coursesArrayList.get(position).getNon_dams().equals("0")) {
                    holder1.priceTV.setText(context.getResources().getString(R.string.free));
                } else {
                    if (coursesArrayList.get(position).getNon_dams().equals(coursesArrayList.get(position).getMrp())) {
                        holder1.priceTV.setText(String.format("%s %s", getCurrencySymbol(),
                                Helper.calculatePriceBasedOnCurrency(coursesArrayList.get(position).getMrp())));
                    } else {
                        StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                        holder1.priceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(coursesArrayList.get(position).getMrp()) + " " +
                                Helper.calculatePriceBasedOnCurrency(coursesArrayList.get(position).getNon_dams()), TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) holder1.priceTV.getText();
                        spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(coursesArrayList.get(position).getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }
        if (coursesArrayList.get(position).isIs_renew() ==  "1") {
            Helper.setCourseButtonText(context, holder1.btnEnroll, Const.RENEW);
        } else if (coursesArrayList.get(position).isIs_purchased()) {
            Helper.setCourseButtonText(context, holder1.btnEnroll, Const.PURCHASED);
        } else {
            if (coursesArrayList.get(position).getMrp().equals("0"))
                Helper.setCourseButtonText(context, holder1.btnEnroll, Const.FREE);
            else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (coursesArrayList.get(position).getFor_dams().equals("0"))
                        Helper.setCourseButtonText(context, holder1.btnEnroll, Const.FREE);
                    else
                        Helper.setCourseButtonText(context, holder1.btnEnroll, Const.PAID);
                } else {
                    if (coursesArrayList.get(position).getNon_dams().equals("0"))
                        Helper.setCourseButtonText(context, holder1.btnEnroll, Const.FREE);
                    else
                        Helper.setCourseButtonText(context, holder1.btnEnroll, Const.PAID);
                }
            }
        }
        holder1.btnEnroll.setTag(coursesArrayList.get(position));
        holder1.btnEnroll.setOnClickListener((View view) -> {
            Course course = (Course) view.getTag();
            if (course.isIs_renew() == "1" ||
                    (!course.isIs_purchased() && !course.getMrp().equals("0"))) {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
//                    if (!course.getFor_dams().equals("0"))
                    Helper.goToCourseInvoiceScreen(context, Helper.getData(course));
                } else {
//                    if (!course.getNon_dams().equals("0"))
                    Helper.goToCourseInvoiceScreen(context, Helper.getData(course));
                }
            }

        });


        holder1.learnerTV.setText((coursesArrayList.get(position).getLearner() +
                (coursesArrayList.get(position).getLearner().equals("1") ? Const.LEARNER : Const.LEARNERS)));
        holder1.ratingTV.setText(coursesArrayList.get(position).getRating());
        holder1.ratingRB.setRating(Float.parseFloat(coursesArrayList.get(position).getRating()));

        Glide.with(context)
                .asBitmap()
                .load(coursesArrayList.get(position).getCover_image())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.courses_blue))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                        holder1.imageIV.setImageBitmap(result);
                        if (Helper.isStringValid(coursesArrayList.get(position).getIs_combo()) && coursesArrayList.get(position).getIs_combo().equalsIgnoreCase("1"))
                            holder1.courseTypeIV.setImageResource(R.drawable.combo);
                        else if (Helper.isStringValid(coursesArrayList.get(position).getCourse_type()) && coursesArrayList.get(position).getCourse_type().equalsIgnoreCase("2"))
                            holder1.courseTypeIV.setImageResource(R.drawable.test);
                        else if (Helper.isStringValid(coursesArrayList.get(position).getCourse_type()) && coursesArrayList.get(position).getCourse_type().equalsIgnoreCase("3"))
                            holder1.courseTypeIV.setImageResource(R.drawable.quiz);
                    }
                });

        holder1.parentLL.setTag(coursesArrayList.get(position));
        holder1.parentLL.setOnClickListener(itemclickListener);
       /* holder1.parentLL.setOnClickListener(v -> {
            Intent courseList = new Intent(context, CourseActivity.class);
            courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_STUDY);
            courseList.putExtra(Const.IMAGE, coursesArrayList.get(position).getDesc_header_image());
            courseList.putExtra(Const.COURSES, coursesArrayList.get(position));
            context.startActivity(courseList);
        });*/
    }

    @Override
    public int getItemCount() {
        return coursesArrayList.size();
    }


    class SearchCourseViewHolder extends RecyclerView.ViewHolder {

        TextView courseTV;
        TextView priceTV;
        TextView learnerTV;
        TextView ratingTV;
        TextView validityTv;
        ImageView imageIV;
        RatingBar ratingRB;
        LinearLayout parentLL;
        Button btnEnroll;
        private ImageView courseTypeIV;

        public SearchCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            btnEnroll = itemView.findViewById(R.id.btnEnroll);
            courseTV = itemView.findViewById(R.id.nameTV);
            priceTV = itemView.findViewById(R.id.priceTV);
            learnerTV = itemView.findViewById(R.id.learnerTV);
            ratingTV = itemView.findViewById(R.id.ratingTV);
            ratingRB = itemView.findViewById(R.id.ratingRB);
            imageIV = itemView.findViewById(R.id.imageIV);
            parentLL = itemView.findViewById(R.id.parentLL);
            validityTv = itemView.findViewById(R.id.validityTv);
            courseTypeIV = itemView.findViewById(R.id.courseTypeIV);
        }
    }
}
