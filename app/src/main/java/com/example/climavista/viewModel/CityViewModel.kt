package com.example.climavista.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climavista.model.CityResponseApi
import com.example.climavista.repository.CityRepository
import com.example.climavista.server.ApiClient
import com.example.climavista.server.ApiServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val repository: CityRepository
) : ViewModel() {

    private val _cityListState = MutableStateFlow<Result<List<CityResponseApi.CityResponseItem>>?>(null)
    val cityListState: StateFlow<Result<List<CityResponseApi.CityResponseItem>>?> = _cityListState

    fun loadCity(q: String, limit: Int) {
        viewModelScope.launch {
            try {
                val cities = repository.getCitiesList(q, limit)
                _cityListState.value = Result.success(cities)
            } catch (e: Exception) {
                _cityListState.value = Result.failure(e)
            }
        }
    }
}

