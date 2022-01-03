package com.emedicoz.app.Books.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.Books.Adapter.*
import com.emedicoz.app.Books.Model.AllCategory
import com.emedicoz.app.Books.Model.Banner
import com.emedicoz.app.Books.Model.BookListResponse
import com.emedicoz.app.Books.Model.DataResponse
import com.emedicoz.app.R
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.ActivityBookHomeBinding
import com.emedicoz.app.network.ApiInterfacesNew
import com.emedicoz.app.newsandarticle.Activity.NewAndArticalDetailActivity
import com.emedicoz.app.newsandarticle.models.MostViewedListRecycleViewAdapter
import com.emedicoz.app.newsandarticle.models.NewsListResponse
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.apiinterfaces.BookApis
import com.emedicoz.app.utilso.Helper

import com.emedicoz.app.utilso.SharedPreference
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.layout_common_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class BookHomeActivity : AppCompatActivity() {
    var binding: ActivityBookHomeBinding? = null

    private lateinit var mprogress: Progress
    private  val TAG = "BookHomeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        mprogress = Progress(this)
        mprogress.setCancelable(false)

        // added in first category
        val categoryItemList: MutableList<String> = ArrayList()
        categoryItemList.add("anil")
        categoryItemList.add("anil")
        categoryItemList.add("anil")
        categoryItemList.add("anil")
        categoryItemList.add("anil")

        // added in second category

        // added in second category
        val categoryItemList2: MutableList<String> = ArrayList()
        categoryItemList2.add("anil")
        categoryItemList2.add("anil")
        categoryItemList2.add("anil")
        categoryItemList2.add("anil")
        categoryItemList2.add("anil")
        categoryItemList2.add("anil")


        // added in 3rd category

        // added in 3rd category
        val categoryItemList3: MutableList<String> = ArrayList()
        categoryItemList3.add("anil")
        categoryItemList3.add("anil")
        categoryItemList3.add("anil")
        categoryItemList3.add("anil")
        categoryItemList3.add("anil")
        categoryItemList3.add("anil")
        categoryItemList3.add("anil")
        categoryItemList3.add("anil")

        // added in 4th category

        // added in 4th category
        val categoryItemList4: MutableList<String> = ArrayList()
        categoryItemList4.add("anil")
        categoryItemList4.add("anil")
        categoryItemList4.add("anil")
        categoryItemList4.add("anil")


        // added in 5th category


        // added in 5th category
        val categoryItemList5: MutableList<String> = ArrayList()
        categoryItemList5.add("anil")
        categoryItemList5.add("anil")
        categoryItemList5.add("anil")
        categoryItemList5.add("anil")
        categoryItemList5.add("anil")
        categoryItemList5.add("anil")
        categoryItemList5.add("anil")
        categoryItemList5.add("anil")


        val allCategoryList: MutableList<AllCategory> = ArrayList()
        allCategoryList.add(AllCategory("Tag Selection", categoryItemList))
        allCategoryList.add(AllCategory("Categories", categoryItemList2))
        allCategoryList.add(AllCategory("Subject Books", categoryItemList3))
        allCategoryList.add(AllCategory("Top Trending", categoryItemList4))
        allCategoryList.add(AllCategory("Best Seller", categoryItemList5))

       // setMainCategoryRecycler(allCategoryList)
        booksList()
        binding!!.cartBuuton.setOnClickListener {

            val intent = Intent(this, BooksListActivity::class.java)
            startActivity(intent)

        }

        toolbarBackIV.setOnClickListener(View.OnClickListener { finish() })


        val myArray3 = arrayOf<String>(resources.getDrawable(R.mipmap.book_banner).toString(),resources.getDrawable(R.mipmap.subject_book).toString())


        //val viewPager: ViewPager =findViewById(R.id.viewPagerMain)

        
    }

    private fun setMainCategoryRecycler(allCategoryList: MutableList<AllCategory>) {
        // mainCategoryRecycler = findViewById<View>(R.id.recyclerView)
       /* val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding!!.recyclerView.setLayoutManager(layoutManager)
        val bookHomeAdapter= BookHomeAdapter(this, allCategoryList)
        binding!!.recyclerView.setAdapter(bookHomeAdapter)*/
    }


    fun booksList() {

        mprogress.show()
        val apis = ApiClient.createService(BookApis::class.java)
        val mCall = apis?.getBookList(SharedPreference.getInstance().getLoggedInUser().getId())
        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Book List", "Response News : " + response.body())

                mprogress.dismiss()
                var bookListResponse : DataResponse = Gson().fromJson(response.body(), DataResponse::class.java)

              if (bookListResponse.status.equals(true))
              {
                  binding!!.layoutNoContentFound.visibility=View.GONE
                  initialize_list(bookListResponse)

                  getBanner(bookListResponse.banner)


              }else
              {
              binding!!.layoutNoContentFound.visibility=View.VISIBLE

                  mprogress.dismiss()
                //  Helper.printError(TAG, response.errorBody())
              }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mprogress.dismiss()
                Helper.showExceptionMsg(this@BookHomeActivity, t)
            }
        })   
    }

    private fun getBanner(list:List<Banner>) {
      //  TODO("Not yet implemented")
        val imageViewPagerAdapter = BookBannerViewPagerAdapter(this, list)
        binding!!.viewPagerMain.adapter = imageViewPagerAdapter
        binding!!.tabLayout.setupWithViewPager(binding!!.viewPagerMain, true)


    }

    private fun initialize_list(mList: DataResponse) {

       // val mAdapter = this?.let { BookHomeAdapter(it, mList.data) }
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding!!.recyclerView.setLayoutManager(layoutManager)
        val bookHomeAdapter= BookHomeAdapter(this, mList.data)
        binding!!.recyclerView.setAdapter(bookHomeAdapter)

      /*  binding!!.recyclerView.setItemAnimator(null)
        binding!!.recyclerView.setAdapter(mAdapter)*/



    }

}