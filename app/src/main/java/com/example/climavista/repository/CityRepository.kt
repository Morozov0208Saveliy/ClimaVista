package com.example.climavista.repository

import com.example.climavista.model.CityResponseApi
import com.example.climavista.server.ApiServices
import retrofit2.awaitResponse
import javax.inject.Inject

class CityRepository @Inject constructor(private val api: ApiServices) {
    suspend fun getCitiesList(q: String, limit: Int): List<CityResponseApi.CityResponseItem> {
        val response =
            api.getCitiesList(q, limit, "0773ade61d7e97ff9b2d9a906d7670bf").awaitResponse()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to load city list")
        }
    }
}

