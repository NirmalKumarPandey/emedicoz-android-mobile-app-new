package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.callback.OnSubscriptionItemClickListener;
import com.emedicoz.app.courses.fragment.CourseInvoice;
import com.emedicoz.app.customviews.DividerItemDecorator;
import com.emedicoz.app.installment.model.Installment;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.SharedPreference;

import java.text.DecimalFormat;
import java.util.List;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;

public class InstallmentParentAdapter extends RecyclerView.Adapter<InstallmentParentAdapter.MyParentViewHolder> {

    private static final String TAG = "InstallmentParentAdapte";
    Context activity;
    List<Installment> installmentList;
    SingleCourseData course;
    OnSubscriptionItemClickListener onSubscriptionItemClickListener;
    private static final DecimalFormat df2 = new DecimalFormat("#.##");


    public InstallmentParentAdapter(Context activity, List<Installment> installmentList, SingleCourseData course, OnSubscriptionItemClickListener onSubscriptionItemClickListener) {
        this.activity = activity;
        this.installmentList = installmentList;
        this.course = course;
        this.onSubscriptionItemClickListener = onSubscriptionItemClickListener;
    }

    @NonNull
    @Override
    public InstallmentParentAdapter.MyParentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.installment_parent_adapter_layout, viewGroup, false);
        return new InstallmentParentAdapter.MyParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstallmentParentAdapter.MyParentViewHolder myViewHolder, int i) {
        Installment installment = installmentList.get(i);
        Log.e(TAG, "onBindViewHolder: " + installment.getName());
        if (!TextUtils.isEmpty(course.getIs_subscription()) && course.getIs_subscription().equals("1")) {
            myViewHolder.tv_name.setText(installment.getName() + " " + activity.getString(R.string.subscription_plan));
        } else if (!TextUtils.isEmpty(course.getIs_instalment()) && course.getIs_instalment().equals("1")) {
            myViewHolder.tv_name.setText(installment.getName() + " " + activity.getString(R.string.installment_plan));
        } else {
            myViewHolder.tv_name.setText(installment.getName() + " " + activity.getString(R.string.installment_plan));
        }
        String s = myViewHolder.tv_name.getText().toString();
        String twoWordsName = s.substring(0, s.indexOf(' ', s.indexOf(' ') + 1));
        myViewHolder.tv_name.setText(twoWordsName);

        if (!TextUtils.isEmpty(course.getIs_subscription()) && course.getIs_subscription().equals("1")) {
            myViewHolder.tv_name.setText(installment.getName());
        }


        if (installment.isOpen()) {
//            myViewHolder.ll_root.setBackground(activity.getResources().getDrawable(R.drawable.bg_round_corner_border_blue));
//            myViewHolder.iv_arrow.setRotation(180);
            myViewHolder.recycler_view_installment_child.setVisibility(View.VISIBLE);
        } else {
//            myViewHolder.ll_root.setBackground(activity.getResources().getDrawable(R.drawable.bg_round_corner_border_gray));
//            myViewHolder.iv_arrow.setRotation(360);
            myViewHolder.recycler_view_installment_child.setVisibility(View.GONE);
        }

        if (installment.isSelected()) {
            if (activity instanceof CourseActivity) {
                Fragment fragment = ((CourseActivity) activity).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (fragment instanceof CourseInvoice) {
                    ((CourseInvoice) fragment).setInvoiceDetail(installment);
                }
            }
            myViewHolder.iv_select.setBackgroundResource(R.drawable.ic_baseline_check_circle_24);
            myViewHolder.ll_parent.setBackgroundResource(R.drawable.bg_blue_item_selection);
        } else {
            myViewHolder.iv_select.setBackgroundResource(R.drawable.ic_outline_circle_24);
            myViewHolder.ll_parent.setBackgroundResource(R.drawable.bg_item);
        }

        if (installment.getAmount_description().getCycle().size() > 1) {
            myViewHolder.iv_arrow.setVisibility(View.VISIBLE);
            myViewHolder.recycler_view_installment_child.setAdapter(new InstallmentChildAdapter(activity, installment.getAmount_description()));
        } else {
            myViewHolder.iv_arrow.setVisibility(View.GONE);
        }


        String coursePrice = "";

        String oneWordName = s.substring(0, s.indexOf(' '));
        int month = Integer.parseInt(oneWordName);

        course.setIs_subscription(!TextUtils.isEmpty(course.getIs_subscription()) ? course.getIs_subscription() : "0");

        if (course.getIs_instalment().equals("1") && course.getIs_subscription().equals("0")) {
            coursePrice = !TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())
                    ? course.getFor_dams() : course.getNon_dams();
        }

        if (!TextUtils.isEmpty(course.getIs_subscription()) && course.getIs_subscription().equals("1")) {
            coursePrice = installment.getAmount_description().getPayment().get(0);
        }

        String gst = !GenericUtils.isEmpty(course.getGst()) ? course.getGst() : "18";
        if (TextUtils.isEmpty(course.getGst_include()) || course.getGst_include().equals("0")) {
            myViewHolder.tvTotalAmount.setText(String.format("%s %s", getCurrencySymbol(), coursePrice));
            myViewHolder.tvPerMonth.setText(String.format("%s %s", getCurrencySymbol(), df2.format((Float.parseFloat(coursePrice) / month))));
        } else {
            float gstPrice = (Float.parseFloat(coursePrice) * Float.parseFloat(gst)) / 100;
            myViewHolder.tvTotalAmount.setText(String.format("%s %s", getCurrencySymbol(), (Float.parseFloat(coursePrice) + gstPrice)));
            myViewHolder.tvPerMonth.setText(String.format("%s %s", getCurrencySymbol(), df2.format(((Float.parseFloat(coursePrice) + gstPrice) / month))));
        }


        if (activity instanceof CourseDetailActivity) {
//            myViewHolder.month_amount_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return installmentList.size();
    }

    class MyParentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_name;
        ImageView iv_select, iv_arrow;
        RecyclerView recycler_view_installment_child;
        LinearLayout ll_root;
        RelativeLayout ll_parent;
        LinearLayout month_amount_layout;
        TextView tvTotalAmount;
        TextView tvPerMonth;

        public MyParentViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            ll_root = itemView.findViewById(R.id.ll_root);
            ll_parent = itemView.findViewById(R.id.ll_parent);
            iv_select = itemView.findViewById(R.id.iv_select);
            iv_arrow = itemView.findViewById(R.id.iv_arrow);
            tvTotalAmount = itemView.findViewById(R.id.total_amount);
            tvPerMonth = itemView.findViewById(R.id.per_month_amount);
            month_amount_layout = itemView.findViewById(R.id.month_amount_layout);
            recycler_view_installment_child = itemView.findViewById(R.id.recycler_view_installment_child);
            recycler_view_installment_child.setLayoutManager(new LinearLayoutManager(activity));
            recycler_view_installment_child.addItemDecoration(new DividerItemDecorator(activity.getResources().getDrawable(R.drawable.bg_botton_line)));
            iv_select.setOnClickListener(this);
            iv_arrow.setOnClickListener(this);
            ll_parent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()) {
                case R.id.ll_parent:
                    if (position >= 0) {
                        for (int i = 0; i < installmentList.size(); i++) {
                            if (position == i) {
//                                    if (installmentList.get(position).isOpen()) {
//                                        installmentList.get(position).setOpen(false);
//                                    } else {
                                installmentList.get(position).setSelected(true);
//                                    }
                            } else {
                                installmentList.get(i).setSelected(false);
                            }
                        }
                        notifyDataSetChanged();
                    }

                case R.id.iv_select:
                    if (position >= 0) {

                        onSubscriptionItemClickListener.OnSubscriptionItemClickPosition(position);
                        for (int i = 0; i < installmentList.size(); i++) {
                            if (position == i) {
//                                    if (installmentList.get(position).isOpen()) {
//                                        installmentList.get(position).setOpen(false);
//                                    } else {
                                installmentList.get(position).setSelected(true);
//                                    }
                            } else {
                                installmentList.get(i).setSelected(false);
                            }
                        }
                        notifyDataSetChanged();
                    }
                    break;
                case R.id.iv_arrow:
                    if (position >= 0) {
                        for (int i = 0; i < installmentList.size(); i++) {
                            if (position == i) {
                                installmentList.get(position).setOpen(!installmentList.get(position).isOpen());
                            } else {
                                installmentList.get(i).setOpen(false);
                            }
                        }
                        notifyDataSetChanged();
                    }
                    break;
            }
        }
    }
}