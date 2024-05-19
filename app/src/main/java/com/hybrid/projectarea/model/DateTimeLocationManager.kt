package com.hybrid.projectarea.model

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeLocationManager {
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun getCurrentDateTime(): String {
        val date = Date()
        return dateTimeFormat.format(date)
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, callback: (Location?) -> Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = locationManager.getProviders(true)

        for (provider in providers) {
            locationManager.getLastKnownLocation(provider)?.let {
                callback(it)
                return
            }
        }
        callback(null)
    }
}
