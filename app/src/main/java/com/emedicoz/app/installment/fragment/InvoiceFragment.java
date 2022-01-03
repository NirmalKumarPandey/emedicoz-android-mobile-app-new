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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.testseries.OrderHistoryData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;

public class InvoiceFragment extends Fragment implements View.OnClickListener, PurchasesUpdatedListener, MyNetworkCall.MyNetworkCallBack {

    Activity activity;
    OrderHistoryData orderHistoryData;
    ImageView ivCoverImage;
    TextView tvTitle;
    TextView tvPrice;
    TextView tvCutPrice;
    TextView tvValidity;
    TextView tvInstallmentAmount;
    TextView tvTotalAmount;
    Button btnPayNow;
    Progress mProgress;
    String paymentModeCheck = "";
    String finalPriceValue = "";
    String subscription_code = "";
    String paymentMetaValue = "";
    private BillingClient billingClient;
    private List<String> skuList = new ArrayList();
    MyNetworkCall myNetworkCall;
    private static final String TAG = "InvoiceFragment";
    private CharSequence expiry = "";

    public static InvoiceFragment newInstance(OrderHistoryData orderHistoryData) {
        Bundle args = new Bundle();
        InvoiceFragment fragment = new InvoiceFragment();
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
        return inflater.inflate(R.layout.fragment_invoice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orderHistoryData = (OrderHistoryData) Objects.requireNonNull(getArguments()).getSerializable(Const.ORDER_DETAIL);
        setupBillingClient();
        initViews(view);
    }

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(activity).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

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

    private void initViews(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        ivCoverImage = view.findViewById(R.id.iv_cover_image);
        tvTitle = view.findViewById(R.id.tv_title);
        tvPrice = view.findViewById(R.id.tv_price);
        tvCutPrice = view.findViewById(R.id.tv_cut_price);
        tvValidity = view.findViewById(R.id.tv_validity);
        tvInstallmentAmount = view.findViewById(R.id.tv_due_payment);
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        btnPayNow = view.findViewById(R.id.btn_pay_now);

        btnPayNow.setOnClickListener(this);

        setData();
    }

    private void setData() {
       /* for (int i = 0; i < orderHistoryData.get.size(); i++) {
            if (orderHistoryData.getInstallment().get(i).isSelected())
                expiry = orderHistoryData.getInstallment().get(i).getAmount_description().getExpiry();
        }*/

        finalPriceValue = orderHistoryData.getUpcoming_emi_amount();
        subscription_code = orderHistoryData.getSubscription_code();
        paymentMetaValue = new Gson().toJson(orderHistoryData.getPaymentMeta());
        Log.e(TAG, "setData: " + finalPriceValue);
        Log.e(TAG, "setData: " + subscription_code);

        if (!TextUtils.isEmpty(orderHistoryData.getCover_image())) {
            Glide.with(activity).load(orderHistoryData.getCover_image()).apply(new RequestOptions().error(R.drawable.dams)).into(ivCoverImage);
        }

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

        tvInstallmentAmount.setText(String.format("%s %s %s", getCurrencySymbol(), orderHistoryData.getUpcoming_emi_amount(), "/-"));

        tvTotalAmount.setText(Helper.GetText(tvInstallmentAmount));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pay_now:
                if (getCurrencySymbol().equals(eMedicozApp.getAppContext().getResources().getString(R.string.rs))) {
                    showPaymentModePopup();
                } else
                    showConversion();
                break;
            case R.id.btn_cancel:
                Dialog dialog = (Dialog) v.getTag();
                dialog.dismiss();
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
        }
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
        View view = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name), activity.getString(R.string.amount_wil_be_show_deducted_in_inr));
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

    private void initializeCoursePayment() {
        myNetworkCall.NetworkAPICall(API.API_INITIALIZE_COURSE_PAYMENT, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        switch (apiType) {
            case API.API_INITIALIZE_COURSE_PAYMENT:
                params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
                params.put(Const.COURSE_ID, orderHistoryData.getCourse_id());
                params.put(Const.EXPIRY, !TextUtils.isEmpty(expiry) ? expiry : "");
                params.put(Const.POINTS_RATE, orderHistoryData.getPoints_rate());
                params.put(Const.TAX, "");
                params.put(Const.POINTS_USED, "0");
                params.put(Const.PAYMENT_MODE, "1");
                params.put(Const.PAYMENT_META, paymentMetaValue);
                params.put(Const.PAYMENT_ATTEMPT, Integer.parseInt(orderHistoryData.getUpcoming_attemt()) - 1);
                params.put(Const.IS_COMLETE_PAYMENT, "");
                params.put(Const.COUPON_APPLIED, "");
                params.put("subscription_code", subscription_code);
                params.put("switch_plan", "");
                params.put("penalty", "0");
                params.put(Const.CHILD_COURSES, !TextUtils.isEmpty(orderHistoryData.getChild_courses()) ? orderHistoryData.getChild_courses() : "");
                params.put(Const.PAY_VIA, paymentModeCheck);
                params.put(Const.COURSE_PRICE, finalPriceValue);
                break;
            case API.API_COMPLETE_COURSE_PAYMENT:
                params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
                params.put(Const.COURSE_ID, orderHistoryData.getCourse_id());
                params.put(Const.COURSE_DISCOUNT, "");
                params.put(Const.COURSE_PRICE, finalPriceValue);
                params.put(Const.COURSE_INIT_PAYMENT_TOKEN, SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
                params.put(Const.COURSE_FINAL_PAYMENT_TOKEN, SharedPreference.getInstance().getString(Const.COURSE_FINAL_PAYMENT_TOKEN));
                break;
            case API.API_UPDATE_TRANSACTION_INFO:
                params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
                params.put(Const.COURSE_ID, orderHistoryData.getCourse_id());
                params.put(Const.COURSE_INIT_PAYMENT_TOKEN, SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
                params.put(Const.COURSE_FINAL_PAYMENT_TOKEN, SharedPreference.getInstance().getString(Const.COURSE_FINAL_PAYMENT_TOKEN));
                params.put("subscription_code", subscription_code);
                params.put("total_paid", finalPriceValue);
                break;
        }
        Log.e(TAG, "getAPI: api = " + apiType + " params = " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        switch (apiType) {
            case API.API_INITIALIZE_COURSE_PAYMENT:
                JSONObject data = jsonObject.optJSONObject(Const.DATA);
                SharedPreference.getInstance().putString(Const.COURSE_INIT_PAYMENT_TOKEN, Objects.requireNonNull(data).optString("pre_transaction_id"));
                // only for testing static data
                /*SharedPreference.getInstance().putString(Const.COURSE_FINAL_PAYMENT_TOKEN, String.valueOf(UUID.randomUUID()));
                finalTransForPaytm();
                API_UPDATE_TRANSACTION_INFO();
                completeCoursePayment();*/
                // only for testing static data

                paymentCallbacks(); // live
                break;
            case API.API_COMPLETE_COURSE_PAYMENT:
                Toast.makeText(activity, jsonObject.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                if (orderHistoryData.getCourse_type().equals("1") && orderHistoryData.getIs_combo().equals("1")) {
                    SharedPreference.getInstance().putBoolean(Const.IS_PAYMENT_DONE, true);
                    Intent mycourse = new Intent(activity, CourseActivity.class);
                    mycourse.putExtra(Const.FRAG_TYPE, Const.SUCCESSFULLY_PAYMENT_DONE);
                    activity.startActivity(mycourse);
                    activity.finishAffinity();
                } else if (orderHistoryData.getCourse_type().equalsIgnoreCase("1") || orderHistoryData.getCourse_type().equalsIgnoreCase("4")) {
                    Log.e("TYPE", "1");
                    Intent mycourse = new Intent(activity, CourseActivity.class); // AllCourse List FRAG_TYPE, Const.MYCOURSES
                    mycourse.putExtra(Const.FRAG_TYPE, Const.MYCOURSES);
                    SharedPreference.getInstance().putBoolean(Const.IS_PAYMENT_DONE, true);
                    activity.startActivity(mycourse);
                    activity.finish();
                } else if (orderHistoryData.getCourse_type().equalsIgnoreCase("2")) {
                    Log.e("TYPE", "2");
                    Intent mycourse = new Intent(activity, CourseActivity.class); // AllCourse List FRAG_TYPE, Const.MYCOURSES
                    mycourse.putExtra(Const.FRAG_TYPE, Const.MYTEST);
                    SharedPreference.getInstance().putBoolean(Const.IS_PAYMENT_DONE, true);
                    activity.startActivity(mycourse);
                    activity.finish();
                } else if (orderHistoryData.getCourse_type().equalsIgnoreCase("3")) {
                    Log.e("TYPE", "3");
                    Intent mycourse = new Intent(activity, CourseActivity.class); // AllCourse List FRAG_TYPE, Const.MYCOURSES
                    mycourse.putExtra(Const.FRAG_TYPE, Const.MYQBANK);
                    SharedPreference.getInstance().putBoolean(Const.IS_PAYMENT_DONE, true);
                    activity.startActivity(mycourse);
                    activity.finish();
                }
                break;
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        GenericUtils.showToast(activity, jsonString);
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

                    if (skuDetails.getSku().equals(orderHistoryData.getCourse_id())) {
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

    private void paymentCallbacks() {
        if (!TextUtils.isEmpty(paymentModeCheck)) {
            switch (paymentModeCheck) {
                case Const.ANIN:
                    if (billingClient.isReady()) {
                        Log.e(TAG, "onClick: Billing Client Ready");
                        Log.e(TAG, "onClick: " + orderHistoryData.getCourse_id());
                        skuList.clear();
                        skuList.add(orderHistoryData.getCourse_id());
                        loadAllSKUs();
                    }
                    break;
                case "PAY_TM":
                    networkCallForCheckSumForPaytm();
                    break;
            }
        }
    }

    private void networkCallForCheckSumForPaytm() {
        mProgress.show();
        Call<JsonObject> response = null;
        ApiInterface apiInterface = ApiClient.createService2(ApiInterface.class);
        if (Const.SERVER_TYPE.equals("LIVE")) {
            response = apiInterface.getCheckSumForPaytmLive(String.format(API.API_GET_CHECKSUM_FOR_PAYTM,
                    Const.SERVER_TYPE), Const.PAYTM_MID, SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                    Const.INDUSTRYTYPE_ID, SharedPreference.getInstance().getLoggedInUser().getUser_registration_info().getId(),
                    Const.CHANNELID, finalPriceValue, Const.PAYTM_WEBSITE,
                    Const.CALLBACKURL + SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
        } else {
            response = apiInterface.getCheckSumForPaytm(String.format(API.API_GET_CHECKSUM_FOR_PAYTM,
                    Const.SERVER_TYPE), Const.PAYTM_MID, SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
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

    private void makePaytmTransaction() {
        PaytmPGService Service;

        if (Const.SERVER_TYPE.equals("LIVE")) {
            Service = PaytmPGService.getProductionService();
        } else {
            Service = PaytmPGService.getStagingService("");
        }

        //Kindly create complete Map and checksum on your server side and then put it here in paramMap.

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(Const.MID, Const.PAYTM_MID); // live
        paramMap.put(Const.ORDER_ID, SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
        paramMap.put(Const.CUST_ID, SharedPreference.getInstance().getLoggedInUser().getUser_registration_info().getId());
        paramMap.put(Const.INDUSTRY_TYPE_ID, Const.INDUSTRYTYPE_ID);
        paramMap.put(Const.CHANNEL_ID, Const.CHANNELID);
        paramMap.put(Const.TXN_AMOUNT, finalPriceValue);
        paramMap.put(Const.WEBSITE, Const.PAYTM_WEBSITE); // live :- DAMSDeWAP Local :- APP_STAGING
        paramMap.put(Const.CALLBACK_URL, Const.CALLBACKURL + SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
//        if (Const.SERVER_TYPE.equals("LIVE")) {
//            paramMap.put(Const.CALLBACK_URL, Const.CALLBACKURL);
//        } else {
//            paramMap.put(Const.CALLBACK_URL, Const.CALLBACKURL + SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN));
//        }

//        paramMap.put(Const.CALLBACK_URL, Const.CALLBACKURL);
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
                        Log.d("LOG", "Payment Transaction : " + inResponse);
//                        Toast.makeText(activity, "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                        switch (Objects.requireNonNull(inResponse.getString("STATUS"))) {
                            case "TXN_FAILURE":
                                Toast.makeText(activity, inResponse.getString("RESPMSG"), Toast.LENGTH_SHORT).show();
                                break;
                            case "TXN_SUCCESS":
                                SharedPreference.getInstance().putString(Const.COURSE_FINAL_PAYMENT_TOKEN, inResponse.getString("TXNID"));
                                finalTransForPaytm();
                                completeCoursePayment();
                                API_UPDATE_TRANSACTION_INFO();
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

    private void finalTransForPaytm() {
        networkCallForfinalTransForPaytm();
    }

    private void networkCallForfinalTransForPaytm() {
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService2(ApiInterface.class);
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
        });
    }

    private void completeCoursePayment() {
        myNetworkCall.NetworkAPICall(API.API_COMPLETE_COURSE_PAYMENT, true);
    }

    private void API_UPDATE_TRANSACTION_INFO() {
        myNetworkCall.NetworkAPICall(API.API_UPDATE_TRANSACTION_INFO, true);
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
        if (purchase.getSku().equals(orderHistoryData.getCourse_id()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        SharedPreference.getInstance().putString(Const.COURSE_FINAL_PAYMENT_TOKEN, purchase.getOrderId());
                        finalTransForPaytm();
                        completeCoursePayment();
                        API_UPDATE_TRANSACTION_INFO();
                    }
                });
            }
        }
    }

}
