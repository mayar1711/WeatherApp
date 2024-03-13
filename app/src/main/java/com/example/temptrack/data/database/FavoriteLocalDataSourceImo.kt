package com.example.temptrack.data.database

import com.example.temptrack.data.model.RoomAlert
import com.example.temptrack.data.model.TempData
import com.example.temptrack.data.network.ApiService
import com.example.temptrack.data.network.datasource.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.flow.Flow

class FavoriteLocalDataSourceImo private constructor(private val favoriteDao: FavoriteDao):FavoriteLocalDataSource{

    companion object{
        @Volatile
      private  var instance: FavoriteLocalDataSourceImo?=null

        fun getInstance ( favoriteDao: FavoriteDao): FavoriteLocalDataSourceImo{
            return instance?: synchronized(this){
                instance?: FavoriteLocalDataSourceImo(favoriteDao).also { instance=it }
            }
        }
    }
    override suspend fun insertFavorite(favorite: TempData) {
        favoriteDao.insertFavorite(favorite)
    }

    override suspend fun deleteFavorite(favorite: TempData) {
        favoriteDao.deleteFavorite(favorite)
    }

    override suspend fun updateFavorite(favorite: TempData) {
        favoriteDao.updateAll(favorite)
    }

    override fun getAllFavorite(): Flow<List<TempData>> {
         return favoriteDao.getAllFavorite()
    }

    override fun getAllAlerts(): Flow<List<RoomAlert>> {
         return favoriteDao.getAllAlerts()
    }

    override suspend fun insertAlert(alert: RoomAlert) {
        favoriteDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: RoomAlert) {
        favoriteDao.deleteAlert(alert)
    }
}