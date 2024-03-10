package com.example.temptrack.ui.home.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temptrack.data.network.ApiWeatherData
import com.example.temptrack.data.repositry.WeatherRepository
import com.example.temptrack.location.LocationStatus
import com.example.temptrack.location.WeatherLocationManager
import com.example.temptrack.location.WeatherLocationManagerInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(application: Application, private val repository: WeatherRepository) : ViewModel() {
    private val _weatherForecast = MutableStateFlow<ApiWeatherData>(ApiWeatherData.Loading)
    val weatherForecast: StateFlow<ApiWeatherData> = _weatherForecast
    //val weatherForToday = mutableListOf<WeatherData?>()
    private val locationManager: WeatherLocationManagerInterface = WeatherLocationManager.getInstance(application)
  //  val filteredLists = mutableListOf<List<WeatherData>>()
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
 /*                   val list = result.list
                    val groupedWeatherData = list.groupBy { it.dtTxt?.substring(0, 10) }
                    groupedWeatherData.forEach { (_, weatherList) ->

                        filteredLists.add(weatherList)

                    }
                    if (filteredLists.isNotEmpty()) {
                        filteredLists[0].let { weatherForToday.addAll(it) }
                    }
                    Log.i("TAG", "fetchWeatherForecast: ${filteredLists[0]}")
                    Log.i("TAG", "fetchWeatherForecast: ${filteredLists[1]}")
                    Log.i("TAG", "fetchWeatherForecast: ${filteredLists[2]}")
                    Log.i("TAG", "fetchWeatherForecast: ${filteredLists[3]}")
                    Log.i("TAG", "fetchWeatherForecast: ${filteredLists[4]}")

 */               }
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
 /*   fun getWeatherForWeek(): List<WeatherData?> {
        val weatherForWeek = mutableListOf<WeatherData?>()
        weatherForWeek.add(filteredLists.getOrNull(1)?.getOrNull(0))
        weatherForWeek.add(filteredLists.getOrNull(2)?.getOrNull(0))
        weatherForWeek.add(filteredLists.getOrNull(3)?.getOrNull(0))
        weatherForWeek.add(filteredLists.getOrNull(4)?.getOrNull(0))
        Log.i("TAG", "getWeatherForWeek: $weatherForWeek")
        return weatherForWeek
    }*/
/*
    fun getWeatherForToday(): List<WeatherData?> {
        val weatherForToday = mutableListOf<WeatherData?>()
        if (filteredLists.isNotEmpty()) {
            filteredLists[0].let { weatherForToday.addAll(it) }
        }
        Log.i("TAG", "getWeatherForToday: $weatherForToday")
        return weatherForToday
    }
*/

}

