// NetworkModule.kt
package com.example.climavista.di

import android.content.Context
import androidx.work.WorkerParameters
import com.example.climavista.repository.CityRepository
import com.example.climavista.repository.WeatherRepository
import com.example.climavista.repository.WeatherUpdateWorker
import com.example.climavista.server.ApiClient
import com.example.climavista.server.ApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiServices(retrofit: Retrofit): ApiServices {
        return retrofit.create(ApiServices::class.java)
    }

    @Provides
    @Singleton
    fun provideCityRepository(apiServices: ApiServices): CityRepository {
        return CityRepository(apiServices)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(apiServices: ApiServices): WeatherRepository {
        return WeatherRepository(apiServices)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object WorkerModule {

        @Provides
        fun provideWeatherUpdateWorker(
            @ApplicationContext context: Context,
            params: WorkerParameters,
            weatherRepository: WeatherRepository
        ): WeatherUpdateWorker {
            return WeatherUpdateWorker(context, params, weatherRepository)
        }
    }
}
