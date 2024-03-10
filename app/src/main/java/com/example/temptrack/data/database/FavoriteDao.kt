package com.example.temptrack.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.temptrack.data.model.TempData
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoriteDao {
    @Query("SELECT * FROM FAVORITE")
    fun getAllFavorite(): Flow<List<TempData>>
    @Insert
    suspend fun insertFavorite(favorite:TempData)
    @Delete
    suspend fun deleteFavorite(favorite: TempData)
}