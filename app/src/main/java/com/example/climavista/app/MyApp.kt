package com.example.climavista.app

import android.app.Application
import androidx.work.*
import com.example.climavista.repository.WeatherUpdateWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupPeriodicWork()
    }

    private fun setupPeriodicWork() {
        val inputData = Data.Builder()
            .putDouble("lat", 55.7558)  // Пример: широта для Москвы
            .putDouble("lng", 37.6173)  // Пример: долгота для Москвы
            .putString("units", "metric")
            .build()

        val workRequest = PeriodicWorkRequestBuilder<WeatherUpdateWorker>(
            1, TimeUnit.HOURS
        )
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "WeatherUpdateWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}