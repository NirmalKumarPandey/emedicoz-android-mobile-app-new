package com.emedicoz.app.gamification.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.MyAttemptAdapterBinding

class MyAttemptAdapter(val ctx: Context) : RecyclerView.Adapter<MyAttemptAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val itemBinding = MyAttemptAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        with(holder) {
            with(itemBinding) {
                when (position) {
                    0 -> {
                        tvMyAttempted.text = "Attempted"
                    }
                    1 -> {
                        tvMyAttempted.text = "Won"
                        tvScore.text = "11:00 AM"
                        tvTime.text = "Time"
                        tvTime.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_watch_fill, 0, 0)
                    }
                    2 -> {
                        tvScore.text = "11:00 AM"
                        tvMyAttempted.text = "Hosted"
                        tvTime.text = "Time"
                        tvTime.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_watch_fill, 0, 0)
                    }

                }

            }
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    inner class CustomViewHolder(val itemBinding: MyAttemptAdapterBinding) : RecyclerView.ViewHolder(itemBinding.root)

}