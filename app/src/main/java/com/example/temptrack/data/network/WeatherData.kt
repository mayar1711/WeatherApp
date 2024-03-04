package com.example.temptrack.data.network

import com.example.temptrack.data.model.WeatherForecastResponse

sealed class ApiWeatherData {
    data class Success(val forecast: WeatherForecastResponse) : ApiWeatherData()
    data class Error(val message: Throwable) : ApiWeatherData()
    object Loading:ApiWeatherData()
}
