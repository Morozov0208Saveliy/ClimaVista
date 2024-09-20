package com.example.climavista.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.climavista.R
import com.example.climavista.ViewModel.WeatherViewModel
import com.example.climavista.databinding.ActivityMainBinding
import com.example.climavista.model.CurrentResponseApi
import com.github.matteobattilana.weather.PrecipType
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val calendar by lazy { Calendar.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }


        binding.apply {
            var lat = 51.50
            var lon = -0.12
            var name = "London"

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
                                    it.wind?.speed?.let { Math.round(it).toString() } + "km/h"
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
                                setEffectRain(it.weather?.get(0)?.icon?: "-")
                            }
                        }
                    }

                    override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                        Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT)
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

    private fun setEffectRain(icon: String){
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


