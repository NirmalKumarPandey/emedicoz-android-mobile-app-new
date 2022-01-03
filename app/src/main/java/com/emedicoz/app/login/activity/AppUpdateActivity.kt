package com.emedicoz.app.login.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.emedicoz.app.R
import com.emedicoz.app.utilso.GenericUtils
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class AppUpdateActivity : AppCompatActivity() {
    lateinit var appUpdateManager: AppUpdateManager
    lateinit var button2: Button
    lateinit var btnClose: ImageView
    private val UPDATE_REQUEST_CODE = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        setContentView(R.layout.activity_app_update)

        button2 = findViewById(R.id.button2)
        btnClose = findViewById(R.id.imageView2)

        button2.setOnClickListener(View.OnClickListener {
            appUpdateManager = AppUpdateManagerFactory.create(this)
            appUpdateManager.appUpdateInfo.addOnSuccessListener {
                if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.isUpdateTypeAllowed(
                        AppUpdateType.IMMEDIATE)) {
                    appUpdateManager.startUpdateFlowForResult(it, AppUpdateType.IMMEDIATE, this, UPDATE_REQUEST_CODE)
                }
            }.addOnFailureListener {
                Log.e("ImmediateUpdateActivity", "Failed to check for update: $it")
            }
        })

        btnClose.setOnClickListener(View.OnClickListener { finish() })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            finish()
        }
    }
}