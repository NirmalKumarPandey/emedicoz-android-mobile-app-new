package com.emedicoz.app.cart

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emedicoz.app.network.NetworkRepository
import com.emedicoz.app.network.model.AddressBook
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.ui.podcast.StoryName


class AddressListViewModel : ViewModel() {

    public val addressList = MutableLiveData<List<AddressBook>>()


    fun loadData(  mContext: Context?, mProgressListner: ProgressUpdateListner? , user_id : String) {
        NetworkRepository().callAddressList(mContext,mProgressListner , this , user_id )
    }

    fun insertAddress(  mContext: Context?, mProgressListner: ProgressUpdateListner ,mAddressBook: AddressBook?   ) {
        NetworkRepository().insertAddressList(mContext,mProgressListner , this , mAddressBook )
    }

    fun editAddress(  mContext: Context?, mProgressListner: ProgressUpdateListner ,mAddressBook: AddressBook?   ) {
        NetworkRepository().editAddressList(mContext,mProgressListner , this , mAddressBook )
    }
}