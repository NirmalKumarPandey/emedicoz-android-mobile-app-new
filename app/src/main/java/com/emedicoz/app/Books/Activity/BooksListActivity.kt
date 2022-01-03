package com.emedicoz.app.Books.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.Books.Adapter.BooksListAdapter
import com.emedicoz.app.Books.Model.BookListResponse
import com.emedicoz.app.Books.Model.DataResponse
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.ActivityBooksListBinding
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.apiinterfaces.BookApis
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.layout_common_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class BooksListActivity : AppCompatActivity() {

    var binding:ActivityBooksListBinding?=null
    var type:String?=null
    private lateinit var mprogress: Progress
    private  val TAG = "BooksListActivity"
    val categoryItemList: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityBooksListBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        mprogress = Progress(this)
        mprogress.setCancelable(false)
         type = intent.getStringExtra(Const.BOOK_TYPE)

        for(i in 0..20)
        {
            categoryItemList.add("anil")
        }

        booksListDetail()
        toolbarTitleTV.text = "Top Trending"
        toolbarBackIV.setOnClickListener(View.OnClickListener { finish() })

     //for strike

        /*strike.setPaintFlags(strike.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);*/
    }

    fun booksListDetail() {
        mprogress.show()
        val apis = ApiClient.createService(BookApis::class.java)
        val mCall = apis?.bookDetailsList(SharedPreference.getInstance().getLoggedInUser().getId(),type)
        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Book List", "Response News : " + response.body())

                var bookListResponse : BookListResponse = Gson().fromJson(response.body(), BookListResponse::class.java)
                mprogress.dismiss()
                if (bookListResponse.status.equals(true))
                {
                    binding!!.layoutNoContentFound.visibility=View.GONE
                   // initialize_list(bookListResponse)
                    setMainCategoryRecycler(bookListResponse)

                }else
                {
                    mprogress.dismiss()
                    binding!!.layoutNoContentFound.visibility=View.VISIBLE
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mprogress.dismiss()
                Helper.showExceptionMsg(this@BooksListActivity, t)
            }
        })
    }
    private fun setMainCategoryRecycler(bookListResponse : BookListResponse) {
        val layoutManager = GridLayoutManager(this, 2)
        binding!!.recyclerView.setLayoutManager(layoutManager)
        val booksListAdapter= BooksListAdapter(this,bookListResponse.data)
        binding!!.recyclerView.setAdapter(booksListAdapter)
    }

}