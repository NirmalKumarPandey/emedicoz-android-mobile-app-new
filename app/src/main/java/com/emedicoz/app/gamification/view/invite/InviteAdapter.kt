package com.emedicoz.app.gamification.view.invite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.databinding.AdapterInviteBinding

class InviteAdapter : RecyclerView.Adapter<InviteAdapter.CustomViewHolder>() {

    inner class CustomViewHolder(val adapterBinding: AdapterInviteBinding) : RecyclerView.ViewHolder(adapterBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val adapterBinding = AdapterInviteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(adapterBinding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        with(holder) {
            with(adapterBinding) {
                tvName.text = "Parmatma"
            }
        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}