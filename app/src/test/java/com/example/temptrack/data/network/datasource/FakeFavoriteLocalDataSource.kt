package com.example.temptrack.data.network.datasource

import com.example.temptrack.data.database.FavoriteLocalDataSource
import com.example.temptrack.data.model.RoomAlert
import com.example.temptrack.data.model.TempData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeFavoriteLocalDataSource :FavoriteLocalDataSource {

    private val tempDataList = mutableListOf<TempData>()
    private val alertList = mutableListOf<RoomAlert>()
    override suspend fun insertFavorite(favorite: TempData) {
        tempDataList.add(favorite)
    }

    override suspend fun deleteFavorite(favorite: TempData) {
        tempDataList.remove(favorite)
    }

    override suspend fun updateFavorite(favorite: TempData) {

    }

    override fun getAllFavorite(): Flow<List<TempData>> {
        return flow { emit(tempDataList) }
    }

    override fun getAllAlerts(): Flow<List<RoomAlert>> {
        return flow { emit(alertList) }
    }

    override suspend fun insertAlert(alert: RoomAlert) {
        alertList.add(alert)
    }

    override suspend fun deleteAlert(alert: RoomAlert) {
        alertList.remove(alert)
    }
}