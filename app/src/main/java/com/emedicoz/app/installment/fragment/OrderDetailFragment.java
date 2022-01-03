package com.emedicoz.app.installment.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.feeds.activity.FeedsActivity;
import com.emedicoz.app.modelo.MyRewardPoints;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
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
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.Helper.calculateGST;
import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;
import static com.emedicoz.app.utilso.Helper.getGSTExcludevalue;

public class OrderDetailFragment extends Fragment implements View.OnClickListener, MyNetworkCall.MyNetworkCallBack, PurchasesUpdatedListener {

    TextView tvPlanType;
    TextView tvDownPaymentLabel;
    TextView tvTotalAmountLabel;
    TextView tvTotalAmount;
    TextView tvCourseFee;
    TextView tvHaveAReferralCode;
    TextView tvHaveACouponCode;
    TextView tvReferralDiscount;
    TextView tvCouponDiscount;
    TextView tvSpecialPrice;
    TextView tvGstLabel;
    TextView tvGstPrice;
    TextView tvDuePayment;
    TextView tvDownPayment;
    TextView initialEnrollmentAmount;
    SingleCourseData course;
    String type = "";
    String couponCode = "";
    String gstValue = "";
    String courseDiscount = "";
    String installmentValue = "";
    String paymentModeCheck = "";
    String paymentMetaValue = "";
    String paymentAttemptValue = "";
    String subscription_code = "";
    Activity activity;
    DecimalFormat decimalFormat = new DecimalFormat("0.##");
    Button btnOrderDetailContinue;
    Progress mProgress;
    private final String discountType = Const.COUPON;
    private LinearLayout llInvoiceDetailTitle;
    private LinearLayout llGst;
    private LinearLayout llReferralCode;
    private LinearLayout llCouponCode;
    private LinearLayout llDownPayment;
    private LinearLayout llDuePayment;
    private LinearLayout llReferralDiscount;
    int duePayment;
    private LinearLayout llCouponDiscount;
    MyNetworkCall myNetworkCall;
    private LinearLayout llSpecialPrice;
    private LinearLayout afterDiscountTB;
    private BillingClient billingClient;
    private final List<String> skuList = new ArrayList();
    private static final String TAG = "OrderDetailFragment";
    String expiry = "", coursePrice = "", specialPrice = "", gstOnSpecialPrice, gst = "", referralDiscount = "", couponDiscount = "", referralCode = "", discount_amount = "", couponType = "";
    EditText writeCouponET, couponCodeEt;
    ImageView iv_remove_referral;
    TextView iv_remove_coupon;
    LinearLayout coinsRedeemTB, llRemoveRedeemedCoin;
    RelativeLayout redeem_coins_layout;
    TextView coinsTextTV;
    Button redeemCoins;
    String str = "";
    String redeemPoints = "0";
    TextView coinRedeemValue;
    private MyRewardPoints rewardPoints;
    private boolean isRedeem = false;


    boolean discountApplied = false;
    TextView txtCouponStatusApply;
    TextView txtAfterDiscountValue;
    private static final DecimalFormat df2 = new DecimalFormat("#.##");


    public static OrderDetailFragment newInstance(SingleCourseData course) {
        Bundle args = new Bundle();
        OrderDetailFragment fragment = new OrderDetailFragment();
        args.putSerializable(Const.COURSE_DESC, course);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            course = (SingleCourseData) getArguments().getSerializable(Const.COURSE_DESC);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.order_detail_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        networkCallForRewardPoint();
        setupBillingClient();
    }

    private void initViews(View view) {
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        myNetworkCall = new MyNetworkCall(this, activity);
        tvPlanType = view.findViewById(R.id.tv_plan_type);
        tvTotalAmountLabel = view.findViewById(R.id.tv_total_amount_label);
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        tvDownPaymentLabel = view.findViewById(R.id.tv_down_payment_lable);
        tvCourseFee = view.findViewById(R.id.tv_course_fee);

        tvReferralDiscount = view.findViewById(R.id.tv_referral_discount);
        tvCouponDiscount = view.findViewById(R.id.tv_coupon_discount);
        iv_remove_referral = view.findViewById(R.id.iv_remove_referral);
        iv_remove_coupon = view.findViewById(R.id.iv_remove_coupon);
        tvSpecialPrice = view.findViewById(R.id.tv_special_price);

        tvGstLabel = view.findViewById(R.id.tv_gst_lable);
        tvGstPrice = view.findViewById(R.id.tv_gst_price);
        tvDuePayment = view.findViewById(R.id.tv_due_payment);
        tvDownPayment = view.findViewById(R.id.tv_down_payment);
        initialEnrollmentAmount = view.findViewById(R.id.initial_enrollment_amount);
//        tvTotalAmountLabel.setTextColor(ContextCompat.getColor(activity, R.color.blue));
//        tvTotalAmount.setTextColor(ContextCompat.getColor(activity, R.color.blue));
        llInvoiceDetailTitle = view.findViewById(R.id.ll_invoice_detail_title);
        llGst = view.findViewById(R.id.ll_gst);
        llReferralCode = view.findViewById(R.id.ll_referral_code);
        llCouponCode = view.findViewById(R.id.ll_coupon_code);
        llDownPayment = view.findViewById(R.id.ll_down_payment);
        llDuePayment = view.findViewById(R.id.ll_due_payment);
        llReferralDiscount = view.findViewById(R.id.ll_referral_discount);
        llCouponDiscount = view.findViewById(R.id.ll_coupon_discount);
        llSpecialPrice = view.findViewById(R.id.ll_special_price);
        tvHaveAReferralCode = view.findViewById(R.id.tv_have_a_referral_code);
        tvHaveACouponCode = view.findViewById(R.id.tv_have_a_coupon_code);
        btnOrderDetailContinue = view.findViewById(R.id.btn_order_detail_continue);
        txtCouponStatusApply = view.findViewById(R.id.txtCouponStatusApply);
        couponCodeEt = view.findViewById(R.id.couponCodeEt);

        coinsTextTV = view.findViewById(R.id.coinsTextTV);
        redeemCoins = view.findViewById(R.id.reddemCoinsBtn);
        coinsRedeemTB = view.findViewById(R.id.coinsRedeemTB);
        llRemoveRedeemedCoin = view.findViewById(R.id.llRemoveRedeemedCoin);
        coinRedeemValue = view.findViewById(R.id.coinRedeemValue);
        redeem_coins_layout = view.findViewById(R.id.redeem_coins_layout);
        afterDiscountTB = view.findViewById(R.id.afterDiscountTB);
        txtAfterDiscountValue = view.findViewById(R.id.txtAfterDiscountValue);
        redeem_coins_layout = view.findViewById(R.id.redeem_coins_layout);
        afterDiscountTB = view.findViewById(R.id.afterDiscountTB);
        txtAfterDiscountValue = view.findViewById(R.id.txtAfterDiscountValue);


        btnOrderDetailContinue.setOnClickListener(this);
        tvHaveAReferralCode.setOnClickListener(this);
        tvHaveACouponCode.setOnClickListener(this);
        iv_remove_referral.setOnClickListener(this);
        iv_remove_coupon.setOnClickListener(this);
        txtCouponStatusApply.setOnClickListener(this);
        redeemCoins.setOnClickListener(this);
        llRemoveRedeemedCoin.setOnClickListener(this);
//        llInvoiceDetailTitle.setVisibility(View.GONE);

        setOrderDetails();
    }

