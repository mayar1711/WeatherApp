package com.example.temptrack.data.database

import com.example.temptrack.data.model.RoomAlert
import com.example.temptrack.data.model.TempData
import kotlinx.coroutines.flow.Flow

interface FavoriteLocalDataSource {
    suspend fun insertFavorite(favorite:TempData)
    suspend fun deleteFavorite(favorite:TempData)
    suspend fun updateFavorite(favorite: TempData)
    fun getAllFavorite(): Flow<List<TempData>>
    fun getAllAlerts(): Flow<List<RoomAlert>>
    suspend fun insertAlert(alert:RoomAlert)
    suspend fun deleteAlert(alert:RoomAlert)
}