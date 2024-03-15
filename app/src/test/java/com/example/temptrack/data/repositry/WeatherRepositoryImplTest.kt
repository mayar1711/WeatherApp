package com.example.temptrack.data.repositry
import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.network.ApiService
import com.example.temptrack.data.network.datasource.FakeFavoriteLocalDataSource
import com.example.temptrack.data.network.datasource.FakeRepository
import com.example.temptrack.data.network.datasource.FakeWeatherRemoteDataSource
import com.example.temptrack.data.repositry.WeatherRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WeatherRepositoryImplTest {

 /*   private lateinit var weatherRepositoryImpl: FakeRepository
    private lateinit var apiService:ApiService
    @Before
    fun setup() {
        val fakeRemoteDataSource = FakeWeatherRemoteDataSource(apiService  )
        val fakeLocalDataSource = FakeFavoriteLocalDataSource()
        weatherRepositoryImpl = WeatherRepositoryImpl.getInstance(fakeRemoteDataSource, fakeLocalDataSource)
    }

    @Test
    fun `test getWeatherForecast`() = runBlocking {
        val response: WeatherForecastResponse = weatherRepositoryImpl.getWeatherForecast(0.0, 0.0, "metric", "en")
        assertEquals(1, response.alerts.size) // Check if alerts are fetched
        assertEquals("Partly cloudy", response.daily.first().summary) // Check if daily summary is fetched
    }

 */
}
