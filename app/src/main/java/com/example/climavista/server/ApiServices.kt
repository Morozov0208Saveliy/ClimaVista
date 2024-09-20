package com.example.climavista.server

import com.example.climavista.model.CurrentResponseApi
import com.example.climavista.model.ForecastResponseApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("units") units:String,
        @Query("appid") ApiKey: String,
    ): Call<CurrentResponseApi>

        @GET("forecast")
        fun getForecastWeather(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("units") units: String
        ): Call<ForecastResponseApi>
    }
