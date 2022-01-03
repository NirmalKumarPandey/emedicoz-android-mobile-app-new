package com.emedicoz.app.Books.Adapter

import Books
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.Books.ItemClickListener
import com.emedicoz.app.Books.Model.AllCategory
import com.emedicoz.app.Books.Model.Category
import com.emedicoz.app.R
import com.emedicoz.app.databinding.BookCategoryItemBinding


class BookTagCategoryAdapter(private val context: Context, private val allCategoryList: List<Category>) : RecyclerView.Adapter<BookTagCategoryAdapter.CategoryViewHolder>() {
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): CategoryViewHolder {

        var binding = BookCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CategoryViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(@NonNull holder: CategoryViewHolder, position: Int) {
        holder.bookTagCategoryItemBinding.nameTv.setText(allCategoryList[position].category)


       // setBookTagItemRecycler(holder.bookTagCategoryItemBinding.recycleViewHorizontal,allCategoryList[position].books)


        holder.bookTagCategoryItemBinding.nameTv.setOnClickListener {


          //  mClickListener?.onItemClick(allCategoryList[position].books)
        }



    }


    companion object {
        var mClickListener: ItemClickListener? = null
    }

    override fun getItemCount(): Int {
        return allCategoryList.size
    }


    /*private fun setBookTagItemRecycler(recyclerView: RecyclerView, categoryItemList: List<Books>) {
        val itemRecyclerAdapter = BookTagSectionAdapter(context, categoryItemList)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = itemRecyclerAdapter
    }
*/
    inner class CategoryViewHolder(var bookTagCategoryItemBinding: BookCategoryItemBinding)
        :RecyclerView.ViewHolder(bookTagCategoryItemBinding.root)

}