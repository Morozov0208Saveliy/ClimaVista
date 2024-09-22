package com.example.climavista.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.climavista.databinding.CityViewholderBinding
import com.example.climavista.model.CityResponseApi

class CityAdapter(
    private val onCityClick: (CityResponseApi.CityResponseItem) -> Unit
) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CityViewholderBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = differ.currentList[position]
        holder.bind(city)
    }

    override fun getItemCount() = differ.currentList.size

    inner class ViewHolder(private val binding: CityViewholderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: CityResponseApi.CityResponseItem) {
            binding.cityTxt.text = city.name
            binding.root.setOnClickListener { onCityClick(city) }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<CityResponseApi.CityResponseItem>() {
        override fun areItemsTheSame(
            oldItem: CityResponseApi.CityResponseItem,
            newItem: CityResponseApi.CityResponseItem
        ): Boolean {
            return oldItem == newItem
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
