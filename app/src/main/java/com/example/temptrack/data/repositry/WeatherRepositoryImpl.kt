package com.example.temptrack.data.repositry

import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.network.ApiService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WeatherRepositoryImpl(private val apiService: ApiService) : WeatherRepository {
    override fun getWeatherForecast(latitude: Double, longitude: Double): Flow<WeatherForecastResponse> = flow {
            val response = apiService.getWeatherForecast(latitude, longitude)
            emit(response)
    }.flowOn(Dispatchers.IO)

    override suspend fun getLocation(): LatLng {
        TODO("Not yet implemented")
    }
}
