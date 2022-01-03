package com.emedicoz.app.referralcode.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emedicoz.app.R;
import com.emedicoz.app.referralcode.model.PaymentHistory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class paymentHistoryAdapter extends RecyclerView.Adapter<paymentHistoryAdapter.ViewHolder> {
    List<PaymentHistory> paymentHistoryList;
    Context context;

    public paymentHistoryAdapter(List<PaymentHistory> paymentHistoryList, Context context) {
        this.paymentHistoryList = paymentHistoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public paymentHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.affiliate_payment_history_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull paymentHistoryAdapter.ViewHolder viewHolder, int i) {
        PaymentHistory paymentHistory = paymentHistoryList.get(i);
        if (paymentHistory.getTransactionDate() != null) {
            try {
                DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date d = f.parse(paymentHistory.getTransactionDate());
                DateFormat date = new SimpleDateFormat("MM/dd/yyyy");

                viewHolder.transactionDateTV.setText(date.format(Objects.requireNonNull(d)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        viewHolder.transactionByTV.setText(paymentHistory.getFirstName());
        viewHolder.courseNameTV.setText(paymentHistory.getCourseName());
        viewHolder.courseFeeTV.setText(paymentHistory.getCourseFee());
        viewHolder.commissionTV.setText(paymentHistory.getNoOfTransaction());
    }

    @Override
    public int getItemCount() {
        if (paymentHistoryList != null) {
            return paymentHistoryList.size();
        } else {
            return 0;
        }

    }

    public void setList(List<PaymentHistory> paymentHistoryList) {
        this.paymentHistoryList = paymentHistoryList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView transactionDateTV;
        TextView transactionByTV;
        TextView courseNameTV;
        TextView courseFeeTV;
        TextView commissionTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionDateTV = itemView.findViewById(R.id.transaction_date_TV);
            transactionByTV = itemView.findViewById(R.id.transaction_by_TV);
            courseNameTV = itemView.findViewById(R.id.course_name_TV);
            courseFeeTV = itemView.findViewById(R.id.course_fee_TV);
            commissionTV = itemView.findViewById(R.id.commission_TV);

        }
    }
}
