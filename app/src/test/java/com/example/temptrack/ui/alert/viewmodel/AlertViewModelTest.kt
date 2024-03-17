package com.example.temptrack.ui.alert.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.temptrack.MainRule
import com.example.temptrack.data.model.Current
import com.example.temptrack.data.model.RoomAlert
import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.model.WeatherItem
import com.example.temptrack.data.network.datasource.FakeWeatherRepository
import com.example.temptrack.data.repositry.WeatherRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AlertViewModelTest{
    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    val alert1= RoomAlert(
        1647294000000,
        1647380400000,
        1747320400000,
        "Country D",
        "Description C")
    val alert2=RoomAlert(
        1647294000000,
        1647380410200,
        17473204011111,
        "Country D",
        "Description C")


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
    val response= WeatherForecastResponse(
        current = currentWeather,
        timezone = "America/New_York",
        timezoneOffset = -14400,
        daily = emptyList(),
        lon = -74.006,
        hourly = emptyList(),
        lat = 40.7128,
        alerts = emptyList()
    )

    private lateinit var repository: WeatherRepository
    private lateinit var viewModel:AlertViewModel

    @Before
    fun setup() {
        repository = FakeWeatherRepository(response)
        viewModel = AlertViewModel(repository)
    }


    @Test
    fun `test getAllAlerts success`() = runBlockingTest {

        viewModel.insertAlert(alert1)

        viewModel.getAllAlert()

        assertEquals(listOf(alert1), repository.getAllAlerts().first())
    }

    @Test
    fun `test deleteAlert`() = runBlockingTest {

        repository.insertAlert(alert1)

        viewModel.deleteAlert(alert1)

        assertEquals(emptyList<RoomAlert>(), repository.getAllAlerts().first())
    }

    @Test
    fun `test insertAlert`() = runBlockingTest {

        viewModel.insertAlert(alert1)

        assertEquals(alert1, repository.getAllAlerts().first().first())
    }
}