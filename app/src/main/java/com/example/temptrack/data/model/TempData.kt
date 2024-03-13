package com.example.temptrack.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "favorite" , primaryKeys = ["lang", "lat"])
data class TempData (

    val minTemp:Double,
    val maxTemp:Double,
    val temp:Double,
    val city:String,
    val icon:String,
    val lang:Double,
    val lat:Double
)