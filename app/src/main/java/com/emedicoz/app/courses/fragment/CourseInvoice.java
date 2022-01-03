package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.adapter.InstallmentParentAdapter;
import com.emedicoz.app.courses.callback.OnSubscriptionItemClickListener;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.feeds.activity.FeedsActivity;
import com.emedicoz.app.installment.model.Installment;
import com.emedicoz.app.modelo.MyRewardPoints;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
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
import static com.emedicoz.app.utilso.Helper.printError;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseInvoice extends Fragment implements View.OnClickListener, PurchasesUpdatedListener, MyNetworkCall.MyNetworkCallBack {

    private static final String TAG = "CourseInvoice";
    TextView txtLearnerValue;
    TextView txtLearner;
    TextView txtCourseType;
    TextView txtCourseTypeValue;
    TextView txtPriceValue;
    private final List<String> skuList = new ArrayList();
    TextView txtCouponStatus;
    TextView txtMrpValue;
    TextView txtReferralStatus;
    TextView txtCouponStatusApply;
    TextView txtCoupon_Status;
    TextView txtReferral_Status;
    TextView txtTotalValue;
    TextView coinsTextTV;
    TextView txtDiscountValue;
    TextView txtGrandTotalValue;
    TextView courseNameTV, courseNameView;
    TextView ratingTV;
    TextView txtServiceTax;
    TextView txtServiceTaxValue;
    TextView coinRedeemValue;
    TextView courseSummaryTV;
    CardView courseCard;
    RatingBar ratingRB;
    TextView txtReferralStatusApply;
    ShapeableImageView imageIV;
    String fragType = "";
    String couponCode = "";
    String referralCode = "";
    String coupon = "";
    String finalPriceValue = "0";
    String grandTotalValue = "";
    String price = "";
    String str = "";
    String redeemPoints = "0";
    String paymentModeCheck = "";
    String type = "";
    int courseDiscount = 0;
    SingleCourseData course;
    Button btnProceed, redeemCoins;
    Activity activity;
    Progress mProgress;
    private MyRewardPoints rewardPoints;
    private BillingClient billingClient;
    LinearLayout coinsRedeemTB, gstTB, discountTB;
    LinearLayout choosePaymentTypeLL, continueBtnLL, llRemoveRedeemedCoin;
    TextView tvOneTime, tvInitialEnrollmentAmount, tvInstallmentPlan;
    NestedScrollView layoutSubscriptionPlan;
    RelativeLayout layoutSinglePayment;
    String paymentType = Const.ONE_TIME, discountType = "", couponType = "";
    RecyclerView recyclerViewInstallmentParent;
    TextView tvInvoiceDetail, tvInstallmentDuration, tvMrp, tvCutPrice;
    DecimalFormat decimalFormat = new DecimalFormat("0.##");
    String gstValue, installmentValue, expiry = "", discountAmount = "";
    EditText writeCouponET;
    EditText couponCodeEt, referralCodeEt;
    LinearLayout couponLayout, referralLayout, couponAppliedLayout, referralAppliedLayout, afterDiscountTB;
    private MyNetworkCall myNetworkCall;
    private boolean discountApplied = false, isRedeem = false;
    TextView couponAppliedText, txtAfterDiscountValue;
    TextView tv_course_category;
    private static final DecimalFormat df2 = new DecimalFormat("#.##");


    public CourseInvoice() {
        // Required empty public constructor
    }

    public static CourseInvoice newInstance(SingleCourseData course, String type) {
        CourseInvoice courseInvoice = new CourseInvoice();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Extras.TYPE, type);
        bundle.putSerializable(Const.COURSE_DESC, course);
        courseInvoice.setArguments(bundle);
        return courseInvoice;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: " );
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        if (getArguments() != null) {
            fragType = getArguments().getString(Const.FRAG_TYPE);
            type = getArguments().getString(Constants.Extras.TYPE);
            course = (SingleCourseData) getArguments().getSerializable(Const.COURSE_DESC);

            if (course != null && GenericUtils.isEmpty(course.getGst())) {   // Setting default gst value 18 if not coming from api
                course.setGst("18");
            }
        }
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_invoice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        networkCallForRewardPoint();
        setupBillingClient();
        initViews(view);
    }

    private void initViews(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        layoutSubscriptionPlan = view.findViewById(R.id.layout_subscription_plan);
        layoutSinglePayment = view.findViewById(R.id.layout_single_payment);
        choosePaymentTypeLL = view.findViewById(R.id.ll_choose_payment_type);

        tvOneTime = view.findViewById(R.id.tv_one_time);
        tvInitialEnrollmentAmount = view.findViewById(R.id.tv_initial_enrollment_amount);
        tvInstallmentPlan = view.findViewById(R.id.tv_installment_plan);
        txtCoupon_Status = view.findViewById(R.id.txtCoupon_Status);
        txtReferral_Status = view.findViewById(R.id.txtReferral_Status);
        txtCouponStatus = view.findViewById(R.id.txtCouponStatus);
        txtCouponStatusApply = view.findViewById(R.id.txtCouponStatusApply);
        txtReferralStatus = view.findViewById(R.id.txtReferralStatus);
        txtReferralStatusApply = view.findViewById(R.id.txtReferralStatusApply);
        txtPriceValue = view.findViewById(R.id.txtPriceValue);
        txtLearnerValue = view.findViewById(R.id.txtLearnerValue);
        txtLearner = view.findViewById(R.id.txtLearner);
        txtCourseType = view.findViewById(R.id.txtCourseType);
        txtCourseTypeValue = view.findViewById(R.id.txtCourseTypeValue);
        txtTotalValue = view.findViewById(R.id.txtTotalValue);
        txtDiscountValue = view.findViewById(R.id.txtDiscountValue);
        txtServiceTaxValue = view.findViewById(R.id.txtServiceTaxValue);
        gstTB = view.findViewById(R.id.gstTB);
        txtServiceTax = view.findViewById(R.id.txtServiceTax);
        txtGrandTotalValue = view.findViewById(R.id.txtGrandTotalValue);
        courseNameTV = view.findViewById(R.id.coursenameTV);
        courseNameView = view.findViewById(R.id.tvCourseName);
        ratingTV = view.findViewById(R.id.ratingTV);
        ratingRB = view.findViewById(R.id.ratingRB);
        imageIV = view.findViewById(R.id.imageIV);
        btnProceed = view.findViewById(R.id.btn_proceed);
        coinsRedeemTB = view.findViewById(R.id.coinsRedeemTB);
        llRemoveRedeemedCoin = view.findViewById(R.id.llRemoveRedeemedCoin);
        discountTB = view.findViewById(R.id.discountTB);
        afterDiscountTB = view.findViewById(R.id.afterDiscountTB);
        coinsTextTV = view.findViewById(R.id.coinsTextTV);
        redeemCoins = view.findViewById(R.id.reddemCoinsBtn);
        coinRedeemValue = view.findViewById(R.id.coinRedeemValue);
        courseSummaryTV = view.findViewById(R.id.courseSummaryTV);
        courseCard = view.findViewById(R.id.courseCard);
        continueBtnLL = view.findViewById(R.id.ll_continue_btn);
        tvInstallmentDuration = view.findViewById(R.id.tv_installment_duration);
        tvMrp = view.findViewById(R.id.tv_mrp);
        tvCutPrice = view.findViewById(R.id.tv_cut_price);
        txtMrpValue = view.findViewById(R.id.course_mrp);
        couponCodeEt = view.findViewById(R.id.couponCodeEt);
        referralCodeEt = view.findViewById(R.id.referalCodeEt);
        couponLayout = view.findViewById(R.id.coupon_layout);
        referralLayout = view.findViewById(R.id.referral_layout);
        couponAppliedLayout = view.findViewById(R.id.CouponAppliedLayout);
        referralAppliedLayout = view.findViewById(R.id.referralAppliedLayout);
        couponAppliedText = view.findViewById(R.id.Coupon_applied);
        txtAfterDiscountValue = view.findViewById(R.id.txtAfterDiscountValue);
        tv_course_category = view.findViewById(R.id.tv_course_category);

        if (!TextUtils.isEmpty(course.getIs_subscription()) && course.getIs_subscription().equals("1")) {
            tvInitialEnrollmentAmount.setText(activity.getString(R.string.subscription_plan));
            courseNameView.setText(course.getTitle().trim());
            layoutSinglePayment.setVisibility(View.GONE);
            layoutSubscriptionPlan.setVisibility(View.VISIBLE);
            initInstallmentViews(view);
        } else if (!TextUtils.isEmpty(course.getIs_instalment()) && course.getIs_instalment().equals("1")) {
            choosePaymentTypeLL.setVisibility(View.VISIBLE);
            tvInitialEnrollmentAmount.setText(activity.getString(R.string.initial_enrolment_amount));
            tvInstallmentPlan.setText(activity.getString(R.string.installment_plan));
            initInstallmentViews(view);
        } else {
            choosePaymentTypeLL.setVisibility(View.GONE);
        }

        setCourseData();

        txtCouponStatus.setOnClickListener(this);
        txtReferralStatus.setOnClickListener(this);

        // Code for Apply Button According to new Design
        txtCouponStatusApply.setOnClickListener(this);
        txtReferralStatusApply.setOnClickListener(this);

        txtCoupon_Status.setOnClickListener(this);
        txtReferral_Status.setOnClickListener(this);
        btnProceed.setOnClickListener(this);
        redeemCoins.setOnClickListener(this);
        llRemoveRedeemedCoin.setOnClickListener(this);

        tvOneTime.setOnClickListener(this);
        tvInstallmentPlan.setOnClickListener(this);
        continueBtnLL.setOnClickListener(this);
        tvInstallmentDuration.setOnClickListener(this);
    }

    private void initInstallmentViews(View view) {
        tvInvoiceDetail = view.findViewById(R.id.tv_invoice_detail);
        recyclerViewInstallmentParent = view.findViewById(R.id.recycler_view_installment_parent);
        recyclerViewInstallmentParent.setLayoutManager(new LinearLayoutManager(activity));
//        tvInvoiceDetail.setOnClickListener(this);
        setInstallmentPlan();
    }

    public String calculateGSTValue(String price) {
        float tempPrice = Float.parseFloat(price);
        return df2.format(tempPrice + ((tempPrice * Float.parseFloat(course.getGst())) / 100));
    }

    private void setCourseData() {
        try {
            courseSummaryTV.setText(type.equals(Const.CPR_INVOICE) ? getString(R.string.CPR_subscription) : getString(R.string.course_summary));

            if (type.equals(Const.CPR_INVOICE)) {
                courseCard.setVisibility(View.GONE);
                txtLearner.setVisibility(View.GONE);
                txtLearnerValue.setVisibility(View.GONE);
                txtCourseType.setText(R.string.CPR_subscription);
                txtCourseTypeValue.setText(SharedPreference.getInstance().getString(Const.SUBSCRIPTION_SELECTED));
            } else {
                courseCard.setVisibility(View.VISIBLE);
                txtLearner.setVisibility(View.VISIBLE);
                txtLearnerValue.setVisibility(View.VISIBLE);
                txtCourseType.setText("Course Type");
                txtCourseTypeValue.setText("Online");

            }

            // Course card Data set
            if (course.getTitle() != null)
                courseNameTV.setText(course.getTitle().trim());
            Glide.with(this)
                    .load(course.getCover_image())
                    .apply(RequestOptions.placeholderOf(R.mipmap.courses_blue).error(R.mipmap.courses_blue))
                    .into(imageIV);
            ratingTV.setText(course.getRating());
            ratingRB.setRating(Float.parseFloat(course.getRating()));


            if (!GenericUtils.isEmpty(course.getCategory_tag())) {
                tv_course_category.setText(course.getCategory_tag());
                tv_course_category.setVisibility(View.VISIBLE);
            } else {
                tv_course_category.setVisibility(View.GONE);
                if (!GenericUtils.isEmpty(course.getCourse_tag())) {
                    tv_course_category.setText(course.getCourse_tag());
                    tv_course_category.setVisibility(View.VISIBLE);
                }
            }

            // Course Price Data
            txtLearnerValue.setText(course.getLearner());
            txtDiscountValue.setText("0");
            price = !TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())
                    ? course.getFor_dams() : course.getNon_dams();
//        price = "1";
            txtPriceValue.setText(String.format("%s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(price)));
            txtMrpValue.setText(String.format("%s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(price)));

            if (!TextUtils.isEmpty(course.getGst_include()) && course.getGst_include().equals("1")) {
                gstTB.setVisibility(View.VISIBLE);
                if (course.getGst().length() > 0) {
                    txtServiceTaxValue.setText(String.format("%s %s", getCurrencySymbol(), (Float.parseFloat(price) * Float.parseFloat(course.getGst())) / 100));
                    txtServiceTax.setText(String.format("%s %s", "GST", "(" + course.getGst() + "%)"));
                } else {
                    gstTB.setVisibility(View.GONE);
                }
            } else {
                gstTB.setVisibility(View.GONE);
            }

            if (course.getGst().length() > 0) {
                txtServiceTaxValue.setText(String.format("%s %s", getCurrencySymbol(), (Float.parseFloat(price) * Float.parseFloat(course.getGst())) / 100));
                txtServiceTax.setText(String.format("%s %s", "GST", "(" + course.getGst() + "%)"));
            } else {
                gstTB.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(course.getGst()) && course.getGst_include().equals("1")) {
                txtTotalValue.setText(String.format("%s %s", getCurrencySymbol(), calculateGSTValue(price)));
                txtGrandTotalValue.setText(txtTotalValue.getText().toString().trim());
                finalPriceValue = calculateGSTValue(price);
            } else {
                txtTotalValue.setText(txtPriceValue.getText().toString().trim());
                txtGrandTotalValue.setText(txtTotalValue.getText().toString().trim());
                finalPriceValue = price;
            }

            grandTotalValue = finalPriceValue;
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public Call<JsonObject> getAPI(String apitype, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        switch (apitype) {
            case API.API_GET_REFERRAL_CODE_VALID:
                params.put(Const.AFFILIATE_REFERRAL_CODE, referralCode);
                break;
            case API.API_GET_REFERRAL_DISCOUNT:
                params.put(Const.COURSE_ID, course.getId());
                break;
        }
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apitype, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apitype) throws JSONException {
        switch (apitype) {
            case API.API_GET_REFERRAL_CODE_VALID:
                if (jsonObject.optString(Const.DATA).equals(Helper.GetText(writeCouponET))) {
                    referralCode = Helper.GetText(writeCouponET);
                    myNetworkCall.NetworkAPICall(API.API_GET_REFERRAL_DISCOUNT, true);
                }
                break;
            case API.API_GET_REFERRAL_DISCOUNT:
                discountAmount = jsonObject.optJSONObject(Const.DATA).optString(Const.DISCOUNT_AMOUNT);
                if (!discountAmount.equals("0")) {
                    discountTB.setVisibility(View.GONE);
                    discountApplied = true;

                    discountType = Const.REFERRAl;
                    couponCode = "";

                    txtReferralStatus.setVisibility(View.GONE);
                    if (txtReferral_Status.getVisibility() == View.GONE)
                        txtReferral_Status.setVisibility(View.VISIBLE);
                    txtReferral_Status.setText(referralCode);

                    referralAppliedLayout.setVisibility(View.VISIBLE);
                    referralLayout.setVisibility(View.GONE);


                    txtDiscountValue.setText(String.format("%s %s %s", "- ", getCurrencySymbol(), decimalFormat.format(Helper.calculateGST(finalPriceValue, discountAmount))));
                    finalPriceValue = decimalFormat.format(Helper.getDiscountedValue("2", finalPriceValue, discountAmount));
                    txtGrandTotalValue.setText(String.format("%s %s", getCurrencySymbol(), finalPriceValue));
                }
                break;
        }
    }

    @Override
    public void errorCallBack(String jsonstring, String apitype) {
        switch (apitype) {
            case API.API_GET_REWARD_POINTS:
                break;
            case API.API_APPLY_COUPON_CODE:
                Toast.makeText(activity, jsonstring, Toast.LENGTH_SHORT).show();
                break;
            default:
                GenericUtils.showToast(activity, jsonstring);
                break;
        }
        if (jsonstring.equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
            Helper.showErrorLayoutForNoNav(apitype, activity, 1, 1);

        if (jsonstring.equalsIgnoreCase(activity.getResources().getString(R.string.exception_api_error_message)))
            Helper.showErrorLayoutForNoNav(apitype, activity, 1, 0);
    }

    private void networkCallForApplyCouponCode() {
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.applyCouponCode(SharedPreference.getInstance().getLoggedInUser().getId(),
                type.equals(Const.CPR_INVOICE) ? "CPR_" + course.getId() : course.getId(), couponCode);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            discountTB.setVisibility(View.GONE);
                            afterDiscountTB.setVisibility(View.VISIBLE);
                            discountApplied = true;
                            txtCouponStatus.setVisibility(View.GONE);
                            if (txtCoupon_Status.getVisibility() == View.GONE)
                                txtCoupon_Status.setVisibility(View.VISIBLE);
                            txtCoupon_Status.setText("(" + couponCode + ")");

                            discountType = Const.COUPON;
                            referralCode = "";
                            couponAppliedLayout.setVisibility(View.VISIBLE);
                            couponCodeEt.setText("");
                            redeemCoins.setEnabled(false);

                            coupon = GenericUtils.getJsonObject(jsonResponse).optString("coupon_value");
                            couponType = GenericUtils.getJsonObject(jsonResponse).optString("coupon_type");

                            if (couponType.equals("1")) {
                                txtDiscountValue.setText(String.format("%s %S %s", "- ", getCurrencySymbol(), decimalFormat.format(Float.parseFloat(coupon))));
                                couponAppliedText.setText(String.format("%s %S %s", "- ", getCurrencySymbol(), decimalFormat.format(Float.parseFloat(coupon))));
                            } else {
                                txtDiscountValue.setText(String.format("%s %S %s", "- ", getCurrencySymbol(), decimalFormat.format(Helper.calculateGST(finalPriceValue, coupon))));
                                couponAppliedText.setText(String.format("%s %S %s", "- ", getCurrencySymbol(), decimalFormat.format(Helper.calculateGST(finalPriceValue, coupon))));
                            }

                            finalPriceValue = decimalFormat.format(Helper.getDiscountedValue(couponType, finalPriceValue, coupon));
                            txtAfterDiscountValue.setText(String.format("%s %s", getCurrencySymbol(), finalPriceValue));
                            txtGrandTotalValue.setText(String.format("%s %s", getCurrencySymbol(), finalPriceValue));

                            Log.e(TAG, "showReferralCodePopup: installmentvalue = " + finalPriceValue);
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_APPLY_COUPON_CODE);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    printError(TAG, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_APPLY_COUPON_CODE, activity, 0, 0);

            }
        });
    }

    private void networkCallForInitializeCoursePayment() {
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.initializeCoursePayment(SharedPreference.getInstance().getLoggedInUser().getId(),
                type.equals(Const.CPR_INVOICE) ? "CPR_" + course.getId() : course.getId(),
                course.getPoints_conversion_rate(),
                course.getGst(),
                redeemPoints,
                couponCode,
                referralCode,
                paymentModeCheck,
                !TextUtils.isEmpty(course.getChild_courses()) ? course.getChild_courses() : "",
                paymentType.equals(Const.ONE_TIME) ? finalPriceValue : installmentValue);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString("status").equals(Const.TRUE)) {
                            JSONObject data = jsonResponse.optJSONObject("data");
                            SharedPreference.getInstance().putString(Const.COURSE_INIT_PAYMENT_TOKEN, Objects.requireNonNull(data).optString("pre_transaction_id"));

                            // todo to check the payment mode section
                            paymentCallbacks();
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_INITIALIZE_COURSE_PAYMENT);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Log.e(TAG, "onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_INITIALIZE_COURSE_PAYMENT, activity, 0, 0);
            }
        });
    }

    private void networkCallForCheckSumForPaytm() {
        mProgress.show();
        finalPriceValue = df2.format(Float.parseFloat(finalPriceValue));
        Call<JsonObject> response = null;
        ApiInterface apiInterface = ApiClient.createService2(ApiInterface.class);
        if (Const.SERVER_TYPE.equals("LIVE")) {
            response = apiInterface.getCheckSumForPaytmLive(String.format(API.API_GET_CHECKSUM_FOR_PAYTM,
                    Const.SERVER_TYPE), Const.PAYTM_MID, SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                    Const.INDUSTRYTYPE_ID, SharedPreference.getInstance().getLoggedInUser().getUser_registration_info().getId(),
                    Const.CHANNELID, finalPriceValue, Const.PAYTM_WEBSITE,
                    Const.CALLBACKURL + SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
        } else {
            response = apiInterface.getCheckSumForPaytm(String.format(API.API_GET_CHECKSUM_FOR_PAYTM, Const.SERVER_TYPE),
                    Const.PAYTM_MID, SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                    Const.INDUSTRYTYPE_ID, SharedPreference.getInstance().getLoggedInUser().getUser_registration_info().getId(),
                    Const.CHANNELID, finalPriceValue, Const.PAYTM_WEBSITE,
                    Const.CALLBACKURL + SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));

        }
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgress.dismiss();
                    if (Objects.requireNonNull(jsonResponse).has("CHECKSUMHASH")) {
                        SharedPreference.getInstance().putString(Const.CHECK_SUM, jsonResponse.optString("CHECKSUMHASH"));
                        makePaytmTransaction();
                        //region Dummy transaction testing
//                        SharedPreference.getInstance().putString(Const.COURSE_FINAL_PAYMENT_TOKEN,
//                        "DUMMY_FINAL_TXN_ID_" + SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
//                        networkCallForFinalTransForPaytm()
//                        completeCoursePayment();
                        //endregion

                    } else {
                        errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_CHECKSUM_FOR_PAYTM);
                        RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
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

    private void completeCoursePayment() {
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.completeCoursePayment(SharedPreference.getInstance().getLoggedInUser().getId(),
                type.equals(Const.CPR_INVOICE) ? "CPR_" + course.getId() : course.getId(),
                courseDiscount,
                finalPriceValue,
                SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                SharedPreference.getInstance().getString(Const.COURSE_FINAL_PAYMENT_TOKEN),
                referralCode);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString("status").equals(Const.TRUE)) {
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
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_COMPLETE_COURSE_PAYMENT);
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
                Helper.showErrorLayoutForNoNav(API.API_COMPLETE_COURSE_PAYMENT, activity, 0, 0);
            }
        });
    }

    private void screenRedirection(String type) {
        Intent mycourse = new Intent(activity, CourseActivity.class); // AllCourse List FRAG_TYPE, Const.MYCOURSES
        mycourse.putExtra(Const.FRAG_TYPE, type);
        SharedPreference.getInstance().putBoolean(Const.IS_PAYMENT_DONE, true);
        activity.startActivity(mycourse);
        activity.finish();
    }

    private void networkCallForfinalTransForPaytm() {
       /* mProgress.show();
        CourseInvoiceApiInterface apiInterface = ApiClient.createService2(CourseInvoiceApiInterface.class);
        Call<JsonObject> response = apiInterface.finalTransactionForPaytm(String.format("Paytm/TxnStatus.php?MID=%s&ORDERID=%s&SERVER=%s", Const.PAYTM_MID,
                SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                Const.SERVER_TYPE));
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    mProgress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();

            }
        });*/
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

    private void networkCallForFreeCourseTransaction() {
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.makeFreeCourseTransaction(SharedPreference.getInstance().getLoggedInUser().getId(),
                course.getPoints_conversion_rate(), "0", redeemPoints, couponCode, type.equals(Const.CPR_INVOICE) ? "CPR_" + course.getId() : course.getId(), "0");
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
                            //Intent myCourse = new Intent(activity, FeedsActivity.class);
                            Intent myCourse = new Intent(activity, HomeActivity.class);
                            myCourse.putExtra(Const.FRAG_TYPE, Constants.StudyType.TESTS);
                            //myCourse.putExtra("frag_type", "All Courses");
                            //myCourse.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES);
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

    public void retryApis(String apiType) {
        switch (apiType) {
            case API.API_APPLY_COUPON_CODE:
                networkCallForApplyCouponCode();
                break;
            case API.API_INITIALIZE_COURSE_PAYMENT:
                networkCallForInitializeCoursePayment();
                break;
            case API.API_GET_CHECKSUM_FOR_PAYTM:
                networkCallForCheckSumForPaytm();
                break;
            case API.API_COMPLETE_COURSE_PAYMENT:
                completeCoursePayment();
                break;
            case API.API_FINAL_TRANSACTION_FOR_PAYTM:
                networkCallForfinalTransForPaytm();
                break;
            case API.API_GET_REWARD_POINTS:
                networkCallForRewardPoint();
                break;
            case API.API_MAKE_FREE_COURSE_TRANSACTION:
                networkCallForFreeCourseTransaction();
                break;
            case API.API_GET_REFERRAL_DISCOUNT:
//                networkCallForReferralDiscount();
                break;
        }
    }

    private void paymentCallbacks() {
        if (!TextUtils.isEmpty(paymentModeCheck)) {
            switch (paymentModeCheck) {
//                case "CCAVENUE":
//                    if (Helper.getCurrencySymbol().equals(getString(R.string.dollar)))
//                        sendCCAvenuePayment(SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
//                                Helper.calculatePriceBasedOnCurrency(finalPriceValue), Const.CURRENCYDOLLAR);
//                    else
//                        sendCCAvenuePayment(SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
//                                finalPriceValue, Const.CURRENCYINR);
//                    break;
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
                    networkCallForCheckSumForPaytm();//NetworkAPICall(API.API_GET_CHECKSUM_FOR_PAYTM, true);
                    break;
            }
        }
    }

   /* private String getDiscountedValue(boolean bool, String couponCode) {
        int price, disccountedPrice;
        if (bool) {
            price = Integer.parseInt(txtTotalValue.getText().toString().replace(getCurrencySymbol(), "").trim());
            disccountedPrice = Integer.parseInt(couponCode);
            if ((price - disccountedPrice) == 0) {
                finalPriceValue = String.valueOf(0);
                return "0";
            } else {
                finalPriceValue = String.valueOf((price - disccountedPrice) < 0 ? "0" : (price - disccountedPrice));
                return finalPriceValue;
            }
//                txtGrandTotalValue.setText(String.format("%s %s", "Rs. ", price - disccountedPrice));
        } else {
            price = Integer.parseInt(txtTotalValue.getText().toString().replace(getCurrencySymbol(), "").trim());
            disccountedPrice = Integer.parseInt(couponCode);
            if ((price - (disccountedPrice / 100)) == 0) {
                finalPriceValue = String.valueOf(0);
                return "0";
            } else {
                finalPriceValue = String.valueOf(price - ((price * disccountedPrice) / 100));
                return finalPriceValue;
            }
        }
    }*/

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
        paramMap.put(Const.TXN_AMOUNT, finalPriceValue);
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
                                networkCallForfinalTransForPaytm();//NetworkAPICall(API.API_FINAL_TRANSACTION_FOR_PAYTM, true);
                                completeCoursePayment();//NetworkAPICall(API.API_COMPLETE_COURSE_PAYMENT, true);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtReferralStatus:
                if (!finalPriceValue.equals("0")) {
                    if (txtReferralStatus.getVisibility() == View.VISIBLE) {
                        if (!discountApplied) {
                            showCouponCodePopup(true);
                        } else {
                            showAlertDialog();
                        }
                    } else {
                        txtReferral_Status.setVisibility(View.GONE);
                        txtReferralStatus.setVisibility(View.VISIBLE);
                        referralCode = "";
                    }
                } else {
                    GenericUtils.showToast(activity, activity.getString(R.string.course_have_already_at_lower_price));
                }
                break;
            case R.id.txtReferral_Status:
                if (txtReferral_Status.getVisibility() == View.VISIBLE) {
                    if (Const.REFERRAl.equals(discountType)) {
                        discountType = "";
                    }
                    discountApplied = false;
                    referralCode = "";
                    discountTB.setVisibility(View.GONE);
                    afterDiscountTB.setVisibility(View.GONE);
                    txtReferral_Status.setVisibility(View.GONE);
                    txtReferralStatus.setVisibility(View.VISIBLE);
                    referralAppliedLayout.setVisibility(View.GONE);
                    updateGradnTotalPrice();
                }
                break;

            case R.id.txtReferralStatusApply:
                if (!finalPriceValue.equals("0")) {
                    if (referralLayout.getVisibility() == View.VISIBLE) {
                        if (!discountApplied) {

                            if (!TextUtils.isEmpty(Helper.GetText(referralCodeEt))) {

                                referralCode = Helper.GetText(referralCodeEt);
                                myNetworkCall.NetworkAPICall(API.API_GET_REFERRAL_CODE_VALID, true);

                            } else {

                                GenericUtils.showToast(activity, activity.getString(R.string.enter_coupon_code_to_get_discount));

                            }


                        } else {
                            showAlertDialog();
                        }
                    } else {
                        referralAppliedLayout.setVisibility(View.GONE);
                        referralLayout.setVisibility(View.VISIBLE);
                        referralCode = "";
                    }
                } else {
                    GenericUtils.showToast(activity, activity.getString(R.string.course_have_already_at_lower_price));
                }
                break;
            case R.id.txtCouponStatusApply:
                if (!finalPriceValue.equals("0")) {
                    if (couponLayout.getVisibility() == View.VISIBLE) {
                        if (!discountApplied) {
                            if (!TextUtils.isEmpty(Helper.GetText(couponCodeEt))) {
                                couponCode = Helper.GetText(couponCodeEt);
                                networkCallForApplyCouponCode();
                            } else {
                                GenericUtils.showToast(activity, activity.getString(R.string.enter_coupon_code_to_get_discount));
                            }

                        } else {
                            showAlertDialog();
                        }
                    } else {
                        couponAppliedLayout.setVisibility(View.GONE);
                        couponLayout.setVisibility(View.VISIBLE);
                        couponCode = "";
                    }
                } else {
                    GenericUtils.showToast(activity, activity.getString(R.string.course_have_already_at_lower_price));
                }
                break;
            case R.id.txtCouponStatus:
                if (!finalPriceValue.equals("0")) {
                    if (txtCouponStatus.getVisibility() == View.VISIBLE) {
                        if (!discountApplied) {
                            showCouponCodePopup(false);
                        } else {
                            showAlertDialog();
                        }
                    } else {
                        txtCoupon_Status.setVisibility(View.GONE);
                        txtCouponStatus.setVisibility(View.VISIBLE);
                        couponCode = "";
                    }
                } else {
                    GenericUtils.showToast(activity, activity.getString(R.string.course_have_already_at_lower_price));
                }
                break;

            case R.id.txtCoupon_Status:
                if (txtCoupon_Status.getVisibility() == View.VISIBLE) {

                    if (isRedeem) {
                        finalPriceValue = grandTotalValue;
                    }
                    if (Const.COUPON.equals(discountType)) {
                        discountType = "";
                    }
                    discountTB.setVisibility(View.GONE);
                    afterDiscountTB.setVisibility(View.GONE);
                    discountApplied = false;
                    txtCoupon_Status.setVisibility(View.GONE);
                    txtCouponStatus.setVisibility(View.VISIBLE);
                    couponCode = "";
                    txtDiscountValue.setText("0");
                    couponAppliedLayout.setVisibility(View.GONE);
                    updateGradnTotalPrice();
                    redeemCoins.setEnabled(true);
                }
                break;
            case R.id.btn_proceed:
                if (finalPriceValue.equals("0")) {   // To make the transaction in case of after applying coupon & Points and made it free
                    networkCallForFreeCourseTransaction();//NetworkAPICall(API.API_MAKE_FREE_COURSE_TRANSACTION, true);
                } else if (Double.parseDouble(finalPriceValue) < 1) {
                    Toast.makeText(activity, "Not a valid amount we cannot proceed", Toast.LENGTH_SHORT).show();
                } else {  // To make the transaction in case of after applying coupon & Points for some Amount of price.
                    if (getCurrencySymbol().equals(eMedicozApp.getAppContext().getResources().getString(R.string.rs))) {
                        showPaymentModePopup();
                    } else
                        showConversion();
                }
                break;

            // to check that anything is selected or not after choosing payment Mode
            case R.id.btn_continue:
                Dialog paymentDialog = (Dialog) view.getTag(R.id.questions);
                View viewTag = (View) view.getTag(R.id.optionsAns);
                RadioGroup radioGroup = viewTag.findViewById(R.id.radioGroupPay);

                if (radioGroup != null && radioGroup.getCheckedRadioButtonId() != -1) {
                    RadioButton rb = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                    if (rb.getText().equals(activity.getString(R.string.paytm))) {
                        paymentModeCheck = Const.PAYTM;
                    } else if (rb.getText().equals(activity.getString(R.string.in_app_purchase))) {
                        paymentModeCheck = Const.ANIN;
                    }
                    paymentDialog.dismiss();

                    Log.e(TAG, "onClick: " + finalPriceValue);
                    networkCallForInitializeCoursePayment();
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
                txtCouponStatusApply.setEnabled(true);
                couponCodeEt.setEnabled(true);
                redeemCoins.setText(activity.getString(R.string.redeem));
                isRedeem = false;
                finalPriceValue = grandTotalValue;
                afterDiscountTB.setVisibility(View.GONE);
                if (Const.COUPON.equals(discountType)) {
                    if (!TextUtils.isEmpty(couponCode))
                        networkCallForApplyCouponCode();
                } else if (Const.REFERRAl.equals(discountType)) {
                    if (!TextUtils.isEmpty(referralCode))
                        myNetworkCall.NetworkAPICall(API.API_GET_REFERRAL_CODE_VALID, true);
                } else {
                    txtGrandTotalValue.setText(String.format("%s %s", getCurrencySymbol(), finalPriceValue));
                }
                break;
            case R.id.tv_one_time:
                if (paymentType.equals(Const.INSTALLMENT_PLAN)) {
                    paymentType = Const.ONE_TIME;
                    changePaymentType(true);
                }
                break;
            case R.id.tv_installment_plan:
                tvMrp.setText(String.format("%s %s", getCurrencySymbol(), !TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())
                        ? course.getFor_dams() : course.getNon_dams()));

                if (paymentType.equals(Const.ONE_TIME)) {
                    paymentType = Const.INSTALLMENT_PLAN;
                    changePaymentType(false);
                }
                break;
            case R.id.ll_continue_btn:
                if (TextUtils.isEmpty(Helper.GetText(tvInstallmentDuration))) {
                    GenericUtils.showToast(activity, activity.getString(R.string.please_select_installment_plan));
                } else if (!TextUtils.isEmpty(Helper.GetText(tvInstallmentDuration))) {
                   /* for (int i = 0; i < course.getInstallment().size(); i++) {
                        if (course.getInstallment().get(i).isSelected()) {
                            installmentvalue = course.getInstallment().get(i).getAmount_description().getPayment().get(0);
                        }
                    }*/

                    startActivity(new Intent(activity, CourseActivity.class).putExtra(Const.FRAG_TYPE, Const.ORDER_DETAIL).putExtra(Const.COURSE_DESC, course));
                  //  startActivity(new Intent(activity, CourseActivity.class).putExtra(Const.FRAG_TYPE, Const.ORDER_DETAIL).putExtra(Const.COURSE_DESC, course));
                  //   Helper.goToCartScreen1(activity, course);
                }
                break;
            case R.id.tv_invoice_detail:
                if (TextUtils.isEmpty(Helper.GetText(tvInstallmentDuration))) {
                    if (course.getIs_subscription() != null && course.getIs_subscription().equals("1"))
                        GenericUtils.showToast(activity, activity.getString(R.string.please_select_subscription_plan));
                    else
                        GenericUtils.showToast(activity, activity.getString(R.string.please_select_installment_plan));
                } else if (!TextUtils.isEmpty(Helper.GetText(tvInstallmentDuration))) {
                    invoiceDetailDialog();
                }
                break;
        }
    }

    private void coinRedemption() {
        if (!finalPriceValue.equals("0")) {
            str = calculateRedeemCoinsValue();
            if (!str.split(",")[1].equals("0")) {
                if (str.contains(",")) {
                    isRedeem = true;

                    setRedeemText(String.valueOf(Integer.parseInt(rewardPoints.getReward_points()) - Integer.parseInt(str.split(",")[0])));
                    coinsRedeemTB.setVisibility(View.VISIBLE);
                    coinRedeemValue.setText(String.format("%s %s %s", "-", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(str.split(",")[1])));
                    finalPriceValue = df2.format(Double.parseDouble(finalPriceValue) - Integer.parseInt(str.split(",")[1]));
                    txtGrandTotalValue.setText(String.format("%s %s", getCurrencySymbol(), (Double.parseDouble(finalPriceValue) < 0) ? "0" : Helper.calculatePriceBasedOnCurrency(finalPriceValue)));
                    redeemPoints = str.split(",")[0];
                    redeemCoins.setText(activity.getString(R.string.redeemed));
                    afterDiscountTB.setVisibility(View.VISIBLE);
                    txtAfterDiscountValue.setText(String.format("%s %s", getCurrencySymbol(), (Double.parseDouble(finalPriceValue) < 0) ? "0" : Helper.calculatePriceBasedOnCurrency(finalPriceValue)));
                    redeemCoins.setOnClickListener(null);

                    txtCouponStatusApply.setEnabled(false);
                    couponCodeEt.setEnabled(false);
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

    private void showAlertDialog() {
        Helper.newCustomDialog(activity,
                "Alert",
                activity.getString(R.string.out_of_referral_discount_and_coupon_discount_you_can_avail_only_one_discount_at_a_time),
                true,
                activity.getString(R.string.ok),
                ContextCompat.getDrawable(activity, R.drawable.bg_round_corner_fill_green));
    }

    private void invoiceDetailDialog() {
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = li.inflate(R.layout.dialog_invoice_detail_layout, null, false);
        final Dialog dialog = new Dialog(activity, R.style.bottomDialogTheme);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        TextView tvTotalAmountLabel;
        TextView tvTotalAmount;
        TextView tvCourseFee;
        TextView tvGstLable;
        TextView tvGstPrice;
        TextView tvDownPaymentLable;
        TextView tvDownPayment;
        TextView initialEnrollmentAmount;
        tvTotalAmountLabel = view.findViewById(R.id.tv_total_amount_label);
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);

        tvCourseFee = view.findViewById(R.id.tv_course_fee);
        tvGstLable = view.findViewById(R.id.tv_gst_lable);
        tvGstPrice = view.findViewById(R.id.tv_gst_price);
        tvDownPaymentLable = view.findViewById(R.id.tv_down_payment_lable);
        tvDownPayment = view.findViewById(R.id.tv_down_payment);
        initialEnrollmentAmount = view.findViewById(R.id.initial_enrollment_amount);

        tvTotalAmountLabel.setTextColor(ContextCompat.getColor(activity, R.color.blue));
        tvTotalAmount.setTextColor(ContextCompat.getColor(activity, R.color.blue));

        LinearLayout planTypeLL = view.findViewById(R.id.ll_plan_type);
        LinearLayout gstLL = view.findViewById(R.id.ll_gst);
        LinearLayout downPaymentLL = view.findViewById(R.id.ll_down_payment);
        LinearLayout duePaymentLL = view.findViewById(R.id.ll_due_payment);
        LinearLayout specialPriceLL = view.findViewById(R.id.ll_special_price);
        LinearLayout referralDiscountLL = view.findViewById(R.id.ll_referral_discount);
        LinearLayout referralCodeLL = view.findViewById(R.id.ll_referral_code);
        LinearLayout couponDiscountLL = view.findViewById(R.id.ll_coupon_discount);
        LinearLayout couponCodeLL = view.findViewById(R.id.ll_coupon_code);
        CardView couponCodeLay = view.findViewById(R.id.courseCouponLay);


        planTypeLL.setVisibility(View.GONE);
        couponCodeLay.setVisibility(View.GONE);

        downPaymentLL.setVisibility(View.VISIBLE);
        duePaymentLL.setVisibility(View.GONE);
        specialPriceLL.setVisibility(View.GONE);
        referralDiscountLL.setVisibility(View.GONE);
        referralCodeLL.setVisibility(View.GONE);
        couponDiscountLL.setVisibility(View.GONE);
        couponCodeLL.setVisibility(View.GONE);

        String coursePrice = "";

        course.setIs_subscription(!TextUtils.isEmpty(course.getIs_subscription()) ? course.getIs_subscription() : "0");

        if (course.getIs_instalment().equals("1") && course.getIs_subscription().equals("0")) {
            coursePrice = !TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())
                    ? course.getFor_dams() : course.getNon_dams();
        }

        for (int i = 0; i < course.getInstallment().size(); i++) {
            if (course.getInstallment().get(i).isSelected()) {
                installmentValue = course.getInstallment().get(i).getAmount_description().getPayment().get(0);
                tvDownPayment.setText(String.format("%s %s", getCurrencySymbol(), course.getInstallment().get(i).getAmount_description().getPayment().get(0)));
                if (course.getIs_subscription().equals("1")) {
                    coursePrice = course.getInstallment().get(i).getAmount_description().getPayment().get(0);
                    tvDownPaymentLable.setText("Course Duration");
                    initialEnrollmentAmount.setText("(" + course.getInstallment().get(i).getName() + ")");
                } else {
                    tvDownPaymentLable.setText("Down Payment");
                    initialEnrollmentAmount.setText("(Initial enrolment amount)");
                }
            }
        }

        if (TextUtils.isEmpty(course.getGst_include()) || course.getGst_include().equals("0")) {
            gstLL.setVisibility(View.GONE);
            tvCourseFee.setText(String.format("%s %s", getCurrencySymbol(), decimalFormat.format(getGSTExcludevalue(coursePrice))));
            tvGstLable.setText(String.format("%s %s", "GST", "(" + course.getGst() + "%)"));
            gstValue = decimalFormat.format(Float.parseFloat(coursePrice) - getGSTExcludevalue(coursePrice));
            tvGstPrice.setText(String.format("%s %s", getCurrencySymbol(), gstValue));
            tvTotalAmount.setText(String.format("%s %s", getCurrencySymbol(), coursePrice));
        } else {
            gstLL.setVisibility(View.VISIBLE);
            tvCourseFee.setText(String.format("%s %s", getCurrencySymbol(), coursePrice));
            tvGstLable.setText(String.format("%s %s", "GST", "(" + course.getGst() + "%)"));
            gstValue = decimalFormat.format(calculateGST(coursePrice, course.getGst()));
            tvGstPrice.setText(String.format("%s %s", getCurrencySymbol(), gstValue));
            tvTotalAmount.setText(String.format("%s %s", getCurrencySymbol(), (Float.parseFloat(coursePrice) + (Float.parseFloat(coursePrice) * Float.parseFloat(course.getGst())) / 100)));
        }

        setInvoiceDetail();
    }

    private void setInvoiceDetail() {
        price = !TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())
                ? course.getFor_dams() : course.getNon_dams();
//        price = "1";
        txtPriceValue.setText(String.format("%s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(price)));
        txtMrpValue.setText(String.format("%s %s", getCurrencySymbol(), Helper.calculatePriceBasedOnCurrency(price)));
    }

    private void changePaymentType(boolean isOneTime) {
        if (isOneTime) {
            tvOneTime.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_btn));
            tvOneTime.setTextColor(ContextCompat.getColor(activity, R.color.off_white));

            tvInstallmentPlan.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_capsule_fill_white));
            tvInstallmentPlan.setTextColor(ContextCompat.getColor(activity, R.color.dark_quiz_grey));

            layoutSinglePayment.setVisibility(View.VISIBLE);
            layoutSubscriptionPlan.setVisibility(View.GONE);
        } else {
            tvInstallmentPlan.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_btn));
            tvInstallmentPlan.setTextColor(ContextCompat.getColor(activity, R.color.off_white));

            tvOneTime.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_capsule_fill_white));
            tvOneTime.setTextColor(ContextCompat.getColor(activity, R.color.dark_quiz_grey));

            layoutSinglePayment.setVisibility(View.GONE);
            layoutSubscriptionPlan.setVisibility(View.VISIBLE);
        }
    }

    private void setInstallmentPlan() {
        //TODO 1. change subscription listing UI as per new design : @layout/subscription_list_item
//        if (course.getIs_subscription() == null || course.getIs_subscription().equals("0")) return;
        if (course.getInstallment() == null) {
            getInstallments();
        } else {
            recyclerViewInstallmentParent.setAdapter(new InstallmentParentAdapter(activity, course.getInstallment(), course, new OnSubscriptionItemClickListener() {
                @Override
                public void OnSubscriptionItemClickPosition(int position) {

                }
            }));
            recyclerViewInstallmentParent.addItemDecoration(new EqualSpacingItemDecoration(0, EqualSpacingItemDecoration.VERTICAL));
        }
    }

    private void getInstallments() {

        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getInstallments(SharedPreference.getInstance().getLoggedInUser().getId(),
                course.getId(), course.isIs_renew() ? "1" : "0");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {

                            Type type = new TypeToken<List<Installment>>() {
                            }.getType();
                            course.setInstallment(new Gson().fromJson(GenericUtils.getJsonArray(jsonResponse).toString(), type));
                            course.setIs_instalment("1");

                            recyclerViewInstallmentParent.setAdapter(new InstallmentParentAdapter(activity, course.getInstallment(), course, new OnSubscriptionItemClickListener() {
                                @Override
                                public void OnSubscriptionItemClickPosition(int position) {

                                }
                            }));
                            recyclerViewInstallmentParent.addItemDecoration(new EqualSpacingItemDecoration(0, EqualSpacingItemDecoration.VERTICAL));

                        } else {
                            Helper.showErrorLayoutForNoNav("getInstallments", activity, 1, 0);
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
                Helper.showErrorLayoutForNoNav("getInstallments", activity, 1, 1);
            }
        });
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
                       /* Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
                        List<Purchase> purchases = result.getPurchasesList();


                        for (Purchase purchase : purchases) {
                            String thisSKU = purchase.getSku();
                            if (thisSKU.equals(iv.getTag())) {
                                isOwned = true;
                                Toast.makeText(MainActivity.this, "you are a premium user", Toast.LENGTH_SHORT).show();
                                btnBuy_2.setVisibility(View.INVISIBLE);
                                break;
                                //item already owned
                            }
                        }*/

                        if (!isOwned) {
                            BillingFlowParams billingFlowParams = BillingFlowParams
                                    .newBuilder()
                                    .setSkuDetails(skuDetails)
                                    .build();
                            billingClient.launchBillingFlow(activity, billingFlowParams);
                        }


