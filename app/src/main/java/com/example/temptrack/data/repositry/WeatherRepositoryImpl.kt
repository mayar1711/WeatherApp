package com.example.temptrack.data.repositry

import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.network.datasource.WeatherRemoteDataSource
import com.example.temptrack.data.network.datasource.WeatherRemoteDataSourceImpl
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WeatherRepositoryImpl private constructor(private val remoteDataSource: WeatherRemoteDataSourceImpl) :
    WeatherRepository {

    companion object {
        @Volatile
        private var instance: WeatherRepositoryImpl? = null

        fun getInstance(remoteDataSource: WeatherRemoteDataSourceImpl): WeatherRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepositoryImpl(remoteDataSource).also { instance = it }
            }
        }
    }

    override fun getWeatherForecast(latitude: Double, longitude: Double, unit: String, language: String): Flow<WeatherForecastResponse> = flow {
        val response = remoteDataSource.getWeatherForecast(latitude, longitude, unit, language)
        emit(response)
    }.flowOn(Dispatchers.IO)

    override suspend fun getLocation(): LatLng {
        TODO("Not yet implemented")
    }
}
