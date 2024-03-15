package com.example.temptrack.ui.home.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.repositry.WeatherRepository
import com.example.temptrack.location.LocationStatus
import com.example.temptrack.location.WeatherLocationManager
import com.example.temptrack.location.WeatherLocationManagerInterface
import com.example.temptrack.util.ResultCallBack
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(application: Application, private val repository: WeatherRepository) : ViewModel() {
    private val _weatherForecast = MutableStateFlow<ResultCallBack<WeatherForecastResponse>>(ResultCallBack.Loading)
    val weatherForecast: StateFlow<ResultCallBack<WeatherForecastResponse>> = _weatherForecast

    private val locationManager: WeatherLocationManagerInterface = WeatherLocationManager.getInstance(application)
    private val _location = MutableStateFlow<LocationStatus>(LocationStatus.Loading)
    val location: StateFlow<LocationStatus> = _location

    private val _latitude = MutableStateFlow(0.0)
    val latitude: StateFlow<Double> = _latitude

    private val _longitude = MutableStateFlow(0.0)
    val longitude: StateFlow<Double> = _longitude

    fun fetchWeatherForecast(latitude: Double, longitude: Double, unit: String, language: String) {
        viewModelScope.launch {
            repository.getWeatherForecast(latitude, longitude, unit, language)
                .catch { e ->
                    _weatherForecast.value = ResultCallBack.Error(e)
                }
                .collect { result ->
                    _weatherForecast.value = ResultCallBack.Success(result)
                }
        }
    }

    fun requestGPSLocation() {
        locationManager.requestLocationByGPS()
        viewModelScope.launch {
            locationManager.location.collect { locationStatus ->
                when (locationStatus) {
                    is LocationStatus.Success -> {
                        _latitude.value = locationStatus.latLng.latitude
                        _longitude.value = locationStatus.latLng.longitude
                        Log.d("HomeViewModel", "Received GPS location: ${locationStatus.latLng}")
                    }
                    is LocationStatus.Failure -> {
                        Log.e("HomeViewModel", "Failed to receive GPS location: ${locationStatus.throwable}")
                    }
                    else -> {
                        Log.e("HomeViewModel", "Failed to receive GPS location")
                    }
                }
            }
        }
    }
    fun cancelCoroutines() {
        viewModelScope.coroutineContext.cancel()
    }

}
