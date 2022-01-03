package com.emedicoz.app.bookmark.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.emedicoz.app.bookmark.adapter.ChildBookmarkAdapter
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.emedicoz.app.bookmark.NewBookMarkActivity
import android.widget.Toast
import com.emedicoz.app.R
import com.emedicoz.app.bookmark.model.Datum
import com.emedicoz.app.databinding.ItembookmarknewBinding
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.Helper
import java.util.ArrayList

class ChildBookmarkAdapter(
    val itemlist: ArrayList<Datum>,
    val context: Context,
    val name: String,
    val qTypeDqb: String
) : RecyclerView.Adapter<ChildBookmarkAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItembookmarknewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        setData(holder, position)
    }

    private fun setData(holder: ChildBookmarkAdapter.MyViewHolder, position: Int) {
        holder.itemBookmarkNewBinding.textbookmarkname.text =
            itemlist[position].text + " " + "(" + itemlist[position].total + ")"
        val dr: Drawable =
            Helper.GetDrawablefunction(itemlist[position].text, context, itemlist[position].id)
        holder.itemBookmarkNewBinding.imgthumb.setImageDrawable(dr)
        holder.itemBookmarkNewBinding.cardview.setOnClickListener {
            if (position >= 0) {
                if (!itemlist[position].total.equals("0", ignoreCase = true)) {
                    Log.e(TAG, Constants.Extras.Q_TYPE_DQB + qTypeDqb)
                    val intent = Intent(context, NewBookMarkActivity::class.java)
                    intent.putExtra(Constants.Extras.ID, itemlist[position].id)
                    intent.putExtra(Constants.Extras.NAME, itemlist[position].text)
                    intent.putExtra(Constants.Extras.TYPE, Constants.Extras.BOOKMARK)
                    intent.putExtra(Constants.Extras.NAME_OF_TAB, name)
                    intent.putExtra(Constants.Extras.Q_TYPE_DQB, qTypeDqb)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.no_bookmarks_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return itemlist.size
    }

    inner class MyViewHolder(val itemBookmarkNewBinding: ItembookmarknewBinding) :
        RecyclerView.ViewHolder(itemBookmarkNewBinding.root)

    companion object {
        private const val TAG = "ChildBookmarkAdapter"
    }

}