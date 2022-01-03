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
import com.emedicoz.app.utilso.SharedPreference;

import fr.maxcom.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutViewModel extends ViewModel
{
    private Context context;
    public MutableLiveData<Logout> mutableLiveData;
    public LiveData<Logout> getAllUser()
    {
        if(mutableLiveData==null) {
            mutableLiveData = new MutableLiveData<Logout>();
            User user = SharedPreference.getInstance().getLoggedInUser();
            MyLogoutApi myLogoutApi = ApiClient.getService();
            Call<Logout> call = myLogoutApi.logoutProfile(user.getId(), user.getDevice_tokken());
            call.enqueue(new Callback<Logout>() {
                @Override
                public void onResponse(Call<Logout> call, Response<Logout> response)
                {
                    if(response.isSuccessful())
                    {
                          Logout logout=response.body();
                        mutableLiveData.setValue(logout);
                    }
                }
                @Override
                public void onFailure(Call<Logout> call, Throwable t)
                {
                    Toast.makeText(context,"Response="+t.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
        return mutableLiveData;
    }
}
