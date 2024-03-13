package com.example.temptrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AlertTable")
data class RoomAlert(

    val dateFrom:Long,
    val dateTo:Long,
 @PrimaryKey
    val time:Long,
    val countryName:String,
    val description:String
)