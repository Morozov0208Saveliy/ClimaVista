package com.example.climavista.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climavista.ViewModel.CityViewModel
import com.example.climavista.adapter.CityAdapter
import com.example.climavista.databinding.FragmentCityListBinding
import com.example.climavista.fragment.CityListFragmentDirections
import com.example.climavista.model.CityResponseApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CityListFragment : Fragment() {

    private lateinit var binding: FragmentCityListBinding
    private val cityViewModel: CityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Установка адаптера с обработчиком кликов
        val cityAdapter = CityAdapter { city ->
            val action = CityListFragmentDirections
                .actionCityListFragmentToWeatherDetailsFragment(
                    city.lat?.toFloat() ?: 0.0f,  // Преобразование Double? в Float
                    city.lon?.toFloat() ?: 0.0f,  // Преобразование Double? в Float
                    city.name ?: "Unknown"        // Обработка null для строки
                )
            findNavController().navigate(action)
        }

        // Настройка RecyclerView
        binding.cityView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cityAdapter
        }

        // Добавление обработчика изменения текста для поиска городов
        binding.cityEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.progressBar3.visibility = View.VISIBLE
                cityViewModel.loadCity(s.toString(), 10).enqueue(object : Callback<CityResponseApi> {
                    override fun onResponse(
                        call: Call<CityResponseApi>,
                        response: Response<CityResponseApi>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            data?.let {
                                binding.progressBar3.visibility = View.GONE
                                cityAdapter.differ.submitList(it)  // Обновление списка городов
                            }
                        }
                    }

                    override fun onFailure(call: Call<CityResponseApi>, t: Throwable) {
                        binding.progressBar3.visibility = View.GONE
                        // Обработка ошибки
                    }
                })
            }
        })
    }
}