package com.example.temptrack.data.repositry

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.work.ListenableWorker.Result.Success
import com.example.temptrack.data.model.Current
import com.example.temptrack.data.model.RoomAlert
import com.example.temptrack.data.model.TempData
import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.model.WeatherItem
import com.example.temptrack.data.network.datasource.FakeFavoriteLocalDataSource
import com.example.temptrack.data.network.datasource.FakeWeatherRemoteDataSource
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi

class WeatherRepositoryImplTest {
    @get:Rule
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private lateinit var weatherRepository: WeatherRepositoryImpl
    private lateinit var mockRemoteDataSource: FakeWeatherRemoteDataSource
    private lateinit var mockLocalDataSource: FakeFavoriteLocalDataSource


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
    var weather1=WeatherForecastResponse(
        current = currentWeather,
        timezone = "America/New_York",
        timezoneOffset = -14400,
        daily = emptyList(),
        lon = -74.006,
        hourly = emptyList(),
        lat = 40.7128,
        alerts = emptyList()
    )
    @Before
    fun setup() {
        mockRemoteDataSource = FakeWeatherRemoteDataSource(weather1)
        mockLocalDataSource = FakeFavoriteLocalDataSource()
        weatherRepository = WeatherRepositoryImpl.getInstance(mockRemoteDataSource, mockLocalDataSource)
    }

    @Test
    fun `test getWeatherForecast`() = runBlockingTest {
        val flow = weatherRepository.getWeatherForecast(0.0, 0.0, "metric", "en")
        val result = flow.firstOrNull()
        assertNotNull(result)
        assertEquals(weather1, result)
    }

    @Test
    fun `test insertFavorite`() = runBlockingTest {
        val tempData = TempData(
            minTemp = 20.0,
            maxTemp = 20.0,
            temp = 30.0,
            city = "cairo",
            icon = "01d",
            lang = 30.000,
            lat = 31.000
        )
        weatherRepository.insertFavorite(tempData)

        val allFavorites = weatherRepository.getAllFavorite().first()
        assertThat(allFavorites.contains(tempData), `is`(true))
    }
    @Test
    fun `test deleteFavorite`() = runBlockingTest {
        val tempData=TempData(
            minTemp = 20.0,
            maxTemp = 20.0,
            temp = 30.0,
            city ="cairo",
            icon = "01d",
            lang = 30.000,
            lat = 31.000
        )
        weatherRepository.insertFavorite(tempData)
        weatherRepository.deleteFavorite(tempData)

        val allFavorites = weatherRepository.getAllFavorite().first()
        assertThat(allFavorites.contains(tempData), `is`(false))
    }
 /*   @Test
    fun `test updateFavorite`() = runBlockingTest {
        val tempData=TempData(
            minTemp = 20.0,
            maxTemp = 20.0,
            temp = 30.0,
            city ="cairo",
            icon = "01d",
            lang = 30.000,
            lat = 31.000
        )
        weatherRepository.insertFavorite(tempData)

        val updatedTempData = tempData.copy(maxTemp = 30.0)
        weatherRepository.updateFavorite(updatedTempData)

        val allFavorites = weatherRepository.getAllFavorite().first()
        assertTrue(allFavorites.contains(updatedTempData))

    }
*/
     @Test
     fun `test getAllFavorite`() = runBlockingTest {

         val allFavorites = weatherRepository.getAllFavorite().first()
         assertThat(allFavorites.size, `is`(0))
     }

    @Test
    fun `test getAllAlerts`() = runBlockingTest {
        // when
        val allAlerts = weatherRepository.getAllAlerts().first()
        //that
        assertThat(allAlerts.size, `is`(0))
    }

    @Test
    fun `test insertAlert`() = runBlockingTest {
        val roomAlert=RoomAlert(
            1647294000000,
            1647380400000,
            1747320400000,
            "Country D",
            "Description C")
        weatherRepository.insertAlert(roomAlert)

        val allAlerts = weatherRepository.getAllAlerts().first()
        assertTrue(allAlerts.contains(roomAlert))
    }
    @Test
    fun `test deleteAlert`() = runBlockingTest {
        val roomAlert=RoomAlert(
            1647294000000,
            1647380400000,
            1747320400000,
            "Country D",
            "Description C")

        weatherRepository.insertAlert(roomAlert)
        weatherRepository.deleteAlert(roomAlert)

        val allAlerts = weatherRepository.getAllAlerts().first()
        assertFalse(allAlerts.contains(roomAlert))
    }
}
