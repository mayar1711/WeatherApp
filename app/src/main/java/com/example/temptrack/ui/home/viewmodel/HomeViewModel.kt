package com.example.temptrack.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temptrack.data.model.WeatherData
import com.example.temptrack.data.network.ApiWeatherData
import com.example.temptrack.data.repositry.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _weatherForecast = MutableStateFlow<ApiWeatherData>(ApiWeatherData.Loading)
    val weatherForecast: StateFlow<ApiWeatherData> = _weatherForecast

    fun fetchWeatherForecast(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repository.getWeatherForecast(latitude, longitude)
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

}

