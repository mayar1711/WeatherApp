package com.example.temptrack.data.network.datasource

import com.example.temptrack.data.model.WeatherForecastResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
    suspend fun getWeatherForecast(latitude: Double, longitude: Double, unit: String, language: String): WeatherForecastResponse

}