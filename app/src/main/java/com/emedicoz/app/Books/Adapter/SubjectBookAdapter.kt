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
import com.emedicoz.app.Books.Model.Subject
import com.emedicoz.app.R
import com.emedicoz.app.databinding.SubjectBookItemBinding


class SubjectBookAdapter(private val context: Context, private val allCategoryList: List<Subject>) : RecyclerView.Adapter<SubjectBookAdapter.SubjectBookViewHolder>() {
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): SubjectBookViewHolder {
       // return SubjectBookViewHolder(LayoutInflater.from(context).inflate(R.layout.subject_book_item, parent, false))

  val binding=SubjectBookItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SubjectBookViewHolder(binding)
    }

    override fun onBindViewHolder(@NonNull holder: SubjectBookViewHolder, position: Int) {
      //  holder.categoryTitle.setText(allCategoryList[position])


       holder.subjectBookItemBinding.parentRv.setOnClickListener {

            val intent = Intent(context, BookDescriptionActivity::class.java)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return allCategoryList.size
    }

  /*  class SubjectBookViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
     //   var categoryTitle: TextView

        init {
         //   categoryTitle = itemView.findViewById(R.id.nameTv)

        }
    }*/

    inner class SubjectBookViewHolder(var subjectBookItemBinding: SubjectBookItemBinding)
        :RecyclerView.ViewHolder(subjectBookItemBinding.root)

}