package com.example.temptrack.ui.home.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temptrack.data.model.WeatherData
import com.example.temptrack.data.network.ApiWeatherData
import com.example.temptrack.data.repositry.WeatherRepository
import com.example.temptrack.location.LocationStatus
import com.example.temptrack.location.WeatherLocationManager
import com.example.temptrack.location.WeatherLocationManagerInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(private val application: Application, private val repository: WeatherRepository) : ViewModel() {
    private val _weatherForecast = MutableStateFlow<ApiWeatherData>(ApiWeatherData.Loading)
    val weatherForecast: StateFlow<ApiWeatherData> = _weatherForecast

    private val locationManager: WeatherLocationManagerInterface = WeatherLocationManager.getInstance(application)

    private val _location = MutableStateFlow<LocationStatus>(LocationStatus.Loading)
    val location: StateFlow<LocationStatus> = _location
    fun fetchWeatherForecast(latitude: Double, longitude: Double,unit:String,language: String) {
        viewModelScope.launch {
            repository.getWeatherForecast(latitude, longitude,unit,language)
                .catch { e ->
                    _weatherForecast.value = ApiWeatherData.Error(e)
                }
                .collect { result ->
                    _weatherForecast.value = ApiWeatherData.Success(result)
                    val list = result.list
                    val filteredLists = mutableListOf<List<WeatherData>>()

                    val groupedWeatherData = list.groupBy { it.dtTxt?.substring(0, 10) }
                    groupedWeatherData.forEach { (_, weatherList) ->

                        filteredLists.add(weatherList)

                    }
                    Log.i("TAG", "fetchWeatherForecast: ${filteredLists[0]}")
                }
        }
    }
    fun requestGPSLocation() {
        locationManager.requestLocationByGPS()
        viewModelScope.launch {
            locationManager.location.collect { locationStatus ->
                when (locationStatus) {
                    is LocationStatus.Success -> {
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

}

