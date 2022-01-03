package com.emedicoz.app.Books.Adapter

import Best_seller
import Books
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.Books.Activity.BooksListActivity
import com.emedicoz.app.Books.Activity.CustomTouchListener
import com.emedicoz.app.Books.Model.Category
import com.emedicoz.app.Books.Model.Data
import com.emedicoz.app.Books.Model.Subject
import com.emedicoz.app.Books.Model.Trending
import com.emedicoz.app.customviews.onItemClickListener
import com.emedicoz.app.databinding.BookHomeItemBinding
import com.emedicoz.app.utilso.Const
import java.util.*
class BookHomeAdapter(private val context: Context, private val allCategoryList: List<Data>) : RecyclerView.Adapter<BookHomeAdapter.MainViewHolder>(){
    var binding:BookHomeItemBinding?=null

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): MainViewHolder {
         binding= BookHomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  MainViewHolder(binding!!)

    }

    override fun onBindViewHolder(@NonNull holder: MainViewHolder, position: Int) {

        var list=allCategoryList.get(position)

        holder.bookHomeItemBinding.nameTv.setText(list.title)

        holder.bookHomeItemBinding.showAllHorizontalLink.setOnClickListener {
            val intent= Intent(context, BooksListActivity::class.java)
             intent.putExtra(Const.BOOK_TYPE,"tag_section")
            context.startActivity(intent)
        }
        val categoryItemList2: MutableList<String> = ArrayList()
        categoryItemList2.add("All Book")
        categoryItemList2.add("Best Seller")
        categoryItemList2.add("New Release")
        categoryItemList2.add("anil")
        categoryItemList2.add("anil")
        categoryItemList2.add("anil")



        if(position==0 ||position==1) {
            setCatItemRecycler(holder.bookHomeItemBinding.tagCategoryRecycleViewHorizontal,holder.bookHomeItemBinding.recycleViewHorizontal, allCategoryList.get(0).category)
           // setBookTagItemRecycler(holder.bookHomeItemBinding.tagCategoryRecycleViewHorizontal, allCategoryList.get(position).category.get(position).books)
        }
        else if(position==2) {
            setSubjectBookItemRecycler(holder.bookHomeItemBinding.recycleViewHorizontal, allCategoryList.get(4).subject)
        }
        else if(position==3) {
            setTopTrendingBookItemRecycler(holder.bookHomeItemBinding.recycleViewHorizontal, allCategoryList.get(2).trending)
        }
        else if(position==4) {
            setBestBookSellerItemRecycler(holder.bookHomeItemBinding.recycleViewHorizontal, allCategoryList.get(3).best_seller)

        }


    }

    override fun getItemCount(): Int {
        return allCategoryList.size
    }


    inner class MainViewHolder(val bookHomeItemBinding: BookHomeItemBinding)
        : RecyclerView.ViewHolder(bookHomeItemBinding.root)

    private fun setCatItemRecycler(recyclerView: RecyclerView,recyclerViewBook: RecyclerView, categoryItemList: List<Category>) {
        val itemRecyclerAdapter = BookTagCategoryAdapter(context, categoryItemList)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = itemRecyclerAdapter


       /* val itemRecyclerAdapter1 = BookTagSectionAdapter(context, categoryItemList.get(0).books)
        recyclerViewBook.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerViewBook.adapter = itemRecyclerAdapter1

        notifyDataSetChanged()*/
        recyclerView.addOnItemTouchListener(CustomTouchListener(context, onItemClickListener { view, index ->
            val itemRecyclerAdapter1 = BookTagSectionAdapter(context, categoryItemList.get(index).books)
            recyclerViewBook.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerViewBook.adapter = itemRecyclerAdapter1
            notifyDataSetChanged()

        }))

    }
    private fun setBookTagItemRecycler(recyclerView: RecyclerView, categoryItemList: List<Books>) {
        val itemRecyclerAdapter = BookTagSectionAdapter(context, categoryItemList)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = itemRecyclerAdapter

       /* recyclerView.addOnItemTouchListener(RecyclerItemClickListener(context)
        {
            Toast.makeText(context,"hello",Toast.LENGTH_SHORT).show()
        }

        )*/
    }

    /* private fun setBookItemRecycler(recyclerView: RecyclerView, categoryItemList: List<String>) {
         val itemRecyclerAdapter = BookTreandingAdapter(context, categoryItemList)
         recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
         recyclerView.adapter = itemRecyclerAdapter
     }*/

    private fun setSubjectBookItemRecycler(recyclerView: RecyclerView, categoryItemList: List<Subject>) {
        val itemRecyclerAdapter = SubjectBookAdapter(context, categoryItemList)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = itemRecyclerAdapter
    }

    private fun setTopTrendingBookItemRecycler(recyclerView: RecyclerView, categoryItemList: List<Trending>) {
        val itemRecyclerAdapter = BookTreandingAdapter(context, categoryItemList)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = itemRecyclerAdapter
    }

    private fun setBestBookSellerItemRecycler(recyclerView: RecyclerView, categoryItemList: List<Best_seller>) {
        val itemRecyclerAdapter = BestSellerBookAdapter(context, categoryItemList)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = itemRecyclerAdapter
    }

 /*   override fun onItemClick(value: List<Books>) {

        setBookTagItemRecycler(holder1?.bookHomeItemBinding!!.recycleViewHorizontal, value)
    }*/


}