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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.fragment.CommonFragForList;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.CoursesData;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

import java.util.ArrayList;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;

/**
 * Created by appsquadz on 25/9/17.
 */

public class AllCoursesAdapater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<CoursesData> coursesDatasArrayList;
    ArrayList<CoursesData> caraouselCourseList;
    Context context;
    CommonFragForList commonFragForList;
    int width = 0;
    LayoutInflater layoutInflater;
    CourseListAdapter courseListAdapter;
    AllCourseListAdapter mCourseListAdapter;
    RecyclerView rvCourseList;
    int mPosition;
    ArrayList<Course> courseArrayList = new ArrayList<>();

    View.OnClickListener onCourseClickListener = (View v) -> {
        Course course = (Course) v.getTag();
        Log.e("AAA", course.getIs_combo());
        Intent courseList = new Intent(context, CourseActivity.class);//FRAG_TYPE, Const.SINGLE_COURSE AllCoursesAdapter
        if (course.getCourse_type().equalsIgnoreCase("1")) {
            if (!GenericUtils.isEmpty(course.getIs_live())) {
                if (course.getIs_live().equals("1")) {
                    SharedPreference.getInstance().putString(Constants.Extras.ID, course.getId());
                    courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_STUDY);
                    courseList.putExtra(Const.IMAGE, course.getDesc_header_image());
                } else {
                    if (course.getIs_combo().equalsIgnoreCase("1")) {
                        courseList.putExtra(Const.FRAG_TYPE, Const.COMBO_COURSE);
                    } else {
                        courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
                    }
                }
            } else {
                if (course.getIs_combo().equalsIgnoreCase("1")) {
                    courseList.putExtra(Const.FRAG_TYPE, Const.COMBO_COURSE);
                } else {
                    courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
                }
            }
        } else {
            if (course.getId().equalsIgnoreCase(Constants.Extras.QUESTION_BANK_COURSE_ID)) {
                courseList.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE);
            } else {
                courseList.putExtra(Const.FRAG_TYPE, Const.TEST_COURSE);
            }
        }
        courseList.putExtra(Const.COURSES, course);
        context.startActivity(courseList);
    };

    View.OnClickListener onseeAllClickListener = (View v) -> {
        CoursesData coursesData = (CoursesData) v.getTag();
        Intent courseList = new Intent(context, CourseActivity.class);//FRAG_TYPE, Const.SEEALL_COURSE AllCoursesAdapter
        courseList.putExtra(Const.FRAG_TYPE, Const.SEEALL_COURSE);
        courseList.putExtra(Const.COURSE_CATEGORY, coursesData.getCategory_info());
        context.startActivity(courseList);
    };

    public AllCoursesAdapater(Context activity, ArrayList<CoursesData> coursesDataArrayList, CommonFragForList commonFragForList) {
        this.context = activity;
        this.coursesDatasArrayList = coursesDataArrayList;
        this.commonFragForList = commonFragForList;
        this.layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        makeCaraouselList();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 0: // 0 = vertical
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlecourse_category_row, parent, false);
                return new ViewHolder(view);

            case 1: // 1 = horizontal
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlecourse_category_row, parent, false);
                return new ViewHolder(view);

            case 2: // 2 = caraousel
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlecourse_category_row, parent, false);
                return new ViewHolder(view);

            default:
                return null;
        }
    }

    public void makeCaraouselList() {
        caraouselCourseList = new ArrayList<>();
        for (CoursesData coursesData : coursesDatasArrayList) {
            if (coursesData.getCategory_info() != null && coursesData.getCategory_info().getIn_carousel().equals("1")) {
                caraouselCourseList.add(coursesData);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder sholder, int position) {
        final ViewHolder holder1 = (ViewHolder) sholder;
        CoursesData coursesData;
        coursesData = coursesDatasArrayList.get(position);
        Log.e("TAG", "onBindViewHolder: position = " + position + ", viewType = " + getItemViewType(position) + ", isSelected = " + coursesData.isSelected());
        if (coursesData.getCategory_info() == null) return;

        if (coursesData.getCategory_info() == null) return;

        if (getItemViewType(position) == 0) {
            holder1.ivDropDown.setVisibility(View.GONE);
            holder1.seeAll.setVisibility(View.VISIBLE);
            holder1.courseVerticalRV.setVisibility(View.VISIBLE);
            holder1.courseLL.setVisibility(View.GONE);
            holder1.topViewItem.setVisibility(View.VISIBLE);
            holder1.courseCategoryTitle.setText(coursesData.getCategory_info().getName());
            courseListAdapter = new CourseListAdapter(context, coursesData.getCourse_list(), Const.COURSE_CATEGORY);
            holder1.courseVerticalRV.setAdapter(courseListAdapter);
        } else if (getItemViewType(position) == 1) {
            holder1.ivDropDown.setVisibility(View.GONE);
            holder1.seeAll.setVisibility(View.VISIBLE);
            holder1.topViewItem.setVisibility(View.VISIBLE);
            holder1.courseLL.setVisibility(View.VISIBLE);
            holder1.courseVerticalRV.setVisibility(View.GONE);
            holder1.courseCategoryTitle.setText(coursesData.getCategory_info().getName());
            holder1.courseLL.removeAllViews();
            for (int i = 0; i < coursesData.getCourse_list().size(); i++) {
                holder1.courseLL.setOrientation(LinearLayout.VERTICAL);
                holder1.courseLL.addView(initCourseHorView(coursesData.getCourse_list().get(i), 0));
            }

/*            if (coursesData.isSelected()) {
                holder1.iv_drop_down.setRotation(180);
                rv_course_list.setVisibility(View.VISIBLE);
                mCourseListAdapter = new AllCourseListAdapter(context, this.courseArrayList, Const.SEEALL_COURSE);
                rv_course_list.setAdapter(mCourseListAdapter);
            } else {
                holder1.iv_drop_down.setRotation(360);
                rv_course_list.setVisibility(View.GONE);
            }*/
        }
        holder1.seeAll.setTag(coursesData);
        holder1.seeAll.setOnClickListener(onseeAllClickListener);
    }

    public LinearLayout initCourseHorView(final Course course, int itemViewType) {
        TextView courseTV, priceTV, learnerTV, ratingTV;
        final ImageView imageIV;
        RatingBar ratingRB;
        Button btnEnroll;
        switch (itemViewType) {
            case 0:
                LinearLayout view = (LinearLayout) View.inflate(context, R.layout.single_row_course_hor, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                courseTV = view.findViewById(R.id.nameTV);
                priceTV = view.findViewById(R.id.priceTV);
                learnerTV = view.findViewById(R.id.learnerTV);
                ratingTV = view.findViewById(R.id.ratingTV);
                ratingRB = view.findViewById(R.id.ratingRB);
                imageIV = view.findViewById(R.id.imageIV);
                btnEnroll = view.findViewById(R.id.btnEnroll);

                getCoursePurchaseData(course, btnEnroll);

                courseTV.setText(course.getTitle());


                if (course.getMrp().equals("0")) {
                    priceTV.setText(context.getResources().getString(R.string.free));
                } else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (course.getFor_dams().equalsIgnoreCase("0")) {
                            priceTV.setText(context.getResources().getString(R.string.free));
                        } else {
                            if (course.getFor_dams().equals(course.getMrp())) {
                                priceTV.setText(String.format("%s %s", getCurrencySymbol(),
                                        Helper.calculatePriceBasedOnCurrency(course.getMrp())));
                            } else {
                                StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                                priceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(course.getMrp()) + " " +
                                        Helper.calculatePriceBasedOnCurrency(course.getFor_dams()), TextView.BufferType.SPANNABLE);
                                Spannable spannable = (Spannable) priceTV.getText();
                                spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(course.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    } else {
                        if (course.getNon_dams().equalsIgnoreCase("0")) {
                            priceTV.setText(context.getResources().getString(R.string.free));
                        } else {
                            if (course.getNon_dams().equals(course.getMrp())) {
                                priceTV.setText(String.format("%s %s", getCurrencySymbol(),
                                        Helper.calculatePriceBasedOnCurrency(course.getMrp())));
                            } else {
                                StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                                priceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(course.getMrp()) + " " +
                                        Helper.calculatePriceBasedOnCurrency(course.getNon_dams()), TextView.BufferType.SPANNABLE);
                                Spannable spannable = (Spannable) priceTV.getText();
                                spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(course.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }

                    }
                }

                learnerTV.setText((course.getLearner() +
                        ((course.getLearner().equals("1") || (course.getLearner().equals("0")) ? Const.LEARNER : Const.LEARNERS))));
                ratingTV.setText(course.getRating());
                ratingRB.setRating(Float.parseFloat(course.getRating()));
                Glide.with(context)
                        .asBitmap()
                        .load(course.getCover_image())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.courses_blue).error(R.mipmap.courses_blue))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                imageIV.setImageBitmap(result);
                            }
                        });
                view.setTag(course);
                view.setOnClickListener(onCourseClickListener);
//                view.setOnClickListener(v -> {
//                    Intent courseList = new Intent(context, CourseActivity.class);
//                    courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_STUDY);
//                    courseList.putExtra(Const.IMAGE, course.getDesc_header_image());
//                    courseList.putExtra(Const.COURSES, course);
//                    context.startActivity(courseList);
//                });
                return view;

            case 1:
                LinearLayout verView = (LinearLayout) View.inflate(context, R.layout.single_row_course_ver, null);
                LinearLayout.LayoutParams verlp = new LinearLayout.LayoutParams(width / 3, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                courseTV = verView.findViewById(R.id.nameTV);
                priceTV = verView.findViewById(R.id.priceTV);
                learnerTV = verView.findViewById(R.id.learnerTV);
                ratingTV = verView.findViewById(R.id.ratingTV);
                ratingRB = verView.findViewById(R.id.ratingRB);
                imageIV = verView.findViewById(R.id.imageIV);

                btnEnroll = verView.findViewById(R.id.btnEnroll);

                getCoursePurchaseData(course, btnEnroll);
                courseTV.setText(course.getTitle());

                if (course.getMrp().equals("0")) {
                    priceTV.setText(context.getResources().getString(R.string.free));
                } else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (course.getFor_dams().equalsIgnoreCase("0")) {
                            priceTV.setText(context.getResources().getString(R.string.free));
                        } else {
                            if (course.getFor_dams().equals(course.getMrp())) {
                                priceTV.setText(String.format("%s %s", getCurrencySymbol(),
                                        Helper.calculatePriceBasedOnCurrency(course.getMrp())));
                            } else {
                                StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                                priceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(course.getMrp()) + " " +
                                        Helper.calculatePriceBasedOnCurrency(course.getFor_dams()), TextView.BufferType.SPANNABLE);
                                Spannable spannable = (Spannable) priceTV.getText();
                                spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(course.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    } else {
                        if (course.getNon_dams().equalsIgnoreCase("0")) {
                            priceTV.setText(context.getResources().getString(R.string.free));
                        } else {
                            if (course.getNon_dams().equals(course.getMrp())) {
                                priceTV.setText(String.format("%s %s", getCurrencySymbol(),
                                        Helper.calculatePriceBasedOnCurrency(course.getMrp())));
                            } else {
                                StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                                priceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(course.getMrp()) + " " +
                                        Helper.calculatePriceBasedOnCurrency(course.getNon_dams()), TextView.BufferType.SPANNABLE);
                                Spannable spannable = (Spannable) priceTV.getText();
                                spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(course.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }

                    }
                }


                learnerTV.setText((course.getLearner() + (course.getLearner().equals("1") ? Const.LEARNER : Const.LEARNERS)));
                ratingTV.setText(course.getRating());
                ratingRB.setRating(Float.parseFloat(course.getRating()));
                Glide.with(context)
                        .asBitmap()
                        .load(course.getCover_image())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.courses_blue).error(R.mipmap.courses_blue))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                imageIV.setImageBitmap(result);
                            }
                        });
                verView.setLayoutParams(verlp);
                verView.setTag(course);
