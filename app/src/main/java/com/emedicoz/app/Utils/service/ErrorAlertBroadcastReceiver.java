package com.emedicoz.app.Utils.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Helper;

public class ErrorAlertBroadcastReceiver extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String code = intent.getStringExtra(Const.AUTH_CODE);
        String msg = intent.getStringExtra(Const.AUTH_MESSAGE);

        shoeAlertDialog(code, msg);
    }

    private void shoeAlertDialog(String code, String message) {
        switch (code) {

            case Const.SOFT_UPDATE_APP_CODE:
                Helper.showUpdatePopUp(context, message, false);
                break;
            case Const.FORCE_UPDATE_APP_CODE:
                Helper.showUpdatePopUp(context, message, true);
                break;

            case Const.SHOW_HARD_POP_UP:
                Helper.showSoftHardPopUp(context, message, true);
                break;
            case Const.SHOW_SOFT_POP_UP:
                Helper.showSoftHardPopUp(context, message, false);
                break;
        }
    }
}