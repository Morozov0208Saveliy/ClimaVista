package com.example.climavista.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.climavista.databinding.FragmentSettingsBinding
import com.example.climavista.viewModel.SettingsViewModel
import com.example.climavista.viewModel.SettingsViewModelFactory

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use SettingsViewModelFactory to pass the context
        val factory = SettingsViewModelFactory(requireContext())
        settingsViewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)

        // Back button navigation
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Observe LiveData from ViewModel
        settingsViewModel.temperatureUnit.observe(viewLifecycleOwner) { unit ->
            binding.unitSwitch.isChecked = (unit == "Fahrenheit")
        }

        settingsViewModel.updateFrequency.observe(viewLifecycleOwner) { frequency ->
            binding.updateFrequencySeekBar.progress = frequency
        }

        // Set up switch listener
        binding.unitSwitch.setOnCheckedChangeListener { _, isChecked ->
            val unit = if (isChecked) "Fahrenheit" else "Celsius"
            settingsViewModel.setTemperatureUnit(unit)
        }

        // Set up seek bar listener
        binding.updateFrequencySeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                settingsViewModel.setUpdateFrequency(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
