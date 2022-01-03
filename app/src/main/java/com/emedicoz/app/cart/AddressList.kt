package com.emedicoz.app.cart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.R
import com.emedicoz.app.databinding.AddresslistBinding
import com.emedicoz.app.network.model.AddressBook
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.ui.podcast.Adapter.SpacesItemDecoration
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.utilso.SharedPreference
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_common_toolbar.*

class AddressList : AppCompatActivity(), View.OnClickListener, ProgressUpdateListner {

    lateinit var mAddressListViewModel: AddressListViewModel;
    var mAddressBook: AddressBook? = null
    lateinit var binding: AddresslistBinding

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                // Handle the Intent
                var t = intent?.extras?.get("Address").toString()
                var addressBook = Gson().fromJson<AddressBook>(t, AddressBook::class.java)
                mAddressBook = addressBook
                // load Address initialize_value(mAddressBook)
            }
        }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.addressbutton -> {
                val i = Intent(this, AddressSetting::class.java)
                startForResult.launch(i)
                this.finish()
            }

            R.id.toolbarBackIV -> onBackPressed()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        binding = AddresslistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*setSupportActionBar(binding.toolbar);
        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar?.setDisplayHomeAsUpEnabled(true)*/

        val linearLayoutManager = LinearLayoutManager(this)
        binding.RecyclerView.setLayoutManager(linearLayoutManager)
        binding.RecyclerView.setHasFixedSize(true)

        binding.RecyclerView.addItemDecoration(SpacesItemDecoration(2))



     /*   setSupportActionBar(binding.toolbar);
        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar?.setDisplayHomeAsUpEnabled(true)*/

        binding.apply {
            addressbutton.setOnClickListener(this@AddressList)
           toolbarBackIV.setOnClickListener(this@AddressList)

        }
        binding.toolbarAddressList.toolbarTitleTV.text = "My Addresses"

        mAddressListViewModel = ViewModelProvider(this).get(AddressListViewModel::class.java)
        setObserver()
    }

    public fun setObserver() {
        mAddressListViewModel.addressList.observe(this,
            Observer<List<AddressBook?>?> { s -> initialize_list(s as List<AddressBook>) })

        binding.progressBar.visibility = View.VISIBLE
        mAddressListViewModel.loadData(this, this, SharedPreference.getInstance().loggedInUser.id)
    }


    private fun initialize_list(mList: List<AddressBook>) {

        // Toast.makeText(getActivity(), "Inside ")

        // Creating the Adapter class
        val mAdapter = AddressListRecycleViewAdapter(this, mList)
        // mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.RecyclerView.setItemAnimator(null)
        binding.RecyclerView.setAdapter(mAdapter)

        if (mList.size != null && mList.size == 1 || mList.size == 0) {
            binding.noofsavedAddrssTv.text = mList.size.toString() + " Saved Address"
        } else {
            binding.noofsavedAddrssTv.text = mList.size.toString() + " Saved Addressess"
        }

        /*    binding.RecyclerView.addOnItemTouchListener(RecyclerItemClickListener(
                this
            ) { view, temPosition ->
                when (temPosition) {
                    0 -> {

                        Toast.makeText(this ,"pos click 0 ", Toast.LENGTH_SHORT ).show()
                    }
                    1 -> {
                    }
                    else -> {
                    }
                }
            })*/
    }


    override fun update(b: Boolean) {

        // System.out.print("Update Recieved : "+ b)

        binding.progressBar.visibility = View.GONE
    }

    fun returnAddress(mAddress: AddressBook, pos: Int) {

        val returnIntent = Intent()
        returnIntent.putExtra("result", "result")
        returnIntent.putExtra("Address", Gson().toJson(mAddress))


        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}