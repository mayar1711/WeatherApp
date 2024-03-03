package com.example.temptrack.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temptrack.data.repositry.WeatherRepository
import com.example.temptrack.data.model.WeatherForecastResponse
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _weatherForecast = MutableLiveData<WeatherForecastResponse>()
    val weatherForecast: LiveData<WeatherForecastResponse> = _weatherForecast

    fun fetchWeatherForecast(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val forecast = repository.getWeatherForecast(latitude, longitude)
                _weatherForecast.postValue(forecast)
                Log.i("TAG", "fetchWeatherForecast: $forecast")
            } catch (e: Exception) {
                Log.i("TAG", "fetchWeatherForecast: ${e.message}")
            }
        }
    }
}