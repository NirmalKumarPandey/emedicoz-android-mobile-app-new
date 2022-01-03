package com.emedicoz.app.cart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.cart.callback.OnCartAddressClick
import com.emedicoz.app.databinding.SingleItemMyCartAddressBinding
import com.emedicoz.app.network.model.AddressBook

class MyCartAddressAdapter(
        private val context: Context,
        var addresslist: List<AddressBook>,
        var onCartAddressClick: OnCartAddressClick
) : RecyclerView.Adapter<MyCartAddressAdapter.MyCartAddressViewHolder>() {

    var selectedPosition: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartAddressViewHolder {
        val binding = SingleItemMyCartAddressBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        )
        return MyCartAddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyCartAddressViewHolder, position: Int) {
        holder.binding.name.text = addresslist[position].name
        holder.binding.phone.text = "Phone: ${addresslist[position].phone}"
        holder.binding.address.text = addresslist[position].address

        if (addresslist[position].is_default == "1") {
            holder.binding.defaultBtn.visibility = View.VISIBLE
        } else {
            holder.binding.defaultBtn.visibility = View.GONE
        }

        if (selectedPosition == -1){
            if (addresslist[position].is_default == "1") {
                selectedPosition = position
            }
        }

        if (selectedPosition == position) {
            holder.binding.cartAddressCV.setCardBackgroundColor(
                    ContextCompat.getColor(
                            context,
                            R.color.sky_blue
                    )
            )
        } else {
            holder.binding.cartAddressCV.setCardBackgroundColor(
                    ContextCompat.getColor(
                            context,
                            R.color.colorGray2
                    )
            )
        }

        holder.binding.cartAddressCV.setOnClickListener {
            selectedPosition = position
            addresslist[selectedPosition].selectedAddress = "1"
            // addresslist[position].is_default = "1"
            onCartAddressClick.onCartAddressClicked(addresslist[position], position)
            notifyDataSetChanged()

        }
    }

    override fun getItemCount(): Int {
        return addresslist.size
    }

    class MyCartAddressViewHolder(var binding: SingleItemMyCartAddressBinding) :
            RecyclerView.ViewHolder(binding.root)
}