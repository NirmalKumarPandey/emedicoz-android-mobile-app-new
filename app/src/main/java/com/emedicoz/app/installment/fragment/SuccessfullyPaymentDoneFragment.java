package com.emedicoz.app.installment.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.testseries.OrderHistoryData;
import com.emedicoz.app.utilso.Const;

public class SuccessfullyPaymentDoneFragment extends Fragment {

    public static SuccessfullyPaymentDoneFragment newInstance(OrderHistoryData orderHistoryData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.ORDER_DETAIL, orderHistoryData);
        SuccessfullyPaymentDoneFragment fragment = new SuccessfullyPaymentDoneFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_after_payment_thanku, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}
