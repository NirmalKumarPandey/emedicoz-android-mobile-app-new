package com.emedicoz.app.courses.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.databinding.SingleItemCourseAdapterBinding

class SelectCourseAdapter(var context: Context) :
    RecyclerView.Adapter<SelectCourseAdapter.SelectCourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCourseViewHolder {
        val binding =
            SingleItemCourseAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return SelectCourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectCourseViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 5
    }

    class SelectCourseViewHolder(binding: SingleItemCourseAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)
}