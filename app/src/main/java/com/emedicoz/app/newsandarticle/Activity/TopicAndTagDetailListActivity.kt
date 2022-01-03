package com.emedicoz.app.newsandarticle.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.databinding.ActivityTopicAndTagDetailListBinding
import com.emedicoz.app.network.ApiInterfacesNew
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.newsandarticle.models.LatestNewsListRecycleViewAdapter
import com.emedicoz.app.newsandarticle.models.NewsListResponse
import com.emedicoz.app.newsandarticle.models.ResponseData
import com.emedicoz.app.newsandarticle.viewmodel.NewsAndArticleViewModel
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.utilso.SharedPreference
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_topic_and_tag_detail_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopicAndTagDetailListActivity : AppCompatActivity() , ProgressUpdateListner {

    val ARGUMENT: String = "ARGUMENT"
    lateinit var mNewsAndArticleViewModel: NewsAndArticleViewModel
    lateinit var binding: ActivityTopicAndTagDetailListBinding
    private var mProgressBar: ProgressBar? = null

    var type_id:String?=null
    var type:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //   setContentView(R.layout.activity_topic_and_tag_detail_list)


        //   mNewsAndArticleViewModel = ViewModelProvider(this).get(NewsAndArticleViewModel::class.java)
        // setContentView(R.layout.activity_bookmark)

        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityTopicAndTagDetailListBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val bundle: Bundle? = intent.extras
          type = intent.getStringExtra("type")
          type_id = intent.getStringExtra("id")


        mNewsAndArticleViewModel = ViewModelProvider(this).get(NewsAndArticleViewModel::class.java)

        mProgressBar = binding.progressBar


        binding.titleTv.setText(  SharedPreference.getInstance().getString("titleName"))

        //if(type.equals("followed_tag"))
        if(type.equals("all_tag"))
        {
            binding.tagFollow12.visibility=View.VISIBLE
        }else
        {
            binding.tagFollow12.visibility=View.GONE
        }



        binding.apply {

            tag_follow12.setOnClickListener {

                if(tagFollow12.text.equals("FOLLOW THIS #")) {
                    followTagAPI("follow")
                }else{
                    unfollowTagAPI("unfollow")
                }
            }
        }

        binding.apply {

            back.setOnClickListener{

                finish()
            }

        }

        val linearLayoutManager = LinearLayoutManager(this)
        binding.RecyclerView.setLayoutManager(linearLayoutManager)
        binding.RecyclerView.setHasFixedSize(true)

        //  binding.RecyclerView.addItemDecoration(SpacesItemDecoration(1))

     //   mNewsAndArticleViewModel.newsList.observe(this, Observer<NewsListResponse?> { s -> initialize_list(s as NewsListResponse) })
        binding.progressBar.visibility = View.VISIBLE
        //    mNewsAndArticleViewModel.loadNewsList(context,this , "1" )
        // mNewsAndArticleViewModel.loadNewsList(context,this , "1","latest" )

       // mNewsAndArticleViewModel.loadTopDetailList(this, this, "1" ,type!!,"63"/*SharedPreference.getInstance().getLoggedInUser().getId()*/)
      //  mNewsAndArticleViewModel.loadTopDetailList(this, this, SharedPreference.getInstance().getLoggedInUser().getId() ,type!!,type_id!!/*SharedPreference.getInstance().getLoggedInUser().getId()*/)
        getTopicDetailList()
    }



    public fun getTopicDetailList(
         /*   mContext: Context?,
            mProgressListner: ProgressUpdateListner,
            mNewsAndArticleViewModel: NewsAndArticleViewModel,
            userID: String, type:String, type_id:String*/
    ) {

        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.getTopicDetailList( SharedPreference.getInstance().getLoggedInUser().getId(),type,type_id)

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
              //  Log.d(TAG, "Response News : " + response.body())

                var    newsResponse : NewsListResponse = Gson().fromJson(response.body(), NewsListResponse::class.java)

               /* mNewsAndArticleViewModel.newsList.value = newsResponse
                mProgressListner.update(true)*/
                binding.progressBar.visibility = View.GONE


               // if(type.equals("followed_tag")) {
                if(type.equals("all_tag"))
                {
                    if (newsResponse.is_follow.equals("yes") && newsResponse.is_follow != null) {
                        binding.tagFollow12.text = "UNFOLLOW THIS #"
                    } else {
                        binding.tagFollow12.text = "FOLLOW THIS #"
                    }
                }

                initialize_list(newsResponse)


            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                //mProgressListner.update(false)
                binding.progressBar.visibility = View.GONE
            }


        })

    }

    private fun initialize_list(mList: NewsListResponse) {

        // Toast.makeText(getActivity(), "Inside ")

        // Creating the Adapter class
        val mAdapter = this?.let { LatestNewsListRecycleViewAdapter(it, mList.data) }
        // mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.RecyclerView.setItemAnimator(null)
        binding.RecyclerView.setAdapter(mAdapter)


        /*binding.RecyclerView.addOnItemTouchListener(RecyclerItemClickListener(this) { view, temPosition ->
            when (temPosition) {
                0 -> {

                    Toast.makeText(this ,"pos click 0 ", Toast.LENGTH_SHORT ).show()

                }
                1 -> {
                }
                else -> {
                }
            }
        })*/
    }


    override fun update(b: Boolean) {

        Log.d("UPDATE ", " REcieved " + b)
        // System.out.print("Update Recieved : "+ b)

        binding.progressBar.visibility = View.GONE
    }


    private fun followTagAPI( type:String) {
        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)

      //  val mCall = mApiInterface?.getSearchTopicTagList("1", type, searchText)
        val mCall = mApiInterface?.follow_TagAPi(SharedPreference.getInstance().getLoggedInUser().getId() ,type_id,type)
       // val mCall = mApiInterface?.follow_TagAPi("1" ,"63","follow")

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Topic and tag", "Response News : " + response.body())
                var responseData:ResponseData = Gson().fromJson(response.body(), ResponseData::class.java)

                 if(responseData.status.equals(true))
                 {
                     Toast.makeText(this@TopicAndTagDetailListActivity,"Followed",Toast.LENGTH_SHORT).show()
                     
                     binding.tagFollow12.text="UNFOLLOW THIS #"
                 }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                //  mProgressListner.update(false)
            }
        })
    }


    private fun unfollowTagAPI( type:String) {
        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)

        //  val mCall = mApiInterface?.getSearchTopicTagList("1", type, searchText)
        val mCall = mApiInterface?.follow_TagAPi(SharedPreference.getInstance().getLoggedInUser().getId(),type_id,type)
        // val mCall = mApiInterface?.follow_TagAPi("1" ,"63","follow")

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Topic and tag", "Response News : " + response.body())
                var responseData:ResponseData = Gson().fromJson(response.body(), ResponseData::class.java)

                if(responseData.status.equals(true))
                {
                    Toast.makeText(this@TopicAndTagDetailListActivity,"Unfollowed",Toast.LENGTH_SHORT).show()

                    binding.tagFollow12.text="FOLLOW THIS #"
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                //  mProgressListner.update(false)
            }
        })
    }

}