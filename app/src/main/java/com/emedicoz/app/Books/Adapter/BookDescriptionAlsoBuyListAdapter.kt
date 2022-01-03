package com.emedicoz.app.Books.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.Books.Model.AllCategory
import com.emedicoz.app.R


class BookDescriptionAlsoBuyListAdapter(private val context: Context, private val allCategoryList: List<String>) : RecyclerView.Adapter<BookDescriptionAlsoBuyListAdapter.BookDescriptionAlsoBuyViewHolder>() {
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): BookDescriptionAlsoBuyViewHolder {
        return BookDescriptionAlsoBuyViewHolder(LayoutInflater.from(context).inflate(R.layout.book_desc_alsobuy_item, parent, false))
    }

    override fun onBindViewHolder(@NonNull holder: BookDescriptionAlsoBuyViewHolder, position: Int) {
      //  holder.categoryTitle.setText(allCategoryList[position])
    }

    override fun getItemCount(): Int {
        return allCategoryList.size
    }

    class BookDescriptionAlsoBuyViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
     //   var categoryTitle: TextView
        init {
         //   categoryTitle = itemView.findViewById(R.id.nameTv)

        }
    }

}