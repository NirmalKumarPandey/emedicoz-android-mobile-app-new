package com.emedicoz.app.cart

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.AddresslistRowBinding
import com.emedicoz.app.network.ApiInterfacesNew
import com.emedicoz.app.network.model.AddressBook
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import com.google.gson.JsonObject
import com.orhanobut.dialogplus.DialogPlus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressListRecycleViewAdapter(
    private val context: Context,
    var addresslist: List<AddressBook>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding =
            AddresslistRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressListRowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AddressListRowViewHolder).mAddresslistRowBinding.username.text =
            addresslist.get(position).name
        (holder).mAddresslistRowBinding.address.text = addresslist.get(position).address+", "+addresslist[position].city+", "+addresslist[position].state+", "+addresslist[position].country
        (holder).mAddresslistRowBinding.cityPincode.text =
            addresslist.get(position).city + " : " + addresslist.get(position).pincode
        (holder).mAddresslistRowBinding.phonenumber.text =
            "Phone No : " + addresslist.get(position).phone
        (holder).mAddresslistRowBinding.addressSelect.setOnClickListener {
            callDefaultAddressApi(position)
        }
        holder.mAddresslistRowBinding.addressSelect.isChecked =
            addresslist[position].is_default == "1"

        holder.mAddresslistRowBinding.editaddress.setOnClickListener {
            val i = Intent(context, AddressSetting::class.java).putExtra(
                "ADDRESS_DATA",
                addresslist[position]
            ).putExtra("is_edit", "1")
            (context as AddressList).startForResult.launch(i)
        }
        if (addresslist[position].is_default.equals("1", false)) {
            holder.mAddresslistRowBinding.defaultAddress.visibility = View.VISIBLE
          //  holder.mAddresslistRowBinding.defaultAddressBtn.visibility = View.GONE
        } else {
            holder.mAddresslistRowBinding.defaultAddress.visibility = View.GONE
            //holder.mAddresslistRowBinding.defaultAddressBtn.visibility = View.VISIBLE
        }

        holder.mAddresslistRowBinding.defaultAddressBtn.setOnClickListener {
            callDefaultAddressApi(position)
        }

        holder.mAddresslistRowBinding.deleteAddress.setOnClickListener {
            if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.SUBTITLE))) {
                SharedPreference.getInstance().putString(Const.SUBTITLE, "")
            }

            val dialog = DialogPlus.newDialog(context)
                .setContentHolder(com.orhanobut.dialogplus.ViewHolder(R.layout.layout_delete_address))
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .setContentBackgroundResource(R.drawable.dialog_top_corner)
                .create()

            val view = dialog.holderView

            val tvTitle = view.findViewById<TextView>(R.id.tv_title)
            tvTitle.text = "Delete Address"
            val tvName = view.findViewById<TextView>(R.id.tv_name)
            tvName.text = addresslist[position].name

            val tvDesc = view.findViewById<TextView>(R.id.tv_description)
            tvDesc.text = addresslist[position].address

            val tvMobile = view.findViewById<TextView>(R.id.tv_mobile)
            tvMobile.text = "Phone No : " + addresslist[position].phone

            val tvText = view.findViewById<TextView>(R.id.tvText)
            tvText.text = "Are you sure you want to delete this address?"

            val btnCancel = view.findViewById<Button>(R.id.btn_cancel)
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            val btnSubmit = view.findViewById<Button>(R.id.btn_submit)
            btnSubmit.text = "Delete"
            btnSubmit.setOnClickListener {
                dialog.dismiss()
                callDeleteAddressApi(position)
            }
            dialog.show()

/*            getAddressDeleteDialog(
                context,
                "Delete Address",
                addresslist[position].address,
                position
            )*/
        }
    }

    override fun getItemCount(): Int {
        return addresslist.size;
    }

    inner class AddressListRowViewHolder(val mAddresslistRowBinding: AddresslistRowBinding) :
        RecyclerView.ViewHolder(mAddresslistRowBinding.root)

    private fun callDefaultAddressApi(
        position: Int
    ) {

        val mApiInterface =
            ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.setDefaultAddress(
            SharedPreference.getInstance().loggedInUser.id,
            addresslist[position].id
        )

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Response : ", response.body().toString())
                //Toast.makeText(context, "aaaa", Toast.LENGTH_SHORT).show()
                (context as AddressList).setObserver()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {

            }


        })

    }


    private fun callDeleteAddressApi(
        position: Int
    ) {

        val mApiInterface =
            ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.deleteAddress(
            SharedPreference.getInstance().loggedInUser.id,
            addresslist[position].id
        )

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Response : ", response.body().toString())
                //Toast.makeText(context, "aaaa", Toast.LENGTH_SHORT).show()
                // (context as AddressList).setObserver()
                (addresslist as MutableList).removeAt(position)
                notifyDataSetChanged()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {

            }


        })

    }

    private fun getAddressDeleteDialog(
        context1: Context,
        title: String?,
        message: String?,
        position: Int
    ) {
        val v = Helper.newCustomDialog(context1, true, title, message)
        val btnCancel: Button = v.findViewById(R.id.btn_cancel)
        val btnSubmit: Button = v.findViewById(R.id.btn_submit)
        btnCancel.text = context1.getString(R.string.no)
        btnSubmit.text = context1.getString(R.string.yes)
        btnCancel.setOnClickListener { Helper.dismissDialog() }
        btnSubmit.setOnClickListener {
            Helper.dismissDialog()
            callDeleteAddressApi(position)
        }
    }
}