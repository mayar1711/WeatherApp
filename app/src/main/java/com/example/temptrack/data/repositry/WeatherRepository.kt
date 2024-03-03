package com.example.temptrack.data.repositry

import android.util.Log
import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.network.ApiService
import com.example.temptrack.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

interface WeatherRepository {
    suspend fun getWeatherForecast(latitude: Double, longitude: Double): WeatherForecastResponse

}