package com.example.climavista.viewModel

import com.example.climavista.model.CurrentResponseApi
import com.example.climavista.model.ForecastResponseApi
import com.example.climavista.repository.WeatherRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelSuccessTest {

    @Mock
    private lateinit var weatherRepository: WeatherRepository

    private lateinit var weatherViewModel: WeatherViewModel

    // Тестовый диспетчер для корутин
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher) // Устанавливаем тестовый Main диспетчер
        weatherViewModel = WeatherViewModel(weatherRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Сбрасываем Main Dispatcher после тестов
    }

    @Test
    fun `test load current weather successfully`() = runTest {
        val mockWeather = CurrentResponseApi(
            base = "stations",
            clouds = CurrentResponseApi.Clouds(all = 0),
            coord = CurrentResponseApi.Coord(lat = 40.7128, lon = -74.0060),
            cod = 200,
            dt = 1634567890,
            id = 5128581,
            main = CurrentResponseApi.Main(
                temp = 15.0,
                pressure = 1013,
                humidity = 78,
                tempMin = 10.0,
                tempMax = 20.0,
                feelsLike = 14.5,
                seaLevel = null,
                grndLevel = null
            ),
            name = "New York",
            rain = null,
            sys = CurrentResponseApi.Sys(
                country = "US",
                sunrise = 1634561234,
                sunset = 1634602345,
                id = null,
                type = 1
            ),
            timezone = -14400,
            visibility = 10000,
            weather = emptyList(),
            wind = CurrentResponseApi.Wind(speed = 5.0, deg = 300, gust = 7.0)
        )
        Mockito.`when`(weatherRepository.getCurrentWeather(40.7128, -74.0060, "metric"))
            .thenReturn(mockWeather)

        weatherViewModel.loadCurrentWeather(40.7128, -74.0060, "metric")
        advanceUntilIdle() // Завершение всех корутин

        val weatherData = weatherViewModel.currentWeather.first()
        assertEquals(mockWeather, weatherData)
    }

    @Test
    fun `test load forecast weather successfully`() = runTest {
        val mockForecast = ForecastResponseApi(
            city = ForecastResponseApi.City(
                coord = ForecastResponseApi.City.Coord(lat = 40.7128, lon = -74.0060),
                country = "US",
                id = 5128581,
                name = "New York",
                population = 8000000,
                sunrise = 1634561234,
                sunset = 1634602345,
                timezone = -14400
            ),
            cnt = 5,
            cod = "200",
            list = emptyList(),
            message = 0
        )
        Mockito.`when`(weatherRepository.getForecastWeather(40.7128, -74.0060, "metric"))
            .thenReturn(mockForecast)

        weatherViewModel.loadForecastWeather(40.7128, -74.0060, "metric")
        advanceUntilIdle() // Завершение всех корутин

        val forecastData = weatherViewModel.forecastWeather.first()
        assertEquals(mockForecast, forecastData)
    }
}
