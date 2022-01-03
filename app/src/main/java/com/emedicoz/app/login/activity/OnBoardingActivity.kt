package com.emedicoz.app.login.activity

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.emedicoz.app.R
import com.emedicoz.app.courses.fragment.SelectCourseFragment
import com.emedicoz.app.databinding.ActivityOnBoardingBinding
import com.emedicoz.app.login.adapter.OnBoardingAdapter
import com.emedicoz.app.modelo.OnBoarding
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Helper

class OnBoardingActivity : AppCompatActivity() {
    val REQUEST_READ_PHONE_STATE = 100
    val REQUEST_READ_PHONE_STATE1 = 101
    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var onBoardingAdapter: OnBoardingAdapter
    private lateinit var onBoardingList: ArrayList<OnBoarding>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initOnBoardingList()
        onBoardingAdapter = OnBoardingAdapter(this, onBoardingList)
        binding.onBoardingViewPager2.adapter = onBoardingAdapter
        binding.wormDotsIndicator.setViewPager2(binding.onBoardingViewPager2)
        binding.btnLoginReg.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        setSpannable()

        val permissionCheck1 =
            ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") //

        val permissionCheck2 =
            ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE")
        val permissionCheck3 = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA")
        val permissionCheck4 =
            ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO")
        val permissionCheck7 =
            ContextCompat.checkSelfPermission(this, "android.permission.READ_PROFILE")

        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED || permissionCheck3 != PackageManager.PERMISSION_GRANTED || permissionCheck4 != PackageManager.PERMISSION_GRANTED || permissionCheck7 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.CAMERA",
                    "android.permission.RECORD_AUDIO",
                    "android.permission.READ_PROFILE"
                ), REQUEST_READ_PHONE_STATE
            )
        }
    }

    private fun setSpannable() {
        val string: String = resources.getString(R.string.by_login_text)
        val ss = SpannableString(string)
        val span: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Helper.GoToWebViewActivity(this@OnBoardingActivity, Const.TERMS_URL)
            }
        }
        ss.setSpan(ForegroundColorSpan(resources.getColor(R.color.sky_blue)), 25, string.length, 0)
        ss.setSpan(span, 25, string.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.termCondTV.text = ss
        binding.termCondTV.movementMethod = LinkMovementMethod.getInstance();
    }

    private fun initOnBoardingList() {
        onBoardingList = ArrayList()
        onBoardingList.add(
            OnBoarding(
                resources.getString(R.string.daily_challenge),
                resources.getString(R.string.daily_challenge_sub_title),
                ContextCompat.getDrawable(this, R.drawable.image_onboarding_daily_challenge)!!
            )
        )
        onBoardingList.add(
            OnBoarding(
                resources.getString(R.string.instant_doubt_solving),
                resources.getString(R.string.instant_doubt_solving_sub_title),
                ContextCompat.getDrawable(this, R.drawable.image_onboarding_doubt_solving)!!
            )
        )
        onBoardingList.add(
            OnBoarding(
                resources.getString(R.string.qbank),
                resources.getString(R.string.qbank_sub_title),
                ContextCompat.getDrawable(this, R.drawable.image_onboarding_qbank)!!
            )
        )

        onBoardingList.add(
            OnBoarding(
                resources.getString(R.string.test_series),
                resources.getString(R.string.test_series_sub_title),
                ContextCompat.getDrawable(this, R.drawable.image_onboarding_test_series)!!
            )
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_READ_PHONE_STATE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            REQUEST_READ_PHONE_STATE1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            else -> {
            }
        }
    }
}