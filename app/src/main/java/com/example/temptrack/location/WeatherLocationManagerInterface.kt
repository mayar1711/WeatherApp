package com.example.temptrack.location

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow

interface WeatherLocationManagerInterface {
    val location: StateFlow<LocationStatus>

    @SuppressLint("MissingPermission")
    fun requestLocationByGPS()
    fun isLocationEnabled(): Boolean
    fun requestLocationSavedFromMap()
    fun requestLocationByGPS(callback: (LatLng) -> Unit)
    fun removeLocationUpdate(locationCallback: LocationCallback)
}