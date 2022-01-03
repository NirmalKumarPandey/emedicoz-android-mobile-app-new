package com.emedicoz.app.customviews.ccAvenuePayment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.emedicoz.app.R;
import com.emedicoz.app.utilso.GenericUtils;


public class StatusActivity extends AppCompatActivity {
    TextView statusMessage, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_status);

        Intent mainIntent = getIntent();
        statusMessage = findViewById(R.id.textView1);
        message = findViewById(R.id.errorMessageTV);
        statusMessage.setText(mainIntent.getStringExtra("transStatus"));
        statusMessage.setText(mainIntent.getStringExtra("orderStatus"));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setResult(RESULT_OK);
                finish();
            }
        }, 3000);
    }


}
