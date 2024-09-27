package com.example.climavista.ui

import com.example.climavista.adapter.WeatherAlertAdapter
import com.example.climavista.viewModel.WeatherAlertViewModel
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climavista.databinding.FragmentWeatherAlertBinding
import kotlinx.coroutines.launch

class WeatherAlertFragment : Fragment() {

    private lateinit var binding: FragmentWeatherAlertBinding
    private lateinit var weatherAlertViewModel: WeatherAlertViewModel
    private val weatherAlertAdapter by lazy { WeatherAlertAdapter() }

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


        binding.currentTemperatureText.text = "Current Temperature: ${args.currentTemp}°C"

        binding.alertRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        weatherAlertViewModel = ViewModelProvider(requireActivity()).get(WeatherAlertViewModel::class.java)
        binding.alertRecyclerView.adapter = weatherAlertAdapter

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.alertRecyclerView)

        viewLifecycleOwner.lifecycleScope.launch {
            weatherAlertViewModel.alertList.collect { alertList ->
                weatherAlertAdapter.submitList(alertList)
                Log.d("WeatherAlertFragment", "Number of alerts in RecyclerView: ${alertList.size}")
            }
        }

        binding.addAlertButton.setOnClickListener {
            addNewAlert()
            hideKeyboard()
        }

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

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // We don't support moving items in this case
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val alertToRemove = weatherAlertAdapter.currentList[position]

            weatherAlertViewModel.removeAlert(alertToRemove)

            Toast.makeText(requireContext(), "Alert removed", Toast.LENGTH_SHORT).show()
        }
    }
}
