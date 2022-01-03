package com.emedicoz.app.feeds.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.emedicoz.app.R
import com.emedicoz.app.ViewModel.LogoutViewModel
import com.emedicoz.app.api.MyLogoutApi
import com.emedicoz.app.cart.AddressList
import com.emedicoz.app.reward.activity.MyCoinVActivity
import com.emedicoz.app.courses.activity.CourseActivity
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.ActivityNewProfileBinding
import com.emedicoz.app.gamification.GamificationActivity
import com.emedicoz.app.modelo.Logout
import com.emedicoz.app.modelo.User
import com.emedicoz.app.mycourses.MyCourseActivity
import com.emedicoz.app.notifications.NotificationSettingsActivity
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.ProfileApiInterface
import com.emedicoz.app.reward.activity.MyCoinActivity
import com.emedicoz.app.utilso.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_new_profile.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*

class NewProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityNewProfileBinding
    private var id: String? = null
    private var type: String? = null
    private lateinit var user: User
    private var bitmap: Bitmap? = null
    private lateinit var mprogress: Progress
    private lateinit var readmoreclick: ClickableSpan
    private lateinit var readlessclick: ClickableSpan
    private var Course_Description_Length = 15
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityNewProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mprogress = Progress(this)
        mprogress.setCancelable(false)

        //Changes...................

        lateinit var alertDialog: AlertDialog

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.logout)
        builder.setMessage(R.string.logoutMessage)
        builder.setIcon(android.R.drawable.ic_dialog_alert)


        val logoutViewModel: LogoutViewModel = ViewModelProvider(this).get(LogoutViewModel::class.java)
        logoutViewModel.allUser.observe(this,Observer(){
            if(it.status==true)
            {
                fun message()
                {
                    Toast.makeText(applicationContext,"Response===="+it.message,Toast.LENGTH_LONG).show();
                }
                logoutCard.setOnClickListener {

                    builder.setPositiveButton("No") { dialogInterface, which ->
                        alertDialog.dismiss()
                    }
                    builder.setNegativeButton("Yes") { dialogInterface, which ->
                        alertDialog.dismiss()
                        message()
                        Helper.SignOutUser(this@NewProfileActivity)
                    }
                    alertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                }

            }
        })

        binding.apply {
            val clickListener = this@NewProfileActivity
            editTV.setOnClickListener(clickListener)
            changeSteam.setOnClickListener(clickListener)
            myAddressProfile.setOnClickListener(clickListener)
            myCoursesProfileCard.setOnClickListener(clickListener)
            myPostProfileCard.setOnClickListener(clickListener)
            myVideosProfileCard.setOnClickListener(clickListener)
            myBookmarkProfileCard.setOnClickListener(clickListener)
            myRewardsProfileCard.setOnClickListener(clickListener)
            myDownloadsProfileCard.setOnClickListener(clickListener)
            myTestSeriesProfileCard.setOnClickListener(clickListener)
            myOrdersProfileCard.setOnClickListener(clickListener)
            followerLL.setOnClickListener(clickListener)
            followingLL.setOnClickListener(clickListener)
            toolBarProfile.toolbarBackIV.setOnClickListener(clickListener)
          //  logoutCard.setOnClickListener(clickListener)
            gamificationLayout.setOnClickListener(clickListener)
            myNotificationSetting.setOnClickListener(clickListener)
        }
        val masterResp = SharedPreference.getInstance().masterHitResponse
        if (masterResp != null && masterResp.show_bookmark != null && masterResp.show_bookmark.equals(
                "0",
                ignoreCase = true
            )
        ) {
            binding.myBookmarkProfileCard.visibility = View.GONE
        } else {
            binding.myBookmarkProfileCard.visibility = View.VISIBLE
        }
        if (masterResp != null && masterResp.show_my_downloads != null && masterResp.show_my_downloads.equals(
                "0",
                ignoreCase = true
            )
        ) {
            binding.myDownloadsProfileCard.visibility = View.GONE
        } else {
            binding.myDownloadsProfileCard.visibility = View.VISIBLE
        }
        if (intent != null) {
            id = intent.getStringExtra(Constants.Extras.ID)
            type = intent.getStringExtra(Constants.Extras.TYPE)
            if (!TextUtils.isEmpty(id)) networkCallForGetUser() //networkCall.NetworkAPICall(API.API_GET_USER, true);
        }

        binding.toolBarProfile.toolbarTitleTV.text = "Profile"
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.myCoursesProfileCard -> {
                val mycourse = Intent(this, MyCourseActivity::class.java)
                //val mycourse = Intent(this, CourseActivity::class.java) // AllCourse List
                //mycourse.putExtra(Const.FRAG_TYPE, Const.MYCOURSES)
                startActivity(mycourse)
            }
            R.id.myPostProfileCard -> {
                val myPosts = Intent(this, CourseActivity::class.java) // AllCourse List
                myPosts.putExtra(Const.FRAG_TYPE, "MY_POST")
                myPosts.putExtra("ADAPTER_TYPE", "post")
                myPosts.putExtra(Constants.Extras.ID, id)
                startActivity(myPosts)
            }
            R.id.myNotificationSetting -> {
                val intent = Intent(this, NotificationSettingsActivity::class.java)
                startActivity(intent)

            }
            R.id.myVideosProfileCard -> {
            }
            R.id.myBookmarkProfileCard -> {
                val myBookmarks = Intent(this, MyBookmarksActivity::class.java)
                //val myBookmarks = Intent(this, CourseActivity::class.java) // AllCourse List
                //myBookmarks.putExtra(Const.FRAG_TYPE, Const.BOOKMARKS)
                startActivity(myBookmarks)
            }
            R.id.myRewardsProfileCard -> {
                val myRewards = Intent(this, MyCoinActivity::class.java)
                startActivity(myRewards)
//                val myRewards = Intent(this, CourseActivity::class.java) // AllCourse List
//                myRewards.putExtra(Const.FRAG_TYPE, Const.REWARDPOINTS)
//                startActivity(myRewards)
            }
            R.id.myDownloadsProfileCard -> {
                val myDownloads = Intent(this, CourseActivity::class.java) // AllCourse List
                myDownloads.putExtra(Const.FRAG_TYPE, Const.MYDOWNLOAD)
                startActivity(myDownloads)
            }
            R.id.myTestSeriesProfileCard -> {
                val myTestSeries = Intent(this, CourseActivity::class.java) // AllCourse List
                myTestSeries.putExtra(Const.FRAG_TYPE, Const.MYCOURSES)
                startActivity(myTestSeries)
            }
            R.id.myOrdersProfileCard -> {
                val myOrders = Intent(this, CourseActivity::class.java) // AllCourse List
                myOrders.putExtra(Const.FRAG_TYPE, Const.MYORDERS)
                startActivity(myOrders)
            }

            R.id.myAddressProfile -> {
                val i = Intent(this, AddressList::class.java)
                SharedPreference.getInstance().saveCardConst(Const.CARD, "")
                startActivity(i)
            }

            R.id.logoutCard -> {

                lateinit var alertDialog: AlertDialog

                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.logout)
                builder.setMessage(R.string.logoutMessage)
                builder.setIcon(android.R.drawable.ic_dialog_alert)

                builder.setPositiveButton("No") { dialogInterface, which ->
                    alertDialog.dismiss()
                }
                builder.setNegativeButton("Yes") { dialogInterface, which ->
                    binding.progressBarProfile.visibility=View.VISIBLE
                    alertDialog.dismiss()
                     var user= SharedPreference.getInstance().loggedInUser
                var myApi = ApiClient.getService()
                 myApi.logoutProfile(user.id,user.device_tokken)
                     .enqueue(object:Callback<Logout>{
                         override fun onResponse(call: Call<Logout>, response: Response<Logout>)
                         {
                             binding.progressBarProfile.visibility=View.GONE
                             var logout=response.body();
                             if(logout?.status == true)
                             {
                                 Toast.makeText(applicationContext,"Response="+logout.message,Toast.LENGTH_LONG).show();
                                 Helper.SignOutUser(this@NewProfileActivity)
                             }

                         }

                         override fun onFailure(call: Call<Logout>, t: Throwable)
                         {
                             binding.progressBarProfile.visibility=View.GONE
                             Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show();
                         }
                     })
                }
                alertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }


