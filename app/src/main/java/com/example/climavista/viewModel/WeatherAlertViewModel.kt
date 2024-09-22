package com.example.climavista.viewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.asStateFlow

class WeatherAlertViewModel : ViewModel() {

    private val _alertList = MutableStateFlow<List<WeatherAlert>>(emptyList())
    val alertList: StateFlow<List<WeatherAlert>> get() = _alertList.asStateFlow()

    // Add new alert
    fun addAlert(condition: String, threshold: Double) {
        val newAlert = WeatherAlert(condition, threshold)
        _alertList.value += newAlert
    }
}

data class WeatherAlert(val condition: String, val threshold: Double)
