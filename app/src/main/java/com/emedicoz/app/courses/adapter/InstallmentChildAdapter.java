package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.installment.model.AmountDescription;

public class InstallmentChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    AmountDescription amount_description;
    int TITLE_ROW = 0, DATA_ROW = 1;

    public InstallmentChildAdapter(Context context, AmountDescription amount_description) {
        this.context = context;
        this.amount_description = amount_description;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = null;
        if (viewType == TITLE_ROW) {
            view = LayoutInflater.from(context).inflate(R.layout.installment_child_1_adapter_layout, viewGroup, false);
            return new MyChildViewHolder1(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.installment_child_2_adapter_layout, viewGroup, false);
            return new MyChildViewHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//            if (i > 0 && i <= amount_description.getCycle().size())
        if (viewHolder instanceof MyChildViewHolder2) {
            ((MyChildViewHolder2) viewHolder).tv_installment.setText("" + i);

            if (Integer.parseInt(amount_description.getCycle().get(i - 1)) <= 1) {
                ((MyChildViewHolder2) viewHolder).tv_interval.setText(amount_description.getCycle().get(i - 1) + " Day");
            } else {
                ((MyChildViewHolder2) viewHolder).tv_interval.setText(amount_description.getCycle().get(i - 1) + " Days");
            }

            ((MyChildViewHolder2) viewHolder).tv_amount.setText(amount_description.getPayment().get(i - 1));
        }
    }

    @Override
    public int getItemCount() {
        return amount_description.getCycle().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TITLE_ROW;
        else
            return DATA_ROW;
    }

    class MyChildViewHolder1 extends RecyclerView.ViewHolder {


        public MyChildViewHolder1(@NonNull View itemView) {
            super(itemView);

        }
    }

    class MyChildViewHolder2 extends RecyclerView.ViewHolder {

        TextView tv_installment, tv_interval, tv_amount;

        public MyChildViewHolder2(@NonNull View itemView) {
            super(itemView);
            tv_installment = itemView.findViewById(R.id.tv_installment);
            tv_interval = itemView.findViewById(R.id.tv_interval);
            tv_amount = itemView.findViewById(R.id.tv_amount);
        }
    }
}