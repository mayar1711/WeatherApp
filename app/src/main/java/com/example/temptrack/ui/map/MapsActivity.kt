package com.example.temptrack.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.temptrack.R
import com.example.temptrack.location.LocationStatus
import com.example.temptrack.location.WeatherLocationManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, CoroutineScope by MainScope() {

    private lateinit var map: GoogleMap
    private var marker: Marker? = null
    private lateinit var _latLng: LatLng

    private val viewModel by viewModels<MapsViewModel> {
        MapsViewModelFactory(
            WeatherLocationManager.getInstance(applicationContext as Application)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (isPermissionGranted()) {
            enableMyLocation()
            viewModel.requestLocationByGPS()
            moveCameraToMyLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        setMapLongClick(googleMap)
        setPoiClick(googleMap)
        setMapCameraChanged(googleMap)
    }
    private fun moveCameraToMyLocation() {
        launch {
            viewModel.location.collect { locationStatus ->
                if (locationStatus is LocationStatus.Success) {
                    val currentLatLng = LatLng(locationStatus.latLng.latitude, locationStatus.latLng.longitude)
                    Log.i("MapsActivity", "Current Location: $currentLatLng")
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentLatLng, 15.0f
                        )
                    )
                }
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return !(ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        }
    }


    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapClickListener { latLng ->
            marker?.remove()
            val snippet = String.format(
                Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f", latLng.latitude, latLng.longitude
            )
            val markerOptions = MarkerOptions().position(latLng).snippet(snippet)
            map.moveCamera(
                CameraUpdateFactory.newLatLng(
                    latLng
                )
            )
            _latLng = latLng
            marker = map.addMarker(markerOptions)
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            marker?.remove()
            marker = map.addMarker(
                MarkerOptions().position(poi.latLng).title(poi.name)
            )
            _latLng = poi.latLng
            marker?.showInfoWindow()
        }
    }

    private fun setMapCameraChanged(googleMap: GoogleMap) {
        googleMap.setOnCameraMoveListener {
            googleMap.clear()
            _latLng = googleMap.cameraPosition.target
            marker = googleMap.addMarker(MarkerOptions().position(_latLng))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
    // Constants
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
                viewModel.requestLocationByGPS()
                moveCameraToMyLocation()
            } else {
                Log.e("MapsActivity", "Location permission denied")
            }
        }
    }
}
