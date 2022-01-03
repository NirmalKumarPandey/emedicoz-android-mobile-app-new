package com.emedicoz.app.Books.Adapter

import android.app.Fragment
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.*
import com.emedicoz.app.newsandarticle.models.Tag
import com.emedicoz.app.newsandarticle.models.TopicDetail
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class CartAdapter(private val context: Context, var mTopicList: List<String>) : RecyclerView.Adapter<CartAdapter.CartRowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartRowViewHolder {

        val binding =CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartRowViewHolder(binding)
    }

  //  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

      //  (holder as CartRowViewHolder).cartItemBinding.tagNameTv.text = mTopicList.get(position)

     /*  holder.mTopicTagRowBinding.cardView.setOnClickListener(
                View.OnClickListener {
                }
        )*/


 //   }
    override fun getItemCount(): Int {
        return mTopicList.size;
    }


  public  inner class CartRowViewHolder(val cartItemBinding: CartItemBinding)
        : RecyclerView.ViewHolder(cartItemBinding.root)

    override fun onBindViewHolder(holder: CartRowViewHolder, position: Int) {

        holder.cartItemBinding.itemIncreaseTV.setOnClickListener {


               val value: CharSequence? = holder.cartItemBinding.noOfItemTV.text

               // holder.cartItemBinding.noOfItemTV.text

        }

        holder.cartItemBinding.itemDecreaseTv.setOnClickListener {



        }

    }

}