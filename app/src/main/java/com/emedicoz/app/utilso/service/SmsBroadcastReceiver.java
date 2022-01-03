package com.emedicoz.app.utilso.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.emedicoz.app.utilso.Constants;


public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";
    String address = "";
    String smsBody = "";
    String OTP = "";
    String[] message;

    public void onReceive(Context context, Intent intent) {
        try {
            Bundle intentExtras = intent.getExtras();
            if (intentExtras != null) {
                Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
                String smsMessageStr = "";


                for (int i = 0; i < sms.length; ++i) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                    smsBody = smsMessage.getMessageBody();
                    address = smsMessage.getOriginatingAddress();
                    String PhoneNumber = smsMessage.getDisplayOriginatingAddress();
                    String senderNum = PhoneNumber;
                    String message = smsMessage.getDisplayMessageBody();

                    try {
                        Log.e("OTP", message);
                        Intent in = new Intent("broadCastOtp");

                        // Data you need to pass to activity
                        in.putExtra(Constants.Extras.MESSAGE, message);

                        context.sendBroadcast(in);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}