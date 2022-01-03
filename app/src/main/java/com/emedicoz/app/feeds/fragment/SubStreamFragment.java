package com.emedicoz.app.feeds.fragment;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.Registration;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.response.MasterRegistrationResponse;
import com.emedicoz.app.response.registration.SubStreamResponse;
import com.emedicoz.app.retrofit.ApiClient;
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

public class SubStreamFragment extends Fragment {

    TextView streamTV;
    private String openFragment;

    RelativeLayout subStreamRL;
    LinearLayout subStreamOptionLL;
    Button nextBtn;
    ArrayList<View> subStreamViewList;
    ArrayList<SubStreamResponse> subStreamResponseListFrag;
    ArrayList<String> subStreamList;
    Registration registration;
    User user;
    String regType;
    Progress mProgress;
    private Activity activity;

    protected View.OnClickListener onOptionClick = view1 -> {
        registration.setMaster_id_level_two("");
        registration.setMaster_id_level_two_name("");
        int i = 0;
        int j = 0;
        String substr = "";
        while (i < subStreamViewList.size()) {
            View v = subStreamViewList.get(i);
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

        while (j < subStreamResponseListFrag.size()) {
            SubStreamResponse sub = subStreamResponseListFrag.get(j);
            if (sub.getText_name().equals(substr)) {
                registration.setMaster_id_level_one(sub.getId());
                registration.setMaster_id_level_one_name(sub.getText_name());
            }
            j++;
        }
    };

    public SubStreamFragment() {
        // default constructor
    }

    public static SubStreamFragment newInstance(String regtype, String s) {
        SubStreamFragment fragment = new SubStreamFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Extras.TYPE, regtype);
        args.putString(Constants.Extras.OPEN_FROM, s);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);

        if (getArguments() != null) {
            regType = getArguments().getString(Constants.Extras.TYPE);
            openFragment = getArguments().getString(Constants.Extras.OPEN_FROM);
        }
        user = User.getInstance();
        registration = user.getUser_registration_info();
        activity = getActivity();
        subStreamResponseListFrag = new ArrayList<>();
        if (registration != null)
            subStreamResponseListFrag = getsubStreamList(SharedPreference.getInstance().getRegistrationResponse().getMain_sub_category());
    }

    public ArrayList<SubStreamResponse> getsubStreamList(ArrayList<SubStreamResponse> list) {
        ArrayList<SubStreamResponse> subStreamResponses = new ArrayList<>();
        for (SubStreamResponse streamResponse : list) {
            if (streamResponse.getParent_id().equalsIgnoreCase(registration.getMaster_id())) {
                subStreamResponses.add(streamResponse);
            }
        }

        return subStreamResponses;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subStreamResponseListFrag = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sub_stream, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity.setTitle("Sub Stream");
        streamTV = view.findViewById(R.id.mainstreamTV);

        subStreamRL = view.findViewById(R.id.substreamRL);

        subStreamOptionLL = view.findViewById(R.id.substreamoptionLL);

        nextBtn = view.findViewById(R.id.nextBtn);

        streamTV.setText(registration.getMaster_id_name() + " > ");

        if (!subStreamResponseListFrag.isEmpty()) {
            initSubStreamOptions();
        } else {
            networkCallForMasterReg();
        }
        nextBtn.setOnClickListener(v -> {
            if (GenericUtils.isEmpty(registration.getMaster_id_level_one_name()))
                Toast.makeText(activity, "Kindly select any Option first", Toast.LENGTH_SHORT).show();
            else {
                user.setUser_registration_info(registration);
//                if (getFragmentManager() != null)
//                    getFragmentManager().popBackStack(openFragment, 0);
//                else
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    public void initSubStreamOptions() {
        subStreamList = new ArrayList<>();
        for (SubStreamResponse subStreamResponse : subStreamResponseListFrag) {
            subStreamList.add(subStreamResponse.getText_name());
        }
        addViewtoSubStreamOpt();
    }

    public void addViewtoSubStreamOpt() {
        subStreamRL.setVisibility(View.VISIBLE);
        subStreamViewList = new ArrayList<>();
        int i = 0;
        while (i < subStreamList.size()) {
            View v = View.inflate(activity, R.layout.single_row_reg_option, null);
            TextView tv = v.findViewById(R.id.optionTextTV);
            v = changeBackgroundColor(v, 2);
            (v.findViewById(R.id.optioniconIBtn)).setVisibility(View.GONE);
            tv.setText(subStreamList.get(i));
            v.setTag(tv.getText());
            v.setOnClickListener(onOptionClick);

            if (subStreamList.get(i).equals(registration.getMaster_id_level_one_name())) {
                v = changeBackgroundColor(v, 1);
                (v.findViewById(R.id.optioniconIBtn)).setVisibility(View.VISIBLE);
                registration.setMaster_id_level_one(subStreamResponseListFrag.get(i).getId());
                registration.setMaster_id_level_one_name(subStreamResponseListFrag.get(i).getText_name());
            }
            subStreamOptionLL.addView(v);
            subStreamViewList.add(v);
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

    public void networkCallForMasterReg() {
        mProgress.show();
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getMasterRegistrationResponse();
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        mProgress.dismiss();
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            Helper.showErrorLayoutForNoNav(API.API_GET_MASTER_REGISTRATION_HIT, activity, 0, 0);
                            MasterRegistrationResponse masterRegistrationResponse = new Gson().fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), MasterRegistrationResponse.class);
                            subStreamResponseListFrag = getsubStreamList(masterRegistrationResponse.getMain_sub_category());
                            if (!subStreamResponseListFrag.isEmpty()) {
                                SharedPreference.getInstance().setMasterRegistrationData(masterRegistrationResponse);
                                initSubStreamOptions();
                                Log.e("substream", "post count " + subStreamResponseListFrag.size());
                            } else {
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                        } else {
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
                Helper.showErrorLayoutForNoNav(API.API_GET_MASTER_REGISTRATION_HIT, activity, 1, 2);
            }
        });
    }
}
