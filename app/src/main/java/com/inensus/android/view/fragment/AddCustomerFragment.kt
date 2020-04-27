package com.inensus.android.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.inensus.android.R
import com.inensus.android.model.*
import com.inensus.android.util.SharedPreferencesWrapper
import com.inensus.android.util.Utils
import com.inensus.android.util.onChange
import com.inensus.android.util.onTouch
import com.inensus.android.view.adapter.SpinnerAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_add_customer.*


/**
 * A simple [Fragment] subclass.
 * Use the [AddCustomerFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AddCustomerFragment : BaseFragment(), com.google.android.gms.location.LocationListener {

    private var geoPoints = ""

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocation: Location? = null
    private var mLocationRequest: LocationRequest? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_customer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name.onChange { name.error = null }
        surname.onChange { surname.error = null }
        serialNumber.onChange { serialNumber.error = null }

        spManufacturer.onTouch()
        spMeter.onTouch()
        spTariff.onTouch()
        spCity.onTouch()

        if (isAdded && SharedPreferencesWrapper.getInstance().networkState == 1) {
            getManufacturers()
            getMeterTypes()
            getTariffs()
            getCities()
        } else {
            invalidateManufacturerSpinner()
            invalidateMeterTypeSpinner()
            invalidateTariffSpinner()
            invalidateCitySpinner()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buildGoogleApiClient()
    }

    @SuppressLint("CheckResult")
    fun addCustomer() {
        if (!checkGPSEnabled()) {
            return
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                checkLocationPermission()
            }
        } else {
            getLocation()
        }

        if (isFormValid()) {
            mCustomerViewModel.addCustomerToDb(Customer(
                    name = name.text.toString(),
                    surname = surname.text.toString(),
                    phone = phoneNumber.text.toString(),
                    serialNumber = serialNumber.text.toString(),
                    manufacturer = (spManufacturer.selectedItem as Manufacturer).id,
                    meterType = (spMeter.selectedItem as MeterType).id,
                    tariff = (spTariff.selectedItem as Tariff).id,
                    city = (spCity.selectedItem as City).id,
                    geoPoints = geoPoints,
                    isLocal = true))

            resetForm()

            Toast.makeText(activity, "Data has been added into the Local Storage", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("CheckResult")
    fun getManufacturers() {
        mCustomerViewModel.getManufacturers()!!
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showDialog() }
                .subscribe(
                        {
                            hideDialog()
                            if (it.isNotEmpty()) {
                                SharedPreferencesWrapper.getInstance().manufacturers = Gson().toJson(it)
                                invalidateManufacturerSpinner(it)
                            } else {
                                invalidateManufacturerSpinner()
                            }
                        },
                        { error ->
                            showErrorMessage(error)
                            hideDialog()
                            invalidateManufacturerSpinner()
                        }
                )
    }

    @SuppressLint("CheckResult")
    fun getMeterTypes() {
        mCustomerViewModel.getMeterTypes()!!
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showDialog() }
                .subscribe(
                        {
                            hideDialog()
                            if (it.isNotEmpty()) {
                                SharedPreferencesWrapper.getInstance().meterTypes = Gson().toJson(it)
                                invalidateMeterTypeSpinner(it)
                            } else {
                                invalidateMeterTypeSpinner()
                            }
                        },
                        { error ->
                            showErrorMessage(error)
                            hideDialog()
                            invalidateMeterTypeSpinner()
                        }
                )
    }

    @SuppressLint("CheckResult")
    fun getTariffs() {
        mCustomerViewModel.getTariffs()!!
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showDialog() }
                .subscribe(
                        {
                            hideDialog()
                            if (it.isNotEmpty()) {
                                SharedPreferencesWrapper.getInstance().tariffs = Gson().toJson(it)
                                invalidateTariffSpinner(it)
                            } else {
                                invalidateTariffSpinner()
                            }
                        },
                        { error ->
                            showErrorMessage(error)
                            hideDialog()
                            invalidateTariffSpinner()
                        }
                )
    }

    @SuppressLint("CheckResult")
    fun getCities() {
        mCustomerViewModel.getCities()!!
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showDialog() }
                .subscribe(
                        {
                            hideDialog()
                            if (it.isNotEmpty()) {
                                SharedPreferencesWrapper.getInstance().cities = Gson().toJson(it)
                                invalidateCitySpinner(it)
                            } else {
                                invalidateCitySpinner()
                            }
                        },
                        { error ->
                            showErrorMessage(error)
                            hideDialog()
                            invalidateCitySpinner()
                        }
                )
    }

    private fun invalidateManufacturerSpinner(manufacturers: List<Manufacturer> = listOf()) {
        if (isAdded) {
            val mutableManufacturers: MutableList<Manufacturer> = if (manufacturers.isEmpty()) {
                SharedPreferencesWrapper.getInstance().getManufacturerList()
            } else {
                manufacturers.toMutableList()
            }

            mutableManufacturers.add(0, Manufacturer(name = "Manufacturer"))

            val adapter = SpinnerAdapter(context = activity as Context, values = mutableManufacturers)
            spManufacturer.adapter = adapter
        }
    }

    private fun invalidateMeterTypeSpinner(meterTypes: List<MeterType> = listOf()) {
        if (isAdded) {
            val mutableMeterTypes: MutableList<MeterType> = if (meterTypes.isEmpty()) {
                SharedPreferencesWrapper.getInstance().getMeterTypeList()
            } else {
                meterTypes.toMutableList()
            }

            mutableMeterTypes.add(0, MeterType(id = -1))

            val adapter = SpinnerAdapter(context = activity as Context, values = mutableMeterTypes)
            spMeter.adapter = adapter
        }
    }

    private fun invalidateTariffSpinner(tariffs: List<Tariff> = listOf()) {
        if (isAdded) {
            val mutableTariffs: MutableList<Tariff> = if (tariffs.isEmpty()) {
                SharedPreferencesWrapper.getInstance().getTariffList()
            } else {
                tariffs.toMutableList()
            }

            mutableTariffs.add(0, Tariff(name = "Tariff"))

            val adapter = SpinnerAdapter(context = activity as Context, values = mutableTariffs)
            spTariff.adapter = adapter
        }
    }

    private fun invalidateCitySpinner(cities: List<City> = listOf()) {
        if (isAdded) {
            val mutableCities: MutableList<City> = if (cities.isEmpty()) {
                SharedPreferencesWrapper.getInstance().getCityList()
            } else {
                cities.toMutableList()
            }

            mutableCities.add(0, City(name = "City"))

            val adapter = SpinnerAdapter(context = activity as Context, values = mutableCities)
            spCity.adapter = adapter
        }
    }

    private fun showDialog() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideDialog() {
        progressBar.visibility = View.GONE
    }

    private fun isFormValid(): Boolean {
        var isValid = true

        if (name.text.isEmpty()) {
            name.error = "Name field is required"
            isValid = false
        }

        if (surname.text.isEmpty()) {
            surname.error = "Surname field is required"
            isValid = false
        }

        if (serialNumber.text.isEmpty()) {
            serialNumber.error = "Serial Number field is required"
            isValid = false
        }

        if (spManufacturer.selectedItemPosition == 0) {
            (spManufacturer.selectedView as TextView).error = "Manufacturer field is required"
            isValid = false
        }

        if (spManufacturer.selectedItemPosition == 0) {
            (spManufacturer.selectedView as TextView).error = "Meter Type field is required"
            isValid = false
        }

        if (spTariff.selectedItemPosition == 0) {
            (spTariff.selectedView as TextView).error = "Tariff field is required"
            isValid = false
        }

        if (spCity.selectedItemPosition == 0) {
            (spCity.selectedView as TextView).error = "City field is required"
            isValid = false
        }

        if (geoPoints.isEmpty()) {
            Toast.makeText(context!!, "Location not Detected", Toast.LENGTH_LONG).show()
            isValid = false
        }

        return isValid
    }

    private fun resetForm() {
        name.setText("")
        surname.setText("")
        phoneNumber.setText("")
        serialNumber.setText("")

        spManufacturer.setSelection(0)
        spManufacturer.setSelection(0)
        spTariff.setSelection(0)
        spCity.setSelection(0)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (mLocation == null) {
            startLocationUpdates()
        }
        if (mLocation != null) {
            geoPoints = mLocation!!.latitude.toString() + "," + mLocation!!.longitude.toString()
        } else {
            Toast.makeText(context!!, "Location not Detected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startLocationUpdates() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)

        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    override fun onLocationChanged(p0: Location?) {
        geoPoints = p0?.latitude.toString() + "," + p0?.longitude.toString()
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(context!!)
                .addApi(LocationServices.API)
                .build()

        mGoogleApiClient!!.connect()
    }

    private fun checkGPSEnabled(): Boolean {
        if (!isLocationEnabled())
            showAlert()
        return isLocationEnabled()
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(context!!)
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
                .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                    val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(myIntent)
                }
                .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> }
        dialog.show()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(context!!)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK") { _, _ ->
                            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE)
                        }
                        .create()
                        .show()

            } else ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context!!, "permission granted", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(context!!, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient?.connect()
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddCustomerFragment().apply {}

        private const val REQUEST_LOCATION_CODE = 101
        private const val UPDATE_INTERVAL = (2 * 1000).toLong()
        private const val FASTEST_INTERVAL: Long = 2000
    }
}
