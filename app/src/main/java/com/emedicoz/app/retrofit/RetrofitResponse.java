package com.emedicoz.app.retrofit;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

public class RetrofitResponse {

    public static void getApiData(Context context, String authCode) {
        if (context == null) return;
//        handleAuthCode(context, authCode, "");

        if (authCode != null) {
            if (authCode.equals(Const.EXPIRY_AUTH_CODE)) {
                showLogoutMessage(context);
            }
//            else if (authCode.equals(Const.FORCE_UPDATE_APP_CODE)) {
//                Toast.makeText(context, R.string.update_your_app, Toast.LENGTH_LONG).show();
//                Helper.SignOutUser(context);
//            }
        }
    }

    private static void showLogoutMessage(Context context) {
        String msg = getMsg(context);
        if (GenericUtils.isEmpty(msg)) return;

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> Helper.SignOutUser(context), 1000);

    }

    private static String getMsg(Context context) {
        if (GenericUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getName()))
            return "";
        else
            return String.format("User %s is already logged in from some other device, so logging out from here",
                    SharedPreference.getInstance().getLoggedInUser().getName());
    }

    public static void handleAuthCode(Context context, String authCode, String message) {
        if (authCode != null) {
            if (Const.EXPIRY_AUTH_CODE.equals(authCode)) {
                showLogoutMessage(context);
            } else {
                Intent intent = new Intent(Const.ERROR_ALERT_INTENT_FILTER);
                intent.putExtra(Const.AUTH_CODE, authCode);
                intent.putExtra(Const.AUTH_MESSAGE, message);
                context.sendBroadcast(intent);
            }
        }
    }
}
