package com.example.temptrack.ui.favorite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.temptrack.data.model.TempData
import com.example.temptrack.databinding.FavitemBinding
import com.example.temptrack.util.getAddress
import com.example.temptrack.util.getImageIcon

class FavoriteListAdapter(val context: Context,private val clickListener: (TempData) -> Unit, private val deleteListener: (TempData) -> Unit

) :
    ListAdapter<TempData, FavoriteListAdapter.FavoriteItemViewHolder>(FavoriteItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavitemBinding.inflate(inflater, parent, false)
        return FavoriteItemViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: FavoriteItemViewHolder, position: Int) {
        val tempData = getItem(position)
        holder.bind(tempData)
        holder.itemView.setOnClickListener {
            clickListener(tempData)
        }
    }

    inner class FavoriteItemViewHolder(
        private val binding: FavitemBinding,
        private val clickListener: (TempData) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TempData) {
            binding.tempData = item
            binding.root.setOnClickListener { clickListener(item) }
            binding.tvMinTemp.text = item.minTemp.toString()
            binding.tvMin.text = item.maxTemp.toString()
            binding.temp.text = item.temp.toString()
            val icon = getImageIcon(item.icon)
            binding.imageView2.setImageResource(icon)
            binding.tvCity.text= getAddress(context,item.lat,item.lang)
            binding.executePendingBindings()

        }
    }

    class FavoriteItemDiffCallback : DiffUtil.ItemCallback<TempData>() {
        override fun areItemsTheSame(oldItem: TempData, newItem: TempData): Boolean {
            return oldItem.city == newItem.city
        }

        override fun areContentsTheSame(oldItem: TempData, newItem: TempData): Boolean {
            return oldItem == newItem
        }
    }
}
