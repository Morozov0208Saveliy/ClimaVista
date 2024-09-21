package com.example.climavista.viewModel

import androidx.lifecycle.ViewModel
import com.example.climavista.repository.WeatherRepository
import com.example.climavista.server.ApiClient
import com.example.climavista.server.ApiServices

class WeatherViewModel(private val repository: WeatherRepository = WeatherRepository(
    ApiClient().getClient().create(ApiServices::class.java)
)) : ViewModel() {

    fun loadCurrentWeather(lat: Double, lng: Double, units: String) =
        repository.getCurrentWeather(lat, lng, units)

    fun loadForecastWeather(lat: Double, lng: Double, units: String) =
        repository.getForecastWeather(lat, lng, units)
}
