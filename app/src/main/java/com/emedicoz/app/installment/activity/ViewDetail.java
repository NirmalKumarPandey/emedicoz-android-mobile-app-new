package com.emedicoz.app.installment.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.testseries.OrderHistoryData;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;

public class ViewDetail extends Fragment implements MyNetworkCall.MyNetworkCallBack {

    OrderHistoryData orderHistoryData;
    LinearLayout llPaymentType;
    LinearLayout llDueDate;
    LinearLayout llLateFee;
    LinearLayout llPaymentDue;
    LinearLayout llProductCode;
    LinearLayout llProductInfo;
    TextView tvTitle;
    TextView tvPrice;
    TextView tvCutPrice;
    TextView tvValidity;
    TextView tvOrderId;
    TextView tvPlanType;
    TextView tvUpcomingInstallmentDate;
    TextView tvDueAmount;
    TextView tvTotalDueAmount;
    TextView tvUpcomingInstallmentAmount;
    TextView tvInstallmentCount;
    TextView tvNote;
    TextView tvShowTransactionStatement;
    ImageView ivCoverImage;
    Activity activity;
    Button btnSwitchPlan, btn_;
    MyNetworkCall myNetworkCall;
    SingleCourseData comboCourse;
    ArrayList<SingleCourseData> comboCourseArrayList = new ArrayList<>();
    private static final String TAG = "ViewDetail";

    public static ViewDetail newInstance(OrderHistoryData orderHistoryData) {
        Bundle args = new Bundle();
        ViewDetail fragment = new ViewDetail();
        args.putSerializable(Const.ORDER_DETAIL, orderHistoryData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderHistoryData = (OrderHistoryData) getArguments().getSerializable(Const.ORDER_DETAIL);
        initViews(view);
//        hitComboApi();
    }

    private void initViews(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        llPaymentType = view.findViewById(R.id.ll_payment_type);
        llProductInfo = view.findViewById(R.id.ll_product_info);
        llDueDate = view.findViewById(R.id.ll_due_date);
        llLateFee = view.findViewById(R.id.ll_late_fee);
        llPaymentDue = view.findViewById(R.id.ll_payment_due);
        llProductCode = view.findViewById(R.id.ll_product_code);
        ivCoverImage = view.findViewById(R.id.iv_cover_image);
        tvTitle = view.findViewById(R.id.tv_title);
        tvPrice = view.findViewById(R.id.tv_price);
        tvCutPrice = view.findViewById(R.id.tv_cut_price);
        tvValidity = view.findViewById(R.id.tv_validity);
        tvOrderId = view.findViewById(R.id.tv_order_id);
        tvPlanType = view.findViewById(R.id.tv_plantype);

        tvUpcomingInstallmentDate = view.findViewById(R.id.tv_upcoming_installment_date);
        tvDueAmount = view.findViewById(R.id.tv_due_amount);
        tvTotalDueAmount = view.findViewById(R.id.tv_total_due_amount);
        tvUpcomingInstallmentAmount = view.findViewById(R.id.tv_upcoming_installment_amount);
        tvInstallmentCount = view.findViewById(R.id.tv_installment_count);
        tvNote = view.findViewById(R.id.tv_note);
        tvShowTransactionStatement = view.findViewById(R.id.tv_show_transaction_statement);
        btnSwitchPlan = view.findViewById(R.id.btn_switch_plan);
        btn_ = view.findViewById(R.id.btn_);

        llPaymentType.setVisibility(View.GONE);
        llDueDate.setVisibility(View.GONE);
        llLateFee.setVisibility(View.GONE);
        llPaymentDue.setVisibility(View.GONE);
        llProductCode.setVisibility(View.INVISIBLE);
        btnSwitchPlan.setVisibility(View.GONE);

        btn_.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_round_corner_fill_blue));
        btn_.setText(getString(R.string.continue_to_pay));

        if (!TextUtils.isEmpty(orderHistoryData.getIs_subscription()) && orderHistoryData.getIs_subscription().equals("1")) {
            llProductInfo.setVisibility(View.GONE);
            tvPlanType.setText(activity.getString(R.string.subscription));
        } else {
            llProductInfo.setVisibility(View.VISIBLE);
            tvPlanType.setText(activity.getString(R.string.installment));
        }

        if (!TextUtils.isEmpty(orderHistoryData.getUpcoming_emi_amount()) && !orderHistoryData.getUpcoming_emi_amount().equals("0")) {
            btn_.setVisibility(View.VISIBLE);
        } else {
            btn_.setVisibility(View.GONE);
        }

