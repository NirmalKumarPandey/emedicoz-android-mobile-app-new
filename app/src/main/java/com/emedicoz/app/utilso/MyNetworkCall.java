package com.emedicoz.app.utilso;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyNetworkCall {

    private static final String TAG = "MyNetworkCall";
    public Progress mProgress;
    MyNetworkCallBack myCBI;
    Context context;
    FlashCardApiInterface service;
    JSONObject jsonResponse;

    public MyNetworkCall(MyNetworkCallBack callBackInterface, Context context) {
        mProgress = new Progress(context);
        mProgress.setCancelable(false);
        myCBI = callBackInterface;
        this.context = context;
    }

    public void NetworkAPICall(final String apiType, final boolean showprogress) {
        Log.e(TAG, "NetworkAPICall ---> " + apiType);
        service = ApiClient.createService(FlashCardApiInterface.class);
        if (Helper.isConnected(context)) {
            try {
                if (showprogress) {
                    if (mProgress != null) {
                        if (context != null && !((Activity) context).isFinishing()) {
                            mProgress.show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Call<JsonObject> call = myCBI.getAPI(apiType, service);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (showprogress) {
                        try {
                            if (mProgress != null) {
                                if (context != null && !((Activity) context).isFinishing() && mProgress.isShowing())
                                    mProgress.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            try {
                                jsonResponse = new JSONObject(response.body().toString());
                                Log.e(TAG, " api:---> " + apiType + " onResponse: ---> " + jsonResponse);
                                if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                    myCBI.successCallBack(jsonResponse, apiType);
                                } else {
                                    RetrofitResponse.getApiData(context, jsonResponse.optString(Const.AUTH_CODE));
                                    myCBI.errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), apiType);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                Log.e(TAG, " api:---> " + apiType + " onResponseError:- " + response.errorBody().string());
                                myCBI.errorCallBack(response.message(), apiType);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            myCBI.ErrorCallBack(jsonResponse.optString(Const.MESSAGE), apiType);
                        }
                    } else {
                        try {
                            Log.e(TAG, " api:---> " + apiType + " onResponseError:- " + response.errorBody().string());
                            myCBI.errorCallBack(response.message(), apiType);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "onFailure: call---> " + apiType + " Throwable--->" + t.getMessage());
                    try {
                        if (mProgress != null) {
                            if (!((Activity) context).isFinishing() && mProgress.isShowing())
                                mProgress.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    myCBI.errorCallBack(context.getResources().getString(R.string.something_went_wrong), apiType);
                }
            });
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.internet_error_message), Toast.LENGTH_SHORT).show();
            myCBI.errorCallBack(context.getResources().getString(R.string.internet_error_message), apiType);
        }
    }



    public void NetworkAPICallWithoutDialog(final String apiType, final boolean showprogress) {
        Log.e(TAG, "NetworkAPICall ---> " + apiType);
        service = ApiClient.createService(FlashCardApiInterface.class);
        if (Helper.isConnected(context)) {

            Call<JsonObject> call = myCBI.getAPI(apiType, service);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            try {
                                jsonResponse = new JSONObject(response.body().toString());
                                Log.e(TAG, " api:---> " + apiType + " onResponse: ---> " + jsonResponse);
                                if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                    myCBI.successCallBack(jsonResponse, apiType);
                                } else {
                                    RetrofitResponse.getApiData(context, jsonResponse.optString(Const.AUTH_CODE));
                                    myCBI.errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), apiType);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                Log.e(TAG, " api:---> " + apiType + " onResponseError:- " + response.errorBody().string());
                                myCBI.errorCallBack(response.message(), apiType);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            myCBI.ErrorCallBack(jsonResponse.optString(Const.MESSAGE), apiType);
                        }
                    } else {
                        try {
                            Log.e(TAG, " api:---> " + apiType + " onResponseError:- " + response.errorBody().string());
                            myCBI.errorCallBack(response.message(), apiType);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "onFailure: call---> " + apiType + " Throwable--->" + t.getMessage());
                      myCBI.errorCallBack(context.getResources().getString(R.string.something_went_wrong), apiType);
                }
            });
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.internet_error_message), Toast.LENGTH_SHORT).show();
            myCBI.errorCallBack(context.getResources().getString(R.string.internet_error_message), apiType);
        }
    }


    public interface MyNetworkCallBack {

        Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service);

        void successCallBack(JSONObject jsonObject, String apiType) throws JSONException;

        void errorCallBack(String jsonString, String apiType);
    }
}

