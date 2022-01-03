package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.Log;
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

/**
 * Created by appsquadz on 28/9/17.
 */

public class AllCourseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Course> coursesArrayList;
    String type;
    View.OnClickListener onClickListener = (View v) -> {
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

    public AllCourseListAdapter(Context context, List<Course> coursesDataArrayList, String type) {
        this.context = context;
        this.coursesArrayList = coursesDataArrayList;
        this.type = type;
        Log.e("AllCourseListAdapter", "AllCourseListAdapter: type = " + type);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_course_ver, parent, false);
            return new CourseHolder(view);
        } else if (viewType == 2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_course_hor, parent, false);
            return new CourseHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (type.equals(Const.COURSE_CATEGORY))
            return 1;
        else if (type.equals(Const.MYCOURSES) || type.equals(Const.SEEALL_COURSE) || type.equals(Const.SEEALL_INSTRUCTOR_COURSE))
            return 2;
        else return 0;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final CourseHolder holder1 = (CourseHolder) holder;
        holder1.courseTV.setText(coursesArrayList.get(position).getTitle());
        Course course = coursesArrayList.get(position);

        if (getItemViewType(position) == 1) {
            if (!coursesArrayList.get(position).getCalMrp().equalsIgnoreCase(context.getResources().getString(R.string.free))) {
                setSpannable(holder1, position);
            } else {
                holder1.priceTV.setText(coursesArrayList.get(position).getCalMrp());
            }
        } else if (getItemViewType(position) == 2) {
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
        }

        if (!type.equalsIgnoreCase(Const.MYCOURSES)) {
            if (type.equalsIgnoreCase(Const.SEEALL_INSTRUCTOR_COURSE)) {
                holder1.btnEnroll.setVisibility(View.GONE);
            } else {
                if (course.isIs_renew() == "1") {
                    Helper.setCourseButtonText(context, holder1.btnEnroll, Const.RENEW);
                } else if (course.isIs_purchased()) {
                    Helper.setCourseButtonText(context, holder1.btnEnroll, Const.PURCHASED);
                } else {
                    if (course.getMrp().equals("0"))
                        Helper.setCourseButtonText(context, holder1.btnEnroll, Const.FREE);
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                            if (course.getFor_dams().equals("0"))
                                Helper.setCourseButtonText(context, holder1.btnEnroll, Const.FREE);
                            else
                                Helper.setCourseButtonText(context, holder1.btnEnroll, Const.PAID);
                        } else {
                            if (course.getNon_dams().equals("0"))
                                Helper.setCourseButtonText(context, holder1.btnEnroll, Const.FREE);
                            else
                                Helper.setCourseButtonText(context, holder1.btnEnroll, Const.PAID);
                        }
                    }
                }

                holder1.btnEnroll.setOnClickListener((View view) -> {

                    if (coursesArrayList.get(position).isIs_renew() == "1" ||
                            !coursesArrayList.get(position).isIs_purchased() && !coursesArrayList.get(position).getMrp().equals("0")) {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
//                            if (!coursesArrayList.get(position).getFor_dams().equals("0"))
                                Helper.goToCourseInvoiceScreen(context, Helper.getData(course));
                        } else {
//                            if (!coursesArrayList.get(position).getNon_dams().equals("0"))
                                Helper.goToCourseInvoiceScreen(context, Helper.getData(course));
                        }
                    }

                });
            }
        } else {
            if (course.isIs_renew() == "1") {
                Helper.setCourseButtonText(context, holder1.btnEnroll, Const.RENEW);
            } else
                Helper.setCourseButtonText(context, holder1.btnEnroll, Const.PURCHASED);
        }
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
        holder1.parentLL.setOnClickListener(onClickListener);
    }

    private void setSpannable(CourseHolder holder1, int position) {
        if (coursesArrayList.get(position).isDiscounted()) {
            holder1.priceTV.setText(coursesArrayList.get(position).getCalMrp(), TextView.BufferType.SPANNABLE);
            Spannable spannable = (Spannable) holder1.priceTV.getText();
            spannable.setSpan(new StrikethroughSpan(), 2, Helper.calculatePriceBasedOnCurrency(coursesArrayList.get(position).getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            holder1.priceTV.setText(coursesArrayList.get(position).getCalMrp());
        }
    }

    @Override
    public int getItemCount() {
        return coursesArrayList.size();
    }

    public static class CourseHolder extends RecyclerView.ViewHolder {
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

        public CourseHolder(View itemView) {
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