    private void setOrderDetails() {
        if (TextUtils.isEmpty(course.getIs_subscription())) {
            course.setIs_subscription("0");
        }

        for (int i = 0; i < course.getInstallment().size(); i++) {
            if (course.getInstallment().get(i).isSelected())
                expiry = course.getInstallment().get(i).getAmount_description().getExpiry();
        }
        llReferralDiscount.setVisibility(View.GONE);
        llCouponDiscount.setVisibility(View.GONE);
        llSpecialPrice.setVisibility(View.GONE);
        if (course.getIs_instalment().equals("1") && course.getIs_subscription().equals("1")) {
            llDuePayment.setVisibility(View.GONE);
            llDownPayment.setVisibility(View.GONE);
            tvPlanType.setText(activity.getString(R.string.subscription));
        } else {
            llDownPayment.setVisibility(View.VISIBLE);
            llDuePayment.setVisibility(View.VISIBLE);
            tvPlanType.setText(activity.getString(R.string.installment));
        }

//        llReferralCode.setVisibility(View.VISIBLE);
//        llCouponCode.setVisibility(View.VISIBLE);

        if (course.getIs_subscription().equals("1")) {
            for (int i = 0; i < course.getInstallment().size(); i++) {
                if (course.getInstallment().get(i).isSelected()) {
                    coursePrice = course.getInstallment().get(i).getAmount_description().getPayment().get(0);
                }
            }
        } else {
            coursePrice = !TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken()) ? course.getFor_dams() : course.getNon_dams();
        }


        gst = !GenericUtils.isEmpty(course.getGst()) ? course.getGst() : "18";

        if (TextUtils.isEmpty(course.getGst_include()) || course.getGst_include().equals("0")) {
            llGst.setVisibility(View.GONE);
            tvCourseFee.setText(String.format("%s %s", getCurrencySymbol(), coursePrice));
            tvGstLabel.setText(String.format("%s %s", "GST", "(" + gst + "%)"));

            gstValue = decimalFormat.format(Float.parseFloat(coursePrice) - getGSTExcludevalue(coursePrice));
            tvGstPrice.setText(String.format("%s %s", getCurrencySymbol(), gstValue));
            installmentValue = coursePrice;

            tvTotalAmount.setText(String.format("%s %s", getCurrencySymbol(), installmentValue));
        } else {
            llGst.setVisibility(View.VISIBLE);
            tvCourseFee.setText(String.format("%s %s", getCurrencySymbol(), coursePrice));
            tvGstLabel.setText(String.format("%s %s", "GST", "(" + gst + "%)"));
            gstValue = decimalFormat.format(calculateGST(coursePrice, gst));
            tvGstPrice.setText(String.format("%s %s", getCurrencySymbol(), gstValue));
            installmentValue = decimalFormat.format(Float.parseFloat(coursePrice) + (Float.parseFloat(coursePrice) * Integer.parseInt(gst)) / 100);
            tvTotalAmount.setText(String.format("%s %s", getCurrencySymbol(), installmentValue));

        }

      /*  if (course.getIs_subscription().equals("0")) {
            if (course.getGst_include().equals("0")) {
                tv_total_amount.setText(String.format("%s %s", getaCurrencySymbol(), coursePrice));
            } else if (course.getGst_include().equals("1")) {
                tv_total_amount.setText(String.format("%s %s", getCurrencySymbol(), String.valueOf(Integer.parseInt(!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())
                        ? course.getFor_dams() : course.getNon_dams()) + (Integer.parseInt(!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())
                        ? course.getFor_dams() : course.getNon_dams()) * Integer.parseInt(gst)) / 100)));
            }
        }*/

