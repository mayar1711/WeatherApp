package com.example.temptrack.data.repositry

import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.network.ApiService
import com.example.temptrack.data.repositry.WeatherRepository

class WeatherRepositoryImpl(private val apiService: ApiService) : WeatherRepository {
    override suspend fun getWeatherForecast(latitude: Double, longitude: Double): WeatherForecastResponse {
        return apiService.getWeatherForecast(latitude, longitude, "fc9624bf360991a83e6c82fa2996bec3")
    }
}
