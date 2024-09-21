package com.example.climavista.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.climavista.databinding.CityViewholderBinding
import com.example.climavista.model.CityResponseApi

class CityAdapter(
    private val onCityClick: (CityResponseApi.CityResponseItem) -> Unit // Функция для обработки кликов
) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    // Создаем ViewHolder и связываем его с макетом
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CityViewholderBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    // Связываем данные с ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = differ.currentList[position]
        holder.bind(city)
    }

    // Количество элементов
    override fun getItemCount() = differ.currentList.size

    // ViewHolder для элемента списка
    inner class ViewHolder(private val binding: CityViewholderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: CityResponseApi.CityResponseItem) {
            binding.cityTxt.text = city.name
            binding.root.setOnClickListener {
                onCityClick(city) // Обработка клика
            }
        }
    }

    // Используем DiffUtil для оптимизации обновления списка
    private val differCallback = object : DiffUtil.ItemCallback<CityResponseApi.CityResponseItem>() {
        override fun areItemsTheSame(
            oldItem: CityResponseApi.CityResponseItem,
            newItem: CityResponseApi.CityResponseItem
        ): Boolean {
            return oldItem== newItem // Предположим, что у вас есть идентификатор города
        }

        override fun areContentsTheSame(
            oldItem: CityResponseApi.CityResponseItem,
            newItem: CityResponseApi.CityResponseItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}