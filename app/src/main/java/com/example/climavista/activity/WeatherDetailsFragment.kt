package com.example.climavista.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climavista.R
import com.example.climavista.adapter.ForecastAdapter
import com.example.climavista.databinding.FragmentWeatherDetailsBinding
import com.example.climavista.model.CurrentResponseApi
import com.example.climavista.model.ForecastResponseApi
import com.example.climavista.viewModel.WeatherViewModel
import com.github.matteobattilana.weather.PrecipType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class WeatherDetailsFragment : Fragment() {
    private lateinit var binding: FragmentWeatherDetailsBinding
    private val weatherViewModel: WeatherViewModel by viewModels()

    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }

    private val args: WeatherDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle button clicks for navigation
        binding.addCity.setOnClickListener {
            val action = WeatherDetailsFragmentDirections.actionWeatherDetailsFragmentToCityListFragment()
            findNavController().navigate(action)
        }

        binding.settingsButton.setOnClickListener {
            val action = WeatherDetailsFragmentDirections.actionWeatherDetailsFragmentToSettingsFragment()
            findNavController().navigate(action)
        }

        // Retrieve arguments (latitude, longitude, and city name)
        val lat = args.lat.toDouble()
        val lon = args.lon.toDouble()
        val name = args.name

        // Apply weather data
        binding.apply {
            cityTxt.text = name ?: "Unknown City"
            progressBar.visibility = View.VISIBLE

            // Apply user preferences
            applyUserSettings()

            // Load current weather data
            weatherViewModel.loadCurrentWeather(lat, lon, "metric").enqueue(object :
                Callback<CurrentResponseApi> {
                override fun onResponse(call: Call<CurrentResponseApi>, response: Response<CurrentResponseApi>) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        progressBar.visibility = View.GONE
                        detailLayout.visibility = View.VISIBLE
                        data?.let {
                            val temp = it.main?.temp?.toFloat() ?: 0.0f
                            val icon = it.weather?.get(0)?.icon ?: "-"
                            val humidity = it.main?.humidity?.toString() ?: "-"
                            val drawable = if (isNight()) R.mipmap.night_bg else setDinamicallyWallpaper(icon, temp)
                            bgImage.setImageResource(drawable)
                            statusTxt.text = it.weather?.get(0)?.main ?: "-"
                            windTxt.text = it.wind?.speed?.let { Math.round(it).toString() } + " km/h"
                            currentTempTxt.text = it.main?.temp?.let { Math.round(it).toString() } + "°"
                            maxTempTxt.text = it.main?.tempMax?.let { Math.round(it).toString() } + "°"
                            minTempTxt.text = it.main?.tempMin?.let { Math.round(it).toString() } + "°"
                            humidityTxt.text = "$humidity%"
                            setEffectRain(icon)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to load weather data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                    Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show()
                }
            })

            // Setup blur effect
            val decorView = requireActivity().window.decorView
            val windowBackground = decorView.background
            binding.blueView.setupWith(binding.root)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(10f)
                .setBlurAutoUpdate(true)
            blueView.outlineProvider = ViewOutlineProvider.BACKGROUND
            blueView.clipToOutline = true

            // Load forecast weather data
            weatherViewModel.loadForecastWeather(lat, lon, "metric").enqueue(object : Callback<ForecastResponseApi> {
                override fun onResponse(call: Call<ForecastResponseApi>, response: Response<ForecastResponseApi>) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        blueView.visibility = View.VISIBLE

                        data?.let {
                            forecastAdapter.differ.submitList(it.list)
                            forecastView.apply {
                                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                                adapter = forecastAdapter
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {
                    Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // Determine if it's night time based on the hour
    private fun isNight(): Boolean {
        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }

    // Dynamically change wallpaper based on weather icon and temperature
    private fun setDinamicallyWallpaper(icon: String, temp: Float): Int {
        return when (icon.dropLast(1)) {
            "01" -> {
                initWeather(PrecipType.CLEAR)
                if (temp > 15) R.mipmap.sunny_bg else R.mipmap.snow_bg
            }
            "02", "03", "04" -> {
                initWeather(PrecipType.CLEAR)
                R.mipmap.cloudy_bg
            }
            "09", "10", "11" -> {
                initWeather(PrecipType.RAIN)
                R.mipmap.rainy_bg
            }
            "13" -> {
                initWeather(PrecipType.SNOW)
                R.mipmap.snow_bg
            }
            "50" -> {
                initWeather(PrecipType.CLEAR)
                R.mipmap.haze_bg
            }
            else -> 0
        }
    }

    // Apply rain/snow effects based on weather icon
    private fun setEffectRain(icon: String) {
        when (icon.dropLast(1)) {
            "01", "02", "03", "04" -> initWeather(PrecipType.CLEAR)
            "09", "10", "11" -> initWeather(PrecipType.RAIN)
            "13" -> initWeather(PrecipType.SNOW)
            "50" -> initWeather(PrecipType.CLEAR)
        }
    }

    // Initialize weather effects such as rain or snow
    private fun initWeather(type: PrecipType) {
        binding.weatherView.apply {
            setWeatherData(type)
            angle = -20
            emissionRate = 100.0f
        }
    }

    // Apply user settings, such as temperature unit and frequency
    private fun applyUserSettings() {
        val sharedPreferences = requireContext().getSharedPreferences("climavista_prefs", Context.MODE_PRIVATE)

        val temperatureUnit = sharedPreferences.getString("unit_preference", "Celsius")
        val updateFrequency = sharedPreferences.getInt("update_frequency", 1)

        // Convert temperature based on the unit preference
        weatherViewModel.loadCurrentWeather(args.lat.toDouble(), args.lon.toDouble(), "metric").enqueue(object : Callback<CurrentResponseApi> {
            override fun onResponse(call: Call<CurrentResponseApi>, response: Response<CurrentResponseApi>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.let {
                        val temp = it.main?.temp?.toDouble() ?: 0.0
                        val maxTemp = it.main?.tempMax?.toDouble() ?: 0.0
                        val minTemp = it.main?.tempMin?.toDouble() ?: 0.0

                        val adjustedTemp = if (temperatureUnit == "Fahrenheit") {
                            convertToFahrenheit(temp)
                        } else {
                            temp
                        }

                        val adjustedMaxTemp = if (temperatureUnit == "Fahrenheit") {
                            convertToFahrenheit(maxTemp)
                        } else {
                            maxTemp
                        }

                        val adjustedMinTemp = if (temperatureUnit == "Fahrenheit") {
                            convertToFahrenheit(minTemp)
                        } else {
                            minTemp
                        }

                        binding.currentTempTxt.text = "${Math.round(adjustedTemp)}°"
                        binding.maxTempTxt.text = "${Math.round(adjustedMaxTemp)}°"
                        binding.minTempTxt.text = "${Math.round(adjustedMinTemp)}°"
                    }
                }
            }

            override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Convert Celsius to Fahrenheit
    private fun convertToFahrenheit(celsius: Double): Double {
        return (celsius * 9 / 5) + 32
    }
}