package com.emedicoz.app.newsandarticle.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.databinding.ActivityBookmarkBinding
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.newsandarticle.models.LatestNewsListRecycleViewAdapter
import com.emedicoz.app.newsandarticle.models.NewsListResponse
import com.emedicoz.app.newsandarticle.viewmodel.NewsAndArticleViewModel
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.utilso.SharedPreference

class BookmarkActivity : AppCompatActivity(), ProgressUpdateListner {

    val ARGUMENT: String = "ARGUMENT"
    lateinit var mNewsAndArticleViewModel: NewsAndArticleViewModel
    lateinit var binding: ActivityBookmarkBinding
    private var mProgressBar: ProgressBar? = null
    lateinit var activity: BookmarkActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //   mNewsAndArticleViewModel = ViewModelProvider(this).get(NewsAndArticleViewModel::class.java)
        // setContentView(R.layout.activity_bookmark)

        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val type = intent.getStringExtra("type")
        val id = intent.getStringExtra("id")
        mNewsAndArticleViewModel = ViewModelProvider(this).get(NewsAndArticleViewModel::class.java)

        mProgressBar = binding.progressBar

        val linearLayoutManager = LinearLayoutManager(this)
        binding.RecyclerView.setLayoutManager(linearLayoutManager)
        binding.RecyclerView.setHasFixedSize(true)

        //  binding.RecyclerView.addItemDecoration(SpacesItemDecoration(1))

        mNewsAndArticleViewModel.newsList.observe(this,
                Observer<NewsListResponse?> { s -> initialize_list(s as NewsListResponse) })

        binding.progressBar.visibility = View.VISIBLE
        //    mNewsAndArticleViewModel.loadNewsList(context,this , "1" )
        // mNewsAndArticleViewModel.loadNewsList(context,this , "1","latest" )

        mNewsAndArticleViewModel.loadBookmarkList(this, this,  SharedPreference.getInstance().getLoggedInUser().getId())

        activity = BookmarkActivity()

        binding.apply {

            followTagTv.setOnClickListener {

              //  followTagAPI()

            }
        }

        binding.apply {

            back.setOnClickListener{
                finish()
            }

        }
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

}