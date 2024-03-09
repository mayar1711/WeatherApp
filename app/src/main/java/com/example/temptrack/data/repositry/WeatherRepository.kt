package com.example.temptrack.data.repositry

import com.example.temptrack.data.model.WeatherForecastResponse
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
   fun getWeatherForecast(latitude: Double, longitude: Double, unit: String, language: String): Flow<WeatherForecastResponse>
   suspend fun getLocation(): LatLng

}