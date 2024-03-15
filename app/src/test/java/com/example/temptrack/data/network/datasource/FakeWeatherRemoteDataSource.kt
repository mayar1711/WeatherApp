package com.example.temptrack.data.network.datasource

import com.example.temptrack.data.model.Alert
import com.example.temptrack.data.model.Current
import com.example.temptrack.data.model.DailyItem
import com.example.temptrack.data.model.FeelsLike
import com.example.temptrack.data.model.Temp
import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.model.WeatherItem
import com.example.temptrack.data.network.ApiService
import com.example.temptrack.data.network.datasource.WeatherRemoteDataSource
import kotlinx.coroutines.flow.flow

class FakeWeatherRemoteDataSource(private val response: WeatherForecastResponse) : WeatherRemoteDataSource {

    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        unit: String,
        language: String
    ): WeatherForecastResponse {
       return response
    }


}
