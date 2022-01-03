package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

import java.util.List;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;

public class ViewAllLCPracticeAdapter extends RecyclerView.Adapter<ViewAllLCPracticeAdapter.MyViewHodler> {
    Context context;
    List<Course> coursesData;

    public ViewAllLCPracticeAdapter(Context context, List<Course> coursesData) {
        this.context = context;
        this.coursesData = coursesData;
    }

    @Override
    public ViewAllLCPracticeAdapter.MyViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_all_lc_practice_layout, null);
        return new ViewAllLCPracticeAdapter.MyViewHodler(view);
    }

    @Override
    public void onBindViewHolder(ViewAllLCPracticeAdapter.MyViewHodler holder, int position) {
        holder.setData(coursesData.get(position));

    }

    @Override
    public int getItemCount() {
        return coursesData.size();
    }

    public class MyViewHodler extends RecyclerView.ViewHolder {

        ImageView videoImage;
        ImageView liveIV;
        TextView titleTV;
        TextView price;
        RelativeLayout tileRL;

        public MyViewHodler(View itemView) {
            super(itemView);
            liveIV = itemView.findViewById(R.id.liveIV);
            videoImage = itemView.findViewById(R.id.videoImage);
            titleTV = itemView.findViewById(R.id.titleTV);
            price = itemView.findViewById(R.id.priceTV);
            tileRL = itemView.findViewById(R.id.ibt_test_tile_RL);
        }

        public void setData(final com.emedicoz.app.modelo.courses.Course course) {
            if (course != null) {
                titleTV.setText(course.getTitle());
                if (course.getIs_live().equals("1")) {
                    Glide.with(context).asGif().load(R.drawable.live_gif).into(liveIV);
                    liveIV.setVisibility(View.VISIBLE);
                } else
                    liveIV.setVisibility(View.GONE);
                if (course.getMrp().equals("0")) {
                    price.setVisibility(View.GONE);
                    price.setText(context.getResources().getString(R.string.free));
                } else {
                    price.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (course.getFor_dams().equals("0")) {
                            price.setText(context.getResources().getString(R.string.free));
                        } else {
                            if (course.getFor_dams().equals(course.getMrp())) {
                                price.setText(String.format("%s %s", getCurrencySymbol(),
                                        Helper.calculatePriceBasedOnCurrency(course.getMrp())));
                            } else {
                                StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                                price.setText(String.format("%s %s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(course.getMrp()), Helper.calculatePriceBasedOnCurrency(course.getFor_dams())), TextView.BufferType.SPANNABLE);
                                Spannable spannable = (Spannable) price.getText();
                                spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(course.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    } else {
                        if (course.getNon_dams().equals("0")) {
                            price.setText(context.getResources().getString(R.string.free));
                        } else {
                            if (course.getNon_dams().equals(course.getMrp())) {
                                price.setText(String.format("%s %s", getCurrencySymbol(),
                                        Helper.calculatePriceBasedOnCurrency(course.getMrp())));
                            } else {
                                StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                                price.setText(String.format("%s %s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(course.getMrp()), Helper.calculatePriceBasedOnCurrency(course.getNon_dams())), TextView.BufferType.SPANNABLE);
                                Spannable spannable = (Spannable) price.getText();
                                spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(course.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }
                }
                Glide.with(context).load(course.getCover_image()).into(videoImage);
                tileRL.setOnClickListener(v -> {
                    Intent courseList = new Intent(context, CourseActivity.class);//FRAG_TYPE, Const.SINGLE_COURSE AllCoursesAdapter
                    courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_STUDY);
                    courseList.putExtra(Const.IMAGE, course.getDesc_header_image());
                    courseList.putExtra(Const.COURSES, course);
                    SharedPreference.getInstance().putString(Constants.Extras.ID, course.getId());
                    context.startActivity(courseList);
                });
            }
        }
    }
}
