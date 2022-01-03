package com.emedicoz.app.Books.Activity

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.Books.Adapter.CartAdapter
import com.emedicoz.app.R
import com.emedicoz.app.databinding.ActivityCartBinding
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.layout_common_toolbar.*
import java.util.*


class CartActivity : AppCompatActivity() /*,RecyclerItemTouchHelper.RecyclerItemTouchHelperListener*/ {

    var binding: ActivityCartBinding? = null

    val list: ArrayList<String> = ArrayList()
    var cartAdapter: CartAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)

        setContentView(binding!!.root)

        list.add("anil")
        list.add("anil")
        list.add("anil")
        list.add("anil")

        toolbarTitleTV.text = "Cart"
        toolbarBackIV.setOnClickListener(View.OnClickListener { finish() })


        // setContentView(R.layout.activity_cart)
        val linearLayoutManager = LinearLayoutManager(this)
        binding!!.cartRecyclerView.setLayoutManager(linearLayoutManager)
        binding!!.cartRecyclerView.setHasFixedSize(true)
        cartAdapter = CartAdapter(this, list)
        binding!!.cartRecyclerView.setAdapter(cartAdapter)


        /*  val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
          ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(cart_recyclerView )*/


        val callback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Take action for the swiped item

                try {
                    val position = viewHolder.adapterPosition
                    list?.removeAt(position)
                    cartAdapter?.notifyItemRemoved(position)
                    //  val item: String = cartAdapter?.notifyItemRemoved(position)
                    val snackbar = Snackbar.make(viewHolder.itemView, "Item " + (if (android.R.attr.direction == ItemTouchHelper.RIGHT) "deleted" else "archived") + ".", Snackbar.LENGTH_LONG)
                    snackbar.setAction(android.R.string.cancel) {
                        try {
                            //  mAdapter.addItem(item, position)
                        } catch (e: Exception) {
                            //  Log.e("MainActivity", e.message)
                        }
                    }
                    snackbar.show()
                } catch (e: Exception) {
                    // Log.e("MainActivity", e.message)
                }
            }


            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@CartActivity, R.color.cart_delete_bg))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_white_24)
                        //.addSwipeRightLabel(getString(R.string.action_delete))
                        .setSwipeLeftLabelColor(Color.WHITE)
                        //  .addSwipeLeftLabel(getString(R.string.action_archive))
                        .create()
                        .decorate()



                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }


        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(cart_recyclerView)


    }


/*
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        TODO("Not yet implemented")

      // cartAdapter.removeItem(viewHolder.getAdapterPosition());
        list.removeAt(viewHolder?.adapterPosition!!)
       cartAdapter?.notifyItemRemoved(viewHolder?.adapterPosition!!)



    }*/


}

