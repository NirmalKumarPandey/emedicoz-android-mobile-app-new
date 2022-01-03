package com.emedicoz.app.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.databinding.CouponListItemBinding
import com.emedicoz.app.modelo.courses.Coupon

class CouponListAdapter(private val couponList : ArrayList<Coupon>, private val context : Context, private val onCouponClickListener: OnCouponClickListener) : RecyclerView.Adapter<CouponListAdapter.CouponListViewHolder>() {

    inner class CouponListViewHolder(val binding : CouponListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponListViewHolder {
        val binding = CouponListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return couponList.size
    }

    override fun onBindViewHolder(holder: CouponListViewHolder, position: Int) {
        val couponItem = couponList.get(position)

        holder.binding.couponTitleTextView.text = couponItem.coupon_tilte
        holder.binding.couponTotalItem.setOnClickListener {

            holder.binding.couponRadioButton.isChecked = true
            onCouponClickListener.onCouponClick(position)
        }

        holder.binding.couponRadioButton.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {

                onCouponClickListener.onCouponClick(position)
            }
        })

    }
}