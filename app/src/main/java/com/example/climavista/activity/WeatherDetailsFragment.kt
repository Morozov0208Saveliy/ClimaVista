package com.example.climavista.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.navArgs
import com.example.climavista.R
import com.example.climavista.ViewModel.WeatherViewModel
import com.example.climavista.adapter.ForecastAdapter
import com.example.climavista.databinding.FragmentWeatherDetailsBinding
import com.example.climavista.fragment.WeatherDetailsFragmentArgs
import com.example.climavista.fragment.WeatherDetailsFragmentDirections
import com.example.climavista.model.CurrentResponseApi
import com.example.climavista.model.ForecastResponseApi
import com.github.matteobattilana.weather.PrecipType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import eightbitlab.com.blurview.RenderScriptBlur


class WeatherDetailsFragment : Fragment() {
    private lateinit var binding: FragmentWeatherDetailsBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }

    // Получаем аргументы, переданные через Navigation Component
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

        binding.addCity.setOnClickListener {
            // Переход на экран поиска (CityListFragment)
            val action =
                WeatherDetailsFragmentDirections.actionWeatherDetailsFragmentToCityListFragment()
            findNavController().navigate(action)
        }

        // Получаем данные из переданных аргументов
        val lat = args.lat.toDouble()
        val lon = args.lon.toDouble()
        val name = args.name

        binding.apply {
            cityTxt.text = name ?: "Unknown City"
            progressBar.visibility = View.VISIBLE

            // Загрузка текущей погоды
            weatherViewModel.loadCurrentWeather(lat, lon, "metric")
                .enqueue(object : Callback<CurrentResponseApi> {
                    override fun onResponse(
                        call: Call<CurrentResponseApi>,
                        response: Response<CurrentResponseApi>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            progressBar.visibility = View.GONE
                            detailLayout.visibility = View.VISIBLE
                            data?.let {
                                statusTxt.text = it.weather?.get(0)?.main ?: "-"
                                windTxt.text =
                                    it.wind?.speed?.let { Math.round(it).toString() } + " km/h"
                                currentTempTxt.text =
                                    it.main?.temp?.let { Math.round(it).toString() } + "°"
                                maxTempTxt.text =
                                    it.main?.tempMax?.let { Math.round(it).toString() } + "°"
                                minTempTxt.text =
                                    it.main?.tempMin?.let { Math.round(it).toString() } + "°"

                                val drawable = if (isNight()) R.mipmap.night_bg
                                else {
                                    setDinamicallyWallpaper(it.weather?.get(0)?.icon ?: "-")
                                }
                                bgImage.setImageResource(drawable)
                                setEffectRain(it.weather?.get(0)?.icon ?: "-")
                            }
                        }
                    }

                    override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                        Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                })

            // Настройка размытия
            val decorView = requireActivity().window.decorView
            val windowBackground = decorView.background
            blueView.setupWith(binding.root)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(RenderScriptBlur(requireContext()))
                .setBlurRadius(10f)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(true)
            blueView.outlineProvider = ViewOutlineProvider.BACKGROUND
            blueView.clipToOutline = true

            // Загрузка прогноза погоды
            weatherViewModel.loadForecastWeather(lat, lon, "metric")
                .enqueue(object : Callback<ForecastResponseApi> {
                    override fun onResponse(
                        call: Call<ForecastResponseApi>,
                        response: Response<ForecastResponseApi>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            blueView.visibility = View.VISIBLE

                            data?.let {
                                forecastAdapter.differ.submitList(it.list)
                                forecastView.apply {
                                    layoutManager = LinearLayoutManager(
                                        context,
                                        LinearLayoutManager.HORIZONTAL,
                                        false
                                    )
                                    adapter = forecastAdapter
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {
                        Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }
    }

    private fun isNight(): Boolean {
        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }

    private fun setDinamicallyWallpaper(icon: String): Int {
        return when (icon.dropLast(1)) {
            "01" -> {
                initWeather(PrecipType.CLEAR)
                R.mipmap.snow_bg
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

    private fun setEffectRain(icon: String) {
        when (icon.dropLast(1)) {
            "01" -> initWeather(PrecipType.CLEAR)
            "02", "03", "04" -> initWeather(PrecipType.CLEAR)
            "09", "10", "11" -> initWeather(PrecipType.RAIN)
            "13" -> initWeather(PrecipType.SNOW)
            "50" -> initWeather(PrecipType.CLEAR)
        }
    }

    private fun initWeather(type: PrecipType) {
        binding.weatherView.apply {
            setWeatherData(type)
            angle = -20
            emissionRate = 100.0f
        }
    }
}