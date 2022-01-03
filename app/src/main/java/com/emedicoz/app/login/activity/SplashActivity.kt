package com.emedicoz.app.login.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.legacy.widget.Space
import com.emedicoz.app.BuildConfig
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity
import com.emedicoz.app.feeds.activity.PostActivity
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.offlinedata.OnClearFromRecentService
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.nekolaboratory.EmulatorDetector
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SplashActivity : AppCompatActivity() {
    var TAG = "SplashScreen"
    var nextActivity: Class<*>? = null
    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    lateinit var handler: Handler
    lateinit var animationTop: Animation
    lateinit var animationBottom: Animation
    lateinit var imageLogo: ImageView
    lateinit var imageDams: ImageView
    lateinit var textView2: TextView
    lateinit var textView7: TextView
    private val SPLASH_TIME_OUT = 4000
    var USER_INFO = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        SharedPreference.getInstance().putBoolean(Const.IS_STREAM_CHANGE, true)
        SharedPreference.getInstance().putBoolean(Const.IS_LANDING_DATA, true)


        //animation------------------------
        animationTop = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        animationBottom = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)
        imageLogo = findViewById(R.id.imageView4)
        imageDams = findViewById(R.id.imageView5)
        textView2 = findViewById(R.id.textView2)
        textView7 = findViewById(R.id.textView7)

        imageLogo.animation = animationTop
        imageDams.animation = animationBottom
        textView7.animation = animationBottom
        typingAnimation()
        initFirebaseRemoteConfig()

        if (BuildConfig.DEBUG) {
            gotoNext()
        } else {
            if (RootUtil.isDeviceRooted()
                    || Build.MANUFACTURER.contains("Genymotion")
                    || Build.HOST.startsWith("Build")
                    || "google_sdk" == Build.PRODUCT || EmulatorDetector.isEmulator(this)
                    || Build.MODEL.contains("Emulator")
                    || Build.HARDWARE.contains("BlueStack") //bluestacks
            ) {
                showVirtualDeviceError()
            } else {
                gotoNext()
            }
        }

        checkUri()
    }

    private fun checkUri() {
        val intent = Intent("com.emedicoz.app")
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        val bundle = Bundle()
        bundle.putString("msg_from_browser", "Launched from Browser")
        intent.putExtras(bundle)
    }

    private fun initFirebaseRemoteConfig() {
        // Get Firebase Remote Config instance.
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        // Create a Remote Config Setting to enable developer mode,
        val configBuilder = FirebaseRemoteConfigSettings.Builder()
        // Sets the minimum interval between successive fetch calls.
        /**
         * For developer mode I'm setting 0 (zero) second
         * The default mode is 12 Hours. So for production mode it will be 12 hours
         */
        if (BuildConfig.DEBUG) {
            val cacheInterval: Long = 0
            configBuilder.minimumFetchIntervalInSeconds = cacheInterval
        }
        // finally build config settings and sets to Remote Config
        firebaseRemoteConfig.setConfigSettingsAsync(configBuilder.build())
        /**
         * Set default Remote Config parameter values. An app uses the in-app default values, and
         * when you need to adjust those defaults, you set an updated value for only the values you
         * want to change in the Firebase console
         */
//        //set default values
//        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        /* Constants.BASE_API_URL = firebaseRemoteConfig.getString("api_base_url");
        Constants.BASE_DOMAIN_URL = firebaseRemoteConfig.getString("domain_base_url");
*/fetchRemoteValues()
    }

    private fun fetchRemoteValues() {
        // [START fetch_config_with_callback]
        // override default value from Remote Config
        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this) { task: Task<Boolean?> ->
                    /*         Constants.BASE_API_URL = firebaseRemoteConfig.getString("api_base_url");
                 Constants.BASE_DOMAIN_URL = firebaseRemoteConfig.getString("domain_base_url");
*/if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("FirebaseConfig", "Config params updated: $updated")
                    //                        Toast.makeText(SplashScreen.this, "Fetch and activate succeeded", Toast.LENGTH_SHORT).show();
                } else {
//                        Toast.makeText(SplashScreen.this, "Fetch failed", Toast.LENGTH_SHORT).show();
                }
                }
        // [END fetch_config_with_callback]
        if (GenericUtils.isEmpty(Constants.BASE_API_URL)) {
            Constants.BASE_API_URL = BuildConfig.BASE_URL
            Constants.BASE_DOMAIN_URL = BuildConfig.BASE_URL_DOMAIN
        }
    }

    fun checkDeepLinking() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData -> // Get deep link from result (may be null if no link is found)
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        Log.e("onSuccess: ", "Not Null")
                        deepLink = pendingDynamicLinkData.link
                    } else {
                        Log.e("onSuccess: ", "Null")
                    }
                    if (deepLink != null) {
                        if (deepLink.getBooleanQueryParameter(Const.INVITEDBY, false)) {
                            val reff = deepLink.getQueryParameter(Const.INVITEDBY)
                            SharedPreference.getInstance().putString(Const.INVITEDBY, reff)
                            Log.e("Splash Log", "referrerUid $reff")
                        }
                        if (deepLink.getBooleanQueryParameter(Const.POSTID_REFFERED, false)) {
                            val postiId = deepLink.getQueryParameter(Const.POSTID_REFFERED)
                            SharedPreference.getInstance().putString(Const.POSTID_REFFERED, postiId)
                            Log.e("Splash Log", "referrerUid $postiId")
                        }
                        if (deepLink.getBooleanQueryParameter(Const.COURSE_ID, false)) {
                            val courseId = deepLink.getQueryParameter(Const.COURSE_ID)
                            handler.removeCallbacksAndMessages(null)
                            val course = Course()
                            course.id = courseId
                            val intent = Intent(this, CourseDetailActivity::class.java)
                            intent.putExtra(Const.COURSE, course)
                            intent.putExtra(Const.FRAG_TYPE, "")
                            startActivity(intent)
                            finish()
                            Log.e("Splash Log", "courseId $courseId")
                        }
                    }
                }
    }

    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "printHashKey()", e)
        } catch (e: Exception) {
            Log.e(TAG, "printHashKey()", e)
        }
    }

    private fun clearVideoLinks() {
        SharedPreference.getInstance().putString("DVL_LINK", "")
        SharedPreference.getInstance().putString("LINK", "")
        SharedPreference.getInstance().putString("SOLUTION_LINK", "")
    }

    fun showVirtualDeviceError() {
        val v = Helper.newCustomDialog(this, false, getString(R.string.app_name), "Please run this application on physical device only.")
        val space: Space
        val btn_cancel: Button
        val btn_submit: Button
        space = v.findViewById(R.id.space)
        btn_cancel = v.findViewById(R.id.btn_cancel)
        btn_submit = v.findViewById(R.id.btn_submit)
        space.visibility = View.GONE
        btn_submit.visibility = View.GONE
        btn_cancel.text = getString(R.string.close)
        btn_cancel.setOnClickListener {
            Helper.dismissDialog()
            finish()
        }
    }

    private fun gotoNext() {
        if (!isTaskRoot
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.action != null && intent.action == Intent.ACTION_MAIN) {
            finish()
            return
        }
        printHashKey()
        clearVideoLinks()
        try {
            val intent = Intent(eMedicozApp.getAppContext(), OnClearFromRecentService::class.java)
            startService(intent)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        if (SharedPreference.getInstance().getBoolean(Const.IS_USER_LOGGED_IN) &&
                SharedPreference.getInstance().getBoolean(Const.IS_USER_REGISTRATION_DONE)) {
            nextActivity = HomeActivity::class.java
        } else {
            nextActivity = SliderActivity::class.java
        }
        Helper.logUser(this)
        USER_INFO = String.format("%s %s%s", Helper.getDeviceModelName(), Const.APP_VERSION, Helper.getVersionName(this))
        Log.e("USERInfo", USER_INFO)
        SharedPreference.getInstance().putString(Constants.SharedPref.USER_INFO, USER_INFO)
        handler = Handler()
        handler.postDelayed({
            val intent: Intent = Intent(this, nextActivity)
            if (nextActivity == HomeActivity::class.java) { //replace activity FeedsActivity
                intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES)
            }
            if (nextActivity == PostActivity::class.java) {
                intent.putExtra(Const.FRAG_TYPE, Const.FOLLOW_THE_EXPERT_FIRST)
            }
            startActivity(intent)
            finish()
        }, SPLASH_TIME_OUT.toLong())
        val preversion = SharedPreference.getInstance().getInt(Const.VERSION_CODE)
        if (preversion > 0 && preversion < Helper.getVersionCode(this)) {
            SharedPreference.getInstance().remove(Const.MODERATOR_SELECTED_STREAM)
        }
        checkDeepLinking()
        Helper.generateKeyHash(this)
    }


    //typing text animation in eMedicoz tex
    private fun typingAnimation() {
        Handler().postDelayed({ textView2.text = "e" }, 200)
        Handler().postDelayed({ textView2.append("M") }, 300)
        Handler().postDelayed({ textView2.append("e") }, 400)
        Handler().postDelayed({ textView2.append("d") }, 500)
        Handler().postDelayed({ textView2.append("i") }, 600)
        Handler().postDelayed({ textView2.append("c") }, 700)
        Handler().postDelayed({ textView2.append("o") }, 800)
        Handler().postDelayed({ textView2.append("z") }, 900)
    }
}