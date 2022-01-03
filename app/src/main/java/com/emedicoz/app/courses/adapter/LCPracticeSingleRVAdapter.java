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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.CoursesData;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;

public class LCPracticeSingleRVAdapter extends RecyclerView.Adapter<LCPracticeSingleRVAdapter.MyViewHodler> {

    Context context;
    CoursesData coursesData;
    Bitmap bitmap;


    public LCPracticeSingleRVAdapter(Context context, CoursesData coursesData) {
        this.context = context;
        this.coursesData = coursesData;
    }

    @Override
    public MyViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ibt_single_item_practice_test, null);
        return new MyViewHodler(view);
    }

    @Override
    public void onBindViewHolder(MyViewHodler holder, int position) {
        holder.setData(coursesData.getCourse_list().get(position));

    }

    @Override
    public int getItemCount() {
        if (!GenericUtils.isListEmpty(coursesData.getCourse_list()) && coursesData.getCourse_list().size() <= 4) {
            return coursesData.getCourse_list().size();
        } else {
            return 4;
        }
    }

    public class MyViewHodler extends RecyclerView.ViewHolder {

        ImageView videoImage;
        ImageView liveIV;
        TextView titleTV, descriptionTV, price, validityTextTV, description_TV, mrpCutTV;
        RelativeLayout tileRL, videoplayerRL;

        public MyViewHodler(View itemView) {
            super(itemView);
            liveIV = itemView.findViewById(R.id.liveIV);
            videoImage = itemView.findViewById(R.id.videoImage);
            titleTV = itemView.findViewById(R.id.titleTV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            description_TV = itemView.findViewById(R.id.description_TV);
            validityTextTV = itemView.findViewById(R.id.validityTextTV);
            mrpCutTV = itemView.findViewById(R.id.mrpCutTV);
            price = itemView.findViewById(R.id.priceTV);
            tileRL = itemView.findViewById(R.id.ibt_test_tile_RL);
            videoplayerRL = itemView.findViewById(R.id.videoplayerRL);
        }

        public void setData(final Course course) {

            if (course != null) {
                titleTV.setText(course.getTitle());
                Glide.with(context).asGif()
                        .load(R.drawable.live_gif)
                        .into(liveIV);
                if (course.getIs_live() != null) {
                    if (course.getIs_live().equals("1"))
                        liveIV.setVisibility(View.VISIBLE);
                    else
                        liveIV.setVisibility(View.GONE);
                } else {
                    liveIV.setVisibility(View.GONE);
                }
                if (course.getMrp().equals("0")) {
                    price.setText("Free");
                } else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (course.getFor_dams().equals("0")) {
                            price.setText("Free");
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
                            price.setText("Free");
                        } else {
                            if (course.getNon_dams().equals(course.getMrp())) {
                                price.setText(String.format("%s %s", getCurrencySymbol(),
                                        Helper.calculatePriceBasedOnCurrency(course.getMrp())));
                            } else {
                                StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();
                                price.setText(String.format("%s %s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(course.getMrp()), Helper.calculatePriceBasedOnCurrency(course.getNon_dams())), TextView.BufferType.SPANNABLE);
                                Spannable spannable = (Spannable) price.getText();
                                spannable.setSpan(STRIKE_THROUGH_SPAN, 2, Helper.calculatePriceBasedOnCurrency(course.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }
                }
                if (course.getCover_image() != null) {
                    Glide.with(context).load(course.getCover_image())
                            .error(Glide.with(videoImage).load(R.mipmap.courses_blue)).into(videoImage);
                } else {
                    videoImage.setImageResource(R.drawable.app_icon_small);
                }
                tileRL.setOnClickListener((View view) -> {
                    Intent courseList = new Intent(context, CourseActivity.class);//FRAG_TYPE, Const.SINGLE_COURSE AllCoursesAdapter
                    courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_STUDY);
                    courseList.putExtra(Const.IMAGE, course.getDesc_header_image());
                    SharedPreference.getInstance().putString(Constants.Extras.ID, course.getId());

                    courseList.putExtra(Const.COURSES, course);
                    courseList.putExtra(Const.PARENT_ID, course.getId());
                    context.startActivity(courseList);
                });
            }
        }
    }
}
