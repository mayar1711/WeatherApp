package com.example.temptrack.data.network.datasource

import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.network.ApiService

class WeatherRemoteDataSourceImpl private constructor(private val apiService: ApiService) :
    WeatherRemoteDataSource {

    companion object{
        @Volatile
        var instance:WeatherRemoteDataSourceImpl?=null

        fun getInstance ( apiService: ApiService):WeatherRemoteDataSourceImpl{
            return instance?: synchronized(this){
                instance?:WeatherRemoteDataSourceImpl(apiService).also { instance=it }
            }
        }
    }

    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        unit: String,
        language: String
    ): WeatherForecastResponse {
        return apiService.getWeatherForecast(latitude, longitude, unit, language)
    }
}
