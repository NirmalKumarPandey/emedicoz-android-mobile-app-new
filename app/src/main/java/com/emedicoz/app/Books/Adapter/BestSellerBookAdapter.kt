package com.emedicoz.app.Books.Adapter

import Best_seller
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
import com.emedicoz.app.R
import com.emedicoz.app.databinding.BestSellerBookItemBinding
import com.emedicoz.app.databinding.SubjectBookItemBinding


class BestSellerBookAdapter(private val context: Context, private val allCategoryList: List<Best_seller>) : RecyclerView.Adapter<BestSellerBookAdapter.BestSellerBookViewHolder>() {
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): BestSellerBookViewHolder {
     val binding=BestSellerBookItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BestSellerBookViewHolder(binding)
    }

    override fun onBindViewHolder(@NonNull holder: BestSellerBookViewHolder, position: Int) {
      //  holder.categoryTitle.setText(allCategoryList[position])


        holder.bestSellerBookItemBinding.parentRv.setOnClickListener {

            val intent = Intent(context, BookDescriptionActivity::class.java)

            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return allCategoryList.size
    }

   /* class BestSellerBookViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
     //   var categoryTitle: TextView
        init {
         //   categoryTitle = itemView.findViewById(R.id.nameTv)

        }
    }*/
    
    
    inner class BestSellerBookViewHolder(var bestSellerBookItemBinding: BestSellerBookItemBinding)
        :RecyclerView.ViewHolder(bestSellerBookItemBinding.root)

}