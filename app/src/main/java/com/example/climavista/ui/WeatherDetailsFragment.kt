package com.example.climavista.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import android.Manifest
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.climavista.R
import com.example.climavista.adapter.ForecastAdapter
import com.example.climavista.databinding.FragmentWeatherDetailsBinding
import com.example.climavista.model.CurrentResponseApi
import com.example.climavista.model.ForecastResponseApi
import com.example.climavista.viewModel.WeatherViewModel
import com.github.matteobattilana.weather.PrecipType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class WeatherDetailsFragment : Fragment() {
    private lateinit var binding: FragmentWeatherDetailsBinding
    private lateinit var notificationManager: NotificationManager

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Запросить разрешение, если оно не было предоставлено
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
                Log.d("Notification", "Notification permission not granted, requesting permission.")
                return
            }
        }

        createNotificationChannel()

        binding.addCity.setOnClickListener {
            val action =
                WeatherDetailsFragmentDirections.actionWeatherDetailsFragmentToCityListFragment()
            findNavController().navigate(action)


//            println("call button press send notify ")
//                    val sharedPreferences =
//            requireContext().getSharedPreferences("WeatherAlerts", Context.MODE_PRIVATE)
//
//     val savedThreshold = sharedPreferences.getFloat("threshold", Float.MAX_VALUE)
//            sendNotification(100.0f, savedThreshold)

        }

        binding.weatherAlertImageView.setOnClickListener {
            val currentTemperatureString =
                binding.currentTempTxt.text.toString().replace("°", "").trim()
            val currentTemperature = currentTemperatureString.toFloat()

            val action =
                WeatherDetailsFragmentDirections.actionWeatherDetailsFragmentToWeatherAlertFragment(
                    currentTemperature
                )
            findNavController().navigate(action)
        }

        val lat = args.lat.toDouble()
        val lon = args.lon.toDouble()
        val name = args.name

        binding.apply {
            cityTxt.text = name ?: "Unknown City"
            progressBar.visibility = View.VISIBLE

            observeViewModel(lat, lon)
        }
    }

    private fun observeViewModel(lat: Double, lon: Double) {
        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.loadCurrentWeather(lat, lon, "metric")
            weatherViewModel.currentWeather.collect { weatherData ->
                weatherData?.let { updateUIWithCurrentWeather(it) }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.loadForecastWeather(lat, lon, "metric")
            weatherViewModel.forecastWeather.collect { forecastData ->
                forecastData?.let { updateUIWithForecastWeather(it) }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.error.collect { errorMessage ->
                errorMessage?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    weatherViewModel.clearError()
                }
            }
        }
    }

    private fun updateUIWithCurrentWeather(data: CurrentResponseApi) {
        val temp = data.main?.temp?.toFloat() ?: 0.0f
        val icon = data.weather?.get(0)?.icon ?: "-"
        val humidity = data.main?.humidity?.toString() ?: "-"
        val drawable = if (isNight()) R.mipmap.night_bg else setDinamicallyWallpaper(icon, temp)

        binding.apply {
            bgImage.setImageResource(drawable)
            statusTxt.text = data.weather?.get(0)?.main ?: "-"
            windTxt.text = data.wind?.speed?.let { Math.round(it).toString() } + " km/h"
            currentTempTxt.text = data.main?.temp?.let { Math.round(it).toString() } + "°"
            maxTempTxt.text = data.main?.tempMax?.let { Math.round(it).toString() } + "°"
            minTempTxt.text = data.main?.tempMin?.let { Math.round(it).toString() } + "°"
            humidityTxt.text = "$humidity%"
            setEffectRain(icon)
            checkAlerts(temp)
        }
    }

    private fun updateUIWithForecastWeather(data: ForecastResponseApi) {
        forecastAdapter.differ.submitList(data.list)
        binding.forecastView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = forecastAdapter
        }
        binding.blueView.visibility = View.VISIBLE
    }

    private fun isNight(): Boolean {
        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }

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

    private fun setEffectRain(icon: String) {
        when (icon.dropLast(1)) {
            "01", "02", "03", "04" -> initWeather(PrecipType.CLEAR)
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

    private fun checkAlerts(currentTemperature: Float) {
        val sharedPreferences =
            requireContext().getSharedPreferences("WeatherAlerts", Context.MODE_PRIVATE)
        val savedThreshold = sharedPreferences.getFloat("threshold", Float.MAX_VALUE)
        val savedCondition = sharedPreferences.getString("condition", "")


        if (savedCondition == "Above" && currentTemperature > savedThreshold) {
            sendNotification(currentTemperature, savedThreshold)
        }
    }

    private fun createNotificationChannel() {
        val channelId = getString(R.string.default_notification_channel_id)
        val name = "Weather Alerts"
        val descriptionText = "Channel for weather alerts"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        Log.d("Notification", "Creating notification channel: $channelId")

        notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
        Log.d("Notification", "Notification channel created successfully: $channelId")
    }

    private fun sendNotification(currentTemperature: Float, threshold: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Запрос разрешения, если оно не было предоставлено
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
                return
            }
        }

        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)

        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Weather Alert!")
            .setContentText("Temperature is above $threshold°C!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        Log.d("Notification", "Attempting to send notification on channel: $channelId")

        notificationManager.notify(0, builder.build())
        Log.d("Notification", "Notification sent successfully")
    }
}