        btn_.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(orderHistoryData.getUpcoming_emi_amount()) && !orderHistoryData.getUpcoming_emi_amount().equals("0")) {
                activity.startActivity(new Intent(activity, CourseActivity.class).putExtra(Const.FRAG_TYPE, Const.INVOICE)
                        .putExtra(Const.ORDER_DETAIL, orderHistoryData));
            }
        });

        tvShowTransactionStatement.setOnClickListener(v -> activity.startActivity(new Intent(activity, CourseActivity.class).putExtra(Const.FRAG_TYPE, Const.TRANSACTION_STATEMENT)
                .putExtra(Const.ORDER_DETAIL, orderHistoryData)));

        setData();
    }

    private void setData() {
        Glide.with(activity).load(orderHistoryData.getCover_image()).apply(new RequestOptions().error(R.drawable.dams)).into(ivCoverImage);

        tvTitle.setText(orderHistoryData.getTitle());

        tvPrice.setText(String.format("%s %s %s", getCurrencySymbol(), orderHistoryData.getCourse_price(), "/-"));
        if (!TextUtils.isEmpty(orderHistoryData.getGst())) {
            tvCutPrice.setText(String.format("(%s %s + %s %s (GST))", getCurrencySymbol(), orderHistoryData.getNet_amt(), getCurrencySymbol(), orderHistoryData.getGst()));
        } else {
            tvCutPrice.setText("");
        }

        if (orderHistoryData.getIs_validity().equals("1")) {
            tvValidity.setVisibility(View.VISIBLE);
            tvValidity.setText(Helper.gaetFormatedDate(Long.parseLong(orderHistoryData.getValidity())));
        } else {
            tvValidity.setVisibility(View.INVISIBLE);
        }

        tvOrderId.setText(orderHistoryData.getPre_transaction_id());

        try {
            tvTotalDueAmount.setText(String.format("%s %s %s", getCurrencySymbol(), orderHistoryData.getUpcoming_emi_amount(), "/-"));

            tvUpcomingInstallmentDate.setText(String.format("Upcoming Installment on %s", Helper.getDate(Long.parseLong(orderHistoryData.getUpcoming_emi_date()))));
            tvDueAmount.setText(String.format("%s %s %s", getCurrencySymbol(), orderHistoryData.getUpcoming_emi_amount(), " /-"));

            tvUpcomingInstallmentAmount.setText(Helper.GetText(tvDueAmount));
            if (orderHistoryData.getPaymentMeta().getAmount_description().getPayment().size() > 1) {
                tvInstallmentCount.setText(String.format("(%s of %d Installments)", Helper.ordinalNo((Integer.parseInt(orderHistoryData.getPaymentMeta().getAmount_description().getEmi_paid_count()) + 1)), orderHistoryData.getPaymentMeta().getAmount_description().getPayment().size()));
            } else {
                tvInstallmentCount.setText(String.format("(%s of %d Installment)", Helper.ordinalNo((Integer.parseInt(orderHistoryData.getPaymentMeta().getAmount_description().getEmi_paid_count()) + 1)), orderHistoryData.getPaymentMeta().getAmount_description().getPayment().size()));
            }

            tvNote.setText(orderHistoryData.getNote());

        } catch (NullPointerException ex) {
            Log.e(TAG, "setData: " + ex.getLocalizedMessage());
        } catch (IndexOutOfBoundsException ie) {
            Log.e(TAG, "setData: " + ie.getLocalizedMessage());
        } catch (NumberFormatException ne) {
            Log.e(TAG, "setData: " + ne.getLocalizedMessage());
        }
    }

    private void hitComboApi() {
        myNetworkCall.NetworkAPICall(API.API_COMBO_PACKAGE, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        JSONObject dataJsonObject = GenericUtils.getJsonObject(jsonObject);
        JSONArray jsonArray = dataJsonObject.optJSONArray(Const.PACKAGES);
        if (jsonArray != null && jsonArray.length() != 0) {
            comboCourseArrayList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                comboCourse = new Gson().fromJson(jsonArray.optJSONObject(i).toString(), SingleCourseData.class);
                comboCourse.setGst(dataJsonObject.optString(Const.GST));
                comboCourse.setPoints_conversion_rate(dataJsonObject.optString(Const.POINTS_CONVERSION_RATE));
                comboCourseArrayList.add(comboCourse);
            }

        } else {
            errorCallBack(jsonObject.optString(Constants.Extras.MESSAGE), apiType);
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {

    }

}
