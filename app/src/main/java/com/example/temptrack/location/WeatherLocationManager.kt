package com.example.temptrack.location

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WeatherLocationManager private constructor(private var application: Application) :
    WeatherLocationManagerInterface {

    private val _location = MutableStateFlow<LocationStatus>(LocationStatus.Loading)
    override val location = _location.asStateFlow()

    private val mFusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application)
    }

    @SuppressLint("MissingPermission")
    override fun requestLocationByGPS() {
        val tokenSource = CancellationTokenSource()
        val token = tokenSource.token
        mFusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,token).addOnSuccessListener {
            val isEmitted =
                _location.tryEmit(LocationStatus.Success(LatLng(it.latitude, it.longitude)))
            Log.d("TAG", "onLocationResult: $isEmitted")
        }
    }
    @SuppressLint("MissingPermission")
    override fun requestLocationByGPS(callback: (LatLng) -> Unit) {
        val tokenSource = CancellationTokenSource()
        val token = tokenSource.token
        mFusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,token).addOnSuccessListener {
            callback(LatLng(it.latitude, it.longitude))
        }
    }

    override fun requestLocationSavedFromMap() {

    }

    override fun removeLocationUpdate(locationCallback: LocationCallback) {
        mFusedLocationProviderClient.removeLocationUpdates(
            locationCallback
        )
    }


    override fun isLocationEnabled(): Boolean {
        val weatherLocationManager: LocationManager =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return weatherLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    companion object {
        private lateinit var instance: WeatherLocationManager
        fun getInstance(application: Application): WeatherLocationManager {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = WeatherLocationManager(application)
                }
                return instance
            }
        }
    }
}