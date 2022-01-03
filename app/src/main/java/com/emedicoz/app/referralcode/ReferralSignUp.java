package com.emedicoz.app.referralcode;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.customviews.imagecropper.TakeImageClass;
import com.emedicoz.app.modelo.MediaFile;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.referralcode.model.BankInfo;
import com.emedicoz.app.referralcode.model.BankInfoResponse;
import com.emedicoz.app.referralcode.model.ProfileInfoBankUpdateData;
import com.emedicoz.app.referralcode.model.ProfileInfoBankUpdateResponse;
import com.emedicoz.app.referralcode.model.ProfileInfoResponse;
import com.emedicoz.app.referralcode.model.ProfileTypeList;
import com.emedicoz.app.referralcode.model.ProfileTypeResponse;
import com.emedicoz.app.referralcode.model.RefData;
import com.emedicoz.app.referralcode.model.RefSignUpResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.ReferralApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.amazonupload.AmazonCallBack;
import com.emedicoz.app.utilso.amazonupload.s3ImageUploading;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rilixtech.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferralSignUp extends Fragment implements View.OnClickListener, AmazonCallBack, TakeImageClass.ImageFromCropper {

    Progress mProgress;
    Activity activity;
    EditText firstNameET;
    EditText lastNameET;
    EditText emailAddressET;
    EditText phoneNumberET;
    EditText addressET;
    EditText postalCodeET;
    EditText cityET;
    EditText stateET;
    EditText accountHolderNameET;
    EditText bankNameET;
    EditText bankBranchNameET;
    EditText accountNumberET;
    EditText ifscCodeET;
    EditText panCardET;
    EditText adharCardET;
    Button signMeUpBtn, updateInfo;
    String firstName;
    String lastName;
    String emailAddress;
    String phoneNumber;
    String address;
    String postal;
    String city;
    String state;
    String country;
    String accountHolderName;
    String bankName;
    String bankBranchName;
    String accountNumber;
    String ifscCode;
    String bankInfoId;
    String referralCode;
    String deviceId = "";
    String c_code;
    String panCard;
    String adharCard;
    String instructorName;
    String adharImage;
    String panImage;
    String bankDocumentImage;
    CountryCodePicker ccp;
    TextView termCondId;
    CheckBox checkBoxId;
    private Spinner countryET;
    String imageUploadType;
    TextInputLayout firstNameTIL;
    TextInputLayout lastNameTIL;
    TextInputLayout emailAddressTIL;
    TextInputLayout phoneNumberTIL;
    TextInputLayout accountHolderNameTIL;
    TextInputLayout bankNameTIL;
    TextInputLayout accountNumberTIL;
    TextInputLayout ifscCodeTIL;
    List<String> list;
    Dialog dialog;
    ProfileInfoResponse profileInfoResponse;
    BankInfoResponse bankInfoResponse;
    List<BankInfo> bankInfo;
    ProfileTypeResponse profileTypeResponse;
    private AppCompatSpinner instructorNameSpinner;
    private TakeImageClass takeImageClass;
    TextView privacyPolicyTV, agreementTV;
    private s3ImageUploading s3ImageUploading;
    Pattern pattern;
    private ImageView imgBankDoc, imgPanCard, imgAdharCard;

    public static ReferralSignUp newInstance() {
        return new ReferralSignUp();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_referral_sign_up, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof HomeActivity) {
            ((HomeActivity) activity).itemMyCartFragment.setVisible(false);
            ((HomeActivity) activity).itemNotification.setVisible(false);
            ((HomeActivity) activity).bottomNavigationView.setVisibility(View.GONE);
            ActionBar actionBar = ((HomeActivity) activity).getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
            ((HomeActivity) activity).toolbarHeader.setVisibility(View.GONE);
        }
//        if (activity instanceof BaseABNavActivity) {
//
//            ((BaseABNavActivity) activity).manageToolbar(Constants.ScreenName.AFFILIATE);
//            BaseABNavActivity.bottomPanelRL.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        takeImageClass = new TakeImageClass(activity, this);
        Log.e("register", "deviceId: " + deviceId);
        String affiliateId = SharedPreference.getInstance().getLoggedInUser().getAffiliate_user_id();
        if (affiliateId != null && !affiliateId.isEmpty()) {
            networkCallForBankId();
            profileInfoResponse = SharedPreference.getInstance().getAffiliateProfileInfo();
            signMeUpBtn.setVisibility(View.GONE);
            updateInfo.setVisibility(View.VISIBLE);
            setData();
        } else
            setDefaultData();
        networkCallForProfileTypeList();
    }

    private void setDefaultData() {
        phoneNumberET.setText(SharedPreference.getInstance().getLoggedInUser().getMobile());
        emailAddressET.setText(SharedPreference.getInstance().getLoggedInUser().getEmail());
    }

    public void initViews(View view) {
        firstNameET = view.findViewById(R.id.firstNameET);
        lastNameET = view.findViewById(R.id.lastNameET);
        emailAddressET = view.findViewById(R.id.emailAddressET);
        phoneNumberET = view.findViewById(R.id.phoneNumberET);
        addressET = view.findViewById(R.id.addressET);
        postalCodeET = view.findViewById(R.id.postalCodeET);
        cityET = view.findViewById(R.id.cityET);
        stateET = view.findViewById(R.id.stateET);
        accountHolderNameET = view.findViewById(R.id.accountHolderNameET);
        bankNameET = view.findViewById(R.id.bankNameET);
        bankBranchNameET = view.findViewById(R.id.bankBranchNameET);
        accountNumberET = view.findViewById(R.id.accountNumberET);
        updateInfo = view.findViewById(R.id.updateInfo);
        updateInfo.setOnClickListener(this);
        ifscCodeET = view.findViewById(R.id.ifscCodeET);
        signMeUpBtn = view.findViewById(R.id.signMeUpBtn);
        countryET = view.findViewById(R.id.countryET);
        instructorNameSpinner = view.findViewById(R.id.instructorNameSpinner);
        firstNameTIL = view.findViewById(R.id.firstNameTIL);
        lastNameTIL = view.findViewById(R.id.lastNameTIL);
        emailAddressTIL = view.findViewById(R.id.emailAddressTIL);
        phoneNumberTIL = view.findViewById(R.id.phoneNumberTIL);
        accountHolderNameTIL = view.findViewById(R.id.accountHolderNameTIL);
        bankNameTIL = view.findViewById(R.id.bankNameTIL);
        accountNumberTIL = view.findViewById(R.id.accountNumberTIL);
        ifscCodeTIL = view.findViewById(R.id.ifscCodeTIL);
        termCondId = view.findViewById(R.id.termCondId);
        termCondId.setOnClickListener(this);
        privacyPolicyTV = view.findViewById(R.id.privacyPolicyTV);
        privacyPolicyTV.setOnClickListener(this);
        agreementTV = view.findViewById(R.id.agreementTV);
        agreementTV.setOnClickListener(this);
        panCardET = view.findViewById(R.id.panCardET);
        adharCardET = view.findViewById(R.id.aadhaarCardET);
        imgAdharCard = view.findViewById(R.id.imgAadharCard);
        imgPanCard = view.findViewById(R.id.imgPanCard);
        imgBankDoc = view.findViewById(R.id.imgBankDoc);
        checkBoxId = view.findViewById(R.id.checkBoxId);


        imgPanCard.setOnClickListener(v -> {
            imageUploadType = "pancard";
            takeImageClass.getImagePickerDialog(activity, getString(R.string.upload_pan_picture), getString(R.string.choose_image));
        });
        imgAdharCard.setOnClickListener(v -> {
            imageUploadType = "aadharcard";
            takeImageClass.getImagePickerDialog(activity, getString(R.string.upload_aadhar_picture), getString(R.string.choose_image));
        });
        imgBankDoc.setOnClickListener(v -> {
            imageUploadType = "bankdocument";
            takeImageClass.getImagePickerDialog(activity, getString(R.string.upload_bandoc_picture), getString(R.string.choose_image));
        });

        //termCondId.setOnClickListener(this);
        signMeUpBtn.setOnClickListener(this);
        ccp = view.findViewById(R.id.ccp);
        ccp.enableHint(false);
        ccp.registerPhoneNumberTextView(phoneNumberET);
        ccp.setOnCountryChangeListener(country -> {
            if (phoneNumberET.getError() != null) {
                phoneNumberET.setError(null);
            }
        });

    }

    private boolean regex_matcher(Pattern pattern, String string) {

        Matcher m = pattern.matcher(string);
        return m.find() && (m.group(0) != null);

    }

    public void profileTypeData() {
        List<ProfileTypeList> profileTypeList = profileTypeResponse.getData().getProfileTypeList();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < profileTypeList.size(); i++) {
            list.add(profileTypeList.get(i).getTitle());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), R.layout.single_row_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instructorNameSpinner.setAdapter(adapter);
        if (profileInfoResponse != null) {
            String compareValue = profileInfoResponse.getData().getProfileInfo().getProfileType();
            if (compareValue != null) {
                int spinnerPosition = adapter.getPosition(compareValue);
                instructorNameSpinner.setSelection(spinnerPosition);
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signMeUpBtn:
                ccp.isValid();
                if (checkValidation()) {
                    if (TextUtils.isEmpty(deviceId)) {
                        deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID);
                        if (TextUtils.isEmpty(deviceId)) {
                            deviceId = FirebaseInstanceId.getInstance().getToken();
                        }
                    }
                    networkCallForReferralApi();
                }
                //showAlertBankConfirm(view);
                break;
            case R.id.termCondId:
                Helper.GoToWebViewActivity(activity, Const.TERMS_URL);
                break;
            case R.id.updateInfo:
                if (checkValidation())
                    networkCallForUpdateAffiliateInfo();
                break;
            case R.id.privacyPolicyTV:
                Helper.GoToWebViewActivity(activity, Const.PRIVACY_URL);
                break;
            case R.id.agreementTV:
                Helper.GoToWebViewActivity(activity, Const.AGREEMENT_URL);
                break;
        }
    }


    public void setData() {
        if (profileInfoResponse == null || profileInfoResponse.getData() == null) return;

        firstName = profileInfoResponse.getData().getProfileInfo().getFirstName();
        lastName = profileInfoResponse.getData().getProfileInfo().getLastName();
        firstNameET.setText(firstName);
        lastNameET.setText(lastName);

        emailAddress = profileInfoResponse.getData().getProfileInfo().getEmail();
        emailAddressET.setText(emailAddress);
        phoneNumber = profileInfoResponse.getData().getProfileInfo().getPhone();
        phoneNumberET.setText(phoneNumber);

        address = profileInfoResponse.getData().getProfileInfo().getAddress();
        addressET.setText(address);
        postal = profileInfoResponse.getData().getProfileInfo().getPostalCode();
        postalCodeET.setText(postal);
        city = profileInfoResponse.getData().getProfileInfo().getCity();
        cityET.setText(city);
        state = profileInfoResponse.getData().getProfileInfo().getState();
        stateET.setText(state);
        panImage = profileInfoResponse.getData().getProfileInfo().getPanImage();
        adharImage = profileInfoResponse.getData().getProfileInfo().getAadharImage();
        bankDocumentImage = profileInfoResponse.getData().getProfileInfo().getBankImage();
        panCard = profileInfoResponse.getData().getProfileInfo().getPancard();
        panCardET.setText(panCard);
        adharCard = profileInfoResponse.getData().getProfileInfo().getAadhar();
        adharCardET.setText(adharCard);
        Glide.with(requireActivity())
                .load(profileInfoResponse.getData().getProfileInfo().getPanImage())
                .into(imgPanCard);
        Glide.with(getActivity())
                .load(profileInfoResponse.getData().getProfileInfo().getAadharImage())
                .into(imgAdharCard);
        Glide.with(getActivity())
                .load(profileInfoResponse.getData().getProfileInfo().getBankImage())
                .into(imgBankDoc);

    }

    public void setBankData() {
        accountHolderName = bankInfoResponse.getData().get(0).getAccountHolderName();
        bankName = bankInfoResponse.getData().get(0).getBankName();
        bankBranchName = bankInfoResponse.getData().get(0).getBankBranchName();
        accountNumber = bankInfoResponse.getData().get(0).getAccountNumber();
        ifscCode = bankInfoResponse.getData().get(0).getIfscCode();
        bankInfoId = bankInfoResponse.getData().get(0).getId();

        accountHolderNameET.setText(accountHolderName);
        bankNameET.setText(bankName);
        bankBranchNameET.setText(bankBranchName);
        accountNumberET.setText(accountNumber);
        ifscCodeET.setText(ifscCode);
    }

    public boolean checkValidation() {

        firstName = Helper.GetText(firstNameET);
        lastName = Helper.GetText(lastNameET);
        emailAddress = Helper.GetText(emailAddressET);
        phoneNumber = Helper.GetText(phoneNumberET);
        c_code = ccp.getSelectedCountryCodeWithPlus();
        panCard = Helper.GetText(panCardET);
        adharCard = Helper.GetText(adharCardET);
        address = Helper.GetText(addressET);
        postal = Helper.GetText(postalCodeET);
        city = Helper.GetText(cityET);
        state = Helper.GetText(stateET);
        accountHolderName = Helper.GetText(accountHolderNameET);
        bankName = Helper.GetText(bankNameET);
        bankBranchName = Helper.GetText(bankBranchNameET);
        accountNumber = Helper.GetText(accountNumberET);
        country = countryET.getSelectedItem().toString();
        instructorName = instructorNameSpinner.getSelectedItem().toString();

        ifscCode = Helper.GetText(ifscCodeET);
        boolean isDataValid = true;

        pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
        Matcher matcher = pattern.matcher(panCard);
        if (TextUtils.isEmpty(instructorName))
            isDataValid = Helper.showErrorToast(instructorNameSpinner, "Select Instructor Name");

        else if (TextUtils.isEmpty(firstName))
            isDataValid = Helper.DataNotValid(firstNameET);
//        else if (TextUtils.isEmpty(lastName))
//            isDataValid = Helper.DataNotValid(lastNameET);
        else if ((!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()))
            isDataValid = Helper.DataNotValid(emailAddressET, 1);
        else if (TextUtils.isEmpty(phoneNumber))
            isDataValid = Helper.DataNotValid(phoneNumberET);
        else if (ccp.getSelectedCountryCode().equals("91") &&
                Helper.isInValidIndianMobile(phoneNumberET.getText().toString()))
            isDataValid = Helper.DataNotValid(phoneNumberET, 2);

        else if (TextUtils.isEmpty(panCard)) {
            isDataValid = Helper.DataNotValid(panCardET);
        } else if (!matcher.matches()) {
            isDataValid = Helper.showErrorToast(panCardET, "PAN no is invalid");
        } else if (TextUtils.isEmpty(panImage))
            isDataValid = Helper.showErrorToast(panCardET, "PAN image is required");

        else if (TextUtils.isEmpty(adharCard)) {
            isDataValid = Helper.DataNotValid(adharCardET);
        } else if (adharCard.length() < 12){
            isDataValid = Helper.showErrorToast(adharCardET, "Aadhar number should be of 12 digits..");
        } else if (TextUtils.isEmpty(adharImage))
            isDataValid = Helper.showErrorToast(adharCardET, "Aadhar image is required");

        else if (TextUtils.isEmpty(accountHolderName))
            isDataValid = Helper.DataNotValid(accountHolderNameET);
        else if (TextUtils.isEmpty(bankName))
            isDataValid = Helper.DataNotValid(bankNameET);
        else if (TextUtils.isEmpty(accountNumber))
            isDataValid = Helper.DataNotValid(accountNumberET);
        else if (TextUtils.isEmpty(ifscCode))
            isDataValid = Helper.DataNotValid(ifscCodeET);
        else if (TextUtils.isEmpty(bankDocumentImage))
            isDataValid = Helper.showErrorToast(imgBankDoc, "Bank document image is required");

        else if (!checkBoxId.isChecked())
            isDataValid = Helper.showErrorToast(checkBoxId, "Please check terms and condition");
        return isDataValid;
    }

    private void networkCallForReferralApi() {
        mProgress.show();
        ReferralApiInterface apiInterface = ApiClient.createService(ReferralApiInterface.class);
        Call<JsonObject> response = apiInterface.getReferralSignUpMe(SharedPreference.getInstance().getLoggedInUser().getId(), firstName, lastName, emailAddress, phoneNumber, panCard, adharCard, address, postal, country, state, city, accountHolderName, bankName, bankBranchName, accountNumber, ifscCode,
                panImage, adharImage, bankDocumentImage, instructorName);
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
                            RefSignUpResponse refSignUpResponse = new Gson().fromJson(jsonObject, RefSignUpResponse.class);
                            RefData refData = refSignUpResponse.getData();
                            ReferEarnNowFragment referEarnNowFragment = new ReferEarnNowFragment();
                            Bundle args = new Bundle();
                            args.putString("AFFILIATE_USER_ID", refData.getId());

                            User user = SharedPreference.getInstance().getLoggedInUser();
                            user.setAffiliate_user_id(refData.getId());
                            SharedPreference.getInstance().setLoggedInUser(user);

                            referEarnNowFragment.setArguments(args);
                            ((BaseABNavActivity) activity).toolbarTitleTV.setText("Refer and Earn Now");
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            fragmentTransaction.replace(R.id.fragment_container, referEarnNowFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
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

    private void networkCallForUpdateAffiliateInfo() {
        mProgress.show();
        ReferralApiInterface apiInterface = ApiClient.createService(ReferralApiInterface.class);
        Call<JsonObject> response = apiInterface.updateAffProfileInfoWithBank(SharedPreference.getInstance().getLoggedInUser().getAffiliate_user_id(), bankInfoId, firstName, lastName, emailAddress, phoneNumber, panCard, adharCard, address, postal, country, state, city, accountHolderName, bankName, bankBranchName, accountNumber, ifscCode,
                panImage, adharImage, bankDocumentImage, instructorName);
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
                            ProfileInfoBankUpdateResponse profileInfoBankUpdateResponse = new Gson().fromJson(jsonObject, ProfileInfoBankUpdateResponse.class);
                            ProfileInfoBankUpdateData profileInfoBankUpdateData = profileInfoBankUpdateResponse.getData();

                            ReferEarnNowFragment referEarnNowFragment = new ReferEarnNowFragment();
                            Bundle args = new Bundle();
                            args.putString("AFFILIATE_USER_ID", profileInfoBankUpdateData.getAffiliateUserId());
                            referEarnNowFragment.setArguments(args);
                            ((BaseABNavActivity) activity).toolbarTitleTV.setText("Refer and Earn Now");
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            fragmentTransaction.replace(R.id.fragment_container, referEarnNowFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
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

    private void networkCallForBankId() {
        mProgress.show();
        ReferralApiInterface apiInterface = ApiClient.createService(ReferralApiInterface.class);
        Call<JsonObject> response = apiInterface.getAffiliateUserBankInfo(SharedPreference.getInstance().getLoggedInUser().getAffiliate_user_id());
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
                            bankInfoResponse = new Gson().fromJson(jsonObject, BankInfoResponse.class);
                            bankInfo = bankInfoResponse.getData();
                            setBankData();
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

    private void networkCallForProfileTypeList() {
        mProgress.show();
        ReferralApiInterface apiInterface = ApiClient.createService(ReferralApiInterface.class);
        Call<JsonObject> response = apiInterface.getProfileTypeList();
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
                            profileTypeResponse = new Gson().fromJson(jsonObject, ProfileTypeResponse.class);
                            profileTypeData();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (takeImageClass != null)
            takeImageClass.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void imagePath(String str) {
        if (str != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(str);
            if (bitmap != null) {
                switch (imageUploadType) {
                    case "pancard":
                        imgPanCard.setImageBitmap(bitmap);
                        break;
                    case "aadharcard":
                        imgAdharCard.setImageBitmap(bitmap);
                        break;
                    case "bankdocument":
                        imgBankDoc.setImageBitmap(bitmap);
                        break;
                }

                ArrayList<MediaFile> mediaFile = new ArrayList<>();
                MediaFile mf = new MediaFile();
                mf.setFile_type(Const.IMAGE);
                mf.setImage(bitmap);
                mediaFile.add(mf);

                s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_PROFILE_IMAGES, activity, this, null);
                s3ImageUploading.execute(mediaFile);
            }
        }
    }

    @Override
    public void onS3UploadData(ArrayList<MediaFile> images) {

        if (!images.isEmpty()) {
            String picUrl = images.get(0).getFile();
            if (TextUtils.isEmpty(picUrl)) {
                picUrl = "https://s3.ap-south-1.amazonaws.com/dams-apps-production/medicos_icon.png";
            } else {
                switch (imageUploadType) {
                    case "pancard":
                        panImage = picUrl;
                        break;
                    case "aadharcard":
                        adharImage = picUrl;
                        break;
                    case "bankdocument":
                        bankDocumentImage = picUrl;
                        break;
                }
            }
        }
    }
}
