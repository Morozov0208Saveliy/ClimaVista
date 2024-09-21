package com.example.climavista.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.SharedPreferences

class SettingsViewModel(context: Context) : ViewModel() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("climavista_prefs", Context.MODE_PRIVATE)

    private val _temperatureUnit = MutableLiveData<String>()
    val temperatureUnit: LiveData<String> get() = _temperatureUnit

    private val _updateFrequency = MutableLiveData<Int>()
    val updateFrequency: LiveData<Int> get() = _updateFrequency

    init {
        // Load saved preferences
        _temperatureUnit.value = sharedPreferences.getString("unit_preference", "Celsius")
        _updateFrequency.value = sharedPreferences.getInt("update_frequency", 1)
    }

    fun setTemperatureUnit(unit: String) {
        _temperatureUnit.value = unit
        sharedPreferences.edit().putString("unit_preference", unit).apply()
    }

    fun setUpdateFrequency(frequency: Int) {
        _updateFrequency.value = frequency
        sharedPreferences.edit().putInt("update_frequency", frequency).apply()
    }
}