        for (int i = 0; i < course.getInstallment().size(); i++) {
            if (course.getInstallment().get(i).isSelected()) {
                if (course.getIs_subscription().equals("1")) {
                    tvDownPaymentLabel.setText("Course Duration");
                    initialEnrollmentAmount.setText(String.format("(%s)", course.getInstallment().get(i).getName()));
                } else {
                    tvDownPaymentLabel.setText("Down Payment");
                    initialEnrollmentAmount.setText("(Initial enrolment amount)");
                    installmentValue = course.getInstallment().get(i).getAmount_description().getPayment().get(0);
                }
                tvDownPayment.setText(String.format("%s %s", getCurrencySymbol(), installmentValue));
                paymentMetaValue = new Gson().toJson(course.getInstallment().get(i));
                paymentAttemptValue = String.valueOf((course.getInstallment().get(i).getAmount_description().getEmi_paid_count()));

                for (int j = 0; j < course.getInstallment().get(i).getAmount_description().getPayment().size(); j++) {
                    if (j > 0) {
                        duePayment += Integer.parseInt(course.getInstallment().get(i).getAmount_description().getPayment().get(j));
                    }
                }
            }
        }

        tvDuePayment.setText(String.format("%s %s", getCurrencySymbol(), duePayment));

        Log.e(TAG, "setOrderDetails: installmentvalue = " + installmentValue);
    }

    public String calculateGSTValue(String price) {
        int tempPrice = Integer.parseInt(price);
        return String.valueOf(tempPrice + ((tempPrice * Integer.parseInt(course.getGst())) / 100));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_remove_coupon:
                discountApplied = false;
                couponCode = "";
                discount_amount = "";
//                llCouponCode.setVisibility(View.VISIBLE);
                llCouponDiscount.setVisibility(View.GONE);
                llSpecialPrice.setVisibility(View.GONE);
                redeemCoins.setEnabled(true);

                tvGstPrice.setText(String.format("%s %S", getCurrencySymbol(), gstValue));
                installmentValue = decimalFormat.format(Float.parseFloat(coursePrice) + Float.parseFloat(gstValue));
                tvTotalAmount.setText(String.format("%s %s", getCurrencySymbol(), installmentValue));
                Log.e(TAG, "onClick: installmentvalue = " + installmentValue);
                break;
            case R.id.iv_remove_referral:
                discountApplied = false;
                referralCode = "";
                discount_amount = "";
                llSpecialPrice.setVisibility(View.GONE);
                llReferralDiscount.setVisibility(View.GONE);
                llReferralCode.setVisibility(View.VISIBLE);
                afterDiscountTB.setVisibility(View.GONE);

                tvGstPrice.setText(String.format("%s %S", getCurrencySymbol(), gstValue));
                installmentValue = decimalFormat.format(Float.parseFloat(coursePrice) + Float.parseFloat(gstValue));
                tvTotalAmount.setText(String.format("%s %s", getCurrencySymbol(), installmentValue));
                // updateGradnTotalPrice();

                Log.e(TAG, "onClick: installmentvalue = " + installmentValue);
                break;
            case R.id.tv_have_a_referral_code:
                if (!discountApplied) {
                    showReferralCodePopup(true);
                } else {
                    showAlertDialog();
                }
                break;
            case R.id.tv_have_a_coupon_code:
                if (!discountApplied) {
                    showReferralCodePopup(false);
                } else {
                    showAlertDialog();
                }
                break;
            case R.id.txtCouponStatusApply:
                if (!discountApplied) {
                    if (!TextUtils.isEmpty(Helper.GetText(couponCodeEt))) {
                        couponCode = Helper.GetText(couponCodeEt);
                        myNetworkCall.NetworkAPICall(API.API_APPLY_COUPON_CODE, true);
                    } else {
                        GenericUtils.showToast(activity, activity.getString(R.string.enter_coupon_code_to_get_discount));
                    }
                } else {
                    showAlertDialog();
                }
                break;
            case R.id.btn_order_detail_continue:
                if (installmentValue.equals("0")) {   // To make the transaction in case of after applying coupon & Points and made it free
                    networkCallForFreeCourseTransaction();//NetworkAPICall(API.API_MAKE_FREE_COURSE_TRANSACTION, true);
                } else if (Double.parseDouble(installmentValue) < 1) {
                    Toast.makeText(activity, "Not a valid amount we cannot proceed", Toast.LENGTH_SHORT).show();
                } else {  // To make the transaction in case of after applying coupon & Points for some Amount of price.

                    if (getCurrencySymbol().equals(activity.getResources().getString(R.string.rs))) {
                        showPaymentModePopup();
                    } else {
                        showConversion();
                    }
                }
                break;
            case R.id.btn_continue:
                Dialog paymentDialog = (Dialog) v.getTag(R.id.questions);
                View viewTag = (View) v.getTag(R.id.optionsAns);
                RadioGroup radioGroup = viewTag.findViewById(R.id.radioGroupPay);

                if (radioGroup != null && radioGroup.getCheckedRadioButtonId() != -1) {
                    RadioButton rb = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                    if (rb.getText().equals(activity.getString(R.string.paytm))) {
                        paymentModeCheck = Const.PAYTM;
                    } else if (rb.getText().equals(activity.getString(R.string.in_app_purchase))) {
                        paymentModeCheck = Const.ANIN;
                    }
                    paymentDialog.dismiss();
                    initializeCoursePayment();
                } else {
                    Toast.makeText(activity, getString(R.string.paymentModeSelcetion), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reddemCoinsBtn:
                coinRedemption();
                break;
            case R.id.llRemoveRedeemedCoin:
                coinsRedeemTB.setVisibility(View.GONE);
                setRedeemText(rewardPoints.getReward_points());
                redeemCoins.setOnClickListener(this);
                redeemCoins.setText(activity.getString(R.string.redeem));
                isRedeem = false;
                afterDiscountTB.setVisibility(View.GONE);

                tvGstPrice.setText(String.format("%s %S", getCurrencySymbol(), gstValue));
                installmentValue = decimalFormat.format(Float.parseFloat(coursePrice) + Float.parseFloat(gstValue));
                tvTotalAmount.setText(String.format("%s %s", getCurrencySymbol(), installmentValue));

                couponCodeEt.setEnabled(true);
                txtCouponStatusApply.setEnabled(true);

//                installmentValue = grandTotalValue;
                /*if (Const.COUPON.equals(discountType)) {

                    if (Const.COUPON.equals(discountType)) {
                        if (!TextUtils.isEmpty(couponCode))
                            couponCode = Helper.GetText(couponCodeEt);
                        myNetworkCall.NetworkAPICall(API.API_APPLY_COUPON_CODE, true);
                    } else if (Const.REFERRAl.equals(discountType)) {
                        if (!TextUtils.isEmpty(referralCode))
                            myNetworkCall.NetworkAPICall(API.API_GET_REFERRAL_CODE_VALID, true);
                    } else {
                        tvTotalAmount.setText(String.format("%s %s", getCurrencySymbol(), installmentValue));
                    }

                    updateGradnTotalPrice();
                }*/
                break;
        }
    }

    private void networkCallForFreeCourseTransaction() {
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.makeFreeCourseTransaction(SharedPreference.getInstance().getLoggedInUser().getId(),
                course.getPoints_conversion_rate(), "0", "0", couponCode, type.equals(Const.CPR_INVOICE) ? "CPR_" + course.getId() : course.getId(), "0");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    mProgress.dismiss();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (Objects.requireNonNull(jsonResponse).optString("status").equals(Const.TRUE) && !type.equals(Const.CPR_INVOICE)) {
                        Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        if (!type.equals(Const.CPR_INVOICE)) {
//                                Intent myCourse = new Intent(activity, CourseActivity.class);
//                                myCourse.putExtra(Const.FRAG_TYPE, Const.MYCOURSES);
                            Intent myCourse = new Intent(activity, FeedsActivity.class);
                            myCourse.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES);
                            activity.startActivity(myCourse);
                        }
                        activity.finish();
                    } else {
                        errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_MAKE_FREE_COURSE_TRANSACTION);
                        RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_MAKE_FREE_COURSE_TRANSACTION, activity, 1, 1);
            }
        });
    }

    private void showAlertDialog() {
        Helper.newCustomDialog(activity,
                "Alert",
                activity.getString(R.string.out_of_referral_discount_and_coupon_discount_you_can_avail_only_one_discount_at_a_time),
                true,
                activity.getString(R.string.ok),
                activity.getResources().getDrawable(R.drawable.bg_round_corner_fill_green));
    }

    private void showPaymentModePopup() {
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View promptsView = li.inflate(R.layout.dialog_payment, null, false);
        final Dialog paymentDialog = new Dialog(activity);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setCanceledOnTouchOutside(true);
        paymentDialog.setContentView(promptsView);
        paymentDialog.show();

        final AppCompatRadioButton radio_btn_inApp;
        radio_btn_inApp = promptsView.findViewById(R.id.radio_btn_inApp);
        TextView tv = promptsView.findViewById(R.id.titleDialog);
        tv.setText(getString(R.string.choose_payment_, installmentValue));

        final Button btn_continue = promptsView.findViewById(R.id.btn_continue);

        if (!TextUtils.isEmpty(SharedPreference.getInstance().getMasterHitResponse().getAndroid_inapp())) {
            if (SharedPreference.getInstance().getMasterHitResponse().getAndroid_inapp().equals("1")) {
                radio_btn_inApp.setVisibility(View.VISIBLE);
            } else {
                radio_btn_inApp.setVisibility(View.GONE);
            }
        } else {
            radio_btn_inApp.setVisibility(View.GONE);
        }

        btn_continue.setTag(R.id.questions, paymentDialog);
        btn_continue.setTag(R.id.optionsAns, promptsView);
        btn_continue.setTag(paymentDialog);
        btn_continue.setOnClickListener(this);
    }

    private void showConversion() {
        View view = Helper.newCustomDialog(activity, true, "Alert", activity.getString(R.string.amount_wil_be_show_deducted_in_inr));
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Button btn_submit = view.findViewById(R.id.btn_submit);
        btn_submit.setText(activity.getString(R.string.continueText));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) activity.getResources().getDimension(R.dimen.dp120), (int) activity.getResources().getDimension(R.dimen.dp35));
        params.setMargins(0, (int) activity.getResources().getDimension(R.dimen.dp20), 0, (int) activity.getResources().getDimension(R.dimen.dp20));
        btn_cancel.setLayoutParams(params);
        btn_submit.setLayoutParams(params);

        btn_cancel.setOnClickListener(v -> Helper.dismissDialog());

        btn_submit.setOnClickListener(v -> {
            Helper.dismissDialog();
            showPaymentModePopup();
        });
    }

    private void showReferralCodePopup(boolean isReferral) {
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View promptsView = li.inflate(R.layout.dialog_coupon, null, false);
        final Dialog couponDialog = new Dialog(activity);
        couponDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        couponDialog.setCanceledOnTouchOutside(true);
        couponDialog.setContentView(promptsView);
        couponDialog.show();

        TextView profileName = promptsView.findViewById(R.id.profileName);
        writeCouponET = promptsView.findViewById(R.id.writeCouponET);

        final Button submit = promptsView.findViewById(R.id.btn_submit);
        final Button cancel = promptsView.findViewById(R.id.btn_cancel);

        if (isReferral) {
            profileName.setText(activity.getString(R.string.add_referral_code));
            writeCouponET.setHint(activity.getString(R.string.enter_your_referral_code));
        } else {
            profileName.setText(activity.getString(R.string.add_coupon));
            writeCouponET.setHint(activity.getString(R.string.enter_your_coupon_code));
        }

        submit.setTag(R.id.questions, couponDialog);
        submit.setTag(R.id.optionsAns, promptsView);
        cancel.setTag(couponDialog);
        submit.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(Helper.GetText(writeCouponET))) {
                if (isReferral) {
                    referralCode = Helper.GetText(writeCouponET);
                    myNetworkCall.NetworkAPICall(API.API_GET_REFERRAL_CODE_VALID, true);
                } else {
                    couponCode = Helper.GetText(writeCouponET);
                    myNetworkCall.NetworkAPICall(API.API_APPLY_COUPON_CODE, true);
                }
                couponDialog.dismiss();
            } else {
                if (isReferral) {
                    Toast.makeText(activity, "Enter referral code to get Discount.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Enter coupon code to get Discount.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(v -> {
            couponDialog.dismiss();
        });
    }

    private void initializeCoursePayment() {
        myNetworkCall.NetworkAPICall(API.API_INITIALIZE_COURSE_PAYMENT, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        switch (apiType) {
            case API.API_APPLY_COUPON_CODE:
                params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
                params.put(Const.COURSE_ID, type.equals(Const.CPR_INVOICE) ? "CPR_" + course.getId() : course.getId());
                params.put(Const.COUPON_CODE, couponCode);
                break;
            case API.API_GET_REFERRAL_CODE_VALID:
                params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
                params.put(Const.AFFILIATE_REFERRAL_CODE, referralCode);
                break;
            case API.API_GET_REFERRAL_DISCOUNT:
                params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
                params.put(Const.COURSE_ID, course.getId());
                break;
            case API.API_INITIALIZE_COURSE_PAYMENT:
                params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
                params.put(Const.COURSE_ID, course.getId());
                params.put(Const.EXPIRY, !TextUtils.isEmpty(expiry) ? expiry : "");
                params.put(Const.POINTS_RATE, course.getPoints_conversion_rate());
                params.put(Const.TAX, course.getGst());
                params.put(Const.POINTS_USED, "0");
                params.put(Const.PAYMENT_MODE, "1");
                params.put(Const.PAYMENT_META, paymentMetaValue);
                params.put(Const.PAYMENT_ATTEMPT, paymentAttemptValue);
                params.put(Const.IS_COMLETE_PAYMENT, "");
                params.put(Const.COUPON_APPLIED, couponCode);
                params.put("refral_code", referralCode);
                params.put("subscription_code", "");
                params.put("switch_plan", "");
                params.put("penalty", "0");
                params.put(Const.CHILD_COURSES, !TextUtils.isEmpty(course.getChild_courses()) ? course.getChild_courses() : "");
                params.put(Const.PAY_VIA, paymentModeCheck);
                params.put(Const.COURSE_PRICE, installmentValue);
                break;
            case API.API_COMPLETE_COURSE_PAYMENT:
                params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
                params.put(Const.COURSE_ID, course.getId());
                params.put(Const.COURSE_DISCOUNT, courseDiscount);
                params.put(Const.COURSE_PRICE, installmentValue);
                params.put(Const.COURSE_INIT_PAYMENT_TOKEN, SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
                params.put(Const.COURSE_FINAL_PAYMENT_TOKEN, SharedPreference.getInstance().getString(Const.COURSE_FINAL_PAYMENT_TOKEN));
                if (course.getIs_subscription().equals("1")) {
                    params.put(Const.AFFILIATE_REFERRAL_CODE_BY, !TextUtils.isEmpty(referralCode) ? referralCode : "");
                }
                break;
            case API.API_UPDATE_TRANSACTION_INFO:
                params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
                params.put(Const.COURSE_ID, course.getId());
                params.put(Const.COURSE_INIT_PAYMENT_TOKEN, SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
                params.put(Const.COURSE_FINAL_PAYMENT_TOKEN, SharedPreference.getInstance().getString(Const.COURSE_FINAL_PAYMENT_TOKEN));
                params.put("subscription_code", subscription_code);
                params.put("total_paid", installmentValue);
                break;
            case API.API_FINAL_TRANS_FOR_PAYTM:
                return service.get(String.format(apiType, Const.PAYTM_MID,
                        SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                        Const.SERVER_TYPE));
        }
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        switch (apiType) {
            case API.API_APPLY_COUPON_CODE:
                couponType = jsonObject.getJSONObject(Const.DATA).getString("coupon_type");
                discount_amount = jsonObject.getJSONObject(Const.DATA).getString("coupon_value");
                if (!TextUtils.isEmpty(couponType) && !TextUtils.isEmpty(discount_amount)) {
                    discountApplied = true;
//                    llCouponCode.setVisibility(View.GONE);

                    llCouponDiscount.setVisibility(View.VISIBLE);
                    llSpecialPrice.setVisibility(View.GONE);
                    afterDiscountTB.setVisibility(View.GONE);
                    redeemCoins.setEnabled(false);

                    iv_remove_coupon.setText(String.format("(%s)", couponCodeEt.getText()));

                    specialPrice = "";
                    gstOnSpecialPrice = "";

                    if (couponType.equals("1")) {
                        tvCouponDiscount.setText(String.format("%s %S %s", "- ", getCurrencySymbol(), decimalFormat.format(Float.parseFloat(discount_amount))));
                    } else {
                        tvCouponDiscount.setText(String.format("%s %S %s", "- ", getCurrencySymbol(), decimalFormat.format(Helper.calculateGST(coursePrice, discount_amount))));
                    }

                    specialPrice = decimalFormat.format(getDiscountedValue(couponType, coursePrice, discount_amount));
                    gstOnSpecialPrice = decimalFormat.format(calculateGST(specialPrice, gst));

                    tvSpecialPrice.setText(String.format("%s %S", getCurrencySymbol(), specialPrice));
                    tvGstPrice.setText(String.format("%S %S", getCurrencySymbol(), gstOnSpecialPrice));


                    installmentValue = decimalFormat.format(Float.parseFloat(specialPrice) + Float.parseFloat(gstOnSpecialPrice));
                    tvTotalAmount.setText(String.format("%s %s", getCurrencySymbol(), installmentValue));
                    txtAfterDiscountValue.setText(String.format("%s %s", getCurrencySymbol(), installmentValue));

                    updateGradnTotalPrice();
                    Log.e(TAG, "showReferralCodePopup: installmentvalue = " + installmentValue);
                }
                break;
            case API.API_GET_REFERRAL_CODE_VALID:
                if (jsonObject.optString(Const.DATA).equals(Helper.GetText(writeCouponET))) {
                    referralCode = Helper.GetText(writeCouponET);
                    myNetworkCall.NetworkAPICall(API.API_GET_REFERRAL_DISCOUNT, true);
                }
                break;
            case API.API_GET_REFERRAL_DISCOUNT:
                discount_amount = jsonObject.optJSONObject(Const.DATA).optString(Const.DISCOUNT_AMOUNT);
                if (!discount_amount.equals("0")) {
                    discountApplied = true;
                    llReferralCode.setVisibility(View.GONE);
                    llReferralDiscount.setVisibility(View.VISIBLE);
                    llSpecialPrice.setVisibility(View.GONE);

                    specialPrice = "";
                    gstOnSpecialPrice = "";

                    tvReferralDiscount.setText(String.format("%s %S %s", "- ", getCurrencySymbol(), decimalFormat.format(Helper.calculateGST(coursePrice, discount_amount))));

                    specialPrice = decimalFormat.format(getDiscountedValue("2", coursePrice, discount_amount));
                    gstOnSpecialPrice = decimalFormat.format(calculateGST(specialPrice, gst));

                    tvSpecialPrice.setText(String.format("%s %S", getCurrencySymbol(), specialPrice));
                    tvGstPrice.setText(String.format("%S %S", getCurrencySymbol(), gstOnSpecialPrice));

                    installmentValue = decimalFormat.format(Float.parseFloat(specialPrice) + Float.parseFloat(gstOnSpecialPrice));
                    tvTotalAmount.setText(String.format("%s %s", getCurrencySymbol(), installmentValue));

                    Log.e(TAG, "successCallBack: installmentvalue = " + installmentValue);
                }
                break;
            case API.API_INITIALIZE_COURSE_PAYMENT:
                JSONObject data = jsonObject.optJSONObject(Const.DATA);
                SharedPreference.getInstance().putString(Const.COURSE_INIT_PAYMENT_TOKEN, Objects.requireNonNull(data).optString("pre_transaction_id"));
                // only for testing static data nimesh
                /*SharedPreference.getInstance().putString(Const.COURSE_FINAL_PAYMENT_TOKEN, String.valueOf(UUID.randomUUID()));
                finalTransForPaytm();
                API_UPDATE_TRANSACTION_INFO();
                completeCoursePayment();*/
                // only for testing static data

                paymentCallbacks(); // live
                break;
            case API.API_COMPLETE_COURSE_PAYMENT:
                Toast.makeText(activity, jsonObject.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                if (!type.equals(Const.CPR_INVOICE)) {
//                    Intent myCourse = new Intent(activity, CourseActivity.class);
//                    myCourse.putExtra(Const.FRAG_TYPE, Const.MYCOURSES);

                    Intent myCourse = new Intent(activity, FeedsActivity.class);
                    myCourse.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES);
                    activity.startActivity(myCourse);
                }
                activity.finish();
                break;
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        GenericUtils.showToast(activity, jsonString);
    }

    private Float getDiscountedValue(String mCouponType, String mPrice, String mDiscount) {
        float finalPrice;
        if (mCouponType.equals("1")) {
            if ((Float.parseFloat(mPrice) - Float.parseFloat(mDiscount)) <= 0) {
                finalPrice = 0f;
            } else {
                finalPrice = (Float.parseFloat(mPrice) - Float.parseFloat(mDiscount));
            }
        } else {
            if (Float.parseFloat(mPrice) - (Float.parseFloat(mPrice) * Float.parseFloat(mDiscount)) / 100.0 <= 0) {
                finalPrice = 0f;
            } else {
                finalPrice = (float) (Float.parseFloat(mPrice) - (Float.parseFloat(mPrice) * Float.parseFloat(mDiscount)) / 100.0);
            }
        }
        return finalPrice;
    }

    private void paymentCallbacks() {
        if (!TextUtils.isEmpty(paymentModeCheck)) {
            switch (paymentModeCheck) {
                case Const.ANIN:
                    if (billingClient.isReady()) {
                        Log.e(TAG, "onClick: Billing Client Ready");
                        Log.e(TAG, "onClick: " + course.getId());
                        skuList.clear();
                        skuList.add(course.getId());
                        loadAllSKUs();
                    }
                    break;
                case "PAY_TM":
                    networkCallForCheckSumForPayTm();
                    break;
            }
        }
    }

    private void networkCallForCheckSumForPayTm() {
        mProgress.show();
        installmentValue = df2.format(Float.parseFloat(installmentValue));

        Call<JsonObject> response;
        ApiInterface apiInterface = ApiClient.createService2(ApiInterface.class);
        if (Const.SERVER_TYPE.equals("LIVE")) {
            response = apiInterface.getCheckSumForPaytmLive(String.format(API.API_GET_CHECKSUM_FOR_PAYTM,
                    Const.SERVER_TYPE), Const.PAYTM_MID, SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                    Const.INDUSTRYTYPE_ID, SharedPreference.getInstance().getLoggedInUser().getUser_registration_info().getId(),
                    Const.CHANNELID, installmentValue, Const.PAYTM_WEBSITE,
                    Const.CALLBACKURL + SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
        } else {
            response = apiInterface.getCheckSumForPaytm(String.format(API.API_GET_CHECKSUM_FOR_PAYTM,
                    Const.SERVER_TYPE), Const.PAYTM_MID, SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                    Const.INDUSTRYTYPE_ID, SharedPreference.getInstance().getLoggedInUser().getUser_registration_info().getId(),
                    Const.CHANNELID, installmentValue, Const.PAYTM_WEBSITE,
                    Const.CALLBACKURL + SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
        }
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (Objects.requireNonNull(jsonResponse).has("CHECKSUMHASH")) {
                            SharedPreference.getInstance().putString(Const.CHECK_SUM, jsonResponse.optString("CHECKSUMHASH"));
                            makePaytmTransaction();
                            //region Dummy transaction testing
//                            SharedPreference.getInstance().putString(Const.COURSE_FINAL_PAYMENT_TOKEN,
//                            "DUMMY_FINAL_TXN_ID_" + SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
//                        networkCallForFinalTransForPaytm()
//                            completeCoursePayment();
                            //endregion
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_CHECKSUM_FOR_PAYTM);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_CHECKSUM_FOR_PAYTM, activity, 0, 0);
            }
        });
    }

    private void makePaytmTransaction() {
        PaytmPGService Service;

        if (Const.SERVER_TYPE.equals("LIVE")) {
            Service = PaytmPGService.getProductionService();
        } else {
            Service = PaytmPGService.getStagingService("");
        }

        //Kindly create complete Map and checksum on your server side and then put it here in paramMap.

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(Const.MID, Const.PAYTM_MID);
        paramMap.put(Const.ORDER_ID, SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
        paramMap.put(Const.CUST_ID, SharedPreference.getInstance().getLoggedInUser().getUser_registration_info().getId());
        paramMap.put(Const.INDUSTRY_TYPE_ID, Const.INDUSTRYTYPE_ID);
        paramMap.put(Const.CHANNEL_ID, Const.CHANNELID);
        paramMap.put(Const.TXN_AMOUNT, installmentValue);
        paramMap.put(Const.WEBSITE, Const.PAYTM_WEBSITE);
        paramMap.put(Const.CALLBACK_URL, Const.CALLBACKURL + SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
//        paramMap.put(Const.MOBILE_NO, SharedPreference.getInstance().getLoggedInUser().getMobile());
//        paramMap.put(Const.EMAIL_PAYTM, SharedPreference.getInstance().getLoggedInUser().getEmail());
        paramMap.put(Const.CHECKSUMHASH, SharedPreference.getInstance().getString(Const.CHECK_SUM));
        PaytmOrder Order = new PaytmOrder(paramMap);

        Service.initialize(Order, null);

        Service.startPaymentTransaction(activity, true, true,
                new PaytmPaymentTransactionCallback() {

                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                        Log.e("SOMEUI ERROR", inErrorMessage);
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        if (inResponse == null) return;
                        Log.d("LOG", "Payment Transaction : " + inResponse);
//                        Toast.makeText(activity, "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                        switch (inResponse.getString("STATUS")) {
                            case "TXN_FAILURE":
                                Toast.makeText(activity, inResponse.getString("RESPMSG"), Toast.LENGTH_SHORT).show();
                                break;
                            case "TXN_SUCCESS":
                                SharedPreference.getInstance().putString(Const.COURSE_FINAL_PAYMENT_TOKEN, inResponse.getString("TXNID"));
                                networkCallForFinalTransForPaytm();
                                completeCoursePayment();
                                apiUpdateTransactionInfo();
                                break;
                        }
                    }

                    @Override
                    public void networkNotAvailable() {
                        // If network is not
                        // available, then this
                        // method gets called.
                    }

                    @Override
                    public void onErrorProceed(String s) {

                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                        Log.e("clientAuthentication", inErrorMessage);
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Log.e("onErrorLoadingWebPage", inErrorMessage + "" + iniErrorCode);

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(activity, "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void networkCallForFinalTransForPaytm() {
       /* mProgress.show();
        CourseInvoiceApiInterface apiInterface = ApiClient.createService2(CourseInvoiceApiInterface.class);
        Call<JsonObject> response = apiInterface.finalTransactionForPaytm(String.format("Paytm/TxnStatus.php?MID=%s&ORDERID=%s&SERVER=%s", Const.PAYTM_MID,
                SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                Const.SERVER_TYPE));
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();

            }
        });*/
    }

    private void completeCoursePayment() {
        myNetworkCall.NetworkAPICall(API.API_COMPLETE_COURSE_PAYMENT, true);
    }

    private void apiUpdateTransactionInfo() {
        myNetworkCall.NetworkAPICall(API.API_UPDATE_TRANSACTION_INFO, true);
    }

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(activity).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is setup successfully
//                    Toast.makeText(PurchaseActivity.this, "The BillingClient is setup successfully", Toast.LENGTH_SHORT).show();
//                    loadAllSKUs();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    private void loadAllSKUs() {
        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build();

        billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !skuDetailsList.isEmpty()) {
                for (Object skuDetailsObject : skuDetailsList) {
                    final SkuDetails skuDetails = (SkuDetails) skuDetailsObject;

                    if (skuDetails.getSku().equals(course.getId())) {
                        boolean isOwned = false;
                        if (!isOwned) {
                            BillingFlowParams billingFlowParams = BillingFlowParams
                                    .newBuilder()
                                    .setSkuDetails(skuDetails)
                                    .build();
                            billingClient.launchBillingFlow(activity, billingFlowParams);
                        }
                    }
                }
            } else {
                Toast.makeText(activity, "Product not found.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onSkuDetailsResponse: Empty SKU Detail List");
            }
        });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        int responseCode = billingResult.getResponseCode();
        if (responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                acknowledgePurchase(purchase);
            }
        } else if (responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Toast.makeText(activity, "Payment cancelled", Toast.LENGTH_SHORT).show();
        } else if (responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Toast.makeText(activity, "Already Purchased", Toast.LENGTH_SHORT).show();
        } else {
            //Log.d(TAG, "Other code" + responseCode);
            // Handle any other error codes.
        }
    }

    private void acknowledgePurchase(final Purchase purchase) {
        if (purchase.getSku().equals(course.getId()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        SharedPreference.getInstance().putString(Const.COURSE_FINAL_PAYMENT_TOKEN, purchase.getOrderId());
                        networkCallForFinalTransForPaytm();
                        completeCoursePayment();
                        apiUpdateTransactionInfo();
                    }
                });
            }
        }
    }

    private void networkCallForRewardPoint() {
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getRewardPoints(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgress.dismiss();
                    if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                        Helper.showErrorLayoutForNoNav(API.API_GET_REWARD_POINTS, activity, 0, 0);
                        rewardPoints = new Gson().fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), MyRewardPoints.class);
                        setRedeemText(rewardPoints.getReward_points());

                        if (rewardPoints.getReward_points().equals("0")) {
                            redeem_coins_layout.setVisibility(View.GONE);
                        } else {
                            redeem_coins_layout.setVisibility(View.VISIBLE);
                        }

                    } else if (!jsonResponse.optBoolean(Const.STATUS)) {
                        setRedeemText("0");
                        RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                    } else {
                        errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_REWARD_POINTS);
                        RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                    }
                } else {
                    mProgress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_REWARD_POINTS, activity, 1, 1);
            }
        });
    }

    private void coinRedemption() {
        if (!installmentValue.equals("0")) {
            str = calculateRedeemCoinsValue();
            if (!str.split(",")[1].equals("0")) {
                if (str.contains(",")) {
                    isRedeem = true;
                    afterDiscountTB.setVisibility(View.GONE);
                    setRedeemText(String.valueOf(Integer.parseInt(rewardPoints.getReward_points()) - Integer.parseInt(str.split(",")[0])));
                    coinsRedeemTB.setVisibility(View.VISIBLE);
                    coinRedeemValue.setText(String.format("%s %s %s", "-", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(str.split(",")[1])));
                    installmentValue = df2.format(Float.parseFloat(installmentValue) - Integer.parseInt(str.split(",")[1]));
                    tvTotalAmount.setText(String.format("%s %s", getCurrencySymbol(), (Float.parseFloat(installmentValue) < 0) ? "0" : Helper.calculatePriceBasedOnCurrency(installmentValue)));
                    txtAfterDiscountValue.setText(String.format("%s %s", getCurrencySymbol(), installmentValue));
                    redeemPoints = str.split(",")[0];
                    redeemCoins.setText(activity.getString(R.string.redeemed));
                    redeemCoins.setOnClickListener(null);
                    couponCodeEt.setEnabled(false);
                    txtCouponStatusApply.setEnabled(false);
                }
            } else {
                coinsRedeemTB.setVisibility(View.GONE);
                Helper.newCustomDialog(activity,
                        "Alert",
                        activity.getString(R.string.you_do_not_have_enough_point_to_redeem),
                        true,
                        activity.getString(R.string.ok),
                        ContextCompat.getDrawable(activity, R.drawable.bg_round_corner_fill_green));
            }
        } else {
            Toast.makeText(activity, "Course have already at the lower Price", Toast.LENGTH_SHORT).show();
        }
    }

    private String calculateRedeemCoinsValue() {
        int coinsValue = 0;
        int convertionRate = 100;
        int redeempoints;
        int finalprice;

        if (!GenericUtils.isEmpty(course.getPoints_conversion_rate()))
            convertionRate = Integer.parseInt(course.getPoints_conversion_rate());
        if (rewardPoints != null && !GenericUtils.isEmpty(rewardPoints.getReward_points()))
            coinsValue = Integer.parseInt(rewardPoints.getReward_points());

        try {
            if ((coinsValue / convertionRate) > Integer.parseInt(installmentValue)) {
                finalprice = Integer.parseInt(installmentValue);
                redeempoints = Integer.parseInt(installmentValue) * convertionRate;
            } else {
                finalprice = coinsValue / convertionRate;
                redeempoints = coinsValue - (coinsValue % convertionRate);
            }
        } catch (NumberFormatException e) {
            finalprice = coinsValue / convertionRate;
            redeempoints = coinsValue - (coinsValue % convertionRate);
        }

        return redeempoints + "," + finalprice;
    }

    private void updateGradnTotalPrice() {
//        installmentValue = grandTotalValue;
        if (isRedeem) {
            coinRedemption();
        } else {
            tvTotalAmount.setText(String.format("%S %S", getCurrencySymbol(), installmentValue));
        }
    }


    private void setRedeemText(String rewardPoint) {
        coinsTextTV.setText(String.format("You have %s points to redeem. \n ( %s points = %s 1).", rewardPoint, course.getPoints_conversion_rate(), getCurrencySymbol()));
    }
}
