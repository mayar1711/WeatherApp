package com.example.temptrack.ui.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.temptrack.data.model.HourlyWeather
import com.example.temptrack.databinding.DayItemBinding

class HourlyWeatherAdapter(private val clickListener: (HourlyWeather) -> Unit) :
    ListAdapter<HourlyWeather, HourlyWeatherAdapter.HourlyWeatherViewHolder>(HourlyWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DayItemBinding.inflate(inflater, parent, false)
        return HourlyWeatherViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        val hourlyWeather = getItem(position)
        holder.bind(hourlyWeather)
    }

    inner class HourlyWeatherViewHolder(
        private val binding: DayItemBinding,
        private val clickListener: (HourlyWeather) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HourlyWeather) {
            binding.hourlyWeather = item
            binding.root.setOnClickListener { clickListener(item) }
            binding.tvDayTemp.text = item.temperature.toString()
            binding.dayHour.text=item.hour.toString()
            binding.executePendingBindings()
        }
    }

    class HourlyWeatherDiffCallback : DiffUtil.ItemCallback<HourlyWeather>() {
        override fun areItemsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
            return oldItem.hour == newItem.hour
        }

        override fun areContentsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
            return oldItem == newItem
        }
    }
}
