package com.example.climavista.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.climavista.model.CityResponseApi
import com.example.climavista.repository.CityRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class CityViewModelFailureTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // Для LiveData

    @Mock
    private lateinit var cityRepository: CityRepository

    private lateinit var cityViewModel: CityViewModel

    private val testDispatcher = StandardTestDispatcher() // Создаем тестовый диспетчер

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher) // Подменяем Main Dispatcher на тестовый
        cityViewModel = CityViewModel(cityRepository) // Инициализация ViewModel с мокированным репозиторием
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Возвращаем оригинальный Main Dispatcher после теста
    }

    @Test
    fun `test failure when loading city list`() = runTest {
        // Мокируем исключение при вызове репозитория
        val errorMessage = "Failed to load cities"
        Mockito.`when`(cityRepository.getCitiesList("test", 10)).thenThrow(RuntimeException(errorMessage))

        // Вызываем ViewModel для загрузки списка городов
        cityViewModel.loadCity("test", 10)

        // Завершаем все корутины
        advanceUntilIdle()

        // Получаем результат
        val result = cityViewModel.cityListState.first()
        assertNotNull(result) // Проверяем, что результат не null

        result?.let {
            assertTrue(it.isFailure) // Проверяем, что вызвано исключение
            assertEquals(errorMessage, it.exceptionOrNull()?.message) // Проверяем сообщение ошибки
        }
    }
}
