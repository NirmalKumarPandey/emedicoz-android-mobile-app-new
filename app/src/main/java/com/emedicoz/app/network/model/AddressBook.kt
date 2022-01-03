package com.emedicoz.app.network.model

import android.os.Parcelable
import java.io.Serializable

data class AddressBook(
    val user_id: String,
    val id: String,
    var name: String,
    var code : String,
    var phone: String,
    var pincode: String,
    var address: String,
    var address_2: String,
    var city: String,
    var state: String,
    var country: String,
    var latitude: String,
    var longitude: String,
    var is_default: String,
    var selectedAddress: String,

) : Serializable
