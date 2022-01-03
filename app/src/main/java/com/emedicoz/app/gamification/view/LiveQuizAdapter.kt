package com.emedicoz.app.gamification.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.animation.content.Content
import com.emedicoz.app.R
import com.emedicoz.app.databinding.LiveQuizAdapterBinding

class LiveQuizAdapter(val ctx: Context, val onItemClick: (Any) -> Unit) : RecyclerView.Adapter<LiveQuizAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = LiveQuizAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        with(holder) {
            with(binding) {
                when (position) {
                    0 -> {
                        tvName.text = "Create Your Own Challenge"
                        btnSelect.text = "Host A Challenge"
                    }
                    1 -> {
                        tvName.text = "Live Quiz"
                        btnSelect.text = "Play Challenge"
                    }
                    2 -> {
                        tvName.text = "You Are Challenged"
                        btnSelect.text = "Accept Challenge"
                    }
                    3 -> {
                        tvName.text = ""
                        btnSelect.text = "My Attempts"
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 4
    }

    inner class CustomViewHolder(val binding: LiveQuizAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.parentLayout.setOnClickListener {
                onItemClick.invoke(adapterPosition)
            }
        }
    }
}