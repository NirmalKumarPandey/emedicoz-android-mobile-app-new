package com.emedicoz.app.Books.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.Books.Activity.BookDescriptionActivity
import com.emedicoz.app.Books.Activity.CartActivity
import com.emedicoz.app.Books.Model.Data
import com.emedicoz.app.Books.Model.DataList
import com.emedicoz.app.R
import com.emedicoz.app.databinding.BooksListItemBinding


class BooksListAdapter(private val context: Context, private val allCategoryList: List<DataList>) : RecyclerView.Adapter<BooksListAdapter.BooksListViewHolder>() {
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): BooksListViewHolder {
        val binding = BooksListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BooksListViewHolder(binding)


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(@NonNull holder: BooksListViewHolder, position: Int) {
        var item = allCategoryList.get(position)
        Glide.with(context)
                .load(item.featured_image)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(holder.booksListItemBinding.bookImg)
        holder.booksListItemBinding.txtRateValue.text = item.rating.toString()
        holder.booksListItemBinding.bookNameTV.text = item.title
        holder.booksListItemBinding.authoreNameTv.text = "By :" + item.brand_name
        holder.booksListItemBinding.priceTv.text = context.getString(R.string.price_symble) +"119"
        holder.booksListItemBinding.mrpTv.text =item.mrp.toString()
      //  holder.booksListItemBinding.parcentageTv.text =
        //Resources.getSystem().getString(R.string.btn_yes)

        holder.booksListItemBinding.cartCV.setOnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            context.startActivity(intent)

        }


        holder.booksListItemBinding.parentRv.setOnClickListener {
            val intent = Intent(context, BookDescriptionActivity::class.java)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return allCategoryList.size
    }

    inner class BooksListViewHolder(val booksListItemBinding: BooksListItemBinding)
        : RecyclerView.ViewHolder(booksListItemBinding.root)
}