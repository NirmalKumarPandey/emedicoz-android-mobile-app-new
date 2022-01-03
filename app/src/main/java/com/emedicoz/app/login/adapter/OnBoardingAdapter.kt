package com.emedicoz.app.login.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.SingleItemOnboardingBinding
import com.emedicoz.app.modelo.OnBoarding

class OnBoardingAdapter(
    private val context: Context,
    private val onBoardingList: ArrayList<OnBoarding>
) :
    RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        val binding =
            SingleItemOnboardingBinding.inflate(LayoutInflater.from(context), parent, false)
        return OnBoardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.singleItemOnboardingBinding.apply {
            onBoardingList[position].let {
                onBoardingItemTV.text = it.title
                onBoardingItemSubTV.text = it.subTitle
                onBoardingItemIV.setImageDrawable(it.image)
            }
        }
    }

    override fun getItemCount(): Int {
        return onBoardingList.size
    }

    class OnBoardingViewHolder(var singleItemOnboardingBinding: SingleItemOnboardingBinding) :
        RecyclerView.ViewHolder(singleItemOnboardingBinding.root)

}