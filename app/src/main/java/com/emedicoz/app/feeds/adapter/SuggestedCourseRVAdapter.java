package com.emedicoz.app.feeds.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class SuggestedCourseRVAdapter extends RecyclerView.Adapter<SuggestedCourseRVAdapter.ViewHolder> {

    Activity activity;
    List<Course> courseArrayList;

    public SuggestedCourseRVAdapter(Activity activity, List<Course> courseArrayList) {
        this.activity = activity;
        this.courseArrayList = courseArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_suggested_video_feeds, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setSuggestedCourseData(courseArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return courseArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV;
        TextView descriptionTV;
        TextView priceTV;
        ImageView videoImage;
        ImageView playIconIV;
        View myView;

        public ViewHolder(final View view) {
            super(view);
            titleTV = view.findViewById(R.id.titleTV);
            descriptionTV = view.findViewById(R.id.descriptionTV);
            priceTV = view.findViewById(R.id.priceTV);
            videoImage = view.findViewById(R.id.videoImage);
            playIconIV = view.findViewById(R.id.playIconIV);
            playIconIV.setVisibility(View.GONE);
            priceTV.setVisibility(View.VISIBLE);
            myView = view;
        }


        View.OnClickListener onClickListener = (View v) -> {
            try {
                Course course = (Course) v.getTag();
                Intent courseList = new Intent(activity, CourseActivity.class); // FRAG_TYPE, Const.SINGLE_COURSE from CourseListAdapter
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
                activity.startActivity(courseList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        public void setSuggestedCourseData(final Course data) {
            descriptionTV.setText(HtmlCompat.fromHtml(data.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            titleTV.setText(data.getTitle());

            Glide.with(activity)
                    .load(data.getCover_image()).into(videoImage);

            if (data.getMrp().equals("0")) {
                priceTV.setText("Free");
            } else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (data.getFor_dams().equals(data.getMrp())) {
                        priceTV.setText(String.format("%s %s", getCurrencySymbol(),
                                Helper.calculatePriceBasedOnCurrency(data.getMrp())));

                    } else {
                        StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                        priceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(data.getMrp()) + " " +
                                Helper.calculatePriceBasedOnCurrency(data.getFor_dams()), TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) priceTV.getText();
                        spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(data.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } else {
                    if (data.getNon_dams().equals(data.getMrp())) {
                        priceTV.setText(String.format("%s %s", getCurrencySymbol(),
                                Helper.calculatePriceBasedOnCurrency(data.getMrp())));
                    } else {
                        StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                        priceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(data.getMrp()) + " " +
                                Helper.calculatePriceBasedOnCurrency(data.getNon_dams()), TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) priceTV.getText();
                        spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(data.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

           /* myView.setOnClickListener(view -> {
                    Intent courseList = new Intent(activity, CourseActivity.class);//FRAG_TYPE, Const.SINGLE_COURSE AllCoursesAdapter
                    courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
                    courseList.putExtra(Const.COURSES, data);
                    activity.startActivity(courseList);
            });*/

            myView.setTag(data);
            myView.setOnClickListener(onClickListener);

        }

    }
}

