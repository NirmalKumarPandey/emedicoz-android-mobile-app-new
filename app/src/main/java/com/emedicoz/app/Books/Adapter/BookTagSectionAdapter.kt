package com.emedicoz.app.Books.Adapter

import Books
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
import com.emedicoz.app.Books.Activity.BooksListActivity
import com.emedicoz.app.Books.Model.AllCategory
import com.emedicoz.app.R
import com.emedicoz.app.databinding.TagSelectionItemBinding


class BookTagSectionAdapter(private val context: Context, private val allCategoryList: List<Books>) : RecyclerView.Adapter<BookTagSectionAdapter.BookTagViewHolder>() {
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): BookTagViewHolder {
        val binding = TagSelectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookTagViewHolder(binding)
    }

    override fun onBindViewHolder(@NonNull holder: BookTagViewHolder, position: Int) {
        //  holder.categoryTitle.setText(allCategoryList[position])


        holder.tagSelectionItemBinding.parentRv.setOnClickListener {

            val intent = Intent(context, BookDescriptionActivity::class.java)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return allCategoryList.size
    }
/*
    class BookTagViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
     //   var categoryTitle: TextView
        init {
         //   categoryTitle = itemView.findViewById(R.id.nameTv)

        }
    }*/

    inner class BookTagViewHolder(var tagSelectionItemBinding: TagSelectionItemBinding)
        :RecyclerView.ViewHolder(tagSelectionItemBinding.root)


}