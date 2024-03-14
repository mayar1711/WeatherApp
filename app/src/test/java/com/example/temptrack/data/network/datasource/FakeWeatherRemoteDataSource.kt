package com.example.temptrack.data.network.datasource

import com.example.temptrack.data.model.Alert
import com.example.temptrack.data.model.Current
import com.example.temptrack.data.model.DailyItem
import com.example.temptrack.data.model.FeelsLike
import com.example.temptrack.data.model.Temp
import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.model.WeatherItem
import com.example.temptrack.data.network.ApiService
import com.example.temptrack.data.network.datasource.WeatherRemoteDataSource

class FakeWeatherRemoteDataSource (private val apiService: ApiService): WeatherRemoteDataSource {

    val response:WeatherForecastResponse=createFakeWeatherForecastResponse()
    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        unit: String,
        language: String
    ): WeatherForecastResponse {
       return response
    }
     private fun createFakeWeatherForecastResponse(): WeatherForecastResponse {

        val currentWeather = Current(
            sunrise = 1618076746,
            temp = 22.5,
            visibility = 10000,
            uvi = 3.5,
            pressure = 1010,
            clouds = 20,
            feelsLike = 23.5,
            dt = 1618100000,
            windDeg = 180,
            dewPoint = 15.0,
            sunset = 1618110000,
            weather = listOf(WeatherItem("01d", "Clear", "Clear sky", 800)),
            humidity = 50,
            windSpeed = 3.5
        )

        val dailyWeather = DailyItem(
            moonset = 1618150000,
            summary = "Partly cloudy",
            sunrise = 1618076746,
            temp = Temp(20.0, 25.0, 22.0, 18.0, 23.0, 19.0),
            moonPhase = 0.25,
            uvi = 3.5,
            moonrise = 1618150000,
            pressure = 1010,
            clouds = 40,
            feelsLike = FeelsLike(22.0, 18.0, 23.0, 19.0),
            windGust = 5.0,
            dt = 1618150000,
            pop = 0.2,
            windDeg = 180,
            dewPoint = 15.0,
            sunset = 1618110000,

            weather = listOf(WeatherItem("02d", "Clouds", "Cloudy", 801)),
            humidity = 55,
            windSpeed = 4.0
        )

        val alert = Alert(
            sender_name = "National Weather Service",
            event = "Heat Advisory",
            start = 1618134000,
            end = 1618155600,
            description = "Expect heat index values of up to 105 degrees."
        )

        return WeatherForecastResponse(
            current = currentWeather,
            timezone = "America/New_York",
            timezoneOffset = -14400,
            daily = listOf(dailyWeather),
            lon = -74.006,
            hourly = emptyList(),
            lat = 40.7128,
            alerts = listOf(alert)
        )
    }

}
