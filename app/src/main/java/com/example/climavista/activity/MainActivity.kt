package com.example.climavista.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climavista.R
import com.example.climavista.ViewModel.WeatherViewModel
import com.example.climavista.adapter.ForecastAdapter
import com.example.climavista.databinding.ActivityMainBinding
import com.example.climavista.model.CurrentResponseApi
import com.example.climavista.model.ForecastResponseApi
import com.github.matteobattilana.weather.PrecipType
import eightbitlab.com.blurview.RenderScriptBlur
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }


        binding.apply {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var lon = intent.getDoubleExtra("lon", 0.0)
            var name = intent.getStringExtra("name")

            if (lat == 0.0) {
                lat = 51.50
                lon = -0.12
                name = "London"
            }

            addCity.setOnClickListener {
                startActivity(Intent(this@MainActivity, CityListActivity::class.java))
            }

            cityTxt.text = name
            progressBar.visibility = View.VISIBLE
            weatherViewModel.loadCurrentWeather(lat, lon, "metric")
                .enqueue(object : retrofit2.Callback<CurrentResponseApi> {
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
                        Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                })

            var radius = 10f
            val decorView = window.decorView
            val rootView = (decorView.findViewById(android.R.id.content) as ViewGroup?)
            val windowBackground = decorView.background

            rootView?.let {
                blueView.setupWith(it, RenderScriptBlur(this@MainActivity))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                blueView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blueView.clipToOutline = true
            }


            weatherViewModel.loadForecastWeather(lat, lon, "metric")
                .enqueue(object : Callback<ForecastResponseApi> {
                    override fun onResponse(
                        call: Call<ForecastResponseApi>,
                        response: Response<ForecastResponseApi>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            blueView.visibility = View.VISIBLE

                            data.let {
                                forecastAdapter.differ.submitList(it?.list)
                                forecastView.apply {
                                    layoutManager = LinearLayoutManager(
                                        this@MainActivity,
                                        LinearLayoutManager.HORIZONTAL,
                                        false
                                    )
                                    adapter = forecastAdapter
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {
                        TODO("Not yet implemented")
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
            "01" -> {
                initWeather(PrecipType.CLEAR)
            }

            "02", "03", "04" -> {
                initWeather(PrecipType.CLEAR)

            }

            "09", "10", "11" -> {
                initWeather(PrecipType.RAIN)

            }

            "13" -> {
                initWeather(PrecipType.SNOW)

            }

            "50" -> {
                initWeather(PrecipType.CLEAR)

            }

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


