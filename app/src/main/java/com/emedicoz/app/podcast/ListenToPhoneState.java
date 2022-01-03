package com.emedicoz.app.podcast;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.emedicoz.app.utilso.eMedicozApp;


public class ListenToPhoneState extends PhoneStateListener {

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if (eMedicozApp.getInstance().getPodcastPlayer() != null) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    eMedicozApp.getInstance().getPodcastPlayer().start();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                case TelephonyManager.CALL_STATE_RINGING:
                    eMedicozApp.getInstance().getPodcastPlayer().pause();
                    break;
            }
        }
    }
}
