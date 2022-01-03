package com.emedicoz.app.newsandarticle.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.webkit.WebSettingsCompat
import com.emedicoz.app.R
import com.emedicoz.app.databinding.ActivityNewAndArticalDetailBinding
import com.emedicoz.app.network.ApiInterfacesNew
import com.emedicoz.app.newsandarticle.Adapter.TagNameRecycleViewAdapter
import com.emedicoz.app.newsandarticle.models.*
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.SharedPreference
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_ccawebview.*
import kotlinx.android.synthetic.main.activity_new_and_artical_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class NewAndArticalDetailActivity : AppCompatActivity(), View.OnClickListener {

    var binding: ActivityNewAndArticalDetailBinding? = null

    val tagList = ArrayList<String>()
    var articleId: String?=null
    lateinit var noLike: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewAndArticalDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.RecyclerView.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true))
        binding!!.RecyclerView.setHasFixedSize(true)
        articleId = intent.getStringExtra("articleId").toString()

        getApiCall(SharedPreference.getInstance().getLoggedInUser().getId(), articleId!!)

        binding!!.apply {

            // articleShare.setOnClickListener {
            shareBottomLL.setOnClickListener {

                articleShareAPI()
            }

            articleLikeTv.setOnClickListener(this@NewAndArticalDetailActivity)
            bookmarkTv.setOnClickListener(this@NewAndArticalDetailActivity)

            disLikeTv.setOnClickListener(this@NewAndArticalDetailActivity)
            removeBookmarkTv.setOnClickListener(this@NewAndArticalDetailActivity)

            bookmarBottomkLL.setOnClickListener(this@NewAndArticalDetailActivity)
            likeBottomLL.setOnClickListener(this@NewAndArticalDetailActivity)
            //  shareBottomLL.setOnClickListener(this@NewAndArticalDetailActivity)



        }





        binding!!.apply {

            back.setOnClickListener {

                finish()
            }

        }
    }

    public fun getApiCall(
            userID: String,
            type: String
    ) {

        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.getArticleDetail(userID, type)

        mCall?.enqueue(object : Callback<JsonObject?> {
            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("SetJavaScriptEnabled", "RequiresFeature")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Article Detail", "Response News : " + response.body())

                var newArticleDetailResponse: NewArticleDetailResponse = Gson().fromJson(response.body(), NewArticleDetailResponse::class.java)

                binding!!.newstitle.text = newArticleDetailResponse.data.article.title
                binding!!.createdBy.text = "By : " + newArticleDetailResponse.data.article.createdBy
                // binding!!.creationDate.text = getDate((newArticleDetailResponse.data.article.creationDateval.toLong()), "dd MMM, yyyy hh:mm:aa ").toString()
                binding!!.creationDate.text = getDate((newArticleDetailResponse.data.article.creationDateval.toLong()), "dd-MM-yyyy HH:mm a").toString()
                // binding!!.webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                //  binding!!.webview.loadUrl(newArticleDetailResponse.data.article.content)


                val display: Display = windowManager.defaultDisplay
                val width: Int = display.getWidth()


                if (SharedPreference.getInstance().getBoolean(Const.DARK_MODE)) {
                    WebSettingsCompat.setForceDark(webview.settings, WebSettingsCompat.FORCE_DARK_ON)
                } else {
                    // WebSettingsCompat.setForceDark(webview.settings, WebSettingsCompat.FORCE_DARK_OFF)
                }


                binding!!.webview.getSettings().setLoadWithOverviewMode(true)
                binding!!.webview.getSettings().getAllowUniversalAccessFromFileURLs()
                binding!!.webview.getSettings().javaScriptCanOpenWindowsAutomatically
                binding!!.webview.getSettings().setJavaScriptEnabled(true)
                binding!!.webview.loadDataWithBaseURL(null, /*"<html><body  align='center'></body></html>*/"<style>img{display: inline;height: auto;max-width: 100%;}</style>" + newArticleDetailResponse.data.article.content, "text/html", "UTF-8", null)
                if (!newArticleDetailResponse.data.article.views.equals(null) && newArticleDetailResponse.data.article.views.equals("1") || newArticleDetailResponse.data.article.views.equals("0")) {
                    binding!!.noOfViews.text = (newArticleDetailResponse.data.article.views.toString() + " View")
                } else {
                    binding!!.noOfViews.text = (newArticleDetailResponse.data.article.views.toString() + " Views")
                }

                binding!!.noOfLike.text = newArticleDetailResponse.data.article.likes + " Likes"
                noLike = newArticleDetailResponse.data.article.likes
                //  noLike = newArticleDetailResponse.data.article.likes

                if (newArticleDetailResponse.data.user_like == 1) {
                    binding!!.articleLikeTv.text = "Liked"
                }
                if (newArticleDetailResponse.data.user_bookmark.equals(true)) {
                    binding!!.bookmarkTv.text = "Bookmarked"
                }

                if (newArticleDetailResponse.data.tag.size > 0) {
                    initialize_list(newArticleDetailResponse.data.tag)
                    binding!!.rvnotFound.visibility = View.GONE
                } else {
                    binding!!.RecyclerView.visibility = View.GONE
                    binding!!.rvnotFound.visibility = View.VISIBLE
                }

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // mProgressListner.update(false)
            }


        })

    }

    fun between(value: String, a: String, b: String?): String? {
        // Return a substring between the two strings.
        val posA = value.indexOf(a)
        if (posA == -1) {
            return ""
        }
        val posB = value.lastIndexOf(b!!)
        if (posB == -1) {
            return ""
        }
        val adjustedPosA = posA + a.length
        return if (adjustedPosA >= posB) {
            ""
        } else value.substring(adjustedPosA, posB)
    }

    private fun initialize_list(mList: List<Tag>) {
        val mAdapter = this?.let { TagNameRecycleViewAdapter(it, mList) }
        // mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding!!.RecyclerView.setItemAnimator(null)
        binding!!.RecyclerView.setAdapter(mAdapter)

    }


    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        val date = Date(milliSeconds)
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        return formatter.format(date)
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            /* R.id.article_likeTv -> {

                 if (article_likeTv.text.equals("Like")) {
                     likeAPI()
                 } else {
                     disLikeAPI()
                 }
             }

             R.id.bookmarkTv -> {
                 if (bookmarkTv.text.equals("Bookmark")) {
                     bookmarkAPI()
                 } else {
                     removeBookmarkAPI()
                 }
             }
             R.id.disLikeTv -> {
                 disLikeAPI()

             }
             R.id.removeBookmarkTv -> {
                 removeBookmarkAPI()

             }*/

            R.id.likeBottomLL -> {

                if (article_likeTv.text.equals("Like")) {
                    likeAPI()
                } else {
                    disLikeAPI()
                }
            }

            R.id.bookmarBottomkLL -> {
                if (bookmarkTv.text.equals("Bookmark")) {
                    bookmarkAPI()
                } else {
                    removeBookmarkAPI()
                }
            }
            R.id.disLikeTv -> {
                disLikeAPI()
            }
            R.id.removeBookmarkTv -> {
                removeBookmarkAPI()

            }


        }
    }

    private fun bookmarkAPI() {
        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)
        val mCall = mApiInterface?.addBookmark(SharedPreference.getInstance().getLoggedInUser().getId(), articleId!!)

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Article Detail", "Response News : " + response.body())


                var responseData: ResponseData = Gson().fromJson(response.body(), ResponseData::class.java)

                if (responseData.status.equals(true)) {
                    binding!!.bookmarkTv.text = "Bookmarked"

                    Toast.makeText(this@NewAndArticalDetailActivity, "Bookmarked", Toast.LENGTH_LONG).show()

                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // mProgressListner.update(false)
            }


        })

    }
    private fun likeAPI() {
        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.article_like(SharedPreference.getInstance().getLoggedInUser().getId(), articleId!!)

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Article Detail", "Response News : " + response.body())

                var responseData: ResponseData = Gson().fromJson(response.body(), ResponseData::class.java)

                // Toast.makeText(this@NewAndArticalDetailActivity,"Liked",Toast.LENGTH_LONG).show()

                if (responseData.status.equals(true)) {
                    binding!!.noOfLike.text = (noLike.toInt() + 1).toString() + " Likes"
                    binding!!.articleLikeTv.text = "Liked"

                    Toast.makeText(this@NewAndArticalDetailActivity, "Liked", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // mProgressListner.update(false)
            }


        })
    }

    private fun removeBookmarkAPI() {
        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)
        val mCall = mApiInterface?.removeBookmark(SharedPreference.getInstance().getLoggedInUser().getId(), articleId!!)

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Article Detail", "Response News : " + response.body())

                //  var newArticleDetailResponse: NewArticleDetailResponse = Gson().fromJson(response.body(), NewArticleDetailResponse::class.java)

                var responseData: ResponseData = Gson().fromJson(response.body(), ResponseData::class.java)

                if (responseData.status.equals(true)) {
                    binding!!.bookmarkTv.text = "Bookmark"
                    Toast.makeText(this@NewAndArticalDetailActivity, "Unbookmarked", Toast.LENGTH_LONG).show()

                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // mProgressListner.update(false)
            }


        })
    }

    private fun disLikeAPI() {
        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.removeLike(SharedPreference.getInstance().getLoggedInUser().getId(), articleId!!)

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Article Detail", "Response News : " + response.body())

                var responseData: ResponseData = Gson().fromJson(response.body(), ResponseData::class.java)

                if (responseData.status.equals(true)) {
                    // binding!!.noOfLike.text = (no_of_like.text.toString().toInt() - 1).toString() + " Likes"
                    binding!!.noOfLike.text = ((noLike.toInt() + 1) - 1).toString() + " Likes"
                    binding!!.articleLikeTv.text = "Like"
                    //  Toast.makeText(this@NewAndArticalDetailActivity, "disliked", Toast.LENGTH_LONG).show()


                }


            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // mProgressListner.update(false)
            }


        })
    }


    private fun articleShareAPI() {
        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.article_share_API(SharedPreference.getInstance().getLoggedInUser().getId(), articleId!!)

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Article Detail", "Response News : " + response.body())

                var responseData: ResponseData = Gson().fromJson(response.body(), ResponseData::class.java)

                //   if (responseData.status.equals(true)) {

                // binding!!.noOfLike.text = (no_of_like.text.toString().toInt() - 1).toString() + " Likes"
                //  getShare()
                //  sharePostExternally()
                // }
                sharePostExternally()

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // mProgressListner.update(false)
            }


        })
    }


    /*fun shareApp(activity: Activity?, rewardPoints: MyRewardPoints?) {
        val progress = Progress(activity)
        progress.show()
        val shortLinkTask: Task<ShortDynamicLink> = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(API.BASE_URL.toString() + "?invitedBy=" + "1"))
                .setDynamicLinkDomain("evidyashare.page.link")
                .setAndroidParameters(AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnCompleteListener(activity, object : OnCompleteListener<ShortDynamicLink?>() {
                    fun onComplete( task: Task<ShortDynamicLink?>) {
                        progress.dismiss()
                        if (task.isSuccessful()) {
                            // Short link created
                           // val shortLink: Uri = task.getResult().getShortLink()
                            val shortLink: Uri =
                            val msgHtml = java.lang.String.format("<p>Hey There!!! Checkout new learn learnforward on your Smart Phone. Download it now with my"
                                    + "<a href=\"%s\">referrer link</a>!</p>", shortLink.toString())
                            sendLink(activity, "learn Referral Link", "Hey There!!! Checkout new learn learnforward on your Smart Phone. Download it now with my referrer link: " + shortLink.toString(),
                                    msgHtml)
                        } else {
                            progress.dismiss()
                            Toast.makeText(activity, "Link could not be generated please try again!", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
    }*/


    private fun sharePostExternally() {//
        val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("http://emedicoz.com/?course_id=${""}"))///*course.id*/
                .setDynamicLinkDomain("wn2d8.app.goo.gl")
                .setAndroidParameters(AndroidParameters.Builder().build()
                )

                // Open links with com.example.ios on iOS
                //                .setIosParameters(new DynamicLink.IosParameters.Builder("com.eMedicoz.app").build())
                // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(this
                ) { task: com.google.android.gms.tasks.Task<ShortDynamicLink?> ->
                    if (task.isSuccessful) {
                        // Short link created
                        val shortLink = task.result!!.shortLink
                        val msgHtml = String.format("<p>Let's refer this %s on eMedicoz App." + "<a href=\"%s\">Click Here</a>!</p>",
                                //course.title,
                                title, shortLink.toString()
                        )
                        val msg = "Let's refer this " + /*course.title*/ title + " on eMedicoz App. Click on Link : " + shortLink.toString()
                        //  Helper.sendLink(this, "eMedicoz Post Reference", msg, msgHtml)

                        val sharingIntent = Intent(Intent.ACTION_SEND)
                        sharingIntent.setType("text/plain")
                        /*  val shareBody = "\n Let me recommend you this application"
                          val shareSub = "https://play.google.com/store/apps/details?id=com.aaaBright.main.app\n"*/
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "eMedicoz Post Reference")
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, msg)
                        sharingIntent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml)
                        startActivity(Intent.createChooser(sharingIntent, "Share using"))
                    } else {
                        Toast.makeText(this, "Link could not be generated please try again!", Toast.LENGTH_SHORT).show()
                    }
                }

    }


}