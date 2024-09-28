package com.example.climavista.repository

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WeatherUpdateWorker @Inject constructor(
    @ApplicationContext context: Context,
    params: WorkerParameters,
    private val weatherRepository: WeatherRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("WeatherUpdateWorker", "Starting doWork")

        return try {
            val lat = inputData.getDouble("lat", 0.0)
            val lng = inputData.getDouble("lng", 0.0)
            val units = inputData.getString("units") ?: "metric"

            // Получаем данные о погоде
            weatherRepository.getCurrentWeather(lat, lng, units)
            weatherRepository.getForecastWeather(lat, lng, units)

            Log.d("WeatherUpdateWorker", "Weather data updated successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e("WeatherUpdateWorker", "Error updating weather data: ${e.message}")
            Result.failure()
        }
    }
}
