package com.emedicoz.app.newsandarticle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.R
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.NewsArticleBinding
import com.emedicoz.app.network.ApiInterfacesNew
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.newsandarticle.Activity.BookmarkActivity
import com.emedicoz.app.newsandarticle.Activity.TopicAndTagActivity
import com.emedicoz.app.newsandarticle.models.MostViewedListRecycleViewAdapter
import com.emedicoz.app.newsandarticle.models.NewsListResponse
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.SharedPreference
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.header_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsAndArticle: AppCompatActivity() ,View.OnClickListener, ProgressUpdateListner {
    lateinit var tabs: TabLayout
    private var binding: NewsArticleBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewsArticleBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        ibBack.setOnClickListener(View.OnClickListener { finish() })
        tvHeaderName.text = "News and Articles"

        val linearLayoutManager = LinearLayoutManager(this)
        binding!!.RecyclerView.setLayoutManager(linearLayoutManager)
        binding!!.RecyclerView.setHasFixedSize(true)

        binding!!.progressBar.visibility = View.VISIBLE
        // SharedPreference.getInstance().putString("type", "subject")
        SharedPreference.getInstance().putString("Newstype", "latest")
        call_latest_News_List()

        binding!!.apply {

            featuretab.setOnClickListener(this@NewsAndArticle)
            latesttab.setOnClickListener(this@NewsAndArticle)
            mostlikedtab.setOnClickListener(this@NewsAndArticle)
            mostviewedtab.setOnClickListener(this@NewsAndArticle)
            trendingtab.setOnClickListener(this@NewsAndArticle)
            historytab.setOnClickListener(this@NewsAndArticle)
        }

        binding!!.apply {

            topicTag.setOnClickListener {

                val intent = Intent(this@NewsAndArticle, TopicAndTagActivity::class.java)

                startActivity(intent)

            }
        }


        binding!!.apply {

            bookmarkTv.setOnClickListener {
                val intent = Intent(this@NewsAndArticle, BookmarkActivity::class.java)
                startActivity(intent)

            }
        }
    }

    private fun unSelectTabs() {
        binding!!.featuretab.setImageDrawable(resources.getDrawable(R.mipmap.feature_small_new))
        binding!!.historytab.setImageDrawable(resources.getDrawable(R.mipmap.history_small_new))
        binding!!.latesttab.setImageDrawable(resources.getDrawable(R.mipmap.latest_small_new))
        binding!!.mostlikedtab.setImageDrawable(resources.getDrawable(R.mipmap.mostliked_small_new))
        binding!!.mostviewedtab.setImageDrawable(resources.getDrawable(R.mipmap.mostviewed_viewed_new))
        binding!!.trendingtab.setImageDrawable(resources.getDrawable(R.mipmap.trending_small_new))
    }

    override fun onClick(v: View?) {
        binding!!.view1.visibility = View.GONE
        binding!!.view2.visibility = View.GONE
        binding!!.view3.visibility = View.GONE
        binding!!.view4.visibility = View.GONE
        binding!!.view5.visibility = View.GONE
        binding!!.view6.visibility = View.GONE
        unSelectTabs()
        when (v!!.id) {

            R.id.featuretab -> {

                binding!!.featuretab.setImageDrawable(resources.getDrawable(R.mipmap.feature_large_new))

                binding!!.viewPager.setCurrentItem(2, true)
                binding!!.view3.visibility = View.VISIBLE
                SharedPreference.getInstance().putString("Newstype", "featured")
                call_latest_News_List()
            }
            R.id.historytab -> {
                binding!!.historytab.setImageDrawable(resources.getDrawable(R.mipmap.history_large_new))
                binding!!.viewPager.setCurrentItem(5, true)
                binding!!.view6.visibility = View.VISIBLE
                SharedPreference.getInstance().putString("Newstype", "history")
                call_latest_News_List()
            }
            R.id.latesttab -> {
                binding!!.latesttab.setImageDrawable(resources.getDrawable(R.mipmap.latest_large_new))
                binding!!.viewPager.setCurrentItem(0, true)
                binding!!.view1.visibility = View.VISIBLE
                SharedPreference.getInstance().putString("Newstype", "latest")
                call_latest_News_List()




            }
            R.id.mostlikedtab -> {
                binding!!.mostlikedtab.setImageDrawable(resources.getDrawable(R.mipmap.mostliked_large_new))
                binding!!.viewPager.setCurrentItem(4, true)
                binding!!.view5.visibility = View.VISIBLE
                SharedPreference.getInstance().putString("Newstype", "liked")
                call_latest_News_List()
            }
            R.id.mostviewedtab -> {
                binding!!.mostviewedtab.setImageDrawable(resources.getDrawable(R.mipmap.mostviewed_large_new))

                binding!!.viewPager.setCurrentItem(3, true)
                binding!!.view4.visibility = View.VISIBLE
                SharedPreference.getInstance().putString("Newstype", "viewed")
                call_latest_News_List()

            }
            R.id.trendingtab -> {
                binding!!.trendingtab.setImageDrawable(resources.getDrawable(R.mipmap.trending_large_new))

                binding!!.viewPager.setCurrentItem(1, true)
                binding!!.view2.visibility = View.VISIBLE

                SharedPreference.getInstance().putString("Newstype", "trending")
                call_latest_News_List()
            }
        }
    }


    fun replaceFragment(fragment: Fragment, isAddBackStack: Boolean) {
        val transaction: FragmentTransaction = this.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment).commit()
        //if (isAddBackStack) transaction.addToBackStack(fragmen.c))
    }

    public fun call_latest_News_List() {

        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.getNewsList(SharedPreference.getInstance().getLoggedInUser().getId(),SharedPreference.getInstance().getString("Newstype"))

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("news and Article", "Response News : " + response.body())

                var    newsResponse : NewsListResponse = Gson().fromJson(response.body(), NewsListResponse::class.java)
                if ( newsResponse.status.equals(true)) {
                    if ( newsResponse.data.size > 0) {
                        initialize_list(newsResponse)
                        binding!!.progressBar.visibility = View.GONE
                        binding!!.layoutNoContentFound.visibility = View.GONE
                    } else {
                        binding!!.layoutNoContentFound.visibility = View.VISIBLE
                    }
                } else {
                    binding!!.layoutNoContentFound.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // mProgressListner.update(false)
            }
        })

    }
    private fun initialize_list(mList: NewsListResponse) {
        val mAdapter = this?.let { MostViewedListRecycleViewAdapter(it, mList.data) }

        binding!!.RecyclerView.setItemAnimator(null)
        binding!!.RecyclerView.setAdapter(mAdapter)



    }

    override fun update(b: Boolean) {
        Log.d("UPDATE ", " REcieved "+ b)
        // System.out.print("Update Recieved : "+ b)
        binding!!.progressBar.visibility= View.GONE
    }


}