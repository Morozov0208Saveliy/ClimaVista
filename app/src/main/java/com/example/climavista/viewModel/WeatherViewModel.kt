package com.example.climavista.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climavista.model.CurrentResponseApi
import com.example.climavista.model.ForecastResponseApi
import com.example.climavista.repository.WeatherRepository
import com.example.climavista.server.ApiClient
import com.example.climavista.server.ApiServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository(
        ApiClient().getClient().create(ApiServices::class.java)
    )
) : ViewModel() {

    // StateFlow for current weather
    private val _currentWeather = MutableStateFlow<CurrentResponseApi?>(null)
    val currentWeather: StateFlow<CurrentResponseApi?> = _currentWeather

    // StateFlow for forecast weather
    private val _forecastWeather = MutableStateFlow<ForecastResponseApi?>(null)
    val forecastWeather: StateFlow<ForecastResponseApi?> = _forecastWeather

    // StateFlow for loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // StateFlow for error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadCurrentWeather(lat: Double, lng: Double, units: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getCurrentWeather(lat, lng, units)
                _currentWeather.value = response
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadForecastWeather(lat: Double, lng: Double, units: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getForecastWeather(lat, lng, units)
                _forecastWeather.value = response
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Function to clear the error
    fun clearError() {
        _error.value = null
    }
}
