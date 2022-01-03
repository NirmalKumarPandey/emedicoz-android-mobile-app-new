package com.emedicoz.app.installment.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.installment.model.Statement;
import com.emedicoz.app.modelo.testseries.OrderHistoryData;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;
import static com.emedicoz.app.utilso.Helper.getGSTExcludevalue;

public class ShowTransactionStatementFragment extends Fragment implements MyNetworkCall.MyNetworkCallBack {

    Activity activity;
    RecyclerView recyclerViewTransactionStatement;
    TransactionAdapter transactionAdapter;
    MyNetworkCall myNetworkCall;
    OrderHistoryData orderHistoryData;
    Statement statement;
    ArrayList<Statement> statementArrayList = new ArrayList<>();
    TextView tvTitle;
    TextView tvDueDate;
    TextView tvValidity;
    TextView paymentStatusLabel;
    TextView tvPaymentStatus;
    TextView tvPrice;
    TextView tvCutPrice;
    LinearLayout llTransactionRoot;
    DecimalFormat decimalFormat = new DecimalFormat("0.##");

    public static ShowTransactionStatementFragment newInstance(OrderHistoryData orderHistoryData) {
        Bundle args = new Bundle();
        ShowTransactionStatementFragment fragment = new ShowTransactionStatementFragment();
        args.putSerializable(Const.ORDER_DETAIL, orderHistoryData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_transaction_statement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderHistoryData = (OrderHistoryData) getArguments().getSerializable(Const.ORDER_DETAIL);
        initViews(view);
        hitTransactionStatementApi();
    }

    private void initViews(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        paymentStatusLabel = view.findViewById(R.id.payment_status_lable);
        tvPaymentStatus = view.findViewById(R.id.tv_payment_status);
        llTransactionRoot = view.findViewById(R.id.ll_transaction_root);

        tvPrice = view.findViewById(R.id.tv_price);
        tvCutPrice = view.findViewById(R.id.tv_cut_price);


        tvTitle = view.findViewById(R.id.tv_title);
        tvValidity = view.findViewById(R.id.tv_validity);
        tvDueDate = view.findViewById(R.id.tv_due_date);
        recyclerViewTransactionStatement = view.findViewById(R.id.recycler_view_transaction_statement);
        recyclerViewTransactionStatement.setLayoutManager(new LinearLayoutManager(activity));
        transactionAdapter = new TransactionAdapter();
        recyclerViewTransactionStatement.setAdapter(transactionAdapter);
        recyclerViewTransactionStatement.addItemDecoration(new EqualSpacingItemDecoration((int) activity.getResources().getDimension(R.dimen.dp10), EqualSpacingItemDecoration.VERTICAL));

        paymentStatusLabel.setText(String.format("%s : ", activity.getString(R.string.status)));
        tvPaymentStatus.setTextColor(activity.getResources().getColor(R.color.green_new));
        tvPaymentStatus.setText(activity.getString(R.string.successfull));
    }

    private void hitTransactionStatementApi() {
        myNetworkCall.NetworkAPICall(API.API_TRANSACTION_STATEMENT, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.SUBSCRIPTION_CODE, orderHistoryData.getSubscription_code());
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        JSONObject jsonObject1 = jsonObject.getJSONObject(Const.DATA);
        JSONArray jsonArray = jsonObject1.optJSONArray(Const.STATEMENT);
        if (jsonArray != null && jsonArray.length() != 0) {
            llTransactionRoot.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            statementArrayList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                statement = gson.fromJson(jsonArray.optJSONObject(i).toString(), Statement.class);
                statementArrayList.add(statement);
            }
            transactionAdapter.notifyDataSetChanged();

            tvTitle.setText(jsonObject1.optString(Const.TITLE));

            String price = jsonObject1.optString(Const.NON_DAMS);
            String gstValue = "";

            if (!TextUtils.isEmpty(jsonObject1.optString(Const.GST_INCLUDE)) && jsonObject1.optString(Const.GST_INCLUDE).equals("1")) {
                tvPrice.setText(String.format("%s %s %s", getCurrencySymbol(), price, "/-"));
                gstValue = decimalFormat.format(Float.parseFloat(price) - getGSTExcludevalue(price));
                tvCutPrice.setText(String.format("%s + %s", String.format("%s %s %s", "(", getCurrencySymbol(), decimalFormat.format(getGSTExcludevalue(price))), String.format("%s %s %s", getCurrencySymbol(), gstValue, " (GST))")));
            } else {
                tvPrice.setText(String.format("%s%s/- + %s%% GST", getCurrencySymbol(), price, jsonObject1.optString(Const.GST)));
                tvCutPrice.setVisibility(View.GONE);
            }

            tvValidity.setText(String.format("Valid till : %s", Helper.getDate(Long.parseLong(statementArrayList.get(statementArrayList.size() - 1).getNextDate()))));
            tvDueDate.setText(Helper.getDate(Long.parseLong(statementArrayList.get(statementArrayList.size() - 1).getNextDate())));
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        GenericUtils.showToast(activity, jsonString);
    }

    class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

        @NonNull
        @Override
        public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(activity).inflate(R.layout.transaction_adapter_layout, viewGroup, false);
            return new TransactionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TransactionViewHolder transactionViewHolder, int i) {
            Statement statement = statementArrayList.get(i);
            transactionViewHolder.tvPaymentCount.setText(String.format("%s Installment", Helper.ordinalNo(i + 1)));
            transactionViewHolder.tvPayment.setText(String.format("%s %s %s", Helper.getCurrencySymbol(), statement.getAmountPaid(), "/-"));
            transactionViewHolder.tvInstallmentPaymentDate.setText(Helper.getDate(Long.parseLong(statement.getPaidOn())));
        }

        @Override
        public int getItemCount() {
            return statementArrayList.size();
        }

        class TransactionViewHolder extends RecyclerView.ViewHolder {

            TextView tvPaymentCount, tvPayment, tvInstallmentPaymentDate;

            public TransactionViewHolder(@NonNull View itemView) {
                super(itemView);

                tvPaymentCount = itemView.findViewById(R.id.tv_payment_count);
                tvPayment = itemView.findViewById(R.id.tv_payment);
                tvInstallmentPaymentDate = itemView.findViewById(R.id.tv_installment_payment_date);
            }
        }

    }
}
