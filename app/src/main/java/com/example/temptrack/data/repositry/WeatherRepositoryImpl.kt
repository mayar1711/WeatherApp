package com.example.temptrack.data.repositry

import com.example.temptrack.data.database.FavoriteLocalDataSource
import com.example.temptrack.data.model.RoomAlert
import com.example.temptrack.data.model.TempData
import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.network.datasource.WeatherRemoteDataSourceImpl
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WeatherRepositoryImpl private constructor(private val remoteDataSource: WeatherRemoteDataSourceImpl,private val localDataSource: FavoriteLocalDataSource) :
    WeatherRepository {

    companion object {
        @Volatile
        private var instance: WeatherRepositoryImpl? = null

        fun getInstance(remoteDataSource: WeatherRemoteDataSourceImpl,localDataSource: FavoriteLocalDataSource): WeatherRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepositoryImpl(remoteDataSource,localDataSource).also { instance = it }
            }
        }
    }

    override fun getWeatherForecast(latitude: Double, longitude: Double, unit: String, language: String): Flow<WeatherForecastResponse> = flow {
        val response = remoteDataSource.getWeatherForecast(latitude, longitude, unit, language)
        emit(response)
    }.flowOn(Dispatchers.IO)

    override suspend fun insertFavorite(favorite: TempData) {
        localDataSource.insertFavorite(favorite)
    }
    override suspend fun deleteFavorite(favorite: TempData) {
        localDataSource.deleteFavorite(favorite)
    }
    override suspend fun updateFavorite(favorite: TempData) {
        localDataSource.updateFavorite(favorite)
    }
    override fun getAllFavorite(): Flow<List<TempData>> {
        return localDataSource.getAllFavorite()
    }
    override fun getAllAlerts(): Flow<List<RoomAlert>> {
        return localDataSource.getAllAlerts()
    }
    override suspend fun insertAlert(alert: RoomAlert) {
        localDataSource.insertAlert(alert)
    }
    override suspend fun deleteAlert(alert: RoomAlert) {
        localDataSource.deleteAlert(alert)
    }
}
