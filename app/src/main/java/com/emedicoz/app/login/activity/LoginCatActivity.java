package com.emedicoz.app.login.activity;

import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNoNavActivity;
import com.emedicoz.app.login.fragment.ChangePassword;
import com.emedicoz.app.login.fragment.MobileVerification;
import com.emedicoz.app.login.fragment.OtpVerification;
import com.emedicoz.app.login.fragment.forgetDamspassword;
import com.emedicoz.app.login.fragment.forgetpassword;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;

/**
 * Created by Cbc-03 on 06/08/17.
 */

public class LoginCatActivity extends BaseABNoNavActivity {

    String otp;
    String Frag_type;
    int type;
    String affiliateCode;

    @Override
    protected void initViews() {
        if (getIntent().getExtras() != null) {
            Frag_type = getIntent().getExtras().getString(Const.FRAG_TYPE);
            otp = getIntent().getExtras().getString(Const.OTP);
            type = getIntent().getExtras().getInt(Constants.Extras.TYPE);
            affiliateCode=getIntent().getExtras().getString(Const.AFFILIATE_CODE);
        }
    }

/*    @Override
    protected boolean addBackButton() {
        return true;
    }*/

    @Override
    protected Fragment getFragment() {

        switch (Frag_type) {
            case Const.CHANGEPASSWORD:
                setToolbarTitle(getString(R.string.change_password));
                return ChangePassword.newInstance(otp);
            case Const.FORGETPASSWORD:
                setToolbarTitle(getString(R.string.forget_password));
                return forgetpassword.newInstance();
            case Const.FORGETPASSWORDDAMS:
                setToolbarTitle(getString(R.string.forget_password));
                return forgetDamspassword.newInstance();
            case Const.MOBILEVERIFICATION:
                setToolbarTitle(getString(R.string.mobile_verification));
                return MobileVerification.newInstance(type);
            case Const.OTPVERIFICATION:
                setToolbarTitle(getString(R.string.otp_verification));
                return OtpVerification.newInstance(otp, type, affiliateCode);
        }
        return null;
    }

}
