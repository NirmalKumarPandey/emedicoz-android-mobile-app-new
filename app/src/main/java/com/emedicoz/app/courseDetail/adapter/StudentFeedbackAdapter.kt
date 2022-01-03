package com.emedicoz.app.courseDetail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.databinding.LayoutFeedbackRowBinding

class StudentFeedbackAdapter(private val hashMap: HashMap<Double, Float>, private val Keys: Array<Double>, private val context: Context)
    : RecyclerView.Adapter<StudentFeedbackAdapter.StudentFeedbackAdapterViewHolder>() {


    override fun onBindViewHolder(holder: StudentFeedbackAdapterViewHolder, position: Int) {
        val key = Keys[position]

        holder.binding.apply {
            val itemValue: Float = hashMap[key]!!
            progressBar1.progress = itemValue.toInt()
            tvRating1.text = "$itemValue%"
            ratingLevel1.rating = key.toFloat()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentFeedbackAdapterViewHolder {
        val binding = LayoutFeedbackRowBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentFeedbackAdapterViewHolder(binding)
    }


    inner class StudentFeedbackAdapterViewHolder(val binding: LayoutFeedbackRowBinding)
        : RecyclerView.ViewHolder(binding.root)


    override fun getItemCount() = hashMap.size

}