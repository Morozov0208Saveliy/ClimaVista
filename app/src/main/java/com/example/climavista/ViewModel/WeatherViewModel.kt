package com.example.climavista.ViewModel

import androidx.lifecycle.ViewModel
import com.example.climavista.model.CurrentResponseApi
import com.example.climavista.model.ForecastResponseApi
import com.example.climavista.repository.WeatherRepository
import com.example.climavista.server.ApiClient
import com.example.climavista.server.ApiServices
import retrofit2.Call

class WeatherViewModel(val repository: WeatherRepository) : ViewModel() {
    constructor() : this(WeatherRepository(ApiClient().getClient().create(ApiServices::class.java)))

    fun loadCurrentWeather(lat: Double, lng: Double, units: String) =
        repository.getCurrentWeather(lat, lng, units)

}
