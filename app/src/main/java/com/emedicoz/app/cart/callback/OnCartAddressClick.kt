package com.emedicoz.app.cart.callback

import com.emedicoz.app.network.model.AddressBook

interface OnCartAddressClick {
    fun onCartAddressClicked(address: AddressBook, position: Int)
}