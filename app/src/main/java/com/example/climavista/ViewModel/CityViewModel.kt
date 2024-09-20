package com.example.climavista.ViewModel

import androidx.lifecycle.ViewModel
import com.example.climavista.repository.CityRepository
import com.example.climavista.server.ApiClient
import com.example.climavista.server.ApiServices

class CityViewModel(val repository: CityRepository) : ViewModel() {
    constructor():this (CityRepository(ApiClient().getClient().create(ApiServices::class.java)))

    fun loadCity(q: String, limit: Int) =
        repository.getCitiesList(q, limit)

}