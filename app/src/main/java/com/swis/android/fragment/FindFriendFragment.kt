package com.swis.android.fragment


import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.swis.android.ApiService.ApiClient
import com.swis.android.ApiService.ApiInterface

import com.swis.android.R
import com.swis.android.activity.HomeActivity
import com.swis.android.activity.InviteFriendActivity
import com.swis.android.base.BaseFragment
import com.swis.android.databinding.FragmentFindFriendBinding
import com.swis.android.model.requestmodel.LocationUpdateRequestModel
import com.swis.android.model.responsemodel.UserRegisterResponseModel
import com.swis.android.util.AppConstants
import com.swis.android.util.PermissionUtil
import com.swis.android.util.PrefsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Double
import java.util.*

class FindFriendFragment : BaseFragment() {

    var binding: FragmentFindFriendBinding? = null
    private var isLocationUpdated = false
    private val REQUEST_CHECK_SETTINGS = 102
    private var mLocationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private val UPDATE_INTERVAL = (10 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000

    override fun getLayoutId(): Int {

        return R.layout.fragment_find_friend
    }

    override fun onViewBinded(views: ViewDataBinding?) {
        binding= views as FragmentFindFriendBinding?

        binding!!.btnFind.setOnClickListener {

            InviteFriendActivity.start(mContext,Bundle()) }

        binding!!.btnSkip.setOnClickListener {
            var bundle=Bundle()
            bundle.putBoolean(AppConstants.IS_SIGN_UP,true)
            HomeActivity.start(mContext,bundle) }

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    // Update UI with location data
                    // ...
                    val msg = "Updated Location: " +
                            java.lang.Double.toString(location.latitude) + "," +
                            java.lang.Double.toString(location.longitude)
                    if(!isLocationUpdated || TextUtils.isEmpty(prefHelper.userInfo.city))
                    {
                        val geocoder: Geocoder
                        val addresses: List<Address>
                        geocoder = Geocoder(mContext, Locale.getDefault())

                        addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        var location= LocationUpdateRequestModel()
                        // val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        location.latitude=location.latitude
                        location.longitude = location.longitude
                        location.address = addresses.get(0).getAddressLine(0)
                        updateLocation(location)
                        isLocationUpdated=true

                    }

                }
            }
        }
        if(PermissionUtil.checkLocationPermission(mContext))
        {
            getLastLocation()
            startLocationUpdates()
        }
        else
        {
            PermissionUtil.requestLocationPermission(mContext)
        }
    }


    private fun updateLocation(location: LocationUpdateRequestModel) {

        var service = ApiClient.getClient().create(ApiInterface::class.java).updateLocation(location)
        service.enqueue(object : Callback<UserRegisterResponseModel> {


            override fun onResponse(call: Call<UserRegisterResponseModel>, response: Response<UserRegisterResponseModel>) {
                if(response!=null && response.body()!=null)
                {
                    if(response.body()!!.responseCode.equals("200",true))
                    {
                        prefHelper.saveUserInfo(response.body()!!.user)
                        isLocationUpdated=true
                    }
                    else
                        showToast(response.body()!!.responseMessage)
                }
                else
                {
                    showToast(AppConstants.SOMETHING_WENT_WRONG)
                }

            }

            override fun onFailure(call: Call<UserRegisterResponseModel>, t: Throwable) {

                showToast(AppConstants.SERVER_ERROR)

            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {


            PermissionUtil.LOCATION_REQUEST_CODE->if(PermissionUtil.checkLocationPermission(mContext))
            {
                getLastLocation()
                startLocationUpdates()
            }
        }
    }

    private fun startLocationUpdates() {

        mLocationRequest = LocationRequest()
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.interval = UPDATE_INTERVAL
        mLocationRequest!!.fastestInterval = FASTEST_INTERVAL

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(mContext)


        val task = settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener { getLocationUpdates() }


        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(activity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }

            }
        }
    }

    private fun getLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
            return
        }
        LocationServices.getFusedLocationProviderClient(mContext).requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    private fun getLastLocation() {

        val locationClient = LocationServices.getFusedLocationProviderClient(mContext)

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {


        }
        locationClient.getLastLocation()
                .addOnSuccessListener(OnSuccessListener<Location> { location ->
                    // GPS location can be null if GPS is switched off
                    if (location != null) {
                        val msg = "Updated Location: " +
                                Double.toString(location.latitude) + "," +
                                Double.toString(location.longitude)

                    }
                })
                .addOnFailureListener(OnFailureListener { e ->
                    Log.d("MapDemoActivity", "Error trying to get last GPS location")
                    e.printStackTrace()
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        LocationServices.getFusedLocationProviderClient(mContext).removeLocationUpdates(mLocationCallback)

    }
}
