package com.example.climavista.repository

import com.example.climavista.server.ApiServices

class WeatherRepository(private val api: ApiServices) {

    // Suspend function for current weather
    suspend fun getCurrentWeather(lat: Double, lng: Double, unit: String) =
        api.getCurrentWeather(lat, lng, unit, "0773ade61d7e97ff9b2d9a906d7670bf")

    // Suspend function for forecast weather
    suspend fun getForecastWeather(lat: Double, lng: Double, unit: String) =
        api.getForecastWeather(lat, lng, unit, "0773ade61d7e97ff9b2d9a906d7670bf")
}
