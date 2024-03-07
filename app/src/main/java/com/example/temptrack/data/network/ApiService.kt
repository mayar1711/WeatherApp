package com.example.temptrack.data.network

import com.example.temptrack.data.model.WeatherForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.temptrack.BuildConfig

interface ApiService {
    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units")units:String,
        @Query("lang") language:String,
        @Query("appid") apiKey: String=BuildConfig.WEATHER_API_KEY
    ): WeatherForecastResponse
}