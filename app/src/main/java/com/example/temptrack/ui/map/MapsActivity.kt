package com.example.temptrack.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.temptrack.MainActivity
import com.example.temptrack.R
import com.example.temptrack.data.database.DatabaseClient
import com.example.temptrack.data.database.FavoriteLocalDataSourceImo
import com.example.temptrack.data.model.Current
import com.example.temptrack.data.model.TempData
import com.example.temptrack.data.network.ApiWeatherData
import com.example.temptrack.data.network.RetrofitClient
import com.example.temptrack.data.network.datasource.WeatherRemoteDataSourceImpl
import com.example.temptrack.data.repositry.WeatherRepositoryImpl
import com.example.temptrack.databinding.ActivityMapsBinding
import com.example.temptrack.datastore.SettingDataStorePreferences
import com.example.temptrack.location.LocationStatus
import com.example.temptrack.location.WeatherLocationManager
import com.example.temptrack.util.getAddress
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
    private var isLocationReceived:Boolean=false
    private lateinit var binding:ActivityMapsBinding
    private var latitude:Double = 0.0
    private var longitude:Double = 0.0
    private lateinit var  viewModel:MapsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragmentName = intent?.extras?.getString("fragment_name")
        Log.i("MAP", "onCreate: $fragmentName")


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        val repository = WeatherRepositoryImpl.getInstance(
            WeatherRemoteDataSourceImpl.getInstance(RetrofitClient.weatherApiService),
            FavoriteLocalDataSourceImo.getInstance(DatabaseClient.getInstance(this).favoriteDao()))
        val factory = MapsViewModelFactory( WeatherLocationManager.getInstance(applicationContext as Application),repository)
         viewModel=ViewModelProvider(this, factory)[MapsViewModel::class.java]
         val city= getAddress(context = applicationContext,latitude,longitude)
        binding.add.setOnClickListener {
            if (fragmentName == "HomeFragment") {

                val currentLatitude = latitude
                val currentLongitude = longitude
                saveLocationToDataStore(currentLatitude, currentLongitude)
                Log.i("MapsActivity", "Launched from HomeFragment")
                Log.i("MapsActivity", "onCreate: $currentLatitude , $currentLongitude")
                startActivity(Intent(this, MainActivity::class.java))
            } else if (fragmentName == "FavoriteFragment") {
                Log.i("MapsActivity", "Launched from FavoriteFragment")
                viewModel.fetchWeatherForecast(latitude = latitude, longitude = longitude, language = "en", unit = "metric")
                lifecycleScope.launch {
                    viewModel.weatherForecast.collect{
                            result->
                        when (result) {
                            is ApiWeatherData.Success -> {
                                val result=result.forecast
                                val tempData = TempData(
                                    minTemp = result.daily.get(0).temp.min,
                                    maxTemp = result.daily.get(0).temp.max,
                                    temp = result.current.temp,
                                    city =city,
                                    icon = result.current.weather.get(0).icon,
                                    lang = longitude,
                                    lat = latitude
                                )
                                viewModel.insertFavorite(tempData)
                                finish()
                            }

                            is ApiWeatherData.Error -> {
                                val errorMessage = result.message
                                Log.i("HomeFragment", "Error fetching weather forecast: $errorMessage")
                            }

                            is ApiWeatherData.Loading -> {
                                // Show loading indicator
                            }

                        }
                    }
                }
            }

      }
    }

    private fun saveLocationToDataStore(latitude: Double, longitude: Double) {
        val settingPreferences = SettingDataStorePreferences.getInstance(applicationContext)
       launch {
            settingPreferences.setLatitude(latitude)
            settingPreferences.setLongitude(longitude)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (isPermissionGranted()) {
            enableMyLocation()
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
        setMapClick(googleMap)
    }

    private fun setMapClick(map: GoogleMap) {
        map.setOnMapClickListener { latLng ->
            moveCameraToLocation(latLng)
        }
    }

    private fun moveCameraToLocation(latLng: LatLng) {
        marker?.remove()
        val snippet = String.format(
            Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f", latLng.latitude, latLng.longitude
        )
        val markerOptions = MarkerOptions().position(latLng).snippet(snippet)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f))
        _latLng = latLng
        latitude = latLng.latitude
        longitude = latLng.longitude
        marker = map.addMarker(markerOptions)
    }

    private fun isPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        map.isMyLocationEnabled = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Log.e("MapsActivity", "Location permission denied")
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}