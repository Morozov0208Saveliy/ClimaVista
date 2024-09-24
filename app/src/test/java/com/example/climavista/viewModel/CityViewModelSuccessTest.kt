package com.example.climavista.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.climavista.model.CityResponseApi
import com.example.climavista.repository.CityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class CityViewModelSuccessTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // Для LiveData

    @Mock
    private lateinit var cityRepository: CityRepository

    private lateinit var cityViewModel: CityViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this) // Инициализация моков
        Dispatchers.setMain(testDispatcher) // Устанавливаем тестовый диспетчер для Dispatchers.Main
        cityViewModel = CityViewModel(cityRepository) // Инициализация ViewModel с мокированным репозиторием
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Сбрасываем Main Dispatcher после тестов
    }

    @Test
    fun `test successful city list loading`() = runTest {
        // Мокируем ответ репозитория
        val mockCities = listOf(
            CityResponseApi.CityResponseItem(
                name = "New York",
                lat = 40.7128,
                lon = -74.0060,
                country = "US",
                state = "NY",
                localNames = null
            ),
            CityResponseApi.CityResponseItem(
                name = "Los Angeles",
                lat = 34.0522,
                lon = -118.2437,
                country = "US",
                state = "CA",
                localNames = null
            )
        )

        Mockito.`when`(cityRepository.getCitiesList("test", 10)).thenReturn(mockCities)

        // Вызываем ViewModel для загрузки списка городов
        cityViewModel.loadCity("test", 10)

        // Завершаем выполнение всех отложенных задач
        advanceUntilIdle()

        // Получаем результат
        val result = cityViewModel.cityListState.first()
        assertNotNull(result) // Проверяем, что результат не null

        result?.let {
            assertTrue(it.isSuccess) // Проверяем, что результат успешный
            assertEquals(mockCities, it.getOrNull()) // Сравниваем данные
        }
    }
}