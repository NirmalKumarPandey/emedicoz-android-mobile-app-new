package com.emedicoz.app.registration.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.utilso.Helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FillStudentDetailFragment extends Fragment implements View.OnClickListener {

    Activity activity;
    TextView studentTypeTV, studentNameTV, dobTV, genderTV, fatherNameTV, emailTV, mobileTV, otpTV, addressTV,
            countryTV, stateTV, pincodeTV, alternateMobileNoTV, studentImageTV;

    EditText studentNameET, dobET, fatherNameET, emailET, mobileET, otpET, addressET,
            countryET, stateET, pincodeET, alternateMobileNoET;
    String studentType,studentName,dob,fatherName,email,mobile,gender,otp,address,country,state,pincode,alternateMobileNo;
    Button btnPrevious;
    Button btnNext;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar;
    boolean isImageAdded = false;

    public static FillStudentDetailFragment newInstance() {
        FillStudentDetailFragment fragment = new FillStudentDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_registration_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myCalendar = Calendar.getInstance();
        initView(view);
    }

    private void initView(View view) {
        //........................For TextView----------------
        studentTypeTV = view.findViewById(R.id.studentTypeTV);
        studentNameTV = view.findViewById(R.id.studentNameTV);
        dobTV = view.findViewById(R.id.dobTV);
        genderTV = view.findViewById(R.id.genderTV);
        fatherNameTV = view.findViewById(R.id.fatherNameTV);
        emailTV = view.findViewById(R.id.emailTV);
        mobileTV = view.findViewById(R.id.mobileTV);
        otpTV = view.findViewById(R.id.otpTV);
        addressTV = view.findViewById(R.id.addressTV);
        countryTV = view.findViewById(R.id.countryTV);
        stateTV = view.findViewById(R.id.stateTV);
        pincodeTV = view.findViewById(R.id.pinTV);
        alternateMobileNoTV = view.findViewById(R.id.alternateMobileTV);
        studentImageTV = view.findViewById(R.id.studentImageTV);

        //........................For EditText----------------
        studentNameET = view.findViewById(R.id.studentNameET);
        dobET = view.findViewById(R.id.dobET);
        fatherNameET = view.findViewById(R.id.fatherNameET);
        emailET = view.findViewById(R.id.emailET);
        mobileET = view.findViewById(R.id.mobileET);
        otpET = view.findViewById(R.id.otpET);
        addressET = view.findViewById(R.id.addressET);
        countryET = view.findViewById(R.id.countryET);
        stateET = view.findViewById(R.id.stateET);
        pincodeET = view.findViewById(R.id.pinET);
        alternateMobileNoET = view.findViewById(R.id.alternateMobileET);
        
        //........................For Button----------------        
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        bindControls();
    }

    private void bindControls() {
        dobET.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        setAsteriskOnText();
    }

    private void setAsteriskOnText() {
        Helper.setCompulsoryAsterisk(studentNameTV, activity.getResources().getString(R.string.student_name));
        Helper.setCompulsoryAsterisk(dobTV, activity.getResources().getString(R.string.dob));
        Helper.setCompulsoryAsterisk(fatherNameTV, activity.getResources().getString(R.string.father_name));
        Helper.setCompulsoryAsterisk(emailTV, activity.getResources().getString(R.string.email_id));
        Helper.setCompulsoryAsterisk(mobileTV, activity.getResources().getString(R.string.mobile));
        Helper.setCompulsoryAsterisk(otpTV, activity.getResources().getString(R.string.otp));
        Helper.setCompulsoryAsterisk(addressTV, activity.getResources().getString(R.string.address));
        Helper.setCompulsoryAsterisk(countryTV, activity.getResources().getString(R.string.country));
        Helper.setCompulsoryAsterisk(stateTV, activity.getResources().getString(R.string.state));
        Helper.setCompulsoryAsterisk(pincodeTV, activity.getResources().getString(R.string.pin_code));
        Helper.setCompulsoryAsterisk(alternateMobileNoTV, activity.getResources().getString(R.string.alternate_mobile));
        Helper.setCompulsoryAsterisk(studentImageTV, activity.getResources().getString(R.string.student_image));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dobET:
                selectDateOfBirth();
                break;
            case R.id.btnPrevious:
                // TODO: 17-10-2020 need to add the functionality to go on previous screen 
                break;
            case R.id.btnNext:
                checkValidation();
                break;
            default:
                break;
        }
    }

    private void checkValidation() {
        studentName = Helper.GetText(studentNameET);
        dob = Helper.GetText(dobET);
        fatherName = Helper.GetText(fatherNameET);
        email = Helper.GetText(emailET);
        mobile = Helper.GetText(mobileET);
        otp = Helper.GetText(otpET);
        address = Helper.GetText(addressET);
        country = Helper.GetText(countryET);
        state = Helper.GetText(stateET);
        pincode = Helper.GetText(pincodeET);
        alternateMobileNo = Helper.GetText(alternateMobileNoET);
        boolean isDataValid = true;
        
        if (TextUtils.isEmpty(studentName))
            isDataValid = Helper.DataNotValid(studentNameET);
        else if (TextUtils.isEmpty(dob))
            isDataValid = Helper.DataNotValid(dobET);
        else if (TextUtils.isEmpty(fatherName))
            isDataValid = Helper.DataNotValid(fatherNameET);
        else if ((!Patterns.EMAIL_ADDRESS.matcher(email).matches()))
            isDataValid = Helper.DataNotValid(emailET, 1);
        else if (TextUtils.isEmpty(mobile))
            isDataValid = Helper.DataNotValid(mobileET);
        else if (TextUtils.isEmpty(otp))
            isDataValid = Helper.DataNotValid(otpET);
        else if (TextUtils.isEmpty(address))
            isDataValid = Helper.DataNotValid(addressET);
        else if (TextUtils.isEmpty(country))
            isDataValid = Helper.DataNotValid(countryET);
        else if (TextUtils.isEmpty(state))
            isDataValid = Helper.DataNotValid(stateET);
        else if (TextUtils.isEmpty(pincode))
            isDataValid = Helper.DataNotValid(pincodeET);
        else if (TextUtils.isEmpty(alternateMobileNo))
            isDataValid = Helper.DataNotValid(alternateMobileNoET);
        else if (!isImageAdded) {
            isDataValid = false;
            Toast.makeText(activity, activity.getResources().getString(R.string.select_an_image), Toast.LENGTH_SHORT).show();
        }
        
        if (isDataValid){
            // TODO: 17-10-2020 Hit api to save the data 
        }
    }

    private void selectDateOfBirth() {

        date = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDobEditText();
        };

        new DatePickerDialog(activity, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDobEditText() {
        // formatting of dob in date/month/year format
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dobET.setText(sdf.format(myCalendar.getTime()));
    }
}