//                                billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, new PurchaseHistoryResponseListener() {
//                                    @Override
//                                    public void onPurchaseHistoryResponse(BillingResult billingResult, List<PurchaseHistoryRecord> purchaseHistoryRecordList) {
//                                        //Toast.makeText(MainActivity.this, "history response:" + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
//                                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchaseHistoryRecordList != null)
//                                        {
//                                            boolean isOwned = false;
//                                            for (PurchaseHistoryRecord purchaseHistoryRecord : purchaseHistoryRecordList) {
//                                                String mySKU = purchaseHistoryRecord.getSku();
//                                                //Toast.makeText(MainActivity.this, "history sku=" + mySKU, Toast.LENGTH_SHORT).show();
//
//                                                if (mySKU.equals(sku))
//                                                {
//                                                    isOwned = true;
//                                                    //Toast.makeText(MainActivity.this, "you are a premium user", Toast.LENGTH_SHORT).show();
//                                                    buttonBuyProduct.setVisibility(View.INVISIBLE);
//                                                    break;
//                                                    //item already owned
//                                                }
//                                            }
//                                            if (!isOwned)
//                                            {
//                                                mSkuDetails = skuDetails;
//                                                buttonBuyProduct.setEnabled(true);
//
//                                                buttonBuyProduct.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        BillingFlowParams billingFlowParams = BillingFlowParams
//                                                                .newBuilder()
//                                                                .setSkuDetails(skuDetails)
//                                                                .build();
//                                                        billingClient.launchBillingFlow(MainActivity.this, billingFlowParams);
//
//                                                    }
//                                                });
//                                            }
//                                        }
//
//                                    }
//                                });
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
        if (purchase.getSku().equals(course.getId()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
            AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
            billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    SharedPreference.getInstance().putString(Const.COURSE_FINAL_PAYMENT_TOKEN, purchase.getOrderId());
                    completeCoursePayment();
//                            Toast.makeText(activity, "Purchase Acknowledged", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Log.i("data cming from", data.getStringExtra("transStatus") + "" + data.getStringExtra("transactionId")
                    + "" + data.getStringExtra("orderStatus"));
            SharedPreference.getInstance().putString(Const.COURSE_FINAL_PAYMENT_TOKEN, data.getStringExtra("transactionId"));
            completeCoursePayment();//NetworkAPICall(API.API_COMPLETE_COURSE_PAYMENT, true);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (data != null) {
                Toast.makeText(activity, data.getStringExtra("orderStatus"), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Transaction Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // calling from referral and coupon removed
    private void updateGradnTotalPrice() {
        finalPriceValue = grandTotalValue;
        if (isRedeem) {
            coinRedemption();
        } else {
            txtGrandTotalValue.setText(String.format("%S %S", getCurrencySymbol(), finalPriceValue));
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
            if ((coinsValue / convertionRate) > Integer.parseInt(finalPriceValue)) {
                finalprice = Integer.parseInt(finalPriceValue);
                redeempoints = Integer.parseInt(finalPriceValue) * convertionRate;
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

    private void showCouponCodePopup(boolean isReferral) {
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
                    networkCallForApplyCouponCode();
                }
                couponDialog.dismiss();
            } else {
                if (isReferral) {
                    GenericUtils.showToast(activity, activity.getString(R.string.enter_referral_code_to_get_discount));
                } else {
                    GenericUtils.showToast(activity, activity.getString(R.string.enter_coupon_code_to_get_discount));
                }
            }
        });
        cancel.setOnClickListener(v -> couponDialog.dismiss());
    }

    private void showPaymentModePopup() {
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View promptsView = li.inflate(R.layout.dialog_payment, null, false);
        final Dialog paymentDialog = new Dialog(activity);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setCanceledOnTouchOutside(true);
        paymentDialog.setContentView(promptsView);
        paymentDialog.show();

        final AppCompatRadioButton radioBtnInApp;
        radioBtnInApp = promptsView.findViewById(R.id.radio_btn_inApp);
        TextView tv = promptsView.findViewById(R.id.titleDialog);
        tv.setText(getString(R.string.choose_payment_, finalPriceValue));

        final Button btnContinue = promptsView.findViewById(R.id.btn_continue);

        if (!TextUtils.isEmpty(SharedPreference.getInstance().getMasterHitResponse().getAndroid_inapp())) {
            if (SharedPreference.getInstance().getMasterHitResponse().getAndroid_inapp().equals("1")) {
                radioBtnInApp.setVisibility(View.VISIBLE);
            } else {
                radioBtnInApp.setVisibility(View.GONE);
            }
        } else {
            radioBtnInApp.setVisibility(View.GONE);
        }

        btnContinue.setTag(R.id.questions, paymentDialog);
        btnContinue.setTag(R.id.optionsAns, promptsView);
        btnContinue.setTag(paymentDialog);
        btnContinue.setOnClickListener(this);
    }

    private void showConversion() {
        View view = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name), activity.getString(R.string.amount_wil_be_show_deducted_in_inr));
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnSubmit = view.findViewById(R.id.btn_submit);
        btnSubmit.setText(activity.getString(R.string.continueText));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) activity.getResources().getDimension(R.dimen.dp120), (int) activity.getResources().getDimension(R.dimen.dp35));
        params.setMargins(0, (int) activity.getResources().getDimension(R.dimen.dp20), 0, (int) activity.getResources().getDimension(R.dimen.dp20));
        btnCancel.setLayoutParams(params);
        btnSubmit.setLayoutParams(params);

        btnCancel.setOnClickListener(v -> Helper.dismissDialog());

        btnSubmit.setOnClickListener(v -> {
            Helper.dismissDialog();
            showPaymentModePopup();
        });
    }

    private void setRedeemText(String rewardPoint) {
        coinsTextTV.setText(String.format("You have %s points to redeem. \n ( %s points = %s 1).", rewardPoint, course.getPoints_conversion_rate(), getCurrencySymbol()));
    }

    public void setInvoiceDetail(Installment installment) {

        if (!TextUtils.isEmpty(course.getIs_subscription()) && course.getIs_subscription().equals("1")) {
            String coursePrice = installment.getAmount_description().getPayment().get(0);
            if (coursePrice == null || coursePrice.equals("")) {
                coursePrice = "0";
                tvMrp.setText(coursePrice);
            } else if (TextUtils.isEmpty(course.getGst_include()) || course.getGst_include().equals("0")) {
                gstValue = decimalFormat.format(Float.parseFloat(coursePrice) - getGSTExcludevalue(coursePrice));
                tvMrp.setText(String.format("%s %s %s", getCurrencySymbol(), coursePrice, "/-"));
            } else {
                gstValue = decimalFormat.format(calculateGST(coursePrice, course.getGst()));
                tvMrp.setText(String.format("%s %s %s", getCurrencySymbol(), (Float.parseFloat(coursePrice) + (Float.parseFloat(coursePrice) * Float.parseFloat(course.getGst())) / 100), "/-"));
            }
        }


//        if (!TextUtils.isEmpty(course.getIs_subscription()) && course.getIs_subscription().equals("1"))
//            tvMrp.setText(String.format("%s %s %s", getCurrencySymbol(), installment.getAmount_description().getLoan_amt(), "/-"));
//        tvInstallmentDuration.setVisibility(View.VISIBLE);
        tvInstallmentDuration.setText("For " + installment.getName());
    }
}
