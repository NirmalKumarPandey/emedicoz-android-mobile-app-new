package com.emedicoz.app.cart

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.emedicoz.app.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.emedicoz.app.databinding.ActivityGoogleMapAddressBinding
import com.emedicoz.app.network.model.AddressBook
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.utilso.SharedPreference
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.android.gms.location.LocationCallback

import com.google.android.gms.location.LocationRequest
import com.emedicoz.app.utilso.GpsUtils
import com.emedicoz.app.utilso.GpsUtils.onGpsListener
import com.google.android.gms.location.LocationResult
import java.util.*


class GoogleMapAddress : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private lateinit var mMap: GoogleMap
    var mAddressBook: AddressBook? = null
    private lateinit var binding: ActivityGoogleMapAddressBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    lateinit var marker: MarkerOptions
    var mylatitute: Double = 0.0
    var mylongitude: Double = 0.0
    var address: String? = null
    var state: String? = null
    var city: String? = null
    var country: String? = null
    var houseno: String? = null
    var pin: String? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private val isContinue = false
    private var isGPS = false
    var geocoder: Geocoder? = null
    var addresses: List<Address>? = null

    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
        private const val M_MAX_ENTRIES = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityGoogleMapAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            changelocation.setOnClickListener(this@GoogleMapAddress)
            addressbutton.setOnClickListener(this@GoogleMapAddress)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationRequest = LocationRequest.create()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = (10 * 1000).toLong() // 10 seconds

        locationRequest?.fastestInterval = (5 * 1000).toLong() // 5 seconds


        GpsUtils(this).turnGPSOn(object : onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable
            }
        })

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        mylatitute = location.latitude
                        mylongitude = location.longitude

                        mMap?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    mylatitute,
                                    mylongitude
                                ), DEFAULT_ZOOM.toFloat()
                            )
                        )


                        var mGeoCoder: Geocoder = Geocoder(this@GoogleMapAddress)
                        var mAddress: List<Address> = mGeoCoder.getFromLocation(
                            mylatitute,
                            mylongitude,
                            1
                        )
                        address = mAddress[0].getAddressLine(0)
                        state = mAddress[0].adminArea
                        city = mAddress[0].locality
                        country = mAddress[0].countryName
                        pin = mAddress[0].postalCode
                        var addressString: String =
                            "" + address /*+ "," + city + "," + state + "," + country*/
                        // https://maps.googleapis.com/maps/api/geocode/json?latlng=28.56,77.24&sensor=true
                        val mobile =
                            if (SharedPreference.getInstance().loggedInUser.mobile != null) SharedPreference.getInstance().loggedInUser.mobile else "No mobile"
                        mAddressBook = AddressBook(
                            SharedPreference.getInstance().loggedInUser.id,
                            "",
                            SharedPreference.getInstance().loggedInUser.name,
                                "",
                            mobile,
                            mAddress[0].postalCode,
                            mAddress[0].getAddressLine(0),
                            "",
                            mAddress[0].locality,
                            mAddress[0].adminArea,
                            mAddress[0].countryName,
                            mylatitute.toString(),
                            mylongitude.toString(),
                            "0",
                            "0"
                        )


                        updateLocationUI(addressString)

                        if (!isContinue && fusedLocationClient != null) {
                            fusedLocationClient.removeLocationUpdates(locationCallback)
                        }
                    }
                }
            }
        }


    }

    fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Toast.makeText(this, "No Location Permission Granted", Toast.LENGTH_SHORT).show()
            getLocationPermission()
            return
        }

        try {
            if (isContinue) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        lastKnownLocation = location

                        if (lastKnownLocation != null) {
                            mMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )

                            mylatitute = lastKnownLocation!!.latitude
                            mylongitude = lastKnownLocation!!.latitude


                            var mGeoCoder: Geocoder = Geocoder(this)
                            var mAddress: List<Address> = mGeoCoder.getFromLocation(
                                lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude,
                                1
                            )
                            address = mAddress[0].getAddressLine(0)
                            state = mAddress[0].adminArea
                            city = mAddress[0].locality
                            country = mAddress[0].countryName
                            pin = mAddress[0].postalCode
                            var addressString: String =
                                "" + address
                            // https://maps.googleapis.com/maps/api/geocode/json?latlng=28.56,77.24&sensor=true
                            val mobile =
                                if (SharedPreference.getInstance().loggedInUser.mobile != null) SharedPreference.getInstance().loggedInUser.mobile else "No mobile"
                            mAddressBook = AddressBook(
                                SharedPreference.getInstance().loggedInUser.id,
                                "",
                                SharedPreference.getInstance().loggedInUser.name,
                                    "",
                                mobile,
                                mAddress[0].postalCode,
                                mAddress[0].getAddressLine(0),
                                "",
                                mAddress[0].locality,
                                mAddress[0].adminArea,
                                mAddress[0].countryName,
                                mylatitute.toString(),
                                mylongitude.toString(),
                                "0",
                                "0"
                            )

                            updateLocationUI(addressString)
                        } else {
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                null
                            );
                        }
                    }
            }

        } catch (e: Exception) {

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        if (!isGPS) {
            Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
            return;
        }
        getLastLocation();



        mMap.setOnMapClickListener {
            mMap.clear();
            mylatitute = it.latitude
            mylongitude = it.longitude

            val myadd = LatLng(it.latitude, it.longitude)
            val latitue = myadd.latitude
            var longtiue = myadd.longitude

            marker = MarkerOptions().position(myadd).title("Set This As My Address")

            mMap.moveCamera(CameraUpdateFactory.newLatLng(myadd))
            geocoder = Geocoder(this, Locale.getDefault())
            addresses = geocoder!!.getFromLocation(latitue, longtiue, 1)
            val address = (addresses as MutableList<Address>?)!!.get(0).getAddressLine(0)

            binding.fetchedaddress.text = Editable.Factory.getInstance().newEditable(address)
            mMap.addMarker(marker)

        }

    }


    private fun getLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {


        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (!isGPS) {
                        Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    getLastLocation()
                }
            }
        }

    }


    private fun updateLocationUI(s: String?) {
        if (mMap == null) {
            return
        }
        try {

            mMap?.isMyLocationEnabled = true
            mMap?.uiSettings?.isMyLocationButtonEnabled = true

            binding.fetchedaddress.setText(s)

        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.changelocation -> {

                var mGeoCoder: Geocoder = Geocoder(this)
                var mAddress: List<Address> = mGeoCoder.getFromLocation(mylatitute, mylongitude, 1)
                address = mAddress[0].getAddressLine(0)
                state = mAddress[0].adminArea
                city = mAddress[0].locality
                country = mAddress[0].countryName

                binding.fetchedaddress.setText(address)

                mAddressBook?.address = mAddress[0].getAddressLine(0)
                var city = mAddress[0].locality
                if (city == null) city = mAddress[0].subLocality
                if (city == null) city = mAddress[0].subAdminArea
                if (city == null) city = mAddress[0].adminArea
                mAddressBook?.city = city
                mAddressBook?.state = mAddress[0].adminArea
                mAddressBook?.country = mAddress[0].countryName

                if (mAddress[0].postalCode != null)
                    mAddressBook?.pincode = mAddress[0].postalCode
                else
                    mAddressBook?.pincode = ""
                mAddressBook?.latitude = mylatitute.toString()
                mAddressBook?.longitude = mylongitude.toString()


            }
            R.id.addressbutton -> {
                if (mAddressBook == null) {
                    Toast.makeText(
                        this,
                        " You Havent Input Address Completely ",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    if (!binding.housenumber.text.toString().isEmpty()) {
                        mAddressBook?.address_2 = binding.housenumber.text.toString()
                    }

                    if (!binding.fetchedaddress.text.toString().isEmpty()) {
                        mAddressBook?.address = binding.fetchedaddress.text.toString()
                    }

                    returnAddress(mAddressBook)
                }

            }
        }
    }

    fun returnAddress(mAddress: AddressBook?) {

        val returnIntent = Intent()
        returnIntent.putExtra("result", "result")
        returnIntent.putExtra("Address", Gson().toJson(mAddress))


        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 1001) {
                isGPS = true // flag maintain before get location
                getLastLocation()
            }
        }
    }

}