package com.example.climavista.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherAlertViewModel : ViewModel() {

    private val _alertList = MutableLiveData<List<WeatherAlert>>(emptyList())
    val alertList: LiveData<List<WeatherAlert>> get() = _alertList

    // Add new alert
    fun addAlert(condition: String, threshold: Double) {
        val newAlert = WeatherAlert(condition, threshold)
        _alertList.value = _alertList.value?.plus(newAlert)
    }
}

data class WeatherAlert(val condition: String, val threshold: Double)
