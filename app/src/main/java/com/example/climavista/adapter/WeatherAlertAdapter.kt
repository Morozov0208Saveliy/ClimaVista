package com.example.climavista.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.climavista.databinding.AlertItemBinding
import com.example.climavista.viewModel.WeatherAlert

class WeatherAlertAdapter : ListAdapter<WeatherAlert, WeatherAlertAdapter.AlertViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = AlertItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AlertViewHolder(private val binding: AlertItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alert: WeatherAlert) {
            binding.conditionText.text = "Condition: ${alert.condition}"
            binding.thresholdText.text = "Threshold: ${alert.threshold}"
        }
    }

    // DiffUtil class for more efficient list updates
    class DiffCallback : DiffUtil.ItemCallback<WeatherAlert>() {
        override fun areItemsTheSame(oldItem: WeatherAlert, newItem: WeatherAlert): Boolean {
            // Compare items by unique property, if available
            return oldItem.condition == newItem.condition && oldItem.threshold == newItem.threshold
        }

        override fun areContentsTheSame(oldItem: WeatherAlert, newItem: WeatherAlert): Boolean {
            // Compare the full content of the items
            return oldItem == newItem
        }
    }
}
