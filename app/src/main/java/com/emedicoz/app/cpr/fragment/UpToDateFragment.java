package com.emedicoz.app.cpr.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.cpr.adapter.subscriptionRecyclerAdapter;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.response.SubscriptionOptionList;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SubscriptionApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.common.BaseABNavActivity.bottomPanelRL;


public class UpToDateFragment extends Fragment implements View.OnClickListener {

    public WebView webView;
    BaseABNavActivity baseABNavActivity = null;
    ArrayList<String> arrList = new ArrayList<>();
    subscriptionRecyclerAdapter subscriptionRecyclerAdapter;
    ArrayList<SubscriptionOptionList> arrSubscription = new ArrayList<>();
    SingleCourseData singleCourseData;
    String id = "", isPurchased = "";
    LinearLayout errorLL;
    Button tryAgainBtn;
    ProgressDialog progressBar;
    String url;
    boolean successStatus = false;
    CountDownTimer countDownTimer;
    Dialog dialog;
    Activity activity;

    public UpToDateFragment() {
        // Required empty public constructor
    }

    public static UpToDateFragment newInstance() {
        return new UpToDateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    public void networkCallForGetSubscription() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        SubscriptionApiInterface apiInterface = ApiClient.createService(SubscriptionApiInterface.class);
        Call<JsonObject> response = apiInterface.getSubsDataForUser(SharedPreference.getInstance().getLoggedInUser().getId(), id);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            JSONArray jsonArray = data.optJSONArray(Constants.SUBSCRIPTION);
                            singleCourseData = gson.fromJson(data.toString(), SingleCourseData.class);
                            arrSubscription.clear();
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    SubscriptionOptionList subscriptionOptionList = new SubscriptionOptionList();
                                    subscriptionOptionList = new Gson().fromJson(jsonArray.opt(i).toString(), SubscriptionOptionList.class);
                                    subscriptionOptionList.setSelected(false);
                                    arrSubscription.add(subscriptionOptionList);
                                }
                            }
                            isPurchased = data.optString(Const.IS_PURCHASED);
                            if (isPurchased.equals("0") && !arrList.contains(id))
                                startCounter(data.optString(Const.TIMER));
                            else if (isPurchased.equals("0"))
                                paymentAlert();
                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(activity, t);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_quiz_web_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Helper.getStorageInstance(activity).getRecordObject(Const.SUBSCRIPTION_IDS) != null)
            arrList = (ArrayList<String>) Helper.getStorageInstance(activity).getRecordObject(Const.SUBSCRIPTION_IDS);
        progressBar = ProgressDialog.show(activity, "", "Loading...");
        baseABNavActivity = ((BaseABNavActivity) activity);
        errorLL = view.findViewById(R.id.errorLL);
        tryAgainBtn = view.findViewById(R.id.tryAgainBtn);
        webView = view.findViewById(R.id.webView);
        errorLL.setOnClickListener(this);


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.setWebViewClient(new WebViewClient() {
            boolean returnVal = false;


            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }

                if (!successStatus)
                    webView.setVisibility(View.VISIBLE);
            }


            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);

                if (url.equals(Constants.BASE_API_URL + "CPR/?user_id=" + SharedPreference.getInstance().getLoggedInUser().getId()
                        + "&display=" + SharedPreference.getInstance().getMasterHitResponse().getCpr_display())) {

                    id = "";
                    if (countDownTimer != null)
                        countDownTimer.cancel();
                }

                if (url.contains("#menu_true")) {
                    manageDrawer(true);
                    openDrawer();
                } else {
                    manageDrawer(false);
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                successStatus = true;
                url = failingUrl;
                errorLL.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.contains(getString(R.string.vatham_id_string))) {
                    Uri uri = Uri.parse(url);
                    String vathamId = uri.getQueryParameter(getString(R.string.vatham_id_string));
                    if (Objects.requireNonNull(vathamId).contains(","))
                        vathamId = vathamId.substring(0, vathamId.indexOf(","));

                    if (!id.equals(vathamId)) {
                        id = uri.getQueryParameter(getString(R.string.vatham_id_string));
                        networkCallForGetSubscription();//NetworkAPICall(API.API_GET_SUBSCRIPTION, false);
                    }
                }
                return returnVal;
            }
        });

        webView.loadUrl(Constants.BASE_API_URL + "CPR/?user_id=" + SharedPreference.getInstance().getLoggedInUser().getId() + "&display=" + SharedPreference.getInstance().getMasterHitResponse().getCpr_display());
    }

    @Override
    public void onResume() {
        super.onResume();
        baseABNavActivity.toolbar.setVisibility(View.GONE);
        bottomPanelRL.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(id))
            networkCallForGetSubscription();//NetworkAPICall(API.API_GET_SUBSCRIPTION, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tryAgainBtn:
                successStatus = false;
                errorLL.setVisibility(View.GONE);
                progressBar = ProgressDialog.show(activity, "", "Loading...");
                webView.loadUrl(url);
                break;
            case R.id.subscribeBtn:
                boolean status = false;
                for (SubscriptionOptionList data : arrSubscription) {
                    if (data.isSelected()) {
                        status = true;
                        break;
                    }
                }
                if (status) {
                    Intent courseInvoice = new Intent(activity, CourseActivity.class); // FRAG_TYPE, Const.COURSE_INVOICE CourseInvoice
                    courseInvoice.putExtra(Const.FRAG_TYPE, Const.COURSE_INVOICE);
                    courseInvoice.putExtra(Const.COURSE_DESC, singleCourseData);
                    courseInvoice.putExtra(Constants.Extras.TYPE, Const.CPR_INVOICE);
                    startActivity(courseInvoice);
                    dialog.cancel();
                } else
                    Toast.makeText(activity, "Please Select on of the subscription option.", Toast.LENGTH_SHORT).show();

                break;
            case R.id.cancleBtn:
                dialog.cancel();
                baseABNavActivity.customNavigationClick(Const.Cpr);
                break;
        }
    }

    public void manageDrawer(Boolean status) {
        if (status) {
            baseABNavActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            baseABNavActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public void openDrawer() {
        baseABNavActivity.drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        baseABNavActivity.toolbar.setVisibility(View.VISIBLE);
        bottomPanelRL.setVisibility(View.VISIBLE);
        baseABNavActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        if (countDownTimer != null)
            countDownTimer.cancel();
    }


    void startCounter(String time) {
        long sec = Integer.parseInt(time);

        long result = TimeUnit.SECONDS.toMillis(sec);


        if (countDownTimer != null)
            countDownTimer.cancel();

        countDownTimer = new CountDownTimer(result, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                arrList.add(id);
                Helper.getStorageInstance(activity).addRecordStore(Const.SUBSCRIPTION_IDS, arrList);
                paymentAlert();
            }
        };

        countDownTimer.start();
    }

    private void paymentAlert() {
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.subscription_dialog, null, false);
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(v);
        dialog.show();
        NonScrollRecyclerView recyclerSubscription;
        Button subscribeBtn, cancleBtn;

        recyclerSubscription = dialog.findViewById(R.id.recyclerSubscription);
        subscribeBtn = dialog.findViewById(R.id.subscribeBtn);
        cancleBtn = dialog.findViewById(R.id.cancleBtn);

        recyclerSubscription.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        subscriptionRecyclerAdapter = new subscriptionRecyclerAdapter(UpToDateFragment.this, arrSubscription);
        recyclerSubscription.setAdapter(subscriptionRecyclerAdapter);
        cancleBtn.setOnClickListener(this);
        subscribeBtn.setOnClickListener(this);
        dialog.show();
    }

    public void update(int position) {
        SharedPreference.getInstance().putString(Const.SUBSCRIPTION_SELECTED, arrSubscription.get(position).getMonths());
        for (int i = 0; i < arrSubscription.size(); i++) {
            if (position == i) {
                arrSubscription.get(i).setSelected(true);
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken()))
                    singleCourseData.setFor_dams(arrSubscription.get(i).getForDams());
                else
                    singleCourseData.setNon_dams(arrSubscription.get(i).getNonDams());
            } else
                arrSubscription.get(i).setSelected(false);
        }
        subscriptionRecyclerAdapter.notifyDataSetChanged();
    }


}
