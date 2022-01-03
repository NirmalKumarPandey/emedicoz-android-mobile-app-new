package com.emedicoz.app.Books.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.Books.Activity.BookDescriptionActivity
import com.emedicoz.app.Books.Model.AllCategory
import com.emedicoz.app.Books.Model.Trending
import com.emedicoz.app.R
import com.emedicoz.app.databinding.BookTrendingItemBinding


class BookTreandingAdapter(private val context: Context, private val allCategoryList: List<Trending>) : RecyclerView.Adapter<BookTreandingAdapter.BookTreandingViewHolder>() {
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): BookTreandingViewHolder {

        val binding = BookTrendingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookTreandingViewHolder(binding)
    }

    override fun onBindViewHolder(@NonNull holder: BookTreandingViewHolder, position: Int) {
        //  holder.categoryTitle.setText(allCategoryList[position])
        holder.bookTrendingItemBinding.parentRv.setOnClickListener {

            val intent = Intent(context, BookDescriptionActivity::class.java)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return allCategoryList.size
    }

    inner class BookTreandingViewHolder(var bookTrendingItemBinding: BookTrendingItemBinding)
        : RecyclerView.ViewHolder(bookTrendingItemBinding.root)

}