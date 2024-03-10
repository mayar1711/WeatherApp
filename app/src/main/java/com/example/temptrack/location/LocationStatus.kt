package com.example.temptrack.location

import com.google.android.gms.maps.model.LatLng

sealed class LocationStatus {
    class Success(var latLng: LatLng) : LocationStatus()
    class Failure(var throwable: String) : LocationStatus()

    object Loading : LocationStatus()
}
