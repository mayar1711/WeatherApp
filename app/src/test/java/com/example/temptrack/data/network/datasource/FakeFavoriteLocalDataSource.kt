package com.example.temptrack.data.network.datasource

import com.example.temptrack.data.database.FavoriteLocalDataSource
import com.example.temptrack.data.model.RoomAlert
import com.example.temptrack.data.model.TempData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeFavoriteLocalDataSource : FavoriteLocalDataSource {

    private val favoriteList = mutableListOf<TempData>()
    private val alertList = mutableListOf<RoomAlert>()

    override suspend fun insertFavorite(favorite: TempData) {
        favoriteList.add(favorite)
    }

    override suspend fun deleteFavorite(favorite: TempData) {
        favoriteList.remove(favorite)
    }

    override suspend fun updateFavorite(favorite: TempData) {
        val index = favoriteList.indexOfFirst { it.city == favorite.city }
        if (index != -1) {
            favoriteList[index] = favorite
        }
    }

    override fun getAllFavorite(): Flow<List<TempData>> {
        return flowOf(favoriteList)
    }

    override fun getAllAlerts(): Flow<List<RoomAlert>> {
        return flowOf(alertList)
    }

    override suspend fun insertAlert(alert: RoomAlert) {
        alertList.add(alert)
    }

    override suspend fun deleteAlert(alert: RoomAlert) {
        alertList.remove(alert)
    }
}
