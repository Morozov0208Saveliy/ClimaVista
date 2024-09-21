package com.example.climavista.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climavista.viewModel.CityViewModel
import com.example.climavista.adapter.CityAdapter
import com.example.climavista.databinding.FragmentCityListBinding
import com.example.climavista.model.CityResponseApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CityListFragment : Fragment() {

    private lateinit var binding: FragmentCityListBinding
    private val cityViewModel: CityViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper()) // For debounce

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cityAdapter = CityAdapter { city ->
            val action = CityListFragmentDirections
                .actionCityListFragmentToWeatherDetailsFragment(
                    city.lat?.toFloat() ?: 0.0f,
                    city.lon?.toFloat() ?: 0.0f,
                    city.name ?: "Unknown"
                )
            findNavController().navigate(action)
        }

        binding.cityView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cityAdapter
        }

        // Implement a debounced text watcher
        binding.cityEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                handler.removeCallbacksAndMessages(null) // Cancel any previously scheduled tasks
                val query = s.toString().trim()

                if (query.isNotEmpty()) {
                    val runnable = Runnable {
                        searchCities(query, cityAdapter)
                    }
                    handler.postDelayed(runnable, 500) // 500ms debounce
                }
            }
        })
    }

    private fun searchCities(query: String, cityAdapter: CityAdapter) {
        binding.progressBar3.visibility = View.VISIBLE
        cityViewModel.loadCity(query, 10).enqueue(object : Callback<CityResponseApi> {
            override fun onResponse(call: Call<CityResponseApi>, response: Response<CityResponseApi>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        // Filter out duplicates by city name
                        val filteredCities = data.distinctBy { it.name }
                        cityAdapter.differ.submitList(filteredCities)
                        binding.progressBar3.visibility = View.GONE
                    } else {
                        showError("No results found")
                        binding.progressBar3.visibility = View.GONE
                    }
                } else {
                    showError("Error loading results")
                    binding.progressBar3.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<CityResponseApi>, t: Throwable) {
                showError("Error: ${t.localizedMessage}")
                binding.progressBar3.visibility = View.GONE
            }
        })
    }
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}