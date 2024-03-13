package com.example.temptrack.data.repositry

import com.example.temptrack.data.model.RoomAlert
import com.example.temptrack.data.model.TempData
import com.example.temptrack.data.model.WeatherForecastResponse
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
   fun getWeatherForecast(latitude: Double, longitude: Double, unit: String, language: String): Flow<WeatherForecastResponse>
   suspend fun insertFavorite(favorite: TempData)
   suspend fun deleteFavorite(favorite: TempData)
   suspend fun updateFavorite(favorite: TempData)
   fun getAllFavorite(): Flow<List<TempData>>
   fun getAllAlerts(): Flow<List<RoomAlert>>
   suspend fun  insertAlert(alert: RoomAlert)
   suspend  fun deleteAlert(alert: RoomAlert)
}