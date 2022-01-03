package com.emedicoz.app.feeds.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNoNavActivity;
import com.emedicoz.app.modelo.Country;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;

public class CollegeFragment extends Fragment implements MyNetworkCall.MyNetworkCallBack {

    public Dialog searchDialog;
    MyNetworkCall myNetworkCall;
    Activity activity;
    Country country;
    ArrayList<Country> countryArrayList = new ArrayList<>();
    ArrayList<Country> newCountryArrayList = new ArrayList<>();
    ImageView ivClearSearch;
    RecyclerView searchRecyclerview;
    TextView tvCancel;
    CountryStateCity countryStateCity;
    EditText collegeNameEt;
    EditText countryEt;
    Button submitBtn;
    private String TAG = "CollegeFragment";
    private EditText etSearch;
    private String countryId = "";

    public CollegeFragment() {
        // Required empty public constructor
    }

    public static CollegeFragment newInstance() {
        CollegeFragment fragment = new CollegeFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_college, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        bindControls();
    }


    private void initViews(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        countryEt = view.findViewById(R.id.countryEt);
        submitBtn = view.findViewById(R.id.submitBtn);
        collegeNameEt = view.findViewById(R.id.collegeNameEt);
    }

    private void bindControls() {
        countryEt.setOnClickListener((View view) -> hitCollegeApi());

        submitBtn.setOnClickListener((View view) -> {
            if (!TextUtils.isEmpty(Helper.GetText(countryEt)) && !TextUtils.isEmpty(Helper.GetText(collegeNameEt))) {
                ((BaseABNoNavActivity) activity).countryName = Helper.GetText(countryEt);
                ((BaseABNoNavActivity) activity).collegeName = Helper.GetText(collegeNameEt);
                ((BaseABNoNavActivity) activity).countryId = countryId;
                ((BaseABNoNavActivity) activity).getSupportFragmentManager().popBackStack();
            } else if (TextUtils.isEmpty(Helper.GetText(collegeNameEt))) {
                collegeNameEt.setError("Your institute name is empty.");
            } else if (TextUtils.isEmpty(Helper.GetText(countryEt))) {
                Toast.makeText(activity, "Please Select country", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hitCollegeApi() {
        myNetworkCall.NetworkAPICall(API.API_GET_COUNTRIES, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        return service.get(apiType);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        JSONArray jsonArray = jsonObject.optJSONArray(Const.DATA);
        if (Objects.requireNonNull(jsonArray).length() > 0) {
            Gson gson = new Gson();
            countryArrayList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                country = gson.fromJson(jsonArray.optJSONObject(i).toString(), Country.class);
                countryArrayList.add(country);
            }
            filterList(activity, countryArrayList);
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        Toast.makeText(activity, jsonString, Toast.LENGTH_SHORT).show();
    }

    public void filterList(Context context, ArrayList<Country> stateArrayList) {
        searchDialog = new Dialog(context);
        Objects.requireNonNull(searchDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent_background);
        searchDialog.setContentView(R.layout.country_state_city_dialog);
        searchDialog.setCancelable(true);
        etSearch = searchDialog.findViewById(R.id.et_search);
        TextView tvTitle = searchDialog.findViewById(R.id.tv_title);

        tvTitle.setText("Select College");
        etSearch.setHint("Search College");

        ivClearSearch = searchDialog.findViewById(R.id.iv_clear_search);
        tvCancel = searchDialog.findViewById(R.id.tv_cancel);
        ivClearSearch.setOnClickListener((View view) -> etSearch.setText(""));
        tvCancel.setOnClickListener((View view) -> searchDialog.cancel());
        searchRecyclerview = searchDialog.findViewById(R.id.search_recyclerview);
        searchRecyclerview.setHasFixedSize(true);
        searchRecyclerview.setLayoutManager(new LinearLayoutManager(context));
        countryStateCity = new CountryStateCity(activity, CollegeFragment.this, stateArrayList);
        searchRecyclerview.setAdapter(countryStateCity);

        textWatcher();
        searchDialog.show();
    }

    public void textWatcher() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               // can be used when required
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // can be used when required
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    ivClearSearch.setVisibility(View.VISIBLE);
                } else {
                    ivClearSearch.setVisibility(View.GONE);
                }
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });
    }

    private void filter(String text) {
        newCountryArrayList.clear();
        for (Country country : countryArrayList) {
            if (country.getName().toLowerCase().contains(text.toLowerCase())) {
                newCountryArrayList.add(country);
            }
        }
        if (!newCountryArrayList.isEmpty()) {
            searchRecyclerview.setVisibility(View.VISIBLE);

            countryStateCity.filterCountryList(newCountryArrayList);
        } else {
            searchRecyclerview.setVisibility(View.INVISIBLE);
        }
    }

    public void getCountryData(String countryId, String name) {
        this.countryId = countryId;
        Log.e(TAG, "getCountryData: " + countryId + " --- " + name);
        countryEt.setText(name);
    }
}
