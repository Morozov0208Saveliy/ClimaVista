package com.example.climavista.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climavista.model.CurrentResponseApi
import com.example.climavista.model.ForecastResponseApi
import com.example.climavista.repository.WeatherRepository
import com.example.climavista.server.ApiClient
import com.example.climavista.server.ApiServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _currentWeather = MutableStateFlow<CurrentResponseApi?>(null)
    val currentWeather: StateFlow<CurrentResponseApi?> = _currentWeather

    private val _forecastWeather = MutableStateFlow<ForecastResponseApi?>(null)
    val forecastWeather: StateFlow<ForecastResponseApi?> = _forecastWeather

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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

    fun clearError() {
        _error.value = null
    }
}
