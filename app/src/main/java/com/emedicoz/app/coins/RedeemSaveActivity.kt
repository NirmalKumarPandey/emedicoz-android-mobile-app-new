package com.emedicoz.app.coins
import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.emedicoz.app.R

class RedeemSaveActivity : AppCompatActivity() {
    lateinit var lytViewAll: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redeem_save_layout)
       initViews()
    }
    fun initViews(){
        lytViewAll = findViewById(R.id.lytViewAll)
        lytViewAll.setOnClickListener{
            intent = Intent(this,RedeemCouponActivity::class.java)
            startActivity(intent)
        }
    }
}