package com.emedicoz.app.ViewModel;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emedicoz.app.api.MyLogoutApi;
import com.emedicoz.app.modelo.Logout;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.apiinterfaces.LoginApiInterface;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpFragmentModel extends ViewModel
{

    private Call<JsonObject> call;
    public MutableLiveData<JsonObject> mutableLiveData=new MutableLiveData<>();
    public MutableLiveData<Throwable> onFailureLiveData=new MutableLiveData<>();
    public void getOtpVerify(User user,String otp,String type)
    {

        LoginApiInterface myApi = ApiClient.createService(LoginApiInterface.class);

        if (type == "FROM_MOBILE") {
            if (user.getIs_social() == "1") {
                 call = myApi.userLoginAuthenticationV3(
                        user.getEmail(),
                        otp,
                        user.getIs_social(),
                        user.getSocial_type(),
                        user.getSocial_tokken(),
                        user.getDevice_type(),
                        user.getDevice_tokken()
                );
            } else {
                 call = myApi.userLoginAuthenticationV2(
                        user.getMobile(),
                        user.getC_code(),
                        otp,
                        user.getIs_social(),
                        user.getSocial_type(),
                        user.getSocial_tokken(),
                        user.getDevice_type(),
                        user.getDevice_tokken()
                );
            }
        } else {
             call = myApi.userLoginAuthenticationV3(
                    user.getEmail(),
                    otp,
                    user.getIs_social(),
                    user.getSocial_type(),
                    user.getSocial_tokken(),
                    user.getDevice_type(),
                    user.getDevice_tokken()
            );
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response)
            {
                if(response.isSuccessful())
                {
                    JsonObject jsonObject=response.body();
                    mutableLiveData.setValue(jsonObject);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

               onFailureLiveData.setValue(t);
            }
        });
    }


}
