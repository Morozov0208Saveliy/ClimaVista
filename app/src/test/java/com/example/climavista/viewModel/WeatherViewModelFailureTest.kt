package com.example.climavista.viewModel

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
class WeatherViewModelFailureTest {

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
    fun `test load current weather failure`() = runTest {
        // Mock failure response for current weather
        val exception = RuntimeException("Failed to load current weather")
        Mockito.`when`(weatherRepository.getCurrentWeather(40.7128, -74.0060, "metric"))
            .thenThrow(exception)

        // Trigger the ViewModel to load current weather
        weatherViewModel.loadCurrentWeather(40.7128, -74.0060, "metric")
        advanceUntilIdle() // Ensure all coroutines complete

        // Verify the result
        val weatherData = weatherViewModel.currentWeather.first()
        val errorMessage = weatherViewModel.error.first()

        assertNull(weatherData) // weatherData should be null
        assertEquals("Failed to load current weather", errorMessage)
    }

    @Test
    fun `test load forecast weather failure`() = runTest {
        // Mock failure response for forecast weather
        val exception = RuntimeException("Failed to load forecast weather")
        Mockito.`when`(weatherRepository.getForecastWeather(40.7128, -74.0060, "metric"))
            .thenThrow(exception)

        // Trigger the ViewModel to load forecast weather
        weatherViewModel.loadForecastWeather(40.7128, -74.0060, "metric")
        advanceUntilIdle() // Ensure all coroutines complete

        // Verify the result
        val forecastData = weatherViewModel.forecastWeather.first()
        val errorMessage = weatherViewModel.error.first()

        assertNull(forecastData) // forecastData should be null
        assertEquals("Failed to load forecast weather", errorMessage)
    }
}