package com.example.climavista.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.climavista.databinding.ForecastViewholderBinding
import com.example.climavista.model.ForecastResponseApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {
    private lateinit var binding: ForecastViewholderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ForecastViewholderBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = ForecastViewholderBinding.bind(holder.itemView)
        val data = differ.currentList[position]

        // Используем java.time API для обработки времени
        val dateTime = LocalDateTime.parse(data.dtTxt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val dayOfWeek = dateTime.dayOfWeek.name.take(3) // Получаем сокращённое имя дня недели
        binding.nameDayTxt.text = dayOfWeek

        // 12-часовой формат
        val hour = dateTime.hour
        val amPm = if (hour < 12) "am" else "pm"
        val hour12 = if (hour == 0 || hour == 12) 12 else hour % 12
        binding.hourTxt.text = "$hour12$amPm"

        // Температура
        binding.tempTxt.text = data.main?.temp?.let { Math.round(it).toString() + "°" } ?: "-"

        // Иконка погоды
        val icon = when (data.weather?.get(0)?.icon) {
            "01d", "01n" -> "sunny"
            "02d", "02n", "03d", "03n" -> "cloudy_sunny"
            "04d", "04n" -> "cloudy"
            "09d", "09n" -> "rainy"
            "10d", "10n" -> "rainy"
            "11d", "11n" -> "storm"
            "13d", "13n" -> "snowy"
            "50d", "50n" -> "windy"
            else -> "sunny"
        }

        val drawableResourceId: Int = binding.root.resources.getIdentifier(
            icon,
            "mipmap",
            binding.root.context.packageName
        )
        Glide.with(binding.root.context)
            .load(drawableResourceId)
            .into(binding.pic)
    }

    inner class ViewHolder(binding: ForecastViewholderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<ForecastResponseApi.Data>() {
        override fun areItemsTheSame(
            oldItem: ForecastResponseApi.Data,
            newItem: ForecastResponseApi.Data
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ForecastResponseApi.Data,
            newItem: ForecastResponseApi.Data
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}