package com.emedicoz.app.feeds.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class ChangePasswordFragment extends Fragment implements MyNetworkCall.MyNetworkCallBack, TextWatcher {
    private static final String TAG = "ChangePasswordFragment";
    ImageView ivLogo;
    TextInputLayout oldPasswordTIL;
    TextInputLayout newPasswordTIL;
    TextInputLayout retryPasswordTIL;
    EditText oldPasswordET;
    EditText newPasswordET;
    EditText retryPasswordET;
    MyNetworkCall myNetworkCall;
    Activity activity;
    Button submitBtn;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance() {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_changepassword, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        bindControls();
    }

    private void initViews(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        oldPasswordET = view.findViewById(R.id.oldPasswordET);
        newPasswordET = view.findViewById(R.id.newpasswordET);
        retryPasswordET = view.findViewById(R.id.retrypasswordET);
        ivLogo = view.findViewById(R.id.iv_logo);
        submitBtn = view.findViewById(R.id.submitBtn);
        oldPasswordTIL = view.findViewById(R.id.oldPasswordTIL);
        retryPasswordTIL = view.findViewById(R.id.retrypasswordTIL);
        newPasswordTIL = view.findViewById(R.id.newpasswordTIL);
        ivLogo.setVisibility(View.VISIBLE);
        oldPasswordTIL.setVisibility(View.VISIBLE);
    }

    private void bindControls() {
        oldPasswordET.addTextChangedListener(this);
        newPasswordET.addTextChangedListener(this);
        retryPasswordET.addTextChangedListener(this);

        submitBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(Helper.GetText(oldPasswordET))) {
                oldPasswordTIL.setError(activity.getResources().getString(R.string.enter_old_password));
            } else if (TextUtils.isEmpty(Helper.GetText(newPasswordET))) {
                newPasswordTIL.setError(activity.getResources().getString(R.string.enter_new_password));
            } else if (Helper.GetText(newPasswordET).length() < 8 || Helper.GetText(newPasswordET).length() > 15) {
                Toast.makeText(activity, activity.getResources().getString(R.string.password_must_contain), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(Helper.GetText(retryPasswordET))) {
                retryPasswordTIL.setError(activity.getResources().getString(R.string.retype_password));
            } else {
                hitChangePasswordApi();
            }
        });
    }

    private void hitChangePasswordApi() {
        myNetworkCall.NetworkAPICall(API.API_GET_CHANGE_DAMS_PASSWORD, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> paream = new HashMap<>();
        paream.put(Const.USERNAME, SharedPreference.getInstance().getLoggedInUser() != null ? SharedPreference.getInstance().getLoggedInUser().getDams_tokken() : "");
        paream.put(Const.OLD_PASSWORD, Helper.GetText(oldPasswordET));
        paream.put(Const.NEW_PASSWORD, Helper.GetText(newPasswordET));
        Log.e(TAG, "getAPI: " + paream);
        return service.postData(apiType, paream);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        Helper.showToast(jsonObject.optString(Constants.Extras.MESSAGE), activity);
        Helper.SignOutUser(activity);
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        Helper.showToast(jsonString, activity);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               // can be used when required
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
               // can be used when required
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == oldPasswordET.getEditableText()) {
            if (Helper.GetText(oldPasswordET).length() == 0) {
                oldPasswordTIL.setError(activity.getResources().getString(R.string.enter_old_password));
            } else {
                oldPasswordTIL.setError(null);
            }
        } else if (editable == newPasswordET.getEditableText()) {
            if (Helper.GetText(newPasswordET).length() == 0) {
                newPasswordTIL.setError(activity.getResources().getString(R.string.enter_new_password));
            } else if (Helper.GetText(newPasswordET).length() < 8 || Helper.GetText(newPasswordET).length() > 15) {
                Toast.makeText(activity, activity.getResources().getString(R.string.password_must_contain), Toast.LENGTH_SHORT).show();
            } else {
                newPasswordTIL.setError(null);
            }
        } else if (editable == retryPasswordET.getEditableText()) {
            if (Helper.GetText(retryPasswordET).length() == 0) {
                retryPasswordTIL.setError(activity.getResources().getString(R.string.retype_password));
            } else {
                retryPasswordTIL.setError(null);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof HomeActivity) {
            ((HomeActivity)activity).itemMyCartFragment.setVisible(false);

            //((HomeActivity) activity).itemSavedNotesFragment.setVisible(false);
            ((HomeActivity)activity).toolbarHeader.setVisibility(View.GONE);
            ActionBar actionBar = ((HomeActivity)activity).getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }
}
