package com.example.climavista.repository

import android.content.Context
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
        return try {
            // Получаем данные, переданные в Worker
            val lat = inputData.getDouble("lat", 0.0)
            val lng = inputData.getDouble("lng", 0.0)
            val units = inputData.getString("units") ?: "metric"

            // Вызываем репозиторий для обновления данных о погоде
            weatherRepository.getCurrentWeather(lat, lng, units)
            weatherRepository.getForecastWeather(lat, lng, units)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
