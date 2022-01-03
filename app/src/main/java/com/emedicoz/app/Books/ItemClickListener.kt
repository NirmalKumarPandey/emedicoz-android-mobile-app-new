package com.emedicoz.app.Books

import Books
import android.content.Context
import androidx.recyclerview.widget.RecyclerView

interface ItemClickListener {
    fun onItemClick(value:List<Books>)
    abstract fun RecyclerItemClickListener(context: Context, any: Any): RecyclerView.OnItemTouchListener
}