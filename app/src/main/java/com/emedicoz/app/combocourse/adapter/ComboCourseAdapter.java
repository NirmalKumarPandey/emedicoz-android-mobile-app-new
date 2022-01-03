package com.emedicoz.app.combocourse.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.combocourse.activity.ComboCourseActivity;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

import java.util.List;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;

public class ComboCourseAdapter extends RecyclerView.Adapter<ComboCourseAdapter.MyViewHolder> {

    private static final String TAG = "ComboCourseAdapter";
    private List<SingleCourseData> singleCourseDataArrayList;
    private Context context;

    public ComboCourseAdapter(List<SingleCourseData> singleCourseDataArrayList, Context context) {
        this.singleCourseDataArrayList = singleCourseDataArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.combo_course_screen, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        SingleCourseData singleCourseData = singleCourseDataArrayList.get(i);

        myViewHolder.tvCourseTitle.setText(singleCourseData.getTitle());
        myViewHolder.tvDescription.setText(HtmlCompat.fromHtml(singleCourseData.getDescription().trim(), HtmlCompat.FROM_HTML_MODE_LEGACY));

        if (singleCourseData.getIs_purchased().equals("0")) {
            if (singleCourseData.getMrp().equals("0")) {
                myViewHolder.tvPayPrice.setVisibility(View.GONE);
                myViewHolder.tvCutPrice.setVisibility(View.GONE);
                setButtonBackground(myViewHolder, context.getString(R.string.enroll), ContextCompat.getDrawable(context, R.drawable.bg_round_corner_fill_red));
            } else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (singleCourseData.getFor_dams().equalsIgnoreCase("0")) {
                        myViewHolder.tvPayPrice.setVisibility(View.GONE);
                        myViewHolder.tvCutPrice.setVisibility(View.GONE);
                        setButtonBackground(myViewHolder, context.getString(R.string.enroll), ContextCompat.getDrawable(context, R.drawable.bg_round_corner_fill_red));
                    } else if (singleCourseData.getFor_dams().equals(singleCourseData.getMrp())) {
                        myViewHolder.tvPayPrice.setVisibility(View.VISIBLE);
                        myViewHolder.tvCutPrice.setVisibility(View.GONE);
                        myViewHolder.tvPayPrice.setText(String.format("%s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp())));
                        setButtonBackground(myViewHolder, context.getString(R.string.enroll), ContextCompat.getDrawable(context, R.drawable.bg_round_corner_fill_red));
                    } else {
                        myViewHolder.tvPayPrice.setVisibility(View.VISIBLE);
                        myViewHolder.tvPayPrice.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(singleCourseData.getFor_dams()));
                        myViewHolder.tvCutPrice.setVisibility(View.VISIBLE);
                        StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                        myViewHolder.tvCutPrice.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp()), TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) myViewHolder.tvCutPrice.getText();
                        spannable.setSpan(strikeThroughSpan, 0, Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        setButtonBackground(myViewHolder, context.getString(R.string.enroll), ContextCompat.getDrawable(context, R.drawable.bg_round_corner_fill_red));
                    }
                } else {
                    if (singleCourseData.getNon_dams().equalsIgnoreCase("0")) {
                        myViewHolder.tvPayPrice.setVisibility(View.GONE);
                        myViewHolder.tvCutPrice.setVisibility(View.GONE);
                        setButtonBackground(myViewHolder, context.getString(R.string.enroll), ContextCompat.getDrawable(context, R.drawable.bg_round_corner_fill_red));
                    } else if (singleCourseData.getNon_dams().equals(singleCourseData.getMrp())) {
                        myViewHolder.tvPayPrice.setVisibility(View.VISIBLE);
                        myViewHolder.tvCutPrice.setVisibility(View.GONE);
                        myViewHolder.tvPayPrice.setText(String.format("%s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp())));
                        setButtonBackground(myViewHolder, context.getString(R.string.enroll), ContextCompat.getDrawable(context, R.drawable.bg_round_corner_fill_red));
                    } else {
                        myViewHolder.tvPayPrice.setVisibility(View.VISIBLE);
                        myViewHolder.tvPayPrice.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(singleCourseData.getNon_dams()));
                        myViewHolder.tvCutPrice.setVisibility(View.VISIBLE);
                        StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                        myViewHolder.tvCutPrice.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp()), TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) myViewHolder.tvCutPrice.getText();
                        spannable.setSpan(strikeThroughSpan, 0, Helper.calculatePriceBasedOnCurrency(singleCourseData.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        setButtonBackground(myViewHolder, context.getString(R.string.enroll), ContextCompat.getDrawable(context, R.drawable.bg_round_corner_fill_red));
                    }
                }
            }
        } else {
            myViewHolder.tvPayPrice.setVisibility(View.GONE);
            myViewHolder.tvCutPrice.setVisibility(View.GONE);
            setButtonBackground(myViewHolder, context.getString(R.string.enrolled), ContextCompat.getDrawable(context, R.drawable.bg_round_corner_fill_green));
        }
    }

    private void setButtonBackground(MyViewHolder myViewHolder, String butttonText, Drawable drawable) {
        myViewHolder.btnBuy.setVisibility(View.VISIBLE);
        myViewHolder.btnBuy.setText(butttonText);
        myViewHolder.btnBuy.setBackground(drawable);
    }

    @Override
    public int getItemCount() {
        return singleCourseDataArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvCourseTitle, tvDescription, tvPayPrice, tvCutPrice;
        Button btnBuy;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCourseTitle = itemView.findViewById(R.id.tv_course_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvPayPrice = itemView.findViewById(R.id.tv_pay_price);
            tvCutPrice = itemView.findViewById(R.id.tv_cut_price);
            btnBuy = itemView.findViewById(R.id.btn_buy);
            btnBuy.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.e(TAG, "onClick: " + position);

            if (position >= 0 && singleCourseDataArrayList.get(position).getIs_purchased().equals("0")) {
                if (singleCourseDataArrayList.get(position).getMrp().equals("0")) {
                    ((ComboCourseActivity) context).freeCourseTransaction(singleCourseDataArrayList.get(position).getId(), position);
                } else {
                    Intent courseInvoice = new Intent(context, CourseActivity.class);
                    courseInvoice.putExtra(Const.FRAG_TYPE, Const.COURSE_INVOICE);
                    courseInvoice.putExtra(Const.COURSE_DESC, singleCourseDataArrayList.get(position));
                    courseInvoice.putExtra(Constants.Extras.TYPE, Const.COMBO_COURSE);
                    context.startActivity(courseInvoice);
                }
            }

        }
    }
}
