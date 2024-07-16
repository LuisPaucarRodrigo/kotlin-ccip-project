package com.hybrid.projectarea.model

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeLocationManager {
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private var locationManager: LocationManager? = null
    private var locationCallback: ((Location?) -> Unit)? = null

    fun getCurrentDateTime(): String {
        val date = Date()
        return dateTimeFormat.format(date)
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, callback: (Location?) -> Unit) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationCallback = callback
        val providers = locationManager!!.getProviders(true)

        for (provider in providers) {
            locationManager!!.requestLocationUpdates(provider, 10000L, 10f, locationListener)
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationCallback?.invoke(location)
            locationManager?.removeUpdates(this)
        }

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }

}
