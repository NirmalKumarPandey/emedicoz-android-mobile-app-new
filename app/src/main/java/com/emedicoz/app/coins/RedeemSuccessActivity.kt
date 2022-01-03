package com.emedicoz.app.coins
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.emedicoz.app.R

class RedeemSuccessActivity : AppCompatActivity() {
    lateinit var txtSendCode: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redeem_success_activity)
        initViews()
    }
    fun initViews(){
        txtSendCode = findViewById(R.id.txtSendCode)
        txtSendCode.setOnClickListener{
            intent = Intent(this,CheckOutActivity::class.java)
            startActivity(intent)
        }
    }
}