package com.emedicoz.app.cart

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.courses.adapter.CountryAdapter
import com.emedicoz.app.databinding.ActivityAddressSettingBinding
import com.emedicoz.app.feeds.fragment.CountryStateCity
import com.emedicoz.app.modelo.State
import com.emedicoz.app.network.model.AddressBook
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.MyNetworkCall.MyNetworkCallBack
import com.emedicoz.app.utilso.network.API
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.tv.view.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set


class AddressSetting : AppCompatActivity(), View.OnClickListener, MyNetworkCallBack,
    ProgressUpdateListner {
    lateinit var binding: ActivityAddressSettingBinding
    var statelist: ArrayList<State> = ArrayList()
    var countrylist: ArrayList<State> = ArrayList()
    var citylist: ArrayList<State> = ArrayList()
    var searchDialog: Dialog? = null
    private var etSearch: EditText? = null
    var searchRecyclerview: RecyclerView? = null
    var countryStateCity: CountryStateCity? = null
    var ivClearSearch: ImageView? = null
    var tvCancel: TextView? = null
    var newStateList = ArrayList<String>()
    private lateinit var countryAdapter: CountryAdapter
    lateinit var mAddressListViewModel: AddressListViewModel;

    var type: String = Const.COUNTRY
    var stateId: String = "001"
    var countryId: String = "101"
    var mAddressBook: AddressBook? = null
    var fromGoogleMaps = false
    var isEdit: String? = null

    var myNetworkCall: MyNetworkCall? = null
    var card_id = ""

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data

                if (fromGoogleMaps) {
                    binding.latlonglayout.visibility = View.GONE
                } else {
                    binding.latlonglayout.visibility = View.VISIBLE
                }
                // Handle the Intent

                var t = intent?.extras?.get("Address").toString()
                var addressBook = Gson().fromJson<AddressBook>(t, AddressBook::class.java)
                mAddressBook = addressBook
                initialize_value(mAddressBook)

            }
        }

    private fun initialize_value(ad: AddressBook?) {
        if (ad != null) {
            try {
                Log.e("TAG", "initialize_value: ${ad.country}")
              //  binding.countrybox.text = ad.country

                binding.countrybox.setText(ad.country)
                binding.citybox.setText(ad.city)
                binding.statebox.setText(ad.state)
                binding.pincode.setText(ad.pincode)
                binding.houseno.setText(ad.address + ad.address_2)
                binding.latbox.setText(ad.latitude)
                binding.longbox.setText(ad.longitude)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {

            Log.e("TAG", "NULL")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityAddressSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent != null) {
            mAddressBook = intent.getSerializableExtra("ADDRESS_DATA") as AddressBook?
            isEdit = intent.getStringExtra("is_edit")
            Log.e("onCreate: ", mAddressBook.toString())



        }

        card_id = SharedPreference.getInstance().getCardCons(Const.CARD, "")
        System.out.println("card_id----------hggg-----------"+card_id)
      /*  setSupportActionBar(binding.toolbar);
        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar?.setDisplayHomeAsUpEnabled(true)*/


        binding.toolbarAddress.toolbarTitleTV.text = "My Addresses"



        mAddressListViewModel = ViewModelProvider(this).get(AddressListViewModel::class.java)

        binding.apply {
            addressbutton.setOnClickListener(this@AddressSetting)
            addressfromgoogle.setOnClickListener(this@AddressSetting)
            toolbarAddress.toolbarBackIV.setOnClickListener(this@AddressSetting)

        }

      //  ccp = view.findViewById(android.R.id.fragment_signup_next_ccp) as CountryCodePicker
        binding.fragmentSignupNextCcp.selectedCountryCodeWithPlus
        binding.fullname.setText(mAddressBook?.name)
        binding.phonenumber.setText(mAddressBook?.phone)
        binding.pincode.setText(mAddressBook?.pincode)
        binding.houseno.setText(mAddressBook?.address)
        binding.citybox.setText(mAddressBook?.city)
        binding.statebox.setText(mAddressBook?.state)
     //   binding.countrybox.text = mAddressBook?.country
        binding.countrybox.setText(mAddressBook?.country)
        binding.latbox.setText(mAddressBook?.latitude) 
        binding.longbox.setText(mAddressBook?.longitude)

        myNetworkCall = MyNetworkCall(this, this)
        binding.countrybox.setOnClickListener {
           // myNetworkCall!!.NetworkAPICallWithoutDialog(API.API_GET_COUNTRIES, true);
            //for making click action false
        }

        myNetworkCall!!.NetworkAPICallWithoutDialog(API.API_GET_STATES, true);

        myNetworkCall!!.NetworkAPICallWithoutDialog(API.API_GET_CITIES, true);
        setUpEditTextObservable()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.addressbutton -> {

                if (mAddressBook == null) {
                    if (binding.fullname.text.toString().isEmpty()) {
                        Toast.makeText(this, " Please provide Your Name", Toast.LENGTH_SHORT).show()
                    } else if (binding.phonenumber.text.toString().isEmpty()) {
                        Toast.makeText(this, " Enter Your Number ", Toast.LENGTH_SHORT).show()
                    } else if (binding.houseno.text.toString().isEmpty()) {
                        Toast.makeText(this, " Enter Your House Address ", Toast.LENGTH_SHORT)
                                .show()
                    } else {
                        mAddressBook = AddressBook(
                                SharedPreference.getInstance().loggedInUser.id,
                                "",
                                binding.fullname.text.toString(),
                                binding.fragmentSignupNextCcp.selectedCountryCodeWithPlus,
                                binding.phonenumber.text.toString(),
                                binding.pincode.text.toString(),
                                binding.houseno.text.toString(),
                                "",
                                binding.citybox.text.toString(),
                                binding.statebox.text.toString(),
                                binding.countrybox.text.toString(),
                                binding.latbox.text.toString(),
                                binding.longbox.text.toString(),
                                "0",
                                "0")
                        returnAddress(mAddressBook)
                    }
                    //Toast.makeText(this, " You Havent Select Address", Toast.LENGTH_SHORT).show()
                } else {
                    if (binding.fullname.text.toString().isEmpty()) {
                        Toast.makeText(this, " Please provide Your Name", Toast.LENGTH_SHORT).show()
                    } else if (binding.phonenumber.text.toString().isEmpty()) {
                        Toast.makeText(this, " Enter Your Number ", Toast.LENGTH_SHORT).show()
                    } else if (binding.houseno.text.toString().isEmpty()) {
                        Toast.makeText(this, " Enter Your House Address ", Toast.LENGTH_SHORT)
                                .show()
                    } else {
                        mAddressBook?.name = binding.fullname.text.toString()
                        mAddressBook?.phone = binding.phonenumber.text.toString()
                        mAddressBook?.address = binding.houseno.text.toString()
                        returnAddress(mAddressBook)

                    }
                }
            }
            R.id.addressfromgoogle -> {

                fromGoogleMaps = true

                val i = Intent(this, GoogleMapAddress::class.java)
                i.putExtra("helloString", "helloString")
                Toast.makeText(this, "Pick Address", Toast.LENGTH_SHORT).show()
                startForResult.launch(i)

            }

            R.id.toolbarBackIV -> onBackPressed()
        }
    }

    fun returnAddress(mAddress: AddressBook?) {

        /*  val returnIntent = Intent()
          returnIntent.putExtra("result", "result")
          returnIntent.putExtra("Address", Gson().toJson(mAddress)  )
          setResult(Activity.RESULT_OK, returnIntent)
          finish()*/

        val isDefault = if (binding.checkedTextView.isChecked){
            "1"
        }else{
            "0"
        }
        mAddress?.is_default = isDefault
        binding.progressBar.visibility = View.VISIBLE
        if (isEdit == "1") {
            mAddressListViewModel.editAddress(this@AddressSetting, this@AddressSetting, mAddress)
        } else {
            mAddressListViewModel.insertAddress(this@AddressSetting, this@AddressSetting, mAddress)
        }

    }

    override fun update(b: Boolean) {
        binding.progressBar.visibility = View.GONE
        if (card_id.equals("card")){
            finish()

        }else{
            val i = Intent(this, AddressList::class.java)
            startActivity(i)
            finish()
        }
    }


    /*
    Used This call back as used in registration Screen
     */

    override fun getAPI(apiType: String?, service: FlashCardApiInterface?): Call<JsonObject> {
        val params: MutableMap<String, Any> = HashMap<String, Any>()
        when (apiType) {
            API.API_GET_STATES, API.API_GET_COUNTRIES -> return service!![apiType]
            API.API_GET_CITIES -> params[Const.STATE] = stateId
        }
        return service!!.postData(apiType, params)
    }

    override fun successCallBack(jsonObject: JSONObject, apiType: String?) {
        var gson: Gson = Gson();
        var data: JSONObject;
        if (jsonObject?.optBoolean(Const.STATUS)) {
            Helper.showErrorLayoutForNoNav(apiType, this, 0, 0);
            when (apiType) {

                API.API_GET_COUNTRIES -> {

                    var jsonArray: JSONArray = GenericUtils.getJsonArray(jsonObject);
                    Log.d("AddressSetting", " Countries " + jsonArray)

                    for (i in 0 until jsonArray.length()) {
                        countrylist.add(
                                gson.fromJson(
                                        jsonArray.optJSONObject(i).toString(),
                                        State::class.java
                                )
                        )
                    }

                    update_country_state_city(countrylist, API.API_GET_COUNTRIES)

                }

                API.API_GET_STATES -> {

                    var jsonArray: JSONArray = GenericUtils.getJsonArray(jsonObject);
                    Log.d("AddressSetting", " States " + jsonArray)

                    for (i in 0 until jsonArray.length()) {
                        statelist.add(
                                gson.fromJson(
                                        jsonArray.optJSONObject(i).toString(),
                                        State::class.java
                                )
                        )
                    }
                    update_country_state_city(statelist, API.API_GET_STATES)

                }


                API.API_GET_CITIES -> {

                    var jsonArray: JSONArray = GenericUtils.getJsonArray(jsonObject);
                    Log.d("AddressSetting", " Cities " + jsonArray)

                    for (i in 0 until jsonArray.length()) {
                        citylist.add(
                                gson.fromJson(
                                        jsonArray.optJSONObject(i).toString(),
                                        State::class.java
                                )
                        )
                    }
                    update_country_state_city(citylist, API.API_GET_CITIES)
                }
            }
        }
    }

    override fun errorCallBack(jsonString: String?, apiType: String?) {
        Toast.makeText(this, "Error : " + jsonString, Toast.LENGTH_SHORT).show()
    }


    fun update_country_state_city(mList: List<State>, type: String) {

        var mListnew: List<String> = getStringList(mList, type)
        val adap: ArrayAdapter<String> = ArrayAdapter<String>(
                this, R.layout.single_row_spinner_item, mListnew
        )

        when (type) {

            API.API_GET_COUNTRIES -> {
                filterList(mListnew)
                // binding.country.adapter = adap
            }

            API.API_GET_STATES -> {

                binding.state.adapter = adap
            }
            API.API_GET_CITIES -> {

                binding.city.adapter = adap
            }

        }
    }

    private fun getStringList(list: List<State>, type: String): List<String> {
        val stringlist = ArrayList<String>()
        stringlist.add("Select")
        when (type) {
            API.API_GET_COUNTRIES -> {
                for (temp in list) {
                    stringlist.add(temp.name)
                }
            }
            API.API_GET_STATES -> {
                for (temp in list) {
                    stringlist.add(temp.stateName)
                }
            }

            API.API_GET_CITIES -> {
                for (temp in list) {
                    stringlist.add(temp.cityName)
                }
            }
        }
        return stringlist
    }


    private fun filterList(
            stateArrayList: List<String>
    ) {
        searchDialog = Dialog(this)
        searchDialog?.getWindow()?.setBackgroundDrawableResource(R.color.transparent_background)
        searchDialog?.getWindow()?.requestFeature(Window.FEATURE_NO_TITLE)
        searchDialog?.setContentView(R.layout.country_state_city_dialog)
        searchDialog?.setCancelable(true)
        etSearch = searchDialog?.findViewById<EditText>(R.id.et_search)
        val tvTitle: TextView = searchDialog?.findViewById(R.id.tv_title)!!
        ivClearSearch = searchDialog?.findViewById<ImageView>(R.id.iv_clear_search)
        tvCancel = searchDialog?.findViewById<TextView>(R.id.tv_cancel)
        ivClearSearch?.setOnClickListener(View.OnClickListener { v: View? ->
            etSearch?.setText(
                    ""
            )
        })
        tvCancel?.setOnClickListener { searchDialog?.cancel() }
        searchRecyclerview = searchDialog?.findViewById<RecyclerView>(R.id.search_recyclerview)
        searchRecyclerview?.setHasFixedSize(true)
        searchRecyclerview?.layoutManager = LinearLayoutManager(this)
        countryAdapter = CountryAdapter(this, stateArrayList)
        searchRecyclerview?.adapter = countryAdapter
        textWatcher(stateArrayList)
        searchDialog?.show()
    }

    fun textWatcher(stateArrayList: List<String>) {
        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.isNotEmpty()) {
                    ivClearSearch?.setVisibility(View.VISIBLE)
                } else {
                    ivClearSearch?.setVisibility(View.GONE)
                }
                //after the change calling the method and passing the search input
                filter(editable.toString(), stateArrayList)
            }
        })
    }

    private fun filter(text: String, stateArrayList: List<String>) {
        newStateList.clear()
        for (state in stateArrayList) {
            if (state.toLowerCase().contains(text.toLowerCase())) {
                newStateList.add(state)
            }
        }
        if (newStateList.isNotEmpty()) {
            searchRecyclerview?.visibility = View.VISIBLE
            countryAdapter.filterList(newStateList)
        } else {
            searchRecyclerview?.setVisibility(View.INVISIBLE)
        }
    }

    public fun getCountryData(name: String) {
        Log.e("getCountryData: ", name)
        searchDialog?.dismiss()
      //  binding.countrybox.text = name;
        binding.countrybox.setText(name)
    }

    private fun getLatLangFromZip(zip: String) {
        val geocoder = Geocoder(this)
        try {
            val addresses: List<Address> = geocoder.getFromLocationName(zip, 1)
            if (addresses.isNotEmpty()) {
                Helper.closeKeyboard(this)
                binding.citybox.isEnabled = false
                binding.statebox.isEnabled = false
                binding.countrybox.isEnabled = false
                // Use the address as needed
                /*val message = java.lang.String.format(
                    "Latitude: %f, Longitude: %f",
                    addresses[0].latitude, addresses[0].longitude
                )*/

                val address = addresses[0].getAddressLine(0)
                val state = addresses[0].adminArea
                //val city = addresses[0].locality
                var city = addresses[0].locality
                if (city == null) city = addresses[0].subLocality
                if (city == null) city = addresses[0].subAdminArea
                if (city == null) city = addresses[0].adminArea
                val country = addresses[0].countryName
                val pin = addresses[0].postalCode
                var addressString: String =
                    "$address,$city,$state,$country"
                Log.e("TAG", "getLatLangFromZip: $addressString")
                // https://maps.googleapis.com/maps/api/geocode/json?latlng=28.56,77.24&sensor=true
                val mobile =
                    if (SharedPreference.getInstance().loggedInUser.mobile != null) SharedPreference.getInstance().loggedInUser.mobile else "No mobile"
                val addressBook = AddressBook(
                        SharedPreference.getInstance().loggedInUser.id,
                        "",
                        SharedPreference.getInstance().loggedInUser.name,
                        "",
                         mobile,
                        addresses[0].postalCode,
                        "",
                        "",
                        city,
                        addresses[0].adminArea,
                        addresses[0].countryName,
                        addresses[0].latitude.toString(),
                        addresses[0].longitude.toString(),
                        "0",
                        "0"
                )
                initialize_value(addressBook)
                //Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else {
                binding.citybox.isEnabled = true
                binding.statebox.isEnabled = true
                binding.countrybox.isEnabled = true
                // Display appropriate message when Geocoder services are not available
                show("Unable to geocode zipcode", Toast.LENGTH_LONG)
            }
        } catch (e: IOException) {
            // handle exception
            e.printStackTrace()
        }
    }

    private fun setUpEditTextObservable() {
        binding.searchPin.setOnClickListener {
            getLatLangFromZip(binding.pincode.text.toString().trim())
        }
    }

   /* private fun setUpEditTextObservable() {

        binding.pincode.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                if(s.length==6) {
                    getLatLangFromZip(binding.pincode.text.toString().trim())
                    Toast.makeText(this@AddressSetting, "after text", Toast.LENGTH_SHORT).show()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun getPinCode(value:String) {
        try {
            getLatLangFromZip(value)
            Toast.makeText(this@AddressSetting, "Pin should be", Toast.LENGTH_SHORT).show()
        }catch (e:Exception)
        {

        }

    }*/


}