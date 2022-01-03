package com.emedicoz.app.Books.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.Books.Adapter.BookDescriptionAlsoBuyListAdapter
import com.emedicoz.app.Books.Adapter.DescriptionViewPagerAdapter
import com.emedicoz.app.Books.Model.BookDetail
import com.emedicoz.app.Books.Model.BookDetailResponse
import com.emedicoz.app.R
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.ActivityBookDescriptionBinding
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

class BookDescriptionActivity : AppCompatActivity() {

    private lateinit var mprogress: Progress
    private val TAG = "BookDescriptionActivity"

    var binding: ActivityBookDescriptionBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDescriptionBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        mprogress = Progress(this)
        mprogress.setCancelable(false)

        toolbarTitleTV.text = "Description"
        toolbarBackIV.setOnClickListener(View.OnClickListener { finish() })

        bookDetails()

        val myArray3 = arrayOf<String>(resources.getDrawable(R.mipmap.book_toptreanding).toString(), resources.getDrawable(R.mipmap.subject_book).toString())


        //val viewPager: ViewPager =findViewById(R.id.viewPagerMain)
        val imageViewPagerAdapter = DescriptionViewPagerAdapter(this, myArray3)
        binding!!.viewPagerMain.adapter = imageViewPagerAdapter
        binding!!.tabLayout.setupWithViewPager(binding!!.viewPagerMain, true)

        val categoryItemList: MutableList<String> = ArrayList()
        for (i in 0..10) {
            categoryItemList.add("anil")
        }

       setAlsoByBookList(categoryItemList)

        binding!!.buyNowBtn.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)

        }

        binding!!.AddToCartBT.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)

        }

        //  binding!!.mrpTv.setPaintFlags( binding!!.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
        binding!!.mrpTv.paintFlags = binding!!.mrpTv.paint.hinting


    }

    private fun setAlsoByBookList(allCategoryList: MutableList<String>) {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding!!.recyclerView.setLayoutManager(layoutManager)
        val bookHomeAdapter = BookDescriptionAlsoBuyListAdapter(this, allCategoryList)
        binding!!.recyclerView.setAdapter(bookHomeAdapter)
    }


    fun bookDetails() {
        mprogress.show()
        val apis = ApiClient.createService(BookApis::class.java)
        val mCall = apis?.bookDetails(SharedPreference.getInstance().getLoggedInUser().getId(), "677")
        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Book List", "Response News : " + response.body())

                var bookDetailsResponse: BookDetailResponse = Gson().fromJson(response.body(), BookDetailResponse::class.java)

                 mprogress.dismiss()
                if (bookDetailsResponse.status.equals(true)) {
                    binding!!.layoutNoContentFound.visibility = View.GONE
                    // setBookDetails(bookDetailsResponse)

                 //   setBookDetail(bookDetailsResponse.data.detail)

                } else {
                    binding!!.layoutNoContentFound.visibility = View.VISIBLE

                    mprogress.dismiss()
                    //  Helper.printError(TAG, response.errorBody())
                }

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // mProgressListner.update(false)

                mprogress.dismiss()
                Helper.showExceptionMsg(this@BookDescriptionActivity, t)
            }
        })
    }

    private fun setBookDetail(bookDetail: BookDetail) {


        binding!!.bookTitleTV.text = bookDetail.title
        binding!!.mrpTv.text = "MRP: " + bookDetail.mrp.toString()
        binding!!.priceTv.text = "199"

        // binding!!.parcentageTv.text=bookDetail

        //  binding!!.ratingRB.numStars=bookDetail
        binding!!.txtRatingTV.text = bookDetail.rating.toString()+" Rating"
       // binding!!.deleveryDateTv.text = bookDetail.
       // binding!!.availableStockTv.text = bookDetail.
        binding!!.paperPrintTv.text = bookDetail.paper_book_price.toString()
        binding!!.ebookPriceTv.text = bookDetail.ebook_price.toString()
        binding!!.audiblebookTV.text = bookDetail.audiable_price.toString()

        binding!!.noPagesTV.text = bookDetail.ebook_page.toString()
        binding!!.languageTv.text = bookDetail.language
        binding!!.publisherTV.text = bookDetail.publisher
        binding!!.pulicationDateTV.text = bookDetail.publication_date
        binding!!.descriptionTV.text = bookDetail.description

    }

}