//                if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.SUBTITLE))) {
//                    SharedPreference.getInstance().putString(Const.SUBTITLE, "")
//                }
//
//                val dialog = DialogPlus.newDialog(this)
//                    .setContentHolder(com.orhanobut.dialogplus.ViewHolder(R.layout.bottom_sheet_dialog))
//                    .setGravity(Gravity.BOTTOM)
//                    .setCancelable(false)
//                    .setContentBackgroundResource(R.drawable.dialog_top_corner)
//                    .create()
//
//                val view = dialog.holderView
//                val ivClose = view.findViewById<ImageView>(R.id.imageView7)
//                ivClose.setOnClickListener {
//                    dialog.dismiss()
//                }
//
//                val btnExit = view.findViewById<Button>(R.id.btnExit)
//                btnExit.setOnClickListener {
//                    Helper.dismissDialog()
//                    Helper.SignOutUser(this@NewProfileActivity)
//                }
//                dialog.show()


            R.id.gamificationLayout -> {
                val intent = Intent(this, GamificationActivity::class.java)
                startActivity(intent)
            }

            R.id.followerLL -> {
                val follower = Intent(this, CourseActivity::class.java) // AllCourse List
                follower.putExtra(Const.FRAG_TYPE, "FOLLOWER")
                follower.putExtra("ADAPTER_TYPE", "followers")
                follower.putExtra(Constants.Extras.ID, id)
                startActivity(follower)
            }
            R.id.followingLL -> {
                val following = Intent(this, CourseActivity::class.java) // AllCourse List
                following.putExtra(Const.FRAG_TYPE, "FOLLOWING")
                following.putExtra("ADAPTER_TYPE", "following")
                following.putExtra(Constants.Extras.ID, id)
                startActivity(following)
            }
            R.id.editTV -> Helper.GoToEditProfileActivity(this, Const.REGISTRATION, Const.PROFILE)
            R.id.change_steam -> Helper.GoToEditProfileActivity(
                this,
                Const.CHANGE_STREAM,
                Const.PROFILE
            )
            R.id.toolbarBackIV -> onBackPressed()
        }
    }

    private fun networkCallForGetUser() {
        mprogress.show()
        val apiInterface = ApiClient.createService(
            ProfileApiInterface::class.java
        )
        val response = apiInterface.getUser(
            "data_model/user/Registration/get_active_user/"
                    + id + Const.IS_WATCHER + SharedPreference.getInstance().loggedInUser.id
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                mprogress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    var jsonResponse: JSONObject? = null
                    val gson = Gson()
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            binding.rootLL.visibility = View.VISIBLE
                            val data = GenericUtils.getJsonObject(jsonResponse)
                            user = gson.fromJson(data.toString(), User::class.java)
                            //   SharedPreference.getInstance().setLoggedInUser(user);
                            binding.userNameProfile.text = user.name
                            if (!TextUtils.isEmpty(user.profile_picture)) {
                                binding.userProfileImage.visibility = View.VISIBLE
                                Glide.with(this@NewProfileActivity).load(user.profile_picture)
                                    .into(
                                        binding.userProfileImage
                                    )
                            } else {
                                binding.userProfileImage.setImageResource(R.mipmap.default_pic)
                            }
                            if (user.followers_count != null) {
                                binding.followersCountTV.text = user.followers_count
                                binding.followerLL.isEnabled = user.followers_count != "0"
                            }
                            if (user.following_count != null) {
                                binding.followerLL.isEnabled = user.following_count != "0"
                                binding.followingCountTV.text = user.following_count
                            }
                            binding.rewardPointsProfile.text =
                                String.format("%s Points", user.reward_points)
                            binding.streamProfileTV.text =
                                user.user_registration_info.master_id_name
                            binding.subStreamProfileTV.text =
                                user.user_registration_info.master_id_level_one_name
                            binding.specializationProfileTV.text =
                                user.user_registration_info.master_id_level_two_name
                            // examPrepTV.setText(user.getUser_registration_info().getInterested_course_text());
                            var exam = user.user_registration_info.interested_course_text
                            if (exam.length > Course_Description_Length) {
                                exam = exam.substring(0, Course_Description_Length) + "..."
                                binding.examPrepProfileTV.text =
                                    exam + Html.fromHtml("<font color='#33A2D9'> <u>Read More</u></font>")
                                readmoreclick = object : ClickableSpan() {
                                    override fun onClick(view: View) {
                                        binding.examPrepProfileTV.text = String.format(
                                            "%s %s",
                                            user.user_registration_info.interested_course_text,
                                            Html.fromHtml("<font color='#33A2D9'> <u>Read Less</u></font>")
                                        )
                                        Helper.makeLinks(
                                            binding.examPrepProfileTV,
                                            "Read Less",
                                            readlessclick
                                        )
                                    }
                                }
                                val finalExam = exam
                                readlessclick = object : ClickableSpan() {
                                    override fun onClick(view: View) {
                                        binding.examPrepProfileTV.text = String.format(
                                            "%s %s",
                                            finalExam,
                                            Html.fromHtml("<font color='#33A2D9'> <u>Read More</u></font>")
                                        )
                                        Helper.makeLinks(
                                            binding.examPrepProfileTV,
                                            "Read More",
                                            readmoreclick
                                        )
                                    }
                                }
                                Helper.makeLinks(
                                    binding.examPrepProfileTV,
                                    "Read More",
                                    readmoreclick
                                )
                            } else {
                                binding.examPrepProfileTV.text = exam
                            }
                            if (!user.getId().equals(
                                    SharedPreference.getInstance().loggedInUser.id,
                                    ignoreCase = true
                                )
                            ) {
                                binding.apply {
                                    editTV.visibility = View.GONE
                                    changeSteam.visibility = View.GONE
                                    myCoursesProfileCard.visibility = View.GONE
                                    myBookmarkProfileCard.visibility = View.GONE
                                    myDownloadsProfileCard.visibility = View.GONE
                                    myOrdersProfileCard.visibility = View.GONE
                                    myRewardsProfileCard.visibility = View.GONE
                                    postTV.text = "Posts"
                                }
                            }
                        } else {
                            binding.rootLL.visibility = View.GONE
                            RetrofitResponse.getApiData(
                                this@NewProfileActivity, jsonResponse.optString(
                                    Const.AUTH_CODE
                                )
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    mprogress.dismiss()
                    Helper.printError(TAG, response.errorBody())
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mprogress.dismiss()
                Helper.showExceptionMsg(this@NewProfileActivity, t)
                //  replaceErrorLayout(API.API_GET_USER, 1, 1);
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.e("RESUME", "onResume is called")
        networkCallForGetUser()
    }

    companion object {
        @JvmField
        var IS_PROFILE_UPDATED = false
        private const val TAG = "NewProfileActivity"
    }
}

private fun Any.observe(newProfileActivity: NewProfileActivity, observer: Observer<ArrayList<Logout?>?>) {

}

