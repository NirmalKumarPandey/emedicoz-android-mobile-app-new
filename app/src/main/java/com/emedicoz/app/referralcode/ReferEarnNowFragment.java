package com.emedicoz.app.referralcode;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.referralcode.adapter.MyAffiliationAdapter;
import com.emedicoz.app.referralcode.adapter.paymentHistoryAdapter;
import com.emedicoz.app.referralcode.model.AffPaymentHistoryResponse;
import com.emedicoz.app.referralcode.model.BankInfo;
import com.emedicoz.app.referralcode.model.BankInfoResponse;
import com.emedicoz.app.referralcode.model.MyAffiliation;
import com.emedicoz.app.referralcode.model.MyAffiliationData;
import com.emedicoz.app.referralcode.model.MyAffiliationResponse;
import com.emedicoz.app.referralcode.model.PaymentHistory;
import com.emedicoz.app.referralcode.model.PaymentHistoryData;
import com.emedicoz.app.referralcode.model.ProfileInfoResponse;
import com.emedicoz.app.referralcode.model.RefProfileData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.ReferralApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferEarnNowFragment extends Fragment implements View.OnClickListener {

    Activity activity;
    Progress mProgress;
    public MyAffiliationAdapter affiliationAdapter;
    TextView profileNameTV;
    TextView referralLinkTV;
    TextView earnedMoneyTV;
    TextView joinedFriendTV;
    String profileName;
    String referralCode;
    String affiliateUserId;
    String earnedMoney;
    String bankInfoId;
    int pageNumber = 1;
    int perPageTotal = 20;
    LinearLayout transferMoneyLL;
    LinearLayout profileInfoLayout;
    LinearLayout myAffiliateLayout;
    LinearLayout paymentHistoryLayout;
    Integer joinedFriends = null;
    RecyclerView affiliateRecyclerView;
    RecyclerView paymentHistoryRecycler;
    RefProfileData refProfileData;
    TextView myProfileTV;
    TextView myAffiliationsTV;
    TextView paymentHistoryTV;
    List<PaymentHistory> paymentHistoryList;
    List<MyAffiliation> affiliationList;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    public int previousTotalItemCount;
    LinearLayout shareLinkLL;
    String referralLink;
    ImageView profileImageAffiliate;
    public paymentHistoryAdapter paymentHistoryAdapter;

    public static ReferEarnNowFragment newInstance() {

        Bundle args = new Bundle();

        ReferEarnNowFragment fragment = new ReferEarnNowFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);

        assert getArguments() != null;
        affiliateUserId = getArguments().getString(Constants.Extras.AFFILIATE_USER_ID);
        if (affiliateUserId == null) {
            affiliateUserId = SharedPreference.getInstance().getLoggedInUser().getAffiliate_user_id();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.refer_earn_now_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        networkCallForProfileInfo();
        Objects.requireNonNull(((BaseABNavActivity) requireActivity())
                .getSupportActionBar()).setTitle("Refer and Earn Now");

    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof BaseABNavActivity) {

            ((BaseABNavActivity) activity).manageToolbar(Constants.ScreenName.AFFILIATE);
            BaseABNavActivity.bottomPanelRL.setVisibility(View.GONE);
        }
    }

    public void initViews(View view) {
        profileNameTV = view.findViewById(R.id.profile_name_TV);
        referralLinkTV = view.findViewById(R.id.referral_link_TV);
        transferMoneyLL = view.findViewById(R.id.transfer_money_LL);
        transferMoneyLL.setOnClickListener(this);

        earnedMoneyTV = view.findViewById(R.id.earned_money_TV);
        joinedFriendTV = view.findViewById(R.id.joined_friend_TV);
        shareLinkLL = view.findViewById(R.id.shareLinkLL);
        shareLinkLL.setOnClickListener(this);
        affiliateRecyclerView = view.findViewById(R.id.my_affiliation_recyclerview);
        paymentHistoryRecycler = view.findViewById(R.id.payment_history_recycler);

        setAffiliationAdapter();
        setPaymentHistoryAdapter();



        myAffiliationsTV = view.findViewById(R.id.my_affiliations_TV);
        paymentHistoryTV = view.findViewById(R.id.payment_history_TV);
        myProfileTV = view.findViewById(R.id.my_profile_TV);
        myProfileTV.setOnClickListener(this);
        myAffiliationsTV.setOnClickListener(this);
        paymentHistoryTV.setOnClickListener(this);
        profileInfoLayout = view.findViewById(R.id.profile_info_layout);
        myAffiliateLayout = view.findViewById(R.id.my_affiliate_layout);
        paymentHistoryLayout = view.findViewById(R.id.payment_history_layout);
        profileImageAffiliate = view.findViewById(R.id.profileImageAffiliate);

    }

    public void setAffiliationAdapter() {
        if (affiliationAdapter == null) {
            affiliationAdapter = new MyAffiliationAdapter(affiliationList, getContext());
            LinearLayoutManager myAffLayoutManager = new LinearLayoutManager(getContext());
            affiliateRecyclerView.setLayoutManager(myAffLayoutManager);
            affiliateRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int visibleItemCount = myAffLayoutManager.getChildCount();
                    int totalItemCount = myAffLayoutManager.getItemCount();
                    int firstVisibleItemPosition = myAffLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0 && totalItemCount >= perPageTotal) {
                            pageNumber++;
                            networkCallForMyAffiliation();
                        }
                    }
                }
            });
            affiliateRecyclerView.setAdapter(affiliationAdapter);
        } else {
            affiliationAdapter.setList(affiliationList);
            affiliationAdapter.notifyDataSetChanged();
        }
    }

    public void setPaymentHistoryAdapter() {
        if (paymentHistoryAdapter == null) {
            paymentHistoryAdapter = new paymentHistoryAdapter(paymentHistoryList, getContext());
            paymentHistoryRecycler.setHasFixedSize(true);
            LinearLayoutManager payLinearLayoutManager = new LinearLayoutManager(getContext());
            paymentHistoryRecycler.setLayoutManager(payLinearLayoutManager);
            paymentHistoryRecycler.setAdapter(paymentHistoryAdapter);

            paymentHistoryRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int visibleItemCount = payLinearLayoutManager.getChildCount();
                    int totalItemCount = payLinearLayoutManager.getItemCount();
                    int firstVisibleItemPosition = payLinearLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        isLoading = true;
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0 && totalItemCount >= perPageTotal) {
                            pageNumber++;
                            networkCallForPaymentHistory();

                        }
                    }
                }
            });
        } else {
            paymentHistoryAdapter.setList(paymentHistoryList);
            paymentHistoryAdapter.notifyDataSetChanged();
        }
    }

    private void networkCallForProfileInfo() {
        mProgress.show();
        ReferralApiInterface apiInterface = ApiClient.createService(ReferralApiInterface.class);
        Call<JsonObject> response = apiInterface.getProfileInfo(affiliateUserId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            ProfileInfoResponse profileInfoResponse = new Gson().fromJson(jsonObject, ProfileInfoResponse.class);
                            refProfileData = profileInfoResponse.getData();
                            if (refProfileData != null) {
                                earnedMoney = refProfileData.getReferralMoneyEarned().getReferralMoneyEarned();
                                joinedFriends = refProfileData.getFriendsJoined();
                                profileName = refProfileData.getProfileInfo().getFirstName();
                                referralCode = refProfileData.getProfileInfo().getReferralCode();
                                referralLink = /*"https://emedicoz.com/" +*/ referralCode;
                                referralLinkTV.setText(referralLink);

                                if (profileName != null) {
                                    profileNameTV.setText(profileName);
                                }
                                if (earnedMoney != null)
                                    earnedMoneyTV.setText(String.format("₹%s", earnedMoney));
                                else
                                    earnedMoneyTV.setText("₹0");


                                if (joinedFriends != null)
                                    joinedFriendTV.setText(String.format("%d", joinedFriends));
                                else
                                    joinedFriendTV.setText("0");
                                if (!TextUtils.isEmpty(refProfileData.getProfileInfo().getProfileImage())) {
                                    Glide.with(Objects.requireNonNull(getActivity())).load(refProfileData.getProfileInfo().getProfileImage()).into(profileImageAffiliate);
                                }
                                SharedPreference.getInstance().setAffiliateProfileInfo(profileInfoResponse);
                            } else {
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_LONG).show();
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                        } else {
                                profileInfoLayout.setVisibility(View.GONE);
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void networkCallForBankInfo() {
        mProgress.show();
        ReferralApiInterface apiInterface = ApiClient.createService(ReferralApiInterface.class);
        Call<JsonObject> response = apiInterface.getAffiliateUserBankInfo(affiliateUserId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            BankInfoResponse bankInfoResponse = new Gson().fromJson(jsonObject, BankInfoResponse.class);
                            List<BankInfo> bankInfo = bankInfoResponse.getData();
                            bankInfoId=bankInfoResponse.getData().get(0).getId();
                            showAlertBankConfirm(bankInfo);
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showAlertBankConfirm(List<BankInfo> bankInfo) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final View view = getLayoutInflater().inflate(R.layout.referral_bank_detail_confirm_dialog, null);
        builder.setView(view);
        // add a button
        TextView accHolderNameTv;
        TextView bankNameTv;
        TextView bankBranchTv;
        TextView accNumberTv;
        TextView ifscCodeTv;
        accHolderNameTv = view.findViewById(R.id.acc_holder_name_tv);
        bankNameTv = view.findViewById(R.id.bank_name_tv);
        bankBranchTv = view.findViewById(R.id.bank_branch_tv);
        accNumberTv = view.findViewById(R.id.acc_number_tv);
        ifscCodeTv = view.findViewById(R.id.ifsc_code_tv);
        if (bankInfo != null && !bankInfo.isEmpty()) {
            accHolderNameTv.setText(bankInfo.get(0).getAccountHolderName());
            bankNameTv.setText(bankInfo.get(0).getBankName());
            bankBranchTv.setText(bankInfo.get(0).getBankBranchName());
            accNumberTv.setText(bankInfo.get(0).getAccountNumber());
            ifscCodeTv.setText(bankInfo.get(0).getIfscCode());
        }


        builder.setPositiveButton("OK", (dialog, which) -> {
            // send data from the AlertDialog to the Activity
            networkCallForEnCashRequest();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {

        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void networkCallForMyAffiliation() {
        mProgress.show();
        isLoading = true;

        ReferralApiInterface apiInterface = ApiClient.createService(ReferralApiInterface.class);
        Call<JsonObject> response = apiInterface.myAffiliations(affiliateUserId, pageNumber, perPageTotal);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                isLoading = false;
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            MyAffiliationResponse myAffiliationResponse = new Gson().fromJson(jsonObject, MyAffiliationResponse.class);
                            MyAffiliationData myAffiliationData = myAffiliationResponse.getData();
                            affiliationList.addAll(myAffiliationResponse.getData().getMyAffiliations());
                            setAffiliationAdapter();
                            isLastPage = GenericUtils.isListEmpty(myAffiliationData.getMyAffiliations());

                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void networkCallForPaymentHistory() {
        mProgress.show();
        isLoading = true;

        ReferralApiInterface apiInterface = ApiClient.createService(ReferralApiInterface.class);
        Call<JsonObject> response = apiInterface.getAffiliationsPaymentHistory(affiliateUserId, pageNumber, perPageTotal);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                isLoading = false;
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            AffPaymentHistoryResponse affPaymentHistoryResponse = new Gson().fromJson(jsonObject, AffPaymentHistoryResponse.class);
                            PaymentHistoryData paymentHistoryData = affPaymentHistoryResponse.getData();
                            paymentHistoryList.addAll(paymentHistoryData.getPaymentHistory());
                            setPaymentHistoryAdapter();
                            isLastPage = GenericUtils.isListEmpty(paymentHistoryData.getPaymentHistory());
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void networkCallForEnCashRequest() {
        mProgress.show();
        ReferralApiInterface apiInterface = ApiClient.createService(ReferralApiInterface.class);
        Call<JsonObject> response = apiInterface.getAffiliateEncashRequest(affiliateUserId, bankInfoId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.transfer_money_LL:
                networkCallForBankInfo();
                break;
            case R.id.my_profile_TV:
                profileInfoLayout.setVisibility(View.VISIBLE);
                myAffiliateLayout.setVisibility(View.GONE);
                paymentHistoryLayout.setVisibility(View.GONE);
                myProfileTV.setBackgroundResource(R.color.blue);
                myProfileTV.setTextColor(ContextCompat.getColor(activity,R.color.white));
                myAffiliationsTV.setBackgroundResource(R.color.white);
                paymentHistoryTV.setBackgroundResource(R.color.white);
                networkCallForProfileInfo();
                myAffiliationsTV.setTextColor(ContextCompat.getColor(activity,R.color.black));
                paymentHistoryTV.setTextColor(ContextCompat.getColor(activity,R.color.black));
                break;
            case R.id.my_affiliations_TV:
                myAffiliateLayout.setVisibility(View.VISIBLE);
                profileInfoLayout.setVisibility(View.GONE);
                paymentHistoryLayout.setVisibility(View.GONE);

                pageNumber = 1;
                affiliationList = new ArrayList<>();
                networkCallForMyAffiliation();
                myAffiliationsTV.setBackgroundResource(R.color.blue);
                myAffiliationsTV.setTextColor(ContextCompat.getColor(activity,R.color.white));
                myProfileTV.setBackgroundResource(R.color.white);
                paymentHistoryTV.setBackgroundResource(R.color.white);
                myProfileTV.setTextColor(ContextCompat.getColor(activity,R.color.black));
                paymentHistoryTV.setTextColor(ContextCompat.getColor(activity,R.color.black));
                break;
            case R.id.payment_history_TV:
                paymentHistoryLayout.setVisibility(View.VISIBLE);
                profileInfoLayout.setVisibility(View.GONE);
                myAffiliateLayout.setVisibility(View.GONE);

                pageNumber = 1;
                paymentHistoryList = new ArrayList<>();
                networkCallForPaymentHistory();
                paymentHistoryTV.setBackgroundResource(R.color.blue);
                paymentHistoryTV.setTextColor(ContextCompat.getColor(activity,R.color.white));
                myProfileTV.setBackgroundResource(R.color.white);
                myAffiliationsTV.setBackgroundResource(R.color.white);
                myAffiliationsTV.setTextColor(ContextCompat.getColor(activity,R.color.black));
                myProfileTV.setTextColor(ContextCompat.getColor(activity,R.color.black));
                break;
            case R.id.shareLinkLL:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = referralLink;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            default:
        }
    }
}
