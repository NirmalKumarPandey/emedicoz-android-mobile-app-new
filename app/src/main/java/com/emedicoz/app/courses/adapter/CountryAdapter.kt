package com.emedicoz.app.courses.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.cart.AddressSetting
import com.emedicoz.app.databinding.StateAdapterItemBinding
import com.emedicoz.app.modelo.State

class CountryAdapter(var context: Context,var stringArrayList: List<String>):RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = StateAdapterItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.binding.nameTv.text = stringArrayList[position]
        holder.binding.countryParentLL.setOnClickListener {
            Log.e( "onBindViewHolder: ","CALLED" )
            (context as AddressSetting).getCountryData(stringArrayList[position])
        }
    }

    override fun getItemCount(): Int {
        return stringArrayList.size
    }

    class CountryViewHolder(var binding:StateAdapterItemBinding):RecyclerView.ViewHolder(binding.root)

    fun filterList(newemployeeList: List<String>) {
        stringArrayList = newemployeeList
        notifyDataSetChanged()
    }
}