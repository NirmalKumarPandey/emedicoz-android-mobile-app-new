package com.emedicoz.app.gamification.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.databinding.AcceptChallengeAdapterBinding

class AcceptChallengeAdapter(private val ctx: Context) : RecyclerView.Adapter<AcceptChallengeAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val itemBinding = AcceptChallengeAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        with(holder) {
            with(itemBinding) {
                tvQuestion.text = "30"
            }
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

    inner class CustomViewHolder(val itemBinding: AcceptChallengeAdapterBinding) : RecyclerView.ViewHolder(itemBinding.root)

}