package com.example.climavista.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climavista.adapter.WeatherAlertAdapter
import com.example.climavista.databinding.FragmentWeatherAlertBinding
import com.example.climavista.viewModel.WeatherAlertViewModel

class WeatherAlertFragment : Fragment() {

    private lateinit var binding: FragmentWeatherAlertBinding
    private lateinit var weatherAlertViewModel: WeatherAlertViewModel
    private val weatherAlertAdapter by lazy { WeatherAlertAdapter() }

    // Retrieve arguments passed from WeatherDetailsFragment
    private val args: WeatherAlertFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Display the current temperature received from WeatherDetailsFragment
        binding.currentTemperatureText.text = "Current Temperature: ${args.currentTemp}°C"

        // Set the LayoutManager for the RecyclerView
        binding.alertRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Setup ViewModel and RecyclerView for displaying weather alerts
        weatherAlertViewModel = ViewModelProvider(requireActivity()).get(WeatherAlertViewModel::class.java)
        binding.alertRecyclerView.adapter = weatherAlertAdapter

        weatherAlertViewModel.alertList.observe(viewLifecycleOwner) { alertList ->
            weatherAlertAdapter.submitList(alertList)
            Log.d("WeatherAlertFragment", "Number of alerts in RecyclerView: ${alertList.size}")
        }

        // Add new alert based on user input
        binding.addAlertButton.setOnClickListener {
            addNewAlert()
            hideKeyboard()
        }

        // Back Button to return to WeatherDetailsFragment
        binding.backButton.setOnClickListener {
            findNavController().popBackStack() // Returns to the previous screen in the back stack
        }
    }

    private fun addNewAlert() {
        val threshold = binding.alertThresholdInput.text.toString().toDoubleOrNull()
        if (threshold == null) {
            Toast.makeText(requireContext(), "Please enter a valid threshold", Toast.LENGTH_SHORT).show()
            return
        }

        val condition = binding.alertConditionSpinner.selectedItem.toString()
        weatherAlertViewModel.addAlert(condition, threshold)
        binding.alertThresholdInput.text.clear()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.alertThresholdInput.windowToken, 0)
    }
}