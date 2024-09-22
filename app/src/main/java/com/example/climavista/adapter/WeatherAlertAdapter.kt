package com.example.climavista.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.climavista.databinding.AlertItemBinding
import com.example.climavista.viewModel.WeatherAlert

class WeatherAlertAdapter : RecyclerView.Adapter<WeatherAlertAdapter.AlertViewHolder>() {

    private val alerts = mutableListOf<WeatherAlert>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = AlertItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(alerts[position])
    }

    override fun getItemCount(): Int = alerts.size

    fun submitList(alertList: List<WeatherAlert>) {
        alerts.clear()
        alerts.addAll(alertList)
        notifyDataSetChanged()

        // Log the number of items in the adapter
        Log.d("WeatherAlertAdapter", "Number of items in adapter: ${alerts.size}")
    }

    inner class AlertViewHolder(private val binding: AlertItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alert: WeatherAlert) {
            binding.conditionText.text = "Condition: ${alert.condition}"
            binding.thresholdText.text = "Threshold: ${alert.threshold}"
        }
    }
}
