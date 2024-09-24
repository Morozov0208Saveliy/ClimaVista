package com.example.climavista.ui

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climavista.viewModel.CityViewModel
import com.example.climavista.adapter.CityAdapter
import com.example.climavista.databinding.FragmentCityListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CityListFragment : Fragment() {

    private var _binding: FragmentCityListBinding? = null
    val binding get() = _binding!!

    private val cityViewModel: CityViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper()) // For debounce

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Это предотвращает утечки памяти
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

        // Observe the city list StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            cityViewModel.cityListState.collect { result ->
                binding.progressBar3.visibility = View.GONE
                result?.onSuccess { cityList ->
                    // Filter out duplicates by city name
                    val filteredCities = cityList.distinctBy { it.name }
                    cityAdapter.differ.submitList(filteredCities)
                }?.onFailure { error ->
                    showError("Error loading cities: ${error.localizedMessage}")
                }
            }
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
                        searchCities(query)
                    }
                    handler.postDelayed(runnable, 500) // 500ms debounce
                }
            }
        })
    }

    private fun searchCities(query: String) {
        binding.progressBar3.visibility = View.VISIBLE
        cityViewModel.loadCity(query, 10) // No need for enqueue, this triggers the coroutine
    }

    internal fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}