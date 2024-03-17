package com.example.temptrack.ui.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.temptrack.data.model.DailyWeather
import com.example.temptrack.databinding.WeekItemBinding
import com.example.temptrack.datastore.SettingDataStorePreferences
import com.example.temptrack.util.getDayFormat
import com.example.temptrack.util.getImageIcon

class DailyWeatherAdapter(private val clickListener: (DailyWeather) -> Unit) :
    ListAdapter<DailyWeather, DailyWeatherAdapter.DailyWeatherViewHolder>(DailyWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WeekItemBinding.inflate(inflater, parent, false)
        return DailyWeatherViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        val dailyWeather = getItem(position)
        holder.bind(dailyWeather)
    }

    inner class DailyWeatherViewHolder(
        private val binding: WeekItemBinding,
        private val clickListener: (DailyWeather) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DailyWeather) {
            binding.dailyWeather = item
            binding.root.setOnClickListener { clickListener(item) }
            binding.tvMaxTemp.text=item.maxTemperature.toString()
            binding.tvMinTemp.text=item.minTemperature.toString()
            binding.tvDay.text= item.dayOfWeek
            val icon = getImageIcon(item.weather.firstOrNull()?.icon ?: "")
            binding.weekImage.setImageResource(icon)
            binding.executePendingBindings()
        }
    }

    class DailyWeatherDiffCallback : DiffUtil.ItemCallback<DailyWeather>() {
        override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem == newItem
        }
    }
}
