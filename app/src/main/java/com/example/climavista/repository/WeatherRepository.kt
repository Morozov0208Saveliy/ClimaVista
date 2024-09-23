package com.example.climavista.repository

import com.example.climavista.server.ApiServices
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val api: ApiServices) {
    suspend fun getCurrentWeather(lat: Double, lng: Double, unit: String) =
        api.getCurrentWeather(lat, lng, unit, "0773ade61d7e97ff9b2d9a906d7670bf")

    suspend fun getForecastWeather(lat: Double, lng: Double, unit: String) =
        api.getForecastWeather(lat, lng, unit, "0773ade61d7e97ff9b2d9a906d7670bf")
}

