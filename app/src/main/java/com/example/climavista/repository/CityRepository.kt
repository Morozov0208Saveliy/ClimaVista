package com.example.climavista.repository

import com.example.climavista.server.ApiServices

class CityRepository(val api: ApiServices) {
    fun getCitiesList(q: String, limit: Int) =
        api.getCitiesList(q, limit,"0773ade61d7e97ff9b2d9a906d7670bf")

}