//                verView.setOnClickListener(onCourseClickListener);
                verView.setOnClickListener(v -> {
                    Intent courseList = new Intent(context, CourseActivity.class);
                    courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_STUDY);
                    courseList.putExtra(Const.IMAGE, course.getDesc_header_image());
                    courseList.putExtra(Const.COURSES, course);
                    context.startActivity(courseList);
                });
                return verView;
        }
        return null;
    }

    private void getCoursePurchaseData(Course course, Button btnEnroll) {
        if (course.isIs_renew() == "1") {
            Helper.setCourseButtonText(context, btnEnroll, Const.RENEW);
        } else if (course.isIs_purchased()) {
            Helper.setCourseButtonText(context, btnEnroll, Const.PURCHASED);
        } else {
            if (course.getMrp().equals("0"))
                Helper.setCourseButtonText(context, btnEnroll, Const.FREE);
            else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (course.getFor_dams().equals("0"))
                        Helper.setCourseButtonText(context, btnEnroll, Const.FREE);
                    else
                        Helper.setCourseButtonText(context, btnEnroll, Const.PAID);
                } else {
                    if (course.getNon_dams().equals("0"))
                        Helper.setCourseButtonText(context, btnEnroll, Const.FREE);
                    else
                        Helper.setCourseButtonText(context, btnEnroll, Const.PAID);
                }
            }
        }

        btnEnroll.setOnClickListener((View view) -> {

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
    }

    @Override
    public int getItemCount() {
        return coursesDatasArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // 0 = vertical , 1 = horizontal , 2 = caraousel
        if (coursesDatasArrayList.get(position).getViewItemType() == 0) {
            return 0;
        } else if (coursesDatasArrayList.get(position).getViewItemType() == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    public void seeAllCourse(ArrayList<Course> courseArrayList) {
        this.courseArrayList.clear();
        this.courseArrayList = courseArrayList;
        Log.e("TAG", "seeAllCourse: mPosition = " + mPosition + ", list size = " + this.courseArrayList.size());
        if (this.courseArrayList.size() > 0) {
            mCourseListAdapter = new AllCourseListAdapter(context, this.courseArrayList, Const.SEEALL_COURSE);
            rvCourseList.setAdapter(mCourseListAdapter);
            notifyDataSetChanged();
        } else {
            GenericUtils.showToast(context, "No more data.");
            coursesDatasArrayList.get(mPosition).setSelected(false);
        }
        commonFragForList.mProgress.dismiss();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RecyclerView courseVerticalRV;
        LinearLayout courseLL;
        LinearLayout parentLL;
        LinearLayout topViewItem;
        Button seeAll;
        TextView courseCategoryTitle;
        ImageView ivDropDown;

        public ViewHolder(View itemView) {
            super(itemView);
            ivDropDown = itemView.findViewById(R.id.iv_drop_down);
            courseCategoryTitle = itemView.findViewById(R.id.tv1);
            seeAll = itemView.findViewById(R.id.courseCatseeAllBtn);
            topViewItem = itemView.findViewById(R.id.topViewItem);
            courseLL = itemView.findViewById(R.id.coursesoptionLL);
            parentLL = itemView.findViewById(R.id.parentLL);
            courseVerticalRV = itemView.findViewById(R.id.categoryRV);
            rvCourseList = itemView.findViewById(R.id.rv_course_list);
            rvCourseList.setLayoutManager(new LinearLayoutManager(context));
            rvCourseList.setVisibility(View.GONE);
            courseVerticalRV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            if (courseCategoryTitle.getVisibility() == View.GONE)
                courseCategoryTitle.setVisibility(View.VISIBLE);

            // iv_drop_down.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mPosition = getAdapterPosition();
            Log.e("TAG", "onClick: position before = " + mPosition);
            if (mPosition >= 0) {
                Log.e("TAG", "onClick: position after = " + mPosition);
                for (int i = 0; i < coursesDatasArrayList.size(); i++) {
                    if (mPosition == i) {
                        if (coursesDatasArrayList.get(mPosition).isSelected()) {
                            coursesDatasArrayList.get(mPosition).setSelected(false);
                        } else {
                            coursesDatasArrayList.get(mPosition).setSelected(true);
                        }
                    } else {
                        coursesDatasArrayList.get(i).setSelected(false);
                    }
                }

                boolean isSelected = false;
                for (int i = 0; i < coursesDatasArrayList.size(); i++) {
                    if (coursesDatasArrayList.get(i).isSelected()) {
                        isSelected = true;
                        break;
                    } else {
                        isSelected = false;
                    }
                }
                if (isSelected) {
                    if (commonFragForList.mProgress != null) {
                        commonFragForList.mProgress.show();
                    }
                    commonFragForList.networkCallForAllData(mPosition, coursesDatasArrayList.get(mPosition).getCategory_info().getId());
                    Log.e("TAG", "onClick:id " + coursesDatasArrayList.get(mPosition).getCategory_info().getId());
                } else {
                    notifyDataSetChanged();
                }
            }
        }
    }
}


