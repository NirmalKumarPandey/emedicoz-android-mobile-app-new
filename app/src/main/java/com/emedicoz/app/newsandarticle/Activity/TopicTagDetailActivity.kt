package com.emedicoz.app.newsandarticle.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.databinding.ActivityNewAndArticalDetailBinding
import com.emedicoz.app.network.ApiInterfacesNew
import com.emedicoz.app.newsandarticle.models.NewArticleDetailResponse
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.utilso.SharedPreference
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopicTagDetailActivity : AppCompatActivity() {

    var binding: ActivityNewAndArticalDetailBinding? = null
    val tagList = ArrayList<String>()
    lateinit var articleId:String
    lateinit var noLike:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_topic_tag_detail)

        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityNewAndArticalDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val linearLayoutManager = LinearLayoutManager(this)
        binding!!.RecyclerView.setLayoutManager(linearLayoutManager)
        binding!!.RecyclerView.setHasFixedSize(true)
        articleId = intent.getStringExtra("articleId").toString()
        getApiCall(SharedPreference.getInstance().getLoggedInUser().getId(), articleId)

        binding!!.apply {

          /*  shareTv.setOnClickListener(this@NewAndArticalDetailActivity)
            likeTv.setOnClickListener(this@NewAndArticalDetailActivity)
            bookmarkTv.setOnClickListener(this@NewAndArticalDetailActivity)
            disLikeTv.setOnClickListener(this@NewAndArticalDetailActivity)
            removeBookmarkTv.setOnClickListener(this@NewAndArticalDetailActivity)*/
        }


    }


    public fun getApiCall(userID: String, type: String) {

        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.getArticleDetail(userID, type)

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Article Detail", "Response News : " + response.body())

                var newArticleDetailResponse: NewArticleDetailResponse = Gson().fromJson(response.body(), NewArticleDetailResponse::class.java)

                binding!!.newstitle.text = newArticleDetailResponse.data.article.title
                binding!!.createdBy.text = newArticleDetailResponse.data.article.createdBy


             /*   binding!!.creationDate.text = getDate((newArticleDetailResponse.data.article.creationDateval.toLong()), "dd MMM, yyyy").toString()

                //  binding!!.webview.loadUrl(newArticleDetailResponse.data.article.content)
                binding!!.webview.loadData(newArticleDetailResponse.data.article.content, "text/html", "utf-8")
                binding!!.noOfLike.text = newArticleDetailResponse.data.article.likes + " Likes"
                noLike=newArticleDetailResponse.data.article.likes*/


            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // mProgressListner.update(false)
            }

        })

    }


}