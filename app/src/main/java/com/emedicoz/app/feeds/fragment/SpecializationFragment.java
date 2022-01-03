package com.emedicoz.app.feeds.fragment;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.modelo.Registration;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.response.MasterRegistrationResponse;
import com.emedicoz.app.response.registration.SpecializationResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SpecializationFragment extends Fragment {

    TextView streamTV;
    RelativeLayout specializationRL;
    LinearLayout specializationOptionLL;
    Button nextBtn;
    EditText otherET;
    SpecializationResponse specialization = new SpecializationResponse();
    ArrayList<View> specializationViewList;
    ArrayList<String> specializationList;
    ArrayList<SpecializationResponse> specializationResponseListfrag;
    Registration registration;
    User user;
    boolean isOthers = false;
    String others;
    String regType;
    private Activity activity;
    private String openFragment;

    protected View.OnClickListener onOptionClick = view1 -> {
        int i = 0;
        int j = 0;
        String substr = "";
        while (i < specializationViewList.size()) {
            View v = specializationViewList.get(i);
            if (view1.getTag() == v.getTag()) {
                (v.findViewById(R.id.optioniconIBtn)).setVisibility(View.VISIBLE);
                v = changeBackgroundColor(v, 1);
                substr = String.valueOf(v.getTag());
            } else {
                (v.findViewById(R.id.optioniconIBtn)).setVisibility(View.GONE);
                v = changeBackgroundColor(v, 2);
            }
            i++;
        }
        while (j < specializationResponseListfrag.size()) {
            SpecializationResponse sub = specializationResponseListfrag.get(j);
            if (sub.getText_name().equals(substr)) {
                specialization = sub;
                registration.setMaster_id_level_two(sub.getId());
                registration.setMaster_id_level_two_name(sub.getText_name());
                registration.setOptional_text("");
                otherET.setVisibility(View.GONE);
                otherET.setText("");
                isOthers = false;
            }

            j++;
        }
    };

    public SpecializationFragment() {
        // Required empty public constructor
    }

    public static SpecializationFragment newInstance(String regType, String s) {
        SpecializationFragment fragment = new SpecializationFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Extras.TYPE, regType);
        args.putString(Constants.Extras.OPEN_FROM, s);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            regType = getArguments().getString(Constants.Extras.TYPE);
            openFragment = getArguments().getString(Constants.Extras.OPEN_FROM);

        }
        user = User.getInstance();
        activity = getActivity();
        registration = user.getUser_registration_info();
        specializationResponseListfrag = new ArrayList<>();
        if (registration != null)
            specializationResponseListfrag = getSpecializationList(SharedPreference.getInstance().getRegistrationResponse().getSpecialization());
    }

    public ArrayList<SpecializationResponse> getSpecializationList(ArrayList<SpecializationResponse> list) {
        ArrayList<SpecializationResponse> specializationResponses = new ArrayList<>();
        for (SpecializationResponse specializationResponse : list) {
            if (specializationResponse.getParent_id().equalsIgnoreCase(registration.getMaster_id_level_one())) {
                specializationResponses.add(specializationResponse);
            }
        }

        return specializationResponses;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_specialization, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity.setTitle("Specialization");

        streamTV = view.findViewById(R.id.mainstreamTV);

        otherET = view.findViewById(R.id.otherET);

        specializationRL = view.findViewById(R.id.specializationRL);

        specializationOptionLL = view.findViewById(R.id.specializationoptionLL);

        nextBtn = view.findViewById(R.id.nextBtn);

        streamTV.setText(registration.getMaster_id_name() + " > " + registration.getMaster_id_level_one_name());
        if (!specializationResponseListfrag.isEmpty())
            initSpecializationOptions();
        else
            networkCallForMasterRegHit();

        nextBtn.setOnClickListener(view1 -> {
            if (isOthers) {
                checkValidation();
            } else if (!TextUtils.isEmpty(registration.getMaster_id_level_two())) {
                user.setUser_registration_info(registration);
//                if (getFragmentManager() != null)
//                    getFragmentManager().popBackStack(openFragment, 0);
//                else
                Objects.requireNonNull(getActivity()).onBackPressed();
            } else {
                Toast.makeText(activity, R.string.kindly_select_any_option_first, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        specializationResponseListfrag = new ArrayList<>();
    }

    public void checkValidation() {
        others = Helper.GetText(otherET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(others))
            isDataValid = Helper.DataNotValid(otherET);

        if (isDataValid) {
            registration.setMaster_id_level_two("");
            registration.setMaster_id_level_two_name("");
            registration.setOptional_text(others);
            user.setUser_registration_info(registration);
//            if (getFragmentManager() != null)
//                getFragmentManager().popBackStack(openFragment, 0);
//            else
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    public void initSpecializationOptions() {
        specializationList = new ArrayList<>();
        int i = 0;
        while (i < specializationResponseListfrag.size()) {
            specializationList.add(specializationResponseListfrag.get(i).getText_name());
            i++;
        }
        if (!specializationList.isEmpty()) {
            addViewToSpecializationOpt();
        }
    }

    public void addViewToSpecializationOpt() {
        specializationRL.setVisibility(View.VISIBLE);
        specializationViewList = new ArrayList<>();
        int i = 0;
        while (i < specializationList.size()) {
            View v = View.inflate(activity, R.layout.single_row_reg_option, null);
            TextView tv = v.findViewById(R.id.optionTextTV);
            v = changeBackgroundColor(v, 2);
            (v.findViewById(R.id.optioniconIBtn)).setVisibility(View.GONE);
            tv.setText(specializationList.get(i));
            v.setTag(tv.getText());
            v.setOnClickListener(onOptionClick);

            if (specializationList.get(i).equals(registration.getMaster_id_level_two_name())) {
                v = changeBackgroundColor(v, 1);
                (v.findViewById(R.id.optioniconIBtn)).setVisibility(View.VISIBLE);
            }
            specializationOptionLL.addView(v);
            specializationViewList.add(v);
            i++;
        }
    }

    public View changeBackgroundColor(View v, int type) {

        v.setBackgroundResource(R.drawable.bg_refcode_et);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        drawable.setColor(ContextCompat.getColor(activity, R.color.white));
        if (type == 1) drawable.setStroke(3, ContextCompat.getColor(activity, R.color.blue));
        else drawable.setStroke(3, ContextCompat.getColor(activity, R.color.transparent));
        return v;
    }

    private void networkCallForMasterRegHit() {
        ApiInterface feedApis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = feedApis.getMasterRegistrationResponse();
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Gson gson = new Gson();

                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            Helper.showErrorLayoutForNoNav(API.API_GET_MASTER_REGISTRATION_HIT, activity, 0, 0);

                            MasterRegistrationResponse masterRegistrationResponse = gson.fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), MasterRegistrationResponse.class);
                            specializationResponseListfrag = getSpecializationList(masterRegistrationResponse.getSpecialization());
                            SharedPreference.getInstance().setMasterRegistrationData(masterRegistrationResponse);
                            initSpecializationOptions();
                        } else {
                            Helper.showErrorLayoutForNoNav(API.API_GET_MASTER_REGISTRATION_HIT, activity, 1, 2);

                            JSONObject data = null;
                            String popupMessage = "";
                            data = GenericUtils.getJsonObject(jsonResponse);
                            popupMessage = data.getString("popup_msg");

                            RetrofitResponse.handleAuthCode(activity, jsonResponse.optString(Const.AUTH_CODE), popupMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("onFailure: ", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}
