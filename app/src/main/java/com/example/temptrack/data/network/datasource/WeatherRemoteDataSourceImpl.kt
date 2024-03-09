package com.example.temptrack.data.network.datasource

import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WeatherRemoteDataSourceImpl (private val apiService: ApiService) : WeatherRemoteDataSource {
    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        unit: String,
        language: String
    ): WeatherForecastResponse {
        return apiService.getWeatherForecast(latitude, longitude, unit, language)
    }
}