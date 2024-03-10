package com.example.temptrack.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temptrack.data.model.TempData
import com.example.temptrack.data.network.ApiWeatherData
import com.example.temptrack.data.repositry.WeatherRepository
import com.example.temptrack.location.WeatherLocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MapsViewModel(  private val _loc: WeatherLocationManager,private val repository: WeatherRepository) :ViewModel(){
    val location = _loc.location
    private val _weatherForecast = MutableStateFlow<ApiWeatherData>(ApiWeatherData.Loading)
    val weatherForecast: StateFlow<ApiWeatherData> = _weatherForecast

    fun requestLocationByGPS() {
        _loc.requestLocationByGPS()
    }

    fun fetchWeatherForecast(latitude: Double, longitude: Double,unit:String,language: String) {
        viewModelScope.launch {
            repository.getWeatherForecast(latitude, longitude,unit,language)
                .catch { e ->
                    _weatherForecast.value = ApiWeatherData.Error(e)
                }
                .collect { result ->
                    _weatherForecast.value = ApiWeatherData.Success(result)
                }
        }
    }
    fun insertFavorite(tempData:TempData){
        viewModelScope.launch{
            repository.insertFavorite(tempData)
        }
    